import { cookies } from "next/headers";
import { apiGet, ApiClientError } from "../api/client";
import { isProfileComplete as checkUserProfileComplete } from "../api/user";
import { AUTH_COOKIE_NAME } from "@/lib/config";
import type { UserProfile } from "../api/user";

export async function isAuthenticated(): Promise<boolean> {
    const cookieStore = await cookies();
    const token = cookieStore.get(AUTH_COOKIE_NAME);
    return !!token?.value;
}

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

export async function isProfileComplete(): Promise<boolean> {
    const user = await getServerUser();
    return checkUserProfileComplete(user);
}

export async function getAuthToken(): Promise<string | undefined> {
    const cookieStore = await cookies();
    return cookieStore.get(AUTH_COOKIE_NAME)?.value;
}
