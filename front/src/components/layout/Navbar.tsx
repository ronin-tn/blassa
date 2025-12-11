"use client";

import { useState, useEffect, useRef } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter, usePathname } from "next/navigation";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import NotificationDropdown from "@/components/layout/NotificationDropdown";
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
    ChevronDown,
    LayoutDashboard,
    Star,
} from "lucide-react";
import { cn } from "@/lib/utils";

export default function Navbar() {
    const router = useRouter();
    const pathname = usePathname();
    const { user, isAuthenticated, isLoading, logout } = useAuth();
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isProfileOpen, setIsProfileOpen] = useState(false);
    const [isScrolled, setIsScrolled] = useState(false);
    const profileRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 10);
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    // Close profile dropdown on outside click
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                profileRef.current &&
                !profileRef.current.contains(event.target as Node)
            ) {
                setIsProfileOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleLogout = () => {
        logout();
        setIsProfileOpen(false);
        router.push("/");
    };

    const getInitials = (firstName: string, lastName: string) => {
        return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    };

    // Check if link is active
    const isActiveLink = (href: string) => {
        if (href === "/dashboard") {
            return pathname === "/dashboard";
        }
        return pathname.startsWith(href);
    };

    return (
        <>
            {/* Main Navbar */}
            <nav
                className={cn(
                    "fixed top-0 left-0 right-0 z-50 bg-white transition-all duration-300",
                    isScrolled
                        ? "shadow-md border-b border-slate-100"
                        : "border-b border-slate-200"
                )}
            >
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        {/* Left: Logo + Main Nav Links (when logged in) */}
                        <div className="flex items-center gap-8">
                            {/* Logo */}
                            <Link href="/" className="flex items-center gap-2 shrink-0">
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

                            {/* Main Nav Links - Only show when logged in */}
                            {isAuthenticated && (
                                <div className="hidden lg:flex items-center gap-1">
                                    <Link
                                        href="/dashboard"
                                        className={cn(
                                            "px-4 py-2 text-sm font-medium rounded-lg transition-colors",
                                            isActiveLink("/dashboard")
                                                ? "text-[#006B8F] bg-[#006B8F]/5"
                                                : "text-slate-600 hover:text-[#006B8F] hover:bg-slate-50"
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
                                                ? "text-[#006B8F] bg-[#006B8F]/5"
                                                : "text-slate-600 hover:text-[#006B8F] hover:bg-slate-50"
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
                                                ? "text-[#006B8F] bg-[#006B8F]/5"
                                                : "text-slate-600 hover:text-[#006B8F] hover:bg-slate-50"
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

                        {/* Right: Actions */}
                        <div className="flex items-center gap-2 md:gap-3">
                            {/* Search - Desktop (only when not logged in) */}
                            {!isAuthenticated && (
                                <Link
                                    href="/#search"
                                    className="hidden md:flex items-center gap-2 px-3 py-2 text-sm font-medium text-slate-600 hover:text-[#006B8F] transition-colors"
                                >
                                    <Search className="w-4 h-4" />
                                    <span>Rechercher</span>
                                </Link>
                            )}

                            {/* Publish Button - Always visible on desktop */}
                            <Link href="/publish" className="hidden md:block">
                                <Button
                                    variant="outline"
                                    className="rounded-full border-[#006B8F] text-[#006B8F] hover:bg-[#006B8F] hover:text-white font-medium px-4 gap-2"
                                >
                                    <Plus className="w-4 h-4" />
                                    Publier un trajet
                                </Button>
                            </Link>

                            {/* Auth Section */}
                            {isLoading ? (
                                // Loading skeleton
                                <div className="hidden md:flex items-center gap-2">
                                    <div className="w-20 h-9 bg-slate-100 rounded-full animate-pulse"></div>
                                </div>
                            ) : isAuthenticated && user ? (
                                // Logged in state
                                <div className="hidden md:flex items-center gap-2">
                                    {/* Divider */}
                                    <div className="w-px h-6 bg-slate-200 mx-1"></div>

                                    {/* Notifications */}
                                    <NotificationDropdown />

                                    {/* Profile Dropdown - Simplified */}
                                    <div className="relative" ref={profileRef}>
                                        <button
                                            onClick={() => setIsProfileOpen(!isProfileOpen)}
                                            className="flex items-center gap-2 px-2 py-1.5 rounded-full hover:bg-slate-50 transition-colors"
                                        >
                                            {/* Avatar */}
                                            {user.profilePictureUrl ? (
                                                <Image
                                                    src={user.profilePictureUrl}
                                                    alt={user.firstName}
                                                    width={32}
                                                    height={32}
                                                    className="w-8 h-8 rounded-full object-cover"
                                                />
                                            ) : (
                                                <div className="w-8 h-8 rounded-full bg-[#006B8F] flex items-center justify-center text-white text-sm font-medium">
                                                    {getInitials(user.firstName, user.lastName)}
                                                </div>
                                            )}
                                            <ChevronDown
                                                className={cn(
                                                    "w-4 h-4 text-slate-400 transition-transform",
                                                    isProfileOpen && "rotate-180"
                                                )}
                                            />
                                        </button>

                                        {/* Dropdown Menu - Simplified (only profile actions) */}
                                        {isProfileOpen && (
                                            <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-slate-100 py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-200">
                                                {/* User Info */}
                                                <div className="px-4 py-3 border-b border-slate-100">
                                                    <p className="text-sm font-medium text-slate-900">
                                                        {user.firstName} {user.lastName}
                                                    </p>
                                                    <p className="text-xs text-slate-500 truncate">
                                                        {user.email}
                                                    </p>
                                                </div>

                                                {/* Menu Items - Only profile-related */}
                                                <div className="py-1">
                                                    <Link
                                                        href="/dashboard/profile"
                                                        onClick={() => setIsProfileOpen(false)}
                                                        className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                                                    >
                                                        <User className="w-4 h-4 text-slate-400" />
                                                        Mon profil
                                                    </Link>
                                                    <Link
                                                        href="/dashboard/reviews"
                                                        onClick={() => setIsProfileOpen(false)}
                                                        className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                                                    >
                                                        <Star className="w-4 h-4 text-slate-400" />
                                                        Mes avis
                                                    </Link>
                                                    <Link
                                                        href="/settings"
                                                        onClick={() => setIsProfileOpen(false)}
                                                        className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                                                    >
                                                        <Settings className="w-4 h-4 text-slate-400" />
                                                        Paramètres
                                                    </Link>
                                                </div>

                                                {/* Logout */}
                                                <div className="border-t border-slate-100 pt-1">
                                                    <button
                                                        onClick={handleLogout}
                                                        className="flex items-center gap-3 w-full px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition-colors"
                                                    >
                                                        <LogOut className="w-4 h-4" />
                                                        Déconnexion
                                                    </button>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ) : (
                                // Logged out state
                                <div className="hidden md:flex items-center gap-1">
                                    <Link href="/login">
                                        <Button
                                            variant="ghost"
                                            className="text-slate-600 hover:text-[#006B8F] hover:bg-[#006B8F]/5 font-medium rounded-full px-4"
                                        >
                                            Connexion
                                        </Button>
                                    </Link>
                                    <Link href="/register">
                                        <Button className="bg-[#006B8F] hover:bg-[#005673] text-white font-medium rounded-full px-4">
                                            Inscription
                                        </Button>
                                    </Link>
                                </div>
                            )}

                            {/* Mobile Menu Button */}
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
                        </div>
                    </div>
                </div>

                {/* Mobile Menu */}
                {isMenuOpen && (
                    <div className="lg:hidden border-t border-slate-100 bg-white animate-in slide-in-from-top-2 duration-200">
                        <div className="px-4 py-4 space-y-2">
                            {/* User Info (if logged in) */}
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
                                        <div className="w-10 h-10 rounded-full bg-[#006B8F] flex items-center justify-center text-white text-sm font-medium">
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

                            {/* Main Nav Links for logged-in users */}
                            {isAuthenticated && (
                                <>
                                    <Link
                                        href="/dashboard"
                                        className={cn(
                                            "flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg",
                                            isActiveLink("/dashboard")
                                                ? "text-[#006B8F] bg-[#006B8F]/5"
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
                                                ? "text-[#006B8F] bg-[#006B8F]/5"
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
                                                ? "text-[#006B8F] bg-[#006B8F]/5"
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

                            {/* Search (for non-logged in users) */}
                            {!isAuthenticated && (
                                <Link
                                    href="/#search"
                                    className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 rounded-lg"
                                    onClick={() => setIsMenuOpen(false)}
                                >
                                    <Search className="w-5 h-5 text-slate-400" />
                                    Rechercher un trajet
                                </Link>
                            )}

                            <Link
                                href="/publish"
                                className="flex items-center gap-3 px-3 py-2.5 text-sm font-medium text-[#006B8F] bg-[#006B8F]/5 rounded-lg"
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
                                            href="/settings"
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
                                            <Button className="w-full rounded-lg bg-[#006B8F] hover:bg-[#005673]">
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

            {/* Mobile Bottom Nav - Updated for logged in users */}
            <div className="lg:hidden fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-slate-200 safe-area-bottom">
                <div className="flex items-center justify-around py-2">
                    {isAuthenticated ? (
                        <>
                            <Link
                                href="/dashboard"
                                className={cn(
                                    "flex flex-col items-center gap-1 px-3 py-2",
                                    isActiveLink("/dashboard") ? "text-[#006B8F]" : "text-slate-500"
                                )}
                            >
                                <LayoutDashboard className="w-5 h-5" />
                                <span className="text-[10px] font-medium">Accueil</span>
                            </Link>
                            <Link
                                href="/dashboard/rides"
                                className={cn(
                                    "flex flex-col items-center gap-1 px-3 py-2",
                                    isActiveLink("/dashboard/rides") ? "text-[#006B8F]" : "text-slate-500"
                                )}
                            >
                                <Car className="w-5 h-5" />
                                <span className="text-[10px] font-medium">Mes trajets</span>
                            </Link>
                            <Link
                                href="/publish"
                                className="flex flex-col items-center gap-1 px-3 py-2 text-[#006B8F]"
                            >
                                <div className="w-10 h-10 bg-[#006B8F] rounded-full flex items-center justify-center -mt-4 shadow-lg">
                                    <Plus className="w-5 h-5 text-white" />
                                </div>
                                <span className="text-[10px] font-medium">Publier</span>
                            </Link>
                            <Link
                                href="/dashboard/bookings"
                                className={cn(
                                    "flex flex-col items-center gap-1 px-3 py-2",
                                    isActiveLink("/dashboard/bookings") ? "text-[#006B8F]" : "text-slate-500"
                                )}
                            >
                                <Ticket className="w-5 h-5" />
                                <span className="text-[10px] font-medium">Réservations</span>
                            </Link>
                            <Link
                                href="/dashboard/profile"
                                className={cn(
                                    "flex flex-col items-center gap-1 px-3 py-2",
                                    isActiveLink("/dashboard/profile") ? "text-[#006B8F]" : "text-slate-500"
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
                                className="flex flex-col items-center gap-1 px-4 py-2 text-[#006B8F]"
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
        </>
    );
}
