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
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {

                if (response.status === 401 || response.status === 403) {
                    try {

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


    useEffect(() => {
        const initAuth = async () => {
            await fetchUserProfile();
            setIsLoading(false);
        };

        initAuth();
    }, [fetchUserProfile]);


    const login = useCallback(async () => {
        await fetchUserProfile();
    }, [fetchUserProfile]);


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

            window.location.href = "/";
        }
    }, []);

    const refreshUser = useCallback(async () => {
        await fetchUserProfile();
    }, [fetchUserProfile]);


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

