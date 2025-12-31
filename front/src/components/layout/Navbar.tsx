"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter, usePathname } from "next/navigation";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import NotificationDropdown from "@/components/layout/NotificationDropdown";
import ProfileDropdown from "@/components/layout/ProfileDropdown";
import MobileBottomNav from "@/components/layout/MobileBottomNav";
import {
    Search,
    Menu,
    X,
    Plus,
    User,
    Car,
    Ticket,
    Settings,
    LogOut,
    LayoutDashboard,
    Star,
} from "lucide-react";
import { cn } from "@/lib/utils";

export default function Navbar() {
    const router = useRouter();
    const pathname = usePathname();
    const { user, isAuthenticated, isLoading, logout } = useAuth();
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isScrolled, setIsScrolled] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 10);
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const handleLogout = () => {
        logout();
        router.push("/");
    };

    const getInitials = (firstName: string, lastName: string) => {
        return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    };

    const isActiveLink = (href: string) => {
        if (href === "/dashboard") {
            return pathname === "/dashboard";
        }
        return pathname.startsWith(href);
    };

    return (
        <>
            <nav
                className={cn(
                    "fixed top-0 left-0 right-0 z-50 bg-white transition-all duration-300",
                    isScrolled
                        ? "shadow-md border-b border-slate-100"
                        : "border-b border-slate-200"
                )}
            >
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16 relative">
                        <Link href="/" className="absolute left-1/2 -translate-x-1/2 md:static md:translate-x-0 flex items-center gap-2 shrink-0">
                            <Image
                                src="/LOGO.png"
                                alt="Blassa"
                                width={36}
                                height={36}
                                className="w-9 h-9"
                            />
                            <span className="text-xl font-bold text-slate-800 font-[family-name:var(--font-poppins)]">
                                Blassa
                            </span>
                        </Link>

                        <div className="hidden lg:flex items-center gap-8">
                            {isAuthenticated && (
                                <div className="flex items-center gap-1">
                                    <Link
                                        href="/dashboard"
                                        className={cn(
                                            "px-4 py-2 text-sm font-medium rounded-lg transition-colors",
                                            isActiveLink("/dashboard")
                                                ? "text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5"
                                                : "text-slate-600 hover:text-[var(--color-blassa-teal)] hover:bg-slate-50"
                                        )}
                                    >
                                        <span className="flex items-center gap-2">
                                            <LayoutDashboard className="w-4 h-4" />
                                            Tableau de bord
                                        </span>
                                    </Link>
                                    <Link
                                        href="/dashboard/rides"
                                        className={cn(
                                            "px-4 py-2 text-sm font-medium rounded-lg transition-colors",
                                            isActiveLink("/dashboard/rides")
                                                ? "text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5"
                                                : "text-slate-600 hover:text-[var(--color-blassa-teal)] hover:bg-slate-50"
                                        )}
                                    >
                                        <span className="flex items-center gap-2">
                                            <Car className="w-4 h-4" />
                                            Mes trajets
                                        </span>
                                    </Link>
                                    <Link
                                        href="/dashboard/bookings"
                                        className={cn(
                                            "px-4 py-2 text-sm font-medium rounded-lg transition-colors",
                                            isActiveLink("/dashboard/bookings")
                                                ? "text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5"
                                                : "text-slate-600 hover:text-[var(--color-blassa-teal)] hover:bg-slate-50"
                                        )}
                                    >
                                        <span className="flex items-center gap-2">
                                            <Ticket className="w-4 h-4" />
                                            Mes réservations
                                        </span>
                                    </Link>
                                </div>
                            )}
                        </div>

                        <div className="w-10 md:hidden" aria-hidden="true"></div>

                        <div className="flex items-center gap-2 md:gap-3">
                            <Link
                                href="/search-form"
                                className="hidden md:flex items-center gap-2 px-3 py-2 text-sm font-medium text-slate-600 hover:text-[var(--color-blassa-teal)] transition-colors"
                            >
                                <Search className="w-4 h-4" />
                                <span>Rechercher</span>
                            </Link>

                            <Link href="/publish" className="hidden md:block">
                                <Button
                                    variant="outline"
                                    className="rounded-full border-[var(--color-blassa-teal)] text-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal)] hover:text-white font-medium px-4 gap-2"
                                >
                                    <Plus className="w-4 h-4" />
                                    Publier un trajet
                                </Button>
                            </Link>

                            {isLoading ? (
                                <div className="hidden md:flex items-center gap-2">
                                    <div className="w-20 h-9 bg-slate-100 rounded-full animate-pulse"></div>
                                </div>
                            ) : isAuthenticated && user ? (
                                <>
                                    <div className="flex items-center">
                                        <NotificationDropdown />
                                    </div>

                                    <div className="flex items-center gap-2">
                                        <div className="hidden md:block w-px h-6 bg-slate-200 mx-1"></div>
                                        <ProfileDropdown user={user} onLogout={handleLogout} />
                                    </div>
                                </>
                            ) : (
                                <div className="hidden md:flex items-center gap-1">
                                    <Link href="/login">
                                        <Button
                                            variant="ghost"
                                            className="text-slate-600 hover:text-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal)]/5 font-medium rounded-full px-4"
                                        >
                                            Connexion
                                        </Button>
                                    </Link>
                                    <Link href="/register">
                                        <Button className="bg-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal-dark)] text-white font-medium rounded-full px-4">
                                            Inscription
                                        </Button>
                                    </Link>
                                </div>
                            )}

                            {!isAuthenticated && (
                                <button
                                    className="lg:hidden p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors"
                                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                                    aria-label="Menu"
                                >
                                    {isMenuOpen ? (
                                        <X className="w-6 h-6" />
                                    ) : (
                                        <Menu className="w-6 h-6" />
                                    )}
                                </button>
                            )}
                        </div>
                    </div>
                </div>

                {isMenuOpen && (
                    <div className="lg:hidden border-t border-slate-100 bg-white animate-in slide-in-from-top-2 duration-200">
                        <div className="px-4 py-4 space-y-2">
                            {isAuthenticated && user && (
                                <div className="flex items-center gap-3 px-3 py-3 bg-slate-50 rounded-xl mb-3">
                                    {user.profilePictureUrl ? (
                                        <Image
                                            src={user.profilePictureUrl}
                                            alt={user.firstName}
                                            width={40}
                                            height={40}
                                            className="w-10 h-10 rounded-full object-cover"
                                        />
                                    ) : (
                                        <div className="w-10 h-10 rounded-full bg-[var(--color-blassa-teal)] flex items-center justify-center text-white text-sm font-medium">
                                            {getInitials(user.firstName, user.lastName)}
                                        </div>
                                    )}
                                    <div>
                                        <p className="text-sm font-medium text-slate-900">
                                            {user.firstName} {user.lastName}
                                        </p>
                                        <p className="text-xs text-slate-500">{user.email}</p>
                                    </div>
                                </div>
                            )}

                            {isAuthenticated && (
                                <>
                                    <Link
                                        href="/dashboard"
                                        className={cn(
                                            "flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg",
                                            isActiveLink("/dashboard")
                                                ? "text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5"
                                                : "text-slate-700 hover:bg-slate-50"
                                        )}
                                        onClick={() => setIsMenuOpen(false)}
                                    >
                                        <LayoutDashboard className="w-5 h-5" />
                                        Tableau de bord
                                    </Link>
                                    <Link
                                        href="/dashboard/rides"
                                        className={cn(
                                            "flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg",
                                            isActiveLink("/dashboard/rides")
                                                ? "text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5"
                                                : "text-slate-700 hover:bg-slate-50"
                                        )}
                                        onClick={() => setIsMenuOpen(false)}
                                    >
                                        <Car className="w-5 h-5" />
                                        Mes trajets
                                    </Link>
                                    <Link
                                        href="/dashboard/bookings"
                                        className={cn(
                                            "flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg",
                                            isActiveLink("/dashboard/bookings")
                                                ? "text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5"
                                                : "text-slate-700 hover:bg-slate-50"
                                        )}
                                        onClick={() => setIsMenuOpen(false)}
                                    >
                                        <Ticket className="w-5 h-5" />
                                        Mes réservations
                                    </Link>
                                    <div className="border-t border-slate-100 my-2"></div>
                                </>
                            )}

                            {!isAuthenticated && (
                                <Link
                                    href="/search-form"
                                    className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 rounded-lg"
                                    onClick={() => setIsMenuOpen(false)}
                                >
                                    <Search className="w-5 h-5 text-slate-400" />
                                    Rechercher un trajet
                                </Link>
                            )}

                            <Link
                                href="/publish"
                                className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-[var(--color-blassa-teal)] bg-[var(--color-blassa-teal)]/5 rounded-lg"
                                onClick={() => setIsMenuOpen(false)}
                            >
                                <Plus className="w-5 h-5" />
                                Publier un trajet
                            </Link>

                            {isAuthenticated && user ? (
                                <>
                                    <div className="border-t border-slate-100 pt-2 mt-2 space-y-1">
                                        <Link
                                            href="/dashboard/profile"
                                            onClick={() => setIsMenuOpen(false)}
                                            className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 rounded-lg"
                                        >
                                            <User className="w-5 h-5 text-slate-400" />
                                            Mon profil
                                        </Link>
                                        <Link
                                            href="/dashboard/reviews"
                                            onClick={() => setIsMenuOpen(false)}
                                            className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 rounded-lg"
                                        >
                                            <Star className="w-5 h-5 text-slate-400" />
                                            Mes avis
                                        </Link>
                                        <Link
                                            href="/dashboard/settings"
                                            onClick={() => setIsMenuOpen(false)}
                                            className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 rounded-lg"
                                        >
                                            <Settings className="w-5 h-5 text-slate-400" />
                                            Paramètres
                                        </Link>
                                    </div>

                                    <div className="border-t border-slate-100 pt-2 mt-2">
                                        <button
                                            onClick={() => {
                                                handleLogout();
                                                setIsMenuOpen(false);
                                            }}
                                            className="flex items-center gap-3 w-full px-3 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg"
                                        >
                                            <LogOut className="w-5 h-5" />
                                            Déconnexion
                                        </button>
                                    </div>
                                </>
                            ) : (
                                <div className="border-t border-slate-100 pt-3 mt-3">
                                    <div className="grid grid-cols-2 gap-2">
                                        <Link href="/login" onClick={() => setIsMenuOpen(false)}>
                                            <Button
                                                variant="outline"
                                                className="w-full rounded-lg border-slate-200"
                                            >
                                                Connexion
                                            </Button>
                                        </Link>
                                        <Link href="/register" onClick={() => setIsMenuOpen(false)}>
                                            <Button className="w-full rounded-lg bg-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal-dark)]">
                                                Inscription
                                            </Button>
                                        </Link>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </nav>

            <MobileBottomNav isAuthenticated={isAuthenticated} />
        </>
    );
}
