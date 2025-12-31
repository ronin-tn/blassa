import { redirect } from "next/navigation";
import { isAuthenticated } from "@/lib/auth/server";
import ProfileGuard from "@/components/ProfileGuard";
import DashboardClientContent from "@/components/dashboard/DashboardClientContent";


export default async function DashboardPage() {

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
