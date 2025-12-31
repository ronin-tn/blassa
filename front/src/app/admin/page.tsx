"use client";

import { useEffect, useState } from "react";
import { adminApi, AdminStats } from "@/lib/api/admin";
import { Spinner } from "@/components/ui/Spinner";
import { Users, Car, AlertTriangle, RotateCcw } from "lucide-react";

export default function AdminDashboard() {
    const [stats, setStats] = useState<AdminStats | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        loadStats();
    }, []);

    const loadStats = async () => {
        setLoading(true);
        try {
            const data = await adminApi.getStats();
            setStats(data);
            setError("");
        } catch (e) {
            setError("Impossible de charger les statistiques. Vérifiez votre connexion.");
        } finally {
            setLoading(false);
        }
    };

    if (loading && !stats) return (
        <div className="flex h-96 items-center justify-center">
            <Spinner size="lg" className="text-[#0e7490]" />
        </div>
    );

    const statCards = [
        {
            label: "Utilisateurs Inscrits",
            value: stats?.totalUsers || 0,
            icon: Users,
            color: "text-[#0e7490]", // Brand Teal
            bg: "bg-cyan-50",
            border: "border-cyan-200"
        },
        {
            label: "Trajets Publiés",
            value: stats?.totalRides || 0,
            icon: Car,
            color: "text-[#f59e0b]", // Brand Amber
            bg: "bg-amber-50",
            border: "border-amber-200"
        },
        {
            label: "Signalements en Attente",
            value: stats?.pendingReports || 0,
            icon: AlertTriangle,
            color: "text-red-600",
            bg: "bg-red-50",
            border: "border-red-200"
        },
    ];

    return (
        <div className="space-y-8 animate-in fade-in duration-500">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Tableau de bord</h1>
                    <p className="mt-2 text-gray-600">Bienvenue dans l'espace d'administration Blassa.</p>
                </div>
                <button
                    onClick={loadStats}
                    className="flex items-center gap-2 rounded-lg bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm ring-1 ring-gray-300 hover:bg-gray-50 active:scale-95 transition-all"
                >
                    <RotateCcw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
                    Actualiser
                </button>
            </div>

            {error && (
                <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700 flex items-center gap-3">
                    <AlertTriangle className="h-5 w-5" />
                    {error}
                </div>
            )}

            <div className="grid gap-6 sm:grid-cols-3">
                {statCards.map((stat, idx) => (
                    <div
                        key={idx}
                        className={`group relative overflow-hidden rounded-2xl bg-white p-6 shadow-sm ring-1 ring-black/5 hover:shadow-md transition-all duration-300`}
                    >
                        <div className={`absolute right-0 top-0 -mr-4 -mt-4 h-24 w-24 rounded-full opacity-10 transition-transform group-hover:scale-110 ${stat.bg.replace('bg-', 'bg-current text-')}`} />

                        <div className="flex items-center gap-4">
                            <div className={`rounded-xl p-3 ${stat.bg} ${stat.color}`}>
                                <stat.icon className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="text-sm font-medium text-gray-500">{stat.label}</p>
                                <p className="mt-1 text-3xl font-bold text-gray-900">{stat.value}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Quick Actions placeholder or recent activity could go here */}
        </div>
    );
}
