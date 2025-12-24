"use client";

import Link from "next/link";
import { Plus, Search, Loader2, AlertCircle, RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import DashboardStats from "@/components/dashboard/DashboardStats";
import UpcomingRidesList from "@/components/dashboard/UpcomingRidesList";
import QuickActions from "@/components/dashboard/QuickActions";
import { useDashboardData } from "@/hooks/useDashboardData";

/**
 * Dashboard content component - uses custom hook for data fetching
 * Note: Authentication is handled server-side in dashboard/page.tsx
 */
export default function DashboardClientContent() {
    const { isAuthenticated, isLoading: authLoading } = useAuth();
    const { stats, upcomingRides, isLoading, error, refetch } = useDashboardData(isAuthenticated);

    // Loading state
    if (authLoading || isLoading) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="flex items-center justify-center py-20">
                    <Loader2 className="w-8 h-8 animate-spin text-[var(--color-blassa-teal)]" />
                    <span className="sr-only">Chargement en cours</span>
                </div>
            </div>
        );
    }

    // Error state
    if (error) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="flex flex-col items-center justify-center py-20 px-4">
                    <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mb-4">
                        <AlertCircle className="w-8 h-8 text-red-600" />
                    </div>
                    <h2 className="text-lg font-semibold text-slate-900 mb-2">Erreur de chargement</h2>
                    <p className="text-slate-600 text-center mb-4 max-w-md">{error}</p>
                    <Button
                        onClick={() => refetch()}
                        className="bg-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal-dark)] text-white rounded-xl"
                    >
                        <RefreshCw className="w-4 h-4 mr-2" />
                        Réessayer
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F8FAFC] pb-24 lg:pb-0">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-900 font-[family-name:var(--font-poppins)]">
                            Tableau de bord
                        </h1>
                        <p className="text-slate-500 mt-1">
                            Bienvenue ! Voici un aperçu de vos activités.
                        </p>
                    </div>
                    <div className="flex gap-3">
                        <Link href="/search-form">
                            <Button
                                variant="outline"
                                className="rounded-xl border-[var(--color-blassa-amber)] text-[var(--color-blassa-amber)] hover:bg-[var(--color-blassa-amber)] hover:text-white"
                            >
                                <Search className="w-4 h-4 mr-2" />
                                Rechercher
                            </Button>
                        </Link>
                        <Link href="/publish">
                            <Button className="rounded-xl bg-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal-dark)] text-white">
                                <Plus className="w-4 h-4 mr-2" />
                                Publier un trajet
                            </Button>
                        </Link>
                    </div>
                </div>

                {/* Stats Grid */}
                <DashboardStats stats={stats} />

                {/* Upcoming Rides Section */}
                <UpcomingRidesList rides={upcomingRides} />

                {/* Quick Actions */}
                <QuickActions />
            </main>
        </div>
    );
}
