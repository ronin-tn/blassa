"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    Ticket,
    Calendar,
    Clock,
    Users,
    ChevronRight,
    ChevronLeft,
    Loader2,
    AlertCircle,
    ArrowLeft,
    Search,
    X,
    User,
    MapPin,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import {
    Booking,
    BookingStatus,
    BookingStatusLabels,
    BookingStatusColors,
} from "@/types/booking";
import { PagedResponse } from "@/types/ride";
import { parseApiError } from "@/lib/api-utils";
import ConfirmationModal from "@/components/ui/confirmation-modal";

export default function MyBookingsPage() {
    const router = useRouter();
    const { isAuthenticated, isLoading: authLoading } = useAuth();

    const [bookings, setBookings] = useState<Booking[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [statusFilter, setStatusFilter] = useState<BookingStatus | "ALL">("ALL");

    const [bookingToCancel, setBookingToCancel] = useState<string | null>(null);
    const [isCancelling, setIsCancelling] = useState(false);

    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const fetchBookings = useCallback(async () => {
        if (!isAuthenticated) return;

        setIsLoading(true);
        setError("");

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/bookings/mine?page=${currentPage}&size=10`,
                {
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!response.ok) {
                throw new Error("Erreur lors du chargement des réservations");
            }

            const data: PagedResponse<Booking> = await response.json();
            setBookings(data.content);
            setTotalPages(data.page.totalPages);
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Erreur lors du chargement"
            );
        } finally {
            setIsLoading(false);
        }
    }, [isAuthenticated, currentPage]);

    useEffect(() => {
        if (isAuthenticated) {
            fetchBookings();
        }
    }, [isAuthenticated, fetchBookings]);

    const handleCancelBooking = (bookingId: string) => {
        setBookingToCancel(bookingId);
    };

    const confirmCancel = async () => {
        if (!bookingToCancel) return;

        setIsCancelling(true);
        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/bookings/${bookingToCancel}`,
                {
                    method: "DELETE",
                    credentials: "include",
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de l'annulation");
                throw new Error(errorMessage);
            }

            fetchBookings();
            setBookingToCancel(null);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Erreur lors de l'annulation");
        } finally {
            setIsCancelling(false);
        }
    };

    const statusPriority: Record<BookingStatus, number> = {
        PENDING: 1,
        CONFIRMED: 2,
        REJECTED: 3,
        CANCELLED: 4,
    };

    const sortedAndFilteredBookings = (() => {
        const result = statusFilter === "ALL"
            ? [...bookings]
            : bookings.filter((booking) => booking.status === statusFilter);

        return result.sort((a, b) => {
            const priorityDiff = statusPriority[a.status] - statusPriority[b.status];
            if (priorityDiff !== 0) return priorityDiff;
            return new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime();
        });
    })();

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            weekday: "short",
            day: "numeric",
            month: "short",
            year: "numeric",
        });
    };

    const formatTime = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("fr-FR", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const formatCreatedAt = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            day: "numeric",
            month: "short",
            year: "numeric",
        });
    };

    if (authLoading || (isLoading && bookings.length === 0)) {
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
        <div className="min-h-screen bg-[#F8FAFC] pb-24 lg:pb-8">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
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
                                Mes Réservations
                            </h1>
                            <p className="text-slate-500 text-sm">
                                Vos trajets réservés en tant que passager
                            </p>
                        </div>
                    </div>
                    <Link href="/#search">
                        <Button
                            variant="outline"
                            className="rounded-xl border-[#006B8F] text-[#006B8F] hover:bg-[#006B8F] hover:text-white"
                        >
                            <Search className="w-4 h-4 mr-2" />
                            Trouver un trajet
                        </Button>
                    </Link>
                </div>

                <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
                    {(["ALL", "PENDING", "CONFIRMED", "REJECTED", "CANCELLED"] as const).map(
                        (status) => (
                            <button
                                key={status}
                                onClick={() => setStatusFilter(status)}
                                className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all ${statusFilter === status
                                    ? "bg-[#006B8F] text-white"
                                    : "bg-white text-slate-600 hover:bg-slate-50 border border-slate-200"
                                    }`}
                            >
                                {status === "ALL" ? "Toutes" : BookingStatusLabels[status]}
                            </button>
                        )
                    )}
                </div>

                {error && (
                    <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 flex items-center gap-3 mb-6">
                        <AlertCircle className="w-5 h-5 shrink-0" />
                        <span>{error}</span>
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={fetchBookings}
                            className="ml-auto"
                        >
                            Réessayer
                        </Button>
                    </div>
                )}

                {sortedAndFilteredBookings.length > 0 ? (
                    <div className="space-y-4">
                        {sortedAndFilteredBookings.map((booking) => (
                            <div
                                key={booking.id}
                                className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden hover:shadow-md transition-shadow"
                            >
                                <div className="p-5">
                                    <div className="flex items-start justify-between gap-4">
                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center gap-2 text-lg font-medium text-slate-900 mb-2">
                                                <MapPin className="w-4 h-4 text-[#FF9A3E] shrink-0" />
                                                <span className="truncate">{booking.rideSummary}</span>
                                            </div>

                                            <div className="flex items-center gap-2 text-sm text-slate-600 mb-3">
                                                <User className="w-4 h-4 text-slate-400" />
                                                <span>Conducteur: {booking.driverName}</span>
                                            </div>

                                            <div className="flex flex-wrap items-center gap-4 text-sm text-slate-500 mb-3">
                                                <span className="flex items-center gap-1">
                                                    <Calendar className="w-4 h-4" />
                                                    {formatDate(booking.departureTime)}
                                                </span>
                                                <span className="flex items-center gap-1">
                                                    <Clock className="w-4 h-4" />
                                                    {formatTime(booking.departureTime)}
                                                </span>
                                                <span className="flex items-center gap-1">
                                                    <Users className="w-4 h-4" />
                                                    {booking.seatsBooked} place{booking.seatsBooked > 1 ? "s" : ""}
                                                </span>
                                            </div>

                                            <div className="flex items-center gap-3">
                                                <span
                                                    className={`px-3 py-1 rounded-full text-xs font-medium ${BookingStatusColors[booking.status]
                                                        }`}
                                                >
                                                    {BookingStatusLabels[booking.status]}
                                                </span>
                                                <span className="text-lg font-bold text-slate-900">
                                                    {booking.priceTotal} TND
                                                </span>
                                                <span className="text-xs text-slate-400">
                                                    Réservé le {formatCreatedAt(booking.createdAt)}
                                                </span>
                                            </div>
                                        </div>

                                        <div className="flex flex-col gap-2">
                                            <Link href={`/rides/${booking.rideID}`}>
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    className="rounded-lg text-xs"
                                                >
                                                    Voir trajet
                                                </Button>
                                            </Link>
                                            {(booking.status === "PENDING" ||
                                                booking.status === "CONFIRMED") &&
                                                booking.rideStatus !== "IN_PROGRESS" &&
                                                booking.rideStatus !== "COMPLETED" && (
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        onClick={() => handleCancelBooking(booking.id)}
                                                        className="rounded-lg text-xs text-red-600 hover:text-red-700 hover:bg-red-50"
                                                    >
                                                        <X className="w-3 h-3 mr-1" />
                                                        Annuler
                                                    </Button>
                                                )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center">
                        <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Ticket className="w-8 h-8 text-slate-400" />
                        </div>
                        <h3 className="text-lg font-medium text-slate-900 mb-2">
                            {statusFilter === "ALL"
                                ? "Aucune réservation"
                                : `Aucune réservation ${BookingStatusLabels[statusFilter].toLowerCase()}`}
                        </h3>
                        <p className="text-slate-500 mb-6">
                            Trouvez un trajet et réservez votre place !
                        </p>
                        <Link href="/#search">
                            <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673]">
                                <Search className="w-4 h-4 mr-2" />
                                Rechercher un trajet
                            </Button>
                        </Link>
                    </div>
                )}

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

            <ConfirmationModal
                isOpen={!!bookingToCancel}
                onClose={() => !isCancelling && setBookingToCancel(null)}
                onConfirm={confirmCancel}
                title="Annuler la réservation"
                message="Êtes-vous sûr de vouloir annuler cette réservation ? Cette action est irréversible."
                confirmLabel="Oui, annuler"
                cancelLabel="Non, garder"
                isDestructive={true}
                isLoading={isCancelling}
            />
        </div>
    );
}
