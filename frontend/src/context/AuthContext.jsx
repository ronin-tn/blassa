/*
  Auth Context - Global Authentication State
  -------------------------------------------
  React Context for managing authentication across the entire app.
  
  This provides:
  - user: The current user object (decoded from JWT or stored)
  - token: The JWT token
  - isAuthenticated: Boolean for quick checks
  - loading: True while checking initial auth state
  - login(): Function to log in
  - register(): Function to register
  - logout(): Function to log out
  
  Usage in components:
  const { user, isAuthenticated, login, logout } = useAuth();
*/

import { createContext, useState, useEffect, useCallback } from 'react';
import { login as loginApi, register as registerApi } from '@/api/authApi';

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

  // Check for existing token on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (storedToken && !isTokenExpired(storedToken)) {
      setToken(storedToken);
      if (storedUser) {
        try {
          setUser(JSON.parse(storedUser));
        } catch {
          // If user parse fails, decode from token
          const decoded = decodeToken(storedToken);
          if (decoded) {
            setUser({ email: decoded.sub });
          }
        }
      }
    } else {
      // Clear expired token
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }

    setLoading(false);
  }, []);

  // Login function
  const login = useCallback(async (credentials) => {
    const response = await loginApi(credentials);
    const { token: newToken } = response;

    // Store token
    localStorage.setItem('token', newToken);
    setToken(newToken);

    // Decode user from token
    const decoded = decodeToken(newToken);
    const userData = { email: decoded?.sub };
    localStorage.setItem('user', JSON.stringify(userData));
    setUser(userData);

    return response;
  }, []);

  // Register function
  const register = useCallback(async (data) => {
    const response = await registerApi(data);
    const { token: newToken } = response;

    // If token is returned, auto-login
    if (newToken) {
      localStorage.setItem('token', newToken);
      setToken(newToken);

      const decoded = decodeToken(newToken);
      const userData = { email: decoded?.sub };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
    }

    return response;
  }, []);

  // Logout function
  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }, []);

  // Context value
  const value = {
    user,
    token,
    isAuthenticated: !!token,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;
