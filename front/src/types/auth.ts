/**
 * Auth-specific types
 * UserProfile is imported from the canonical models.ts
 */

import { UserProfile } from "./models";

// Re-export for convenience
export type { UserProfile };

// Auth state for context
export interface AuthState {
    user: UserProfile | null;
    isAuthenticated: boolean;
    isLoading: boolean;
}
