/**
 * Server-side authentication utilities
 * These functions are meant to be used in Server Components and Route Handlers
 */

import { cookies } from "next/headers";
import { apiGet, ApiClientError } from "../api/client";
import { isProfileComplete as checkUserProfileComplete } from "../api/user";
import { AUTH_COOKIE_NAME } from "@/lib/config";
import type { UserProfile } from "../api/user";

/**
 * Check if the user is authenticated (has valid cookie)
 * Use this in Server Components to conditionally render content
 */
export async function isAuthenticated(): Promise<boolean> {
    const cookieStore = await cookies();
    const token = cookieStore.get(AUTH_COOKIE_NAME);
    return !!token?.value;
}

/**
 * Get the current user from the server side
 * Returns null if not authenticated or if token is invalid
 */
export async function getServerUser(): Promise<UserProfile | null> {
    const hasAuth = await isAuthenticated();
    if (!hasAuth) return null;

    try {
        return await apiGet<UserProfile>("/user/me");
    } catch (error) {
        if (error instanceof ApiClientError && error.status === 401) {
            return null;
        }
        throw error;
    }
}

/**
 * Check if the user's profile is complete
 */
export async function isProfileComplete(): Promise<boolean> {
    const user = await getServerUser();
    return checkUserProfileComplete(user);
}

/**
 * Get the auth token value (for special cases like WebSocket connections)
 * Prefer using apiClient which handles auth automatically
 */
export async function getAuthToken(): Promise<string | undefined> {
    const cookieStore = await cookies();
    return cookieStore.get(AUTH_COOKIE_NAME)?.value;
}
