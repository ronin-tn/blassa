// User profile type matching backend Profile DTO
export interface UserProfile {
    firstName: string;
    lastName: string;
    email: string;
    dob: string;
    gender: "MALE" | "FEMALE";
    phoneNumber: string;
    bio: string | null;
    profilePictureUrl: string | null;
    facebookUrl: string | null;
    instagramUrl: string | null;
}

// Auth state
export interface AuthState {
    user: UserProfile | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}
