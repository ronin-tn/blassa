"use client";

import { useEffect } from "react";
import { AlertTriangle, RefreshCw, Home } from "lucide-react";
import Link from "next/link";

export default function SearchError({
    error,
    reset,
}: {
    error: Error & { digest?: string };
    reset: () => void;
}) {
    useEffect(() => {
        console.error("Search error:", error);
    }, [error]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 px-4">
            <div className="max-w-md w-full bg-white rounded-2xl shadow-lg border border-slate-200 p-8 text-center">
                <div className="w-16 h-16 mx-auto mb-6 rounded-full bg-red-100 flex items-center justify-center">
                    <AlertTriangle className="w-8 h-8 text-red-600" />
                </div>

                <h1 className="text-2xl font-bold text-slate-900 mb-2">
                    Erreur de recherche
                </h1>
                <p className="text-slate-600 mb-6">
                    La recherche a échoué. Veuillez réessayer avec d&apos;autres critères.
                </p>

                {error.digest && (
                    <p className="text-xs text-slate-400 mb-6 font-mono">
                        Code erreur: {error.digest}
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
                        href="/"
                        className="flex-1 flex items-center justify-center gap-2 px-4 py-3 border border-slate-300 text-slate-700 font-medium rounded-xl hover:bg-slate-50 transition-colors"
                    >
                        <Home className="w-4 h-4" />
                        Accueil
                    </Link>
                </div>
            </div>
        </div>
    );
}
