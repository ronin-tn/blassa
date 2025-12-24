"use client";

import { useEffect } from "react";
import { AlertTriangle, RefreshCw, LayoutDashboard } from "lucide-react";
import Link from "next/link";

export default function DashboardError({
    error,
    reset,
}: {
    error: Error & { digest?: string };
    reset: () => void;
}) {
    useEffect(() => {
        console.error("Dashboard error:", error);
    }, [error]);

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-lg border border-slate-200 p-8 text-center">
                <div className="w-16 h-16 mx-auto mb-6 rounded-full bg-amber-100 flex items-center justify-center">
                    <AlertTriangle className="w-8 h-8 text-amber-600" />
                </div>

                <h1 className="text-2xl font-bold text-slate-900 mb-2">
                    Erreur du tableau de bord
                </h1>
                <p className="text-slate-600 mb-6">
                    Impossible de charger votre tableau de bord.
                    Vérifiez votre connexion et réessayez.
                </p>

                {error.digest && (
                    <p className="text-xs text-slate-400 mb-6 font-mono bg-slate-50 p-2 rounded">
                        {error.digest}
                    </p>
                )}

                <div className="flex flex-col sm:flex-row gap-3">
                    <button
                        onClick={() => reset()}
                        className="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-[#006B8F] text-white font-medium rounded-xl hover:bg-[#005673] transition-colors"
                    >
                        <RefreshCw className="w-4 h-4" />
                        Réessayer
                    </button>
                    <Link
                        href="/dashboard"
                        className="flex-1 flex items-center justify-center gap-2 px-4 py-3 border border-slate-300 text-slate-700 font-medium rounded-xl hover:bg-slate-50 transition-colors"
                    >
                        <LayoutDashboard className="w-4 h-4" />
                        Tableau de bord
                    </Link>
                </div>
            </div>
        </div>
    );
}
