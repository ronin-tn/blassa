"use client";

import { Car } from "lucide-react";

export default function DashboardLoading() {
    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center">
            <div className="flex flex-col items-center gap-4">
                <div className="relative animate-bounce">
                    <Car className="w-12 h-12 text-[var(--color-blassa-teal)]" />
                </div>
                <div className="text-center">
                    <p className="mt-4 text-slate-500 text-sm font-medium animate-pulse">Chargement...</p>
                    <p className="text-sm text-slate-500">Récupération de vos trajets...</p>
                </div>
            </div>
        </div>
    );
}
