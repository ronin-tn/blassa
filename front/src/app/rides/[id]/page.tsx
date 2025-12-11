"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import {
    MapPin,
    Calendar,
    Clock,
    Users,
    Loader2,
    AlertCircle,
    ArrowLeft,
    User,
    Star,
    Cigarette,
    CigaretteOff,
    Shield,
    MessageCircle,
    Facebook,
    Instagram,
    Phone,
    Mail,
    UserCheck,
    Check,
    X,
    Play,
    CheckCircle,
    Ban,
    Edit,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import {
    Ride,
    RideStatusLabels,
    RideStatusColors,
    GenderPreferenceLabels,
} from "@/types/ride";
import { BookingStatus, BookingStatusLabels, BookingStatusColors } from "@/types/booking";

interface Passenger {
    bookingId: string;
    passengerName: string;
    passengerEmail: string;
    passengerPhone: string;
    facebookUrl: string | null;
    instagramUrl: string | null;
    seatsBooked: number;
    status: BookingStatus;
}

export default function RideDetailsPage() {
    const params = useParams();
    const router = useRouter();
    const { token, user } = useAuth();

    const [ride, setRide] = useState<Ride | null>(null);
    const [passengers, setPassengers] = useState<Passenger[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [actionLoading, setActionLoading] = useState<string | null>(null);
    const [rideActionLoading, setRideActionLoading] = useState(false);

    const rideId = params.id as string;
    const isOwnRide = ride && user && ride.driverEmail === user.email;

    const fetchRide = useCallback(async () => {
        if (!rideId) return;

        setIsLoading(true);
        setError("");

        try {
            const headers: HeadersInit = { "Content-Type": "application/json" };
            if (token) headers.Authorization = `Bearer ${token}`;

            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}`,
                { headers }
            );

            if (!response.ok) {
                if (response.status === 404) throw new Error("Trajet introuvable");
                throw new Error("Erreur lors du chargement du trajet");
            }

            const data: Ride = await response.json();
            setRide(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Erreur lors du chargement");
        } finally {
            setIsLoading(false);
        }
    }, [rideId, token]);

    const fetchPassengers = useCallback(async () => {
        if (!rideId || !token || !isOwnRide) return;

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/bookings/ride/${rideId}/passengers`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (response.ok) {
                const data = await response.json();
                setPassengers(data);
            }
        } catch (err) {
            console.error("Failed to fetch passengers:", err);
        }
    }, [rideId, token, isOwnRide]);

    useEffect(() => {
        fetchRide();
    }, [fetchRide]);

    useEffect(() => {
        if (isOwnRide) {
            fetchPassengers();
        }
    }, [isOwnRide, fetchPassengers]);

    const handleAcceptBooking = async (bookingId: string) => {
        if (!token) return;
        setActionLoading(bookingId);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/bookings/${bookingId}/accept`,
                {
                    method: "POST",
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (response.ok) {
                // Update local state
                setPassengers((prev) =>
                    prev.map((p) =>
                        p.bookingId === bookingId ? { ...p, status: "CONFIRMED" } : p
                    )
                );
            }
        } catch (err) {
            console.error("Failed to accept booking:", err);
        } finally {
            setActionLoading(null);
        }
    };

    const handleRejectBooking = async (bookingId: string) => {
        if (!token) return;
        setActionLoading(bookingId);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/bookings/${bookingId}/reject`,
                {
                    method: "POST",
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (response.ok) {
                // Remove from local state
                setPassengers((prev) => prev.filter((p) => p.bookingId !== bookingId));
                // Refresh ride to update available seats
                fetchRide();
            }
        } catch (err) {
            console.error("Failed to reject booking:", err);
        } finally {
            setActionLoading(null);
        }
    };

    const handleStartRide = async () => {
        if (!token || !ride) return;
        setRideActionLoading(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${ride.id}/start`,
                {
                    method: "PUT",
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (response.ok) {
                fetchRide();
            } else {
                const errorData = await response.json().catch(() => null);
                alert(errorData?.message || "Erreur lors du démarrage");
            }
        } catch (err) {
            console.error("Failed to start ride:", err);
        } finally {
            setRideActionLoading(false);
        }
    };

    const handleCompleteRide = async () => {
        if (!token || !ride) return;
        setRideActionLoading(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${ride.id}/complete`,
                {
                    method: "PUT",
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (response.ok) {
                fetchRide();
            }
        } catch (err) {
            console.error("Failed to complete ride:", err);
        } finally {
            setRideActionLoading(false);
        }
    };

    const handleCancelRide = async () => {
        if (!token || !ride) return;
        if (!confirm("Êtes-vous sûr de vouloir annuler ce trajet ?")) return;
        setRideActionLoading(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${ride.id}`,
                {
                    method: "DELETE",
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            if (response.ok) {
                router.push("/dashboard/rides");
            }
        } catch (err) {
            console.error("Failed to cancel ride:", err);
        } finally {
            setRideActionLoading(false);
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric",
        });
    };

    const formatTime = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
    };

    const pendingCount = passengers.filter((p) => p.status === "PENDING").length;
    const confirmedCount = passengers.filter((p) => p.status === "CONFIRMED").length;

    if (isLoading) {
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

    if (error || !ride) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="max-w-2xl mx-auto px-4 py-20 text-center">
                    <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <AlertCircle className="w-8 h-8 text-red-600" />
                    </div>
                    <h1 className="text-xl font-bold text-slate-900 mb-2">{error || "Trajet introuvable"}</h1>
                    <p className="text-slate-500 mb-6">Ce trajet n&apos;existe pas ou a été supprimé.</p>
                    <Button onClick={() => router.back()} variant="outline" className="rounded-xl">
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Retour
                    </Button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F8FAFC] pb-20 lg:pb-8">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <button
                    onClick={() => router.back()}
                    className="flex items-center gap-2 text-slate-600 hover:text-slate-900 mb-6 transition-colors"
                >
                    <ArrowLeft className="w-5 h-5" />
                    <span>Retour</span>
                </button>

                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    {/* Route Header */}
                    <div className="p-6 border-b border-slate-100 bg-gradient-to-r from-[#006B8F]/5 to-[#FF9A3E]/5">
                        <div className="flex items-center gap-3 mb-4">
                            <div className="w-12 h-12 bg-[#006B8F] rounded-xl flex items-center justify-center">
                                <MapPin className="w-6 h-6 text-white" />
                            </div>
                            <div>
                                <div className="flex items-center gap-2 text-xl font-bold text-slate-900">
                                    <span>{ride.originName}</span>
                                    <span className="text-slate-400">→</span>
                                    <span>{ride.destinationName}</span>
                                </div>
                                <span className={`inline-block mt-1 px-3 py-0.5 rounded-full text-xs font-medium ${RideStatusColors[ride.status]}`}>
                                    {RideStatusLabels[ride.status]}
                                </span>
                            </div>
                        </div>
                        <div className="flex flex-wrap items-center gap-6 text-slate-600">
                            <div className="flex items-center gap-2">
                                <Calendar className="w-5 h-5 text-[#006B8F]" />
                                <span>{formatDate(ride.departureTime)}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Clock className="w-5 h-5 text-[#006B8F]" />
                                <span>{formatTime(ride.departureTime)}</span>
                            </div>
                        </div>
                    </div>

                    {/* Details Grid */}
                    <div className="p-6 grid sm:grid-cols-2 gap-6">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center">
                                <Users className="w-5 h-5 text-slate-600" />
                            </div>
                            <div>
                                <p className="text-sm text-slate-500">Places disponibles</p>
                                <p className="font-medium text-slate-900">{ride.availableSeats} / {ride.totalSeats}</p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3">
                            {ride.allowsSmoking ? (
                                <div className="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
                                    <Cigarette className="w-5 h-5 text-orange-600" />
                                </div>
                            ) : (
                                <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center">
                                    <CigaretteOff className="w-5 h-5 text-slate-600" />
                                </div>
                            )}
                            <div>
                                <p className="text-sm text-slate-500">Fumeur</p>
                                <p className="font-medium text-slate-900">{ride.allowsSmoking ? "Autorisé" : "Non autorisé"}</p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                                <Shield className="w-5 h-5 text-purple-600" />
                            </div>
                            <div>
                                <p className="text-sm text-slate-500">Préférence</p>
                                <p className="font-medium text-slate-900">{GenderPreferenceLabels[ride.genderPreference]}</p>
                            </div>
                        </div>
                    </div>

                    {/* DRIVER VIEW: Show Passengers with Accept/Reject */}
                    {isOwnRide ? (
                        <div className="p-6 border-t border-slate-100 bg-emerald-50">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-sm font-medium text-emerald-800 flex items-center gap-2">
                                    <Users className="w-4 h-4" />
                                    Réservations
                                </h3>
                                <div className="flex items-center gap-2 text-xs">
                                    {pendingCount > 0 && (
                                        <span className="px-2 py-1 bg-yellow-100 text-yellow-700 rounded-full">
                                            {pendingCount} en attente
                                        </span>
                                    )}
                                    {confirmedCount > 0 && (
                                        <span className="px-2 py-1 bg-emerald-100 text-emerald-700 rounded-full">
                                            {confirmedCount} confirmée{confirmedCount > 1 ? "s" : ""}
                                        </span>
                                    )}
                                </div>
                            </div>

                            {passengers.length === 0 ? (
                                <div className="text-center py-8">
                                    <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-3">
                                        <UserCheck className="w-8 h-8 text-emerald-600" />
                                    </div>
                                    <p className="text-emerald-700 font-medium">Aucune réservation</p>
                                    <p className="text-emerald-600 text-sm mt-1">Les demandes de réservation apparaîtront ici</p>
                                </div>
                            ) : (
                                <div className="space-y-4">
                                    {passengers.map((passenger) => (
                                        <div
                                            key={passenger.bookingId}
                                            className={`bg-white rounded-xl p-4 border ${passenger.status === "PENDING"
                                                ? "border-yellow-300 ring-2 ring-yellow-100"
                                                : "border-emerald-200"
                                                }`}
                                        >
                                            <div className="flex items-start gap-4">
                                                <div className="w-12 h-12 bg-emerald-100 rounded-full flex items-center justify-center text-emerald-700 font-bold text-lg">
                                                    {passenger.passengerName.charAt(0).toUpperCase()}
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <div className="flex items-center justify-between mb-2">
                                                        <div className="flex items-center gap-2">
                                                            <p className="font-semibold text-slate-900">{passenger.passengerName}</p>
                                                            <span className={`text-xs px-2 py-0.5 rounded-full ${BookingStatusColors[passenger.status]}`}>
                                                                {BookingStatusLabels[passenger.status]}
                                                            </span>
                                                        </div>
                                                        <span className="text-sm bg-slate-100 text-slate-700 px-2 py-0.5 rounded-full">
                                                            {passenger.seatsBooked} place{passenger.seatsBooked > 1 ? "s" : ""}
                                                        </span>
                                                    </div>

                                                    <div className="space-y-2">
                                                        <a
                                                            href={`tel:${passenger.passengerPhone}`}
                                                            className="flex items-center gap-2 text-sm text-slate-600 hover:text-emerald-600"
                                                        >
                                                            <Phone className="w-4 h-4" />
                                                            <span>{passenger.passengerPhone}</span>
                                                        </a>
                                                        <a
                                                            href={`mailto:${passenger.passengerEmail}`}
                                                            className="flex items-center gap-2 text-sm text-slate-600 hover:text-emerald-600"
                                                        >
                                                            <Mail className="w-4 h-4" />
                                                            <span className="truncate">{passenger.passengerEmail}</span>
                                                        </a>

                                                        {(passenger.facebookUrl || passenger.instagramUrl) && (
                                                            <div className="flex items-center gap-2 pt-1">
                                                                {passenger.facebookUrl && (
                                                                    <a
                                                                        href={passenger.facebookUrl}
                                                                        target="_blank"
                                                                        rel="noopener noreferrer"
                                                                        className="flex items-center gap-1.5 px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs hover:bg-blue-200"
                                                                    >
                                                                        <Facebook className="w-3 h-3" />
                                                                        Facebook
                                                                    </a>
                                                                )}
                                                                {passenger.instagramUrl && (
                                                                    <a
                                                                        href={passenger.instagramUrl}
                                                                        target="_blank"
                                                                        rel="noopener noreferrer"
                                                                        className="flex items-center gap-1.5 px-2 py-1 bg-pink-100 text-pink-700 rounded text-xs hover:bg-pink-200"
                                                                    >
                                                                        <Instagram className="w-3 h-3" />
                                                                        Instagram
                                                                    </a>
                                                                )}
                                                            </div>
                                                        )}
                                                    </div>

                                                    {/* Accept/Reject buttons for PENDING bookings */}
                                                    {passenger.status === "PENDING" && (
                                                        <div className="flex items-center gap-2 mt-4 pt-3 border-t border-slate-100">
                                                            <button
                                                                onClick={() => handleAcceptBooking(passenger.bookingId)}
                                                                disabled={actionLoading === passenger.bookingId}
                                                                className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 disabled:opacity-50 font-medium"
                                                            >
                                                                {actionLoading === passenger.bookingId ? (
                                                                    <Loader2 className="w-4 h-4 animate-spin" />
                                                                ) : (
                                                                    <Check className="w-4 h-4" />
                                                                )}
                                                                Accepter
                                                            </button>
                                                            <button
                                                                onClick={() => handleRejectBooking(passenger.bookingId)}
                                                                disabled={actionLoading === passenger.bookingId}
                                                                className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 disabled:opacity-50 font-medium"
                                                            >
                                                                <X className="w-4 h-4" />
                                                                Refuser
                                                            </button>
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {/* Driver Action Buttons */}
                            <div className="mt-6 pt-4 border-t border-emerald-200">
                                <h3 className="text-sm font-medium text-emerald-800 mb-3">Actions</h3>
                                <div className="flex flex-wrap gap-3">
                                    {/* Start button for SCHEDULED or FULL */}
                                    {(ride.status === "SCHEDULED" || ride.status === "FULL") && (
                                        <Button
                                            onClick={handleStartRide}
                                            disabled={rideActionLoading}
                                            className="bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl"
                                        >
                                            {rideActionLoading ? (
                                                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                                            ) : (
                                                <Play className="w-4 h-4 mr-2" />
                                            )}
                                            Démarrer le trajet
                                        </Button>
                                    )}

                                    {/* Complete button for IN_PROGRESS */}
                                    {ride.status === "IN_PROGRESS" && (
                                        <Button
                                            onClick={handleCompleteRide}
                                            disabled={rideActionLoading}
                                            className="bg-blue-600 hover:bg-blue-700 text-white rounded-xl"
                                        >
                                            {rideActionLoading ? (
                                                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                                            ) : (
                                                <CheckCircle className="w-4 h-4 mr-2" />
                                            )}
                                            Terminer le trajet
                                        </Button>
                                    )}

                                    {/* Edit button for SCHEDULED */}
                                    {ride.status === "SCHEDULED" && (
                                        <Link href={`/rides/${ride.id}/edit`}>
                                            <Button
                                                variant="outline"
                                                className="rounded-xl border-slate-300"
                                            >
                                                <Edit className="w-4 h-4 mr-2" />
                                                Modifier
                                            </Button>
                                        </Link>
                                    )}

                                    {/* Cancel button for SCHEDULED, FULL, or IN_PROGRESS */}
                                    {(ride.status === "SCHEDULED" || ride.status === "FULL" || ride.status === "IN_PROGRESS") && (
                                        <Button
                                            onClick={handleCancelRide}
                                            disabled={rideActionLoading}
                                            variant="outline"
                                            className="rounded-xl border-red-300 text-red-600 hover:bg-red-50 hover:text-red-700"
                                        >
                                            <Ban className="w-4 h-4 mr-2" />
                                            Annuler le trajet
                                        </Button>
                                    )}
                                </div>
                            </div>

                            <div className="mt-4 pt-4 border-t border-emerald-200">
                                <Link href="/dashboard/rides" className="text-sm text-emerald-700 hover:text-emerald-800 font-medium">
                                    ← Gérer mes trajets
                                </Link>
                            </div>
                        </div>
                    ) : (
                        /* PASSENGER VIEW: Show Driver Contact */
                        <div className="p-6 border-t border-slate-100 bg-slate-50">
                            <h3 className="text-sm font-medium text-slate-500 mb-4">Conducteur</h3>
                            <div className="flex items-center gap-4 mb-4">
                                <div className="w-14 h-14 bg-[#006B8F] rounded-full flex items-center justify-center text-white text-lg font-bold">
                                    {ride.driverName.charAt(0)}
                                </div>
                                <div>
                                    <p className="font-medium text-slate-900 text-lg">{ride.driverName}</p>
                                    {ride.driverRating !== null && ride.driverRating > 0 && (
                                        <div className="flex items-center gap-1 text-sm text-slate-500">
                                            <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                                            <span>{ride.driverRating.toFixed(1)}</span>
                                        </div>
                                    )}
                                </div>
                            </div>

                            <div className="mt-4 pt-4 border-t border-slate-200">
                                <h4 className="text-sm font-medium text-slate-700 mb-3 flex items-center gap-2">
                                    <MessageCircle className="w-4 h-4" />
                                    Contacter le conducteur
                                </h4>

                                <div className="space-y-3">
                                    <a
                                        href={`tel:${ride.driverPhoneNumber}`}
                                        className="flex items-center gap-3 w-full px-4 py-3 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-colors"
                                    >
                                        <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
                                            <Phone className="w-5 h-5" />
                                        </div>
                                        <div>
                                            <p className="font-semibold text-lg">{ride.driverPhoneNumber}</p>
                                            <p className="text-emerald-100 text-sm">Appuyer pour appeler</p>
                                        </div>
                                    </a>

                                    <div className="flex flex-wrap gap-3">
                                        {ride.driverFacebookUrl && (
                                            <a
                                                href={ride.driverFacebookUrl}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors"
                                            >
                                                <Facebook className="w-5 h-5" />
                                                <span className="font-medium">Facebook</span>
                                            </a>
                                        )}
                                        {ride.driverInstagramUrl && (
                                            <a
                                                href={ride.driverInstagramUrl}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="flex items-center gap-2 px-4 py-2.5 bg-gradient-to-r from-purple-600 to-pink-500 text-white rounded-xl hover:from-purple-700 hover:to-pink-600 transition-all"
                                            >
                                                <Instagram className="w-5 h-5" />
                                                <span className="font-medium">Instagram</span>
                                            </a>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>

                <div className="mt-6 text-center">
                    <Link href="/dashboard/bookings" className="text-sm text-slate-500 hover:text-[#006B8F] transition-colors">
                        ← Retour à mes réservations
                    </Link>
                </div>
            </main>
        </div>
    );
}
