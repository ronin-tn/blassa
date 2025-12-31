"use client";

import { useAuth } from "@/contexts/AuthContext";
import CompleteProfileModal from "@/components/CompleteProfileModal";

interface ProfileGuardProps {
    children: React.ReactNode;
}

export default function ProfileGuard({ children }: ProfileGuardProps) {
    const { user, needsProfileCompletion, refreshUser, isLoading } = useAuth();

    if (isLoading) {
        return <>{children}</>;
    }
    if (needsProfileCompletion && user) {
        return (
            <>
                <div className="pointer-events-none select-none opacity-50 blur-sm">
                    {children}
                </div>

                <CompleteProfileModal
                    isOpen={true}
                    onComplete={async () => {
                        await refreshUser();
                    }}

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

    return <>{children}</>;
}
