import { apiGet, apiPut } from "./client";
import { UserProfile, UserStats } from "@/types/models";

export type { UserProfile, UserStats };

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

export async function getCurrentUser(): Promise<UserProfile> {
    return apiGet<UserProfile>("/user/me");
}


export async function updateProfile(data: UpdateProfileRequest): Promise<UserProfile> {
    return apiPut<UserProfile>("/user/me", data);
}


export async function completeProfile(data: CompleteProfileRequest): Promise<UserProfile> {
    return apiPut<UserProfile>("/user/me/complete", data);
}


export async function changePassword(data: ChangePasswordRequest): Promise<void> {
    return apiPut<void>("/user/me/password", data);
}




export async function getUserStats(): Promise<UserStats> {
    return apiGet<UserStats>("/user/me/stats");
}

export function isProfileComplete(user: UserProfile | null): boolean {
    if (!user) return false;
    return !!(user.phoneNumber && user.dob && user.gender);
}
