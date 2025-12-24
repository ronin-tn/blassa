import { redirect } from "next/navigation";
import { isAuthenticated } from "@/lib/auth/server";
import ProfileGuard from "@/components/ProfileGuard";
import DashboardClientContent from "@/components/dashboard/DashboardClientContent";

/**
 * Dashboard Page - Server Component
 * 
 * Performs server-side authentication check before rendering.
 * The client content handles data fetching and interactivity.
 */
export default async function DashboardPage() {
    // Server-side auth check
    const isAuth = await isAuthenticated();

    if (!isAuth) {
        redirect("/login");
    }

    return (
        <ProfileGuard>
            <DashboardClientContent />
        </ProfileGuard>
    );
}
