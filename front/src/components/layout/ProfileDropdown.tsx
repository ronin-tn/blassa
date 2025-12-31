"use client";

import { useRef, useEffect, useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { User, Star, Settings, LogOut, ChevronDown } from "lucide-react";
import { cn } from "@/lib/utils";

export interface ProfileDropdownUser {
    firstName: string;
    lastName: string;
    email: string;
    profilePictureUrl: string | null;
}

interface ProfileDropdownProps {
    user: ProfileDropdownUser;
    onLogout: () => void;
}

export default function ProfileDropdown({ user, onLogout }: ProfileDropdownProps) {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                dropdownRef.current &&
                !dropdownRef.current.contains(event.target as Node)
            ) {
                setIsOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const getInitials = (firstName: string, lastName: string) => {
        return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    };

    return (
        <div className="relative" ref={dropdownRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center gap-2 px-2 py-1.5 rounded-full hover:bg-slate-50 transition-colors"
            >
                {user.profilePictureUrl ? (
                    <Image
                        src={user.profilePictureUrl}
                        alt={user.firstName}
                        width={32}
                        height={32}
                        className="w-8 h-8 rounded-full object-cover"
                    />
                ) : (
                    <div className="w-8 h-8 rounded-full bg-[var(--color-blassa-teal)] flex items-center justify-center text-white text-sm font-medium">
                        {getInitials(user.firstName, user.lastName)}
                    </div>
                )}
                <ChevronDown
                    className={cn(
                        "w-4 h-4 text-slate-400 transition-transform",
                        isOpen && "rotate-180"
                    )}
                />
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-slate-100 py-2 z-50 animate-in fade-in slide-in-from-top-2 duration-200">
                    <div className="px-4 py-3 border-b border-slate-100">
                        <p className="text-sm font-medium text-slate-900">
                            {user.firstName} {user.lastName}
                        </p>
                        <p className="text-xs text-slate-500 truncate">
                            {user.email}
                        </p>
                    </div>

                    <div className="py-1">
                        <Link
                            href="/dashboard/profile"
                            onClick={() => setIsOpen(false)}
                            className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                        >
                            <User className="w-4 h-4 text-slate-400" />
                            Mon profil
                        </Link>
                        <Link
                            href="/dashboard/reviews"
                            onClick={() => setIsOpen(false)}
                            className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                        >
                            <Star className="w-4 h-4 text-slate-400" />
                            Mes avis
                        </Link>
                        <Link
                            href="/dashboard/settings"
                            onClick={() => setIsOpen(false)}
                            className="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                        >
                            <Settings className="w-4 h-4 text-slate-400" />
                            Paramètres
                        </Link>
                    </div>

                    <div className="border-t border-slate-100 pt-1">
                        <button
                            onClick={() => {
                                onLogout();
                                setIsOpen(false);
                            }}
                            className="flex items-center gap-3 w-full px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition-colors"
                        >
                            <LogOut className="w-4 h-4" />
                            Déconnexion
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
