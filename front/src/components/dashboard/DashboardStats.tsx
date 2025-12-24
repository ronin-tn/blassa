"use client";

import { useRouter } from "next/navigation";
import { Car, Star, TrendingUp } from "lucide-react";

export interface DashboardStatsData {
    totalTrips: number;
    totalRides: number;
    savedMoney: number;
    rating: number;
}

interface DashboardStatsProps {
    stats: DashboardStatsData;
}

export default function DashboardStats({ stats }: DashboardStatsProps) {
    const router = useRouter();

    return (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100">
                <div className="flex items-center gap-3">
                    <div className="p-2 bg-[var(--color-blassa-teal)]/10 rounded-xl">
                        <Car className="w-5 h-5 text-[var(--color-blassa-teal)]" />
                    </div>
                </div>
                <p className="text-2xl font-bold text-slate-900">{stats.totalTrips}</p>
                <p className="text-sm text-slate-500">Trajets effectués</p>
            </div>

            <div
                onClick={() => router.push("/dashboard/rides")}
                className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 hover:shadow-md transition-shadow cursor-pointer"
            >
                <div className="flex items-center gap-3">
                    <div className="p-2 bg-[var(--color-blassa-amber)]/10 rounded-xl">
                        <Car className="w-5 h-5 text-[var(--color-blassa-amber)]" />
                    </div>
                </div>
                <p className="text-2xl font-bold text-slate-900">{stats.totalRides ?? 0}</p>
                <p className="text-sm text-slate-500">Trajets publiés</p>
            </div>

            <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100">
                <div className="flex items-center gap-3 mb-3">
                    <div className="w-10 h-10 rounded-xl bg-green-50 flex items-center justify-center">
                        <TrendingUp className="w-5 h-5 text-emerald-600" />
                    </div>
                </div>
                <p className="text-2xl font-bold text-slate-900">{stats.savedMoney} TND</p>
                <p className="text-sm text-slate-500">Gagnés</p>
            </div>

            <div
                onClick={() => router.push("/dashboard/reviews")}
                className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 hover:shadow-md transition-shadow cursor-pointer"
            >
                <div className="flex items-center gap-3 mb-3">
                    <div className="w-10 h-10 rounded-xl bg-yellow-50 flex items-center justify-center">
                        <Star className="w-5 h-5 text-yellow-500" />
                    </div>
                </div>
                <p className="text-2xl font-bold text-slate-900">{stats.rating || "-"}/5</p>
                <p className="text-sm text-slate-500">Note moyenne</p>
            </div>
        </div>
    );
}
