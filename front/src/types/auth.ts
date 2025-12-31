

import { UserProfile } from "./models";


export type { UserProfile };


export interface AuthState {
    user: UserProfile | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}
