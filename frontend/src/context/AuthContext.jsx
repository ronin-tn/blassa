/*
  Auth Context - Global Authentication State
  -------------------------------------------
  React Context for managing authentication across the entire app.
  
  This provides:
  - user: The current user object (from profile API)
  - token: The JWT token
  - isAuthenticated: Boolean for quick checks
  - loading: True while checking initial auth state
  - login(): Function to log in
  - register(): Function to register
  - logout(): Function to log out
  - refreshProfile(): Function to refresh user profile from API
  
  Usage in components:
  const { user, isAuthenticated, login, logout } = useAuth();
*/

import { createContext, useState, useEffect, useCallback } from 'react';
import { login as loginApi, register as registerApi } from '@/api/authApi';
import { getMyProfile } from '@/api/userApi';

// Create the context
export const AuthContext = createContext(null);

// Helper: Decode JWT payload (without verification - server already verified)
const decodeToken = (token) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
};

// Helper: Check if token is expired
const isTokenExpired = (token) => {
  const decoded = decodeToken(token);
  if (!decoded || !decoded.exp) return true;
  // exp is in seconds, Date.now() is in milliseconds
  return decoded.exp * 1000 < Date.now();
};

// Auth Provider Component
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch user profile from API
  const fetchProfile = useCallback(async () => {
    try {
      const profile = await getMyProfile();
      setUser(profile);
      localStorage.setItem('user', JSON.stringify(profile));
      return profile;
    } catch (error) {
      console.error('Failed to fetch profile:', error);
      return null;
    }
  }, []);

  // Check for existing token on mount
  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('token');

      if (storedToken && !isTokenExpired(storedToken)) {
        setToken(storedToken);
        // Fetch fresh profile from API
        await fetchProfile();
      } else {
        // Clear expired token
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }

      setLoading(false);
    };

    initAuth();
  }, [fetchProfile]);

  // Login function
  const login = useCallback(async (credentials) => {
    const response = await loginApi(credentials);
    const { token: newToken } = response;

    // Store token
    localStorage.setItem('token', newToken);
    setToken(newToken);

    // Fetch full profile from API
    await fetchProfile();

    return response;
  }, [fetchProfile]);

  // Register function
  const register = useCallback(async (data) => {
    const response = await registerApi(data);
    const { token: newToken } = response;

    // If token is returned, auto-login
    if (newToken) {
      localStorage.setItem('token', newToken);
      setToken(newToken);

      // Fetch full profile from API
      await fetchProfile();
    }

    return response;
  }, [fetchProfile]);

  // Logout function
  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }, []);

  // Refresh profile (can be called after profile update)
  const refreshProfile = useCallback(async () => {
    if (token) {
      return await fetchProfile();
    }
    return null;
  }, [token, fetchProfile]);

  // Context value
  const value = {
    user,
    token,
    isAuthenticated: !!token,
    loading,
    login,
    register,
    logout,
    refreshProfile,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;
