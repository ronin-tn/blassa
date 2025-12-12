"use client";

import {
    createContext,
    useContext,
    useState,
    useEffect,
    useCallback,
    ReactNode,
} from "react";
import { UserProfile } from "@/types/auth";
import { API_URL } from "@/lib/config";

interface AuthContextType {
    user: UserProfile | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (token: string) => Promise<void>;
    logout: () => void;
    refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_KEY = "blassa_token";

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<UserProfile | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const fetchUserProfile = useCallback(async (authToken: string) => {
        try {
            const response = await fetch(
                `${API_URL}/user/me`,
                {
                    headers: {
                        Authorization: `Bearer ${authToken}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!response.ok) {
                throw new Error("Failed to fetch user profile");
            }

            const profile: UserProfile = await response.json();
            setUser(profile);
            return profile;
        } catch (error) {
            console.error("Error fetching user profile:", error);
            // Token might be invalid, clear it
            localStorage.removeItem(TOKEN_KEY);
            setToken(null);
            setUser(null);
            return null;
        }
    }, []);

    // Initialize auth state from localStorage
    useEffect(() => {
        const initAuth = async () => {
            const storedToken = localStorage.getItem(TOKEN_KEY);
            if (storedToken) {
                setToken(storedToken);
                await fetchUserProfile(storedToken);
            }
            setIsLoading(false);
        };

        initAuth();
    }, [fetchUserProfile]);

    const login = useCallback(
        async (newToken: string) => {
            localStorage.setItem(TOKEN_KEY, newToken);
            setToken(newToken);
            await fetchUserProfile(newToken);
        },
        [fetchUserProfile]
    );

    const logout = useCallback(() => {
        localStorage.removeItem(TOKEN_KEY);
        setToken(null);
        setUser(null);
    }, []);

    const refreshUser = useCallback(async () => {
        if (token) {
            await fetchUserProfile(token);
        }
    }, [token, fetchUserProfile]);

    const value: AuthContextType = {
        user,
        token,
        isAuthenticated: !!user && !!token,
        isLoading,
        login,
        logout,
        refreshUser,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
}
