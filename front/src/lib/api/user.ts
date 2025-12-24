/**
 * User API service for server-side data fetching
 */

import { apiGet, apiPut, apiDelete } from "./client";
import { UserProfile, UserStats } from "@/types/models";

// Re-export types for convenience
export type { UserProfile, UserStats };

// Request Types (specific to this API)
export interface UpdateProfileRequest {
    firstName?: string;
    lastName?: string;
    phoneNumber?: string;
    bio?: string;
    facebookUrl?: string;
    instagramUrl?: string;
}

export interface CompleteProfileRequest {
    phoneNumber: string;
    dob: string;
    gender: "MALE" | "FEMALE";
}

export interface ChangePasswordRequest {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
}

// API Functions

/**
 * Get current user's profile
 */
export async function getCurrentUser(): Promise<UserProfile> {
    return apiGet<UserProfile>("/user/me");
}

/**
 * Update current user's profile
 */
export async function updateProfile(data: UpdateProfileRequest): Promise<UserProfile> {
    return apiPut<UserProfile>("/user/me", data);
}

/**
 * Complete OAuth user's profile
 */
export async function completeProfile(data: CompleteProfileRequest): Promise<UserProfile> {
    return apiPut<UserProfile>("/user/me/complete", data);
}

/**
 * Change password
 */
export async function changePassword(data: ChangePasswordRequest): Promise<void> {
    return apiPut<void>("/user/me/password", data);
}

/**
 * Delete current user's account
 */
export async function deleteAccount(): Promise<void> {
    return apiDelete<void>("/user/me");
}

/**
 * Get current user's stats
 */
export async function getUserStats(): Promise<UserStats> {
    return apiGet<UserStats>("/user/me/stats");
}

/**
 * Check if profile is complete
 */
export function isProfileComplete(user: UserProfile | null): boolean {
    if (!user) return false;
    return !!(user.phoneNumber && user.dob && user.gender);
}
