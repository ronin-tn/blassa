// User profile type matching backend Profile DTO
export interface UserProfile {
    firstName: string;
    lastName: string;
    email: string;
    dob: string | null;
    gender: "MALE" | "FEMALE" | null;
    phoneNumber: string | null;
    bio: string | null;
    profilePictureUrl: string | null;
    facebookUrl: string | null;
    instagramUrl: string | null;
    oauthProvider?: string | null; // For OAuth users
}

// Auth state
export interface AuthState {
    user: UserProfile | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}
