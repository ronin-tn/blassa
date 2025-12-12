"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    Car,
    MapPin,
    Calendar,
    Clock,
    Users,
    Plus,
    ChevronRight,
    ChevronLeft,
    Loader2,
    AlertCircle,
    ArrowLeft,
    MoreVertical,
    Edit,
    Trash2,
    Play,
    CheckCircle,
    Ban,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import {
    Ride,
    RideStatus,
    RideStatusLabels,
    RideStatusColors,
    PagedResponse,
} from "@/types/ride";
import { parseApiError } from "@/lib/api-utils";

export default function MyRidesPage() {
    const router = useRouter();
    const { token, isAuthenticated, isLoading: authLoading } = useAuth();

    const [rides, setRides] = useState<Ride[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [statusFilter, setStatusFilter] = useState<RideStatus | "ALL">("ALL");
    const [openMenuId, setOpenMenuId] = useState<string | null>(null);

    // Redirect if not authenticated
    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const fetchRides = useCallback(async () => {
        if (!token) return;

        setIsLoading(true);
        setError("");

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/mine?page=${currentPage}&size=10`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!response.ok) {
                throw new Error("Erreur lors du chargement des trajets");
            }

            const data: PagedResponse<Ride> = await response.json();
            setRides(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Erreur lors du chargement"
            );
        } finally {
            setIsLoading(false);
        }
    }, [token, currentPage]);

    useEffect(() => {
        if (token) {
            fetchRides();
        }
    }, [token, fetchRides]);

    const handleCancelRide = async (rideId: string) => {
        if (!confirm("Êtes-vous sûr de vouloir annuler ce trajet ?")) return;

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}`,
                {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de l'annulation");
                throw new Error(errorMessage);
            }

            // Refresh rides
            fetchRides();
            setOpenMenuId(null);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Erreur lors de l'annulation");
        }
    };

    const handleStartRide = async (rideId: string) => {
        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}/start`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors du démarrage");
                throw new Error(errorMessage);
            }

            fetchRides();
            setOpenMenuId(null);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Erreur lors du démarrage");
        }
    };

    const handleCompleteRide = async (rideId: string) => {
        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}/complete`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de la complétion");
                throw new Error(errorMessage);
            }

            fetchRides();
            setOpenMenuId(null);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Erreur lors de la complétion");
        }
    };

    const filteredRides =
        statusFilter === "ALL"
            ? rides
            : rides.filter((ride) => ride.status === statusFilter);

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            weekday: "short",
            day: "numeric",
            month: "short",
        });
    };

    const formatTime = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("fr-FR", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    // Loading state
    if (authLoading || (isLoading && rides.length === 0)) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="flex items-center justify-center py-20">
                    <Loader2 className="w-8 h-8 animate-spin text-[#006B8F]" />
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F8FAFC]">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                    <div className="flex items-center gap-3">
                        <Link
                            href="/dashboard"
                            className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
                        >
                            <ArrowLeft className="w-5 h-5" />
                        </Link>
                        <div>
                            <h1 className="text-2xl font-bold text-slate-900 font-[family-name:var(--font-poppins)]">
                                Mes Trajets
                            </h1>
                            <p className="text-slate-500 text-sm">
                                Gérez vos trajets publiés
                            </p>
                        </div>
                    </div>
                    <Link href="/publish">
                        <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                            <Plus className="w-4 h-4 mr-2" />
                            Publier un trajet
                        </Button>
                    </Link>
                </div>

                {/* Status Filter Tabs */}
                <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
                    {(
                        [
                            "ALL",
                            "SCHEDULED",
                            "FULL",
                            "IN_PROGRESS",
                            "COMPLETED",
                            "CANCELLED",
                        ] as const
                    ).map((status) => (
                        <button
                            key={status}
                            onClick={() => setStatusFilter(status)}
                            className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all ${statusFilter === status
                                ? "bg-[#006B8F] text-white"
                                : "bg-white text-slate-600 hover:bg-slate-50 border border-slate-200"
                                }`}
                        >
                            {status === "ALL" ? "Tous" : RideStatusLabels[status]}
                        </button>
                    ))}
                </div>

                {/* Error State */}
                {error && (
                    <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 flex items-center gap-3 mb-6">
                        <AlertCircle className="w-5 h-5 shrink-0" />
                        <span>{error}</span>
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={fetchRides}
                            className="ml-auto"
                        >
                            Réessayer
                        </Button>
                    </div>
                )}

                {/* Rides List */}
                {filteredRides.length > 0 ? (
                    <div className="space-y-4">
                        {filteredRides.map((ride) => (
                            <div
                                key={ride.id}
                                className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow"
                            >
                                <div className="p-5">
                                    <div className="flex items-start justify-between gap-4">
                                        {/* Ride Info */}
                                        <div className="flex-1 min-w-0">
                                            {/* Route */}
                                            <div className="flex items-center gap-2 text-lg font-medium text-slate-900 mb-2">
                                                <MapPin className="w-4 h-4 text-[#006B8F] shrink-0" />
                                                <span className="truncate">{ride.originName}</span>
                                                <ChevronRight className="w-4 h-4 text-slate-400 shrink-0" />
                                                <span className="truncate">{ride.destinationName}</span>
                                            </div>

                                            {/* Date & Time */}
                                            <div className="flex flex-wrap items-center gap-4 text-sm text-slate-500 mb-3">
                                                <span className="flex items-center gap-1">
                                                    <Calendar className="w-4 h-4" />
                                                    {formatDate(ride.departureTime)}
                                                </span>
                                                <span className="flex items-center gap-1">
                                                    <Clock className="w-4 h-4" />
                                                    {formatTime(ride.departureTime)}
                                                </span>
                                                <span className="flex items-center gap-1">
                                                    <Users className="w-4 h-4" />
                                                    {ride.totalSeats - ride.availableSeats}/{ride.totalSeats} places réservées
                                                </span>
                                            </div>

                                            {/* Status & Price */}
                                            <div className="flex items-center gap-3">
                                                <span
                                                    className={`px-3 py-1 rounded-full text-xs font-medium ${RideStatusColors[ride.status]
                                                        }`}
                                                >
                                                    {RideStatusLabels[ride.status]}
                                                </span>
                                                <span className="text-lg font-bold text-slate-900">
                                                    {ride.pricePerSeat} TND
                                                </span>
                                            </div>
                                        </div>

                                        {/* Actions Menu */}
                                        <div className="relative">
                                            <button
                                                onClick={() =>
                                                    setOpenMenuId(
                                                        openMenuId === ride.id ? null : ride.id
                                                    )
                                                }
                                                className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
                                            >
                                                <MoreVertical className="w-5 h-5" />
                                            </button>

                                            {openMenuId === ride.id && (
                                                <div className="absolute right-0 mt-1 w-48 bg-white rounded-xl shadow-lg border border-slate-100 py-1 z-10 animate-in fade-in slide-in-from-top-2">
                                                    <Link
                                                        href={`/rides/${ride.id}`}
                                                        className="flex items-center gap-2 px-4 py-2 text-sm text-slate-700 hover:bg-slate-50"
                                                        onClick={() => setOpenMenuId(null)}
                                                    >
                                                        <Car className="w-4 h-4" />
                                                        Voir détails
                                                    </Link>

                                                    {/* Edit only for SCHEDULED (before anyone books) */}
                                                    {ride.status === "SCHEDULED" && (
                                                        <Link
                                                            href={`/rides/${ride.id}/edit`}
                                                            className="flex items-center gap-2 px-4 py-2 text-sm text-slate-700 hover:bg-slate-50"
                                                            onClick={() => setOpenMenuId(null)}
                                                        >
                                                            <Edit className="w-4 h-4" />
                                                            Modifier
                                                        </Link>
                                                    )}

                                                    {/* Start for SCHEDULED or FULL */}
                                                    {(ride.status === "SCHEDULED" || ride.status === "FULL") && (
                                                        <button
                                                            onClick={() => handleStartRide(ride.id)}
                                                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-emerald-600 hover:bg-emerald-50"
                                                        >
                                                            <Play className="w-4 h-4" />
                                                            Démarrer
                                                        </button>
                                                    )}

                                                    {/* Cancel for SCHEDULED or FULL */}
                                                    {(ride.status === "SCHEDULED" || ride.status === "FULL") && (
                                                        <button
                                                            onClick={() => handleCancelRide(ride.id)}
                                                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                                                        >
                                                            <Ban className="w-4 h-4" />
                                                            Annuler
                                                        </button>
                                                    )}

                                                    {ride.status === "IN_PROGRESS" && (
                                                        <button
                                                            onClick={() => handleCompleteRide(ride.id)}
                                                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-emerald-600 hover:bg-emerald-50"
                                                        >
                                                            <CheckCircle className="w-4 h-4" />
                                                            Terminer
                                                        </button>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    // Empty State
                    <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center">
                        <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Car className="w-8 h-8 text-slate-400" />
                        </div>
                        <h3 className="text-lg font-medium text-slate-900 mb-2">
                            {statusFilter === "ALL"
                                ? "Aucun trajet publié"
                                : `Aucun trajet ${RideStatusLabels[statusFilter].toLowerCase()}`}
                        </h3>
                        <p className="text-slate-500 mb-6">
                            Publiez votre premier trajet et partagez vos frais !
                        </p>
                        <Link href="/publish">
                            <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673]">
                                <Plus className="w-4 h-4 mr-2" />
                                Publier un trajet
                            </Button>
                        </Link>
                    </div>
                )}

                {/* Pagination */}
                {totalPages > 1 && (
                    <div className="flex items-center justify-center gap-2 mt-8">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                            disabled={currentPage === 0}
                            className="rounded-lg"
                        >
                            <ChevronLeft className="w-4 h-4" />
                        </Button>
                        <span className="text-sm text-slate-600 px-4">
                            Page {currentPage + 1} sur {totalPages}
                        </span>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() =>
                                setCurrentPage((p) => Math.min(totalPages - 1, p + 1))
                            }
                            disabled={currentPage === totalPages - 1}
                            className="rounded-lg"
                        >
                            <ChevronRight className="w-4 h-4" />
                        </Button>
                    </div>
                )}
            </main>

            {/* Click outside to close menu */}
            {openMenuId && (
                <div
                    className="fixed inset-0 z-0"
                    onClick={() => setOpenMenuId(null)}
                />
            )}
        </div>
    );
}
