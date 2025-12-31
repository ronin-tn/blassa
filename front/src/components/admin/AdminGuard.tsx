"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { Spinner } from "@/components/ui/Spinner";

export default function AdminGuard({ children }: { children: React.ReactNode }) {
    const { user, isLoading, isAuthenticated } = useAuth();
    const router = useRouter();
    const [isAuthorized, setIsAuthorized] = useState(false);

    useEffect(() => {
        if (!isLoading) {
            if (!isAuthenticated) {
                router.push("/login?redirect=/admin");
            } else if (user?.role !== "ADMIN") {
                router.push("/dashboard"); // Redirect regular users away
            } else {
                setIsAuthorized(true);
            }
        }
    }, [user, isLoading, isAuthenticated, router]);

    if (isLoading || !isAuthorized) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gray-50">
                <Spinner size="lg" className="text-primary" />
            </div>
        );
    }

    return <>{children}</>;
}
