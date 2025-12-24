"use client";

import {
    createContext,
    useContext,
    useState,
    useEffect,
    useCallback,
    ReactNode,
    useMemo,
} from "react";
import { UserProfile } from "@/types/auth";
import { API_URL } from "@/lib/config";

interface AuthContextType {
    user: UserProfile | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    isProfileComplete: boolean;
    needsProfileCompletion: boolean;
    login: () => Promise<void>;
    logout: () => Promise<void>;
    refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Helper function to check if profile is complete
function checkProfileComplete(user: UserProfile | null): boolean {
    if (!user) return false;
    return !!(user.phoneNumber && user.dob && user.gender);
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<UserProfile | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const fetchUserProfile = useCallback(async () => {
        try {
            const response = await fetch(`${API_URL}/user/me`, {
                credentials: "include", // Send cookies
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                // Not authenticated or token invalid
                if (response.status === 401 || response.status === 403) {
                    try {
                        // Clear the invalid cookie
                        await fetch(`${API_URL}/auth/logout`, {
                            method: "POST",
                            credentials: "include",
                        });
                    } catch (e) {
                        console.error("Failed to clear invalid session:", e);
                    }
                }
                setUser(null);
                return null;
            }

            const profile: UserProfile = await response.json();
            setUser(profile);
            return profile;
        } catch (error) {
            console.error("Error fetching user profile:", error);
            setUser(null);
            return null;
        }
    }, []);

    // Initialize auth state by fetching user profile
    useEffect(() => {
        const initAuth = async () => {
            await fetchUserProfile();
            setIsLoading(false);
        };

        initAuth();
    }, [fetchUserProfile]);

    // Login: Fetch user profile after successful backend login
    // (Cookie is set by backend, we just need to fetch user data)
    const login = useCallback(async () => {
        await fetchUserProfile();
    }, [fetchUserProfile]);

    // Logout: Call backend to clear cookie
    const logout = useCallback(async () => {
        try {
            await fetch(`${API_URL}/auth/logout`, {
                method: "POST",
                credentials: "include",
            });
        } catch (error) {
            console.error("Logout error:", error);
        } finally {
            setUser(null);
            // Force redirect to home (middleware will handle if needed)
            window.location.href = "/";
        }
    }, []);

    const refreshUser = useCallback(async () => {
        await fetchUserProfile();
    }, [fetchUserProfile]);

    // Computed properties for profile completion status
    const isProfileComplete = useMemo(() => checkProfileComplete(user), [user]);
    const needsProfileCompletion = useMemo(
        () => !!user && !isProfileComplete,
        [user, isProfileComplete]
    );

    const value: AuthContextType = {
        user,
        isAuthenticated: !!user,
        isLoading,
        isProfileComplete,
        needsProfileCompletion,
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

