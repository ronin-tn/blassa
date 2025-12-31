"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
    LayoutDashboard,
    Users,
    AlertTriangle,
    ArrowLeft,
    Shield
} from "lucide-react";
import AdminGuard from "@/components/admin/AdminGuard";

const navigation = [
    { name: "Tableau de bord", href: "/admin", icon: LayoutDashboard },
    { name: "Utilisateurs", href: "/admin/users", icon: Users },
    { name: "Signalements", href: "/admin/reports", icon: AlertTriangle },
];

export default function AdminLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    const pathname = usePathname();

    return (
        <AdminGuard>
            <div className="flex min-h-screen bg-gray-50 font-sans">
                {/* Brand Sidebar */}
                <div className="hidden w-64 flex-col bg-[#0e7490] text-white md:flex shadow-xl">
                    <div className="flex h-20 items-center px-6 border-b border-white/10">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-white/10 rounded-lg">
                                <Shield className="h-6 w-6 text-[#f59e0b]" />
                            </div>
                            <div>
                                <h1 className="text-xl font-bold tracking-tight">Blassa</h1>
                                <span className="text-xs font-medium text-[#f59e0b] uppercase tracking-wider">Admin Panel</span>
                            </div>
                        </div>
                    </div>

                    <nav className="flex-1 space-y-1 px-3 py-6">
                        {navigation.map((item) => {
                            const isActive = pathname === item.href;
                            return (
                                <Link
                                    key={item.name}
                                    href={item.href}
                                    className={`flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition-all duration-200 ${isActive
                                            ? "bg-white text-[#0e7490] shadow-md translate-x-1"
                                            : "text-white/80 hover:bg-white/10 hover:text-white hover:translate-x-1"
                                        }`}
                                >
                                    <item.icon className={`h-5 w-5 ${isActive ? "text-[#0e7490]" : "text-[#f59e0b]"}`} />
                                    {item.name}
                                </Link>
                            );
                        })}
                    </nav>

                    <div className="border-t border-white/10 p-4">
                        <Link
                            href="/dashboard"
                            className="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium text-white/80 transition-colors hover:bg-red-500/20 hover:text-white"
                        >
                            <ArrowLeft className="h-5 w-5" />
                            Retour au site
                        </Link>
                    </div>
                </div>

                {/* Main Content */}
                <main className="flex-1 overflow-y-auto">
                    {/* Mobile Header */}
                    <div className="flex h-16 items-center justify-between border-b border-gray-200 bg-white px-6 shadow-sm md:hidden">
                        <span className="font-bold text-[#0e7490] text-lg">Blassa Admin</span>
                    </div>

                    <div className="p-8 max-w-7xl mx-auto">
                        {children}
                    </div>
                </main>
            </div>
        </AdminGuard>
    );
}
