"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { LayoutDashboard, Car, Plus, Ticket, User, Search } from "lucide-react";
import { cn } from "@/lib/utils";

interface MobileBottomNavProps {
    isAuthenticated: boolean;
}

export default function MobileBottomNav({ isAuthenticated }: MobileBottomNavProps) {
    const pathname = usePathname();

    const isActiveLink = (href: string) => {
        if (href === "/dashboard") {
            return pathname === "/dashboard";
        }
        return pathname.startsWith(href);
    };

    return (
        <div className="lg:hidden fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-slate-200 safe-area-bottom">
            <div className="flex items-center justify-around py-2">
                {isAuthenticated ? (
                    <>
                        <Link
                            href="/dashboard"
                            className={cn(
                                "flex flex-col items-center gap-1 px-3 py-2",
                                isActiveLink("/dashboard") ? "text-[var(--color-blassa-teal)]" : "text-slate-500"
                            )}
                        >
                            <LayoutDashboard className="w-5 h-5" />
                            <span className="text-[10px] font-medium">Accueil</span>
                        </Link>
                        <Link
                            href="/dashboard/rides"
                            className={cn(
                                "flex flex-col items-center gap-1 px-3 py-2",
                                isActiveLink("/dashboard/rides") ? "text-[var(--color-blassa-teal)]" : "text-slate-500"
                            )}
                        >
                            <Car className="w-5 h-5" />
                            <span className="text-[10px] font-medium">Mes trajets</span>
                        </Link>
                        <Link
                            href="/publish"
                            className="flex flex-col items-center gap-1 px-3 py-2 text-[var(--color-blassa-teal)]"
                        >
                            <div className="w-10 h-10 bg-[var(--color-blassa-teal)] rounded-full flex items-center justify-center -mt-4 shadow-lg">
                                <Plus className="w-5 h-5 text-white" />
                            </div>
                            <span className="text-[10px] font-medium">Publier</span>
                        </Link>
                        <Link
                            href="/dashboard/bookings"
                            className={cn(
                                "flex flex-col items-center gap-1 px-3 py-2",
                                isActiveLink("/dashboard/bookings") ? "text-[var(--color-blassa-teal)]" : "text-slate-500"
                            )}
                        >
                            <Ticket className="w-5 h-5" />
                            <span className="text-[10px] font-medium">Réservations</span>
                        </Link>
                        <Link
                            href="/dashboard/profile"
                            className={cn(
                                "flex flex-col items-center gap-1 px-3 py-2",
                                isActiveLink("/dashboard/profile") ? "text-[var(--color-blassa-teal)]" : "text-slate-500"
                            )}
                        >
                            <User className="w-5 h-5" />
                            <span className="text-[10px] font-medium">Profil</span>
                        </Link>
                    </>
                ) : (
                    <>
                        <Link
                            href="/#search"
                            className="flex flex-col items-center gap-1 px-4 py-2 text-slate-600"
                        >
                            <Search className="w-5 h-5" />
                            <span className="text-xs font-medium">Rechercher</span>
                        </Link>
                        <Link
                            href="/publish"
                            className="flex flex-col items-center gap-1 px-4 py-2 text-[var(--color-blassa-teal)]"
                        >
                            <Plus className="w-5 h-5" />
                            <span className="text-xs font-medium">Publier</span>
                        </Link>
                        <Link
                            href="/login"
                            className="flex flex-col items-center gap-1 px-4 py-2 text-slate-600"
                        >
                            <User className="w-5 h-5" />
                            <span className="text-xs font-medium">Connexion</span>
                        </Link>
                    </>
                )}
            </div>
        </div>
    );
}
