"use client";

import { useAuth } from "@/contexts/AuthContext";
import CompleteProfileModal from "@/components/CompleteProfileModal";

interface ProfileGuardProps {
    children: React.ReactNode;
}

/**
 * Wrapper component that blocks access to protected pages
 * until the user has completed their profile (phone, DOB, gender).
 * Shows a non-dismissable modal overlay.
 */
export default function ProfileGuard({ children }: ProfileGuardProps) {
    const { user, token, needsProfileCompletion, refreshUser, isLoading } = useAuth();

    // Show loading state while checking auth
    if (isLoading) {
        return <>{children}</>;
    }

    // If profile is incomplete, show blocking modal
    if (needsProfileCompletion && user && token) {
        return (
            <>
                {/* Render page content behind (blurred/blocked) */}
                <div className="pointer-events-none select-none opacity-50 blur-sm">
                    {children}
                </div>

                {/* Non-dismissable modal overlay */}
                <CompleteProfileModal
                    isOpen={true}
                    onComplete={async () => {
                        await refreshUser();
                    }}
                    token={token}
                    currentData={{
                        firstName: user.firstName,
                        lastName: user.lastName,
                        phoneNumber: user.phoneNumber,
                        dob: user.dob,
                        gender: user.gender,
                        bio: user.bio,
                        facebookUrl: user.facebookUrl,
                        instagramUrl: user.instagramUrl,
                    }}
                />
            </>
        );
    }

    // Profile is complete, render children normally
    return <>{children}</>;
}
