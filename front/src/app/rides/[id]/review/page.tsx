"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import {
    Star,
    Loader2,
    AlertCircle,
    ArrowLeft,
    MapPin,
    CheckCircle,
    User,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import { cn } from "@/lib/utils";

interface BookingInfo {
    id: string;
    rideID: string;
    rideSummary: string;
    driverName: string;
    departureTime: string;
    seatsBooked: number;
    priceTotal: number;
    status: string;
}

interface RideInfo {
    id: string;
    originName: string;
    destinationName: string;
    departureTime: string;
    driverName: string;
    driverEmail: string;
    status: string;
}

export default function ReviewPage() {
    const params = useParams();
    const router = useRouter();
    const { token, user, isAuthenticated, isLoading: authLoading } = useAuth();

    const [ride, setRide] = useState<RideInfo | null>(null);
    const [booking, setBooking] = useState<BookingInfo | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [rating, setRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0);
    const [comment, setComment] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);

    const rideId = params.id as string;

    // Redirect if not authenticated
    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const fetchData = useCallback(async () => {
        if (!rideId || !token) return;

        setIsLoading(true);
        setError("");

        try {
            // Fetch ride info
            const rideResponse = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!rideResponse.ok) {
                if (rideResponse.status === 404) throw new Error("Trajet introuvable");
                throw new Error("Erreur lors du chargement du trajet");
            }

            const rideData = await rideResponse.json();
            setRide(rideData);

            // Fetch user's booking for this ride
            const bookingResponse = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/bookings/ride/${rideId}/mine`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!bookingResponse.ok) {
                if (bookingResponse.status === 404) {
                    throw new Error("Vous n'avez pas de réservation pour ce trajet");
                }
                throw new Error("Erreur lors du chargement de la réservation");
            }

            const bookingData = await bookingResponse.json();
            setBooking(bookingData);

            // Check if ride is completed
            if (rideData.status !== "COMPLETED") {
                setError("Le trajet doit être terminé pour laisser un avis");
            }

            // Check if booking is confirmed
            if (bookingData.status !== "CONFIRMED") {
                setError("Votre réservation doit être confirmée pour laisser un avis");
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : "Erreur lors du chargement");
        } finally {
            setIsLoading(false);
        }
    }, [rideId, token]);

    useEffect(() => {
        if (token) {
            fetchData();
        }
    }, [token, fetchData]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (rating === 0) {
            alert("Veuillez sélectionner une note");
            return;
        }

        if (!booking) return;

        setIsSubmitting(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/reviews`,
                {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        bookingId: booking.id,
                        rating,
                        comment: comment.trim() || null,
                    }),
                }
            );

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                if (errorData?.message === "ALREADY_REVIEWED") {
                    throw new Error("Vous avez déjà laissé un avis pour ce trajet");
                }
                if (errorData?.message === "RIDE_NOT_COMPLETED") {
                    throw new Error("Le trajet doit être terminé pour laisser un avis");
                }
                throw new Error("Erreur lors de l'envoi de l'avis");
            }

            setIsSuccess(true);
        } catch (err) {
            alert(err instanceof Error ? err.message : "Erreur lors de l'envoi");
        } finally {
            setIsSubmitting(false);
        }
    };

    const isDriver = ride && user && ride.driverEmail === user.email;
    const revieweeName = isDriver
        ? "le passager"
        : ride?.driverName || "le conducteur";

    // Loading state
    if (authLoading || isLoading) {
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

    // Success state
    if (isSuccess) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <main className="max-w-lg mx-auto px-4 py-20 text-center">
                    <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-8">
                        <div className="w-20 h-20 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <CheckCircle className="w-10 h-10 text-emerald-600" />
                        </div>
                        <h1 className="text-2xl font-bold text-slate-900 mb-3">
                            Merci pour votre avis !
                        </h1>
                        <p className="text-slate-600 mb-6">
                            Votre avis aide la communauté Blassa à voyager en toute confiance.
                        </p>
                        <div className="flex flex-col gap-3">
                            <Link href="/dashboard/bookings">
                                <Button className="w-full rounded-xl bg-[#006B8F] hover:bg-[#005673]">
                                    Retour à mes réservations
                                </Button>
                            </Link>
                            <Link href="/">
                                <Button variant="outline" className="w-full rounded-xl">
                                    Retour à l'accueil
                                </Button>
                            </Link>
                        </div>
                    </div>
                </main>
            </div>
        );
    }

    // Error state - can't review
    if (error && !ride) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="max-w-2xl mx-auto px-4 py-20 text-center">
                    <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <AlertCircle className="w-8 h-8 text-red-600" />
                    </div>
                    <h1 className="text-xl font-bold text-slate-900 mb-2">{error}</h1>
                    <p className="text-slate-500 mb-6">
                        Impossible de charger la page d'avis.
                    </p>
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

            <main className="max-w-lg mx-auto px-4 sm:px-6 py-8">
                <button
                    onClick={() => router.back()}
                    className="flex items-center gap-2 text-slate-600 hover:text-slate-900 mb-6 transition-colors"
                >
                    <ArrowLeft className="w-5 h-5" />
                    <span>Retour</span>
                </button>

                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    {/* Header */}
                    <div className="p-6 border-b border-slate-100 bg-gradient-to-r from-[#006B8F]/5 to-[#FF9A3E]/5">
                        <div className="flex items-center gap-3 mb-3">
                            <div className="w-12 h-12 bg-[#006B8F] rounded-xl flex items-center justify-center">
                                <Star className="w-6 h-6 text-white" />
                            </div>
                            <div>
                                <h1 className="text-xl font-bold text-slate-900">
                                    Laisser un avis
                                </h1>
                                <p className="text-slate-500 text-sm">
                                    Comment s'est passé votre trajet ?
                                </p>
                            </div>
                        </div>

                        {/* Ride info summary */}
                        {ride && (
                            <div className="mt-4 p-3 bg-white rounded-xl border border-slate-100">
                                <div className="flex items-center gap-2 text-slate-700">
                                    <MapPin className="w-4 h-4 text-[#006B8F]" />
                                    <span className="font-medium">
                                        {ride.originName} → {ride.destinationName}
                                    </span>
                                </div>
                                <div className="flex items-center gap-2 mt-2 text-sm text-slate-500">
                                    <User className="w-4 h-4" />
                                    <span>
                                        {isDriver ? "Passager" : "Conducteur"}: {revieweeName}
                                    </span>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Review Form */}
                    <form onSubmit={handleSubmit} className="p-6">
                        {/* Error banner if ride not completed */}
                        {error && (
                            <div className="mb-6 p-4 bg-amber-50 border border-amber-200 rounded-xl text-amber-800 text-sm">
                                <AlertCircle className="w-4 h-4 inline mr-2" />
                                {error}
                            </div>
                        )}

                        {/* Star Rating */}
                        <div className="mb-6">
                            <label className="block text-sm font-medium text-slate-700 mb-3">
                                Note *
                            </label>
                            <div className="flex items-center justify-center gap-2">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <button
                                        key={star}
                                        type="button"
                                        onClick={() => setRating(star)}
                                        onMouseEnter={() => setHoverRating(star)}
                                        onMouseLeave={() => setHoverRating(0)}
                                        className="transition-transform hover:scale-110"
                                    >
                                        <Star
                                            className={cn(
                                                "w-10 h-10 transition-colors",
                                                (hoverRating || rating) >= star
                                                    ? "text-yellow-400 fill-yellow-400"
                                                    : "text-slate-300"
                                            )}
                                        />
                                    </button>
                                ))}
                            </div>
                            <p className="text-center text-sm text-slate-500 mt-2">
                                {rating === 1 && "Très mauvais"}
                                {rating === 2 && "Mauvais"}
                                {rating === 3 && "Moyen"}
                                {rating === 4 && "Bien"}
                                {rating === 5 && "Excellent !"}
                                {rating === 0 && "Sélectionnez une note"}
                            </p>
                        </div>

                        {/* Comment */}
                        <div className="mb-6">
                            <label
                                htmlFor="comment"
                                className="block text-sm font-medium text-slate-700 mb-2"
                            >
                                Commentaire (optionnel)
                            </label>
                            <textarea
                                id="comment"
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                placeholder="Partagez votre expérience..."
                                maxLength={500}
                                rows={4}
                                className="w-full px-4 py-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent resize-none"
                            />
                            <p className="text-xs text-slate-400 mt-1 text-right">
                                {comment.length}/500
                            </p>
                        </div>

                        {/* Submit Button */}
                        <Button
                            type="submit"
                            disabled={isSubmitting || rating === 0 || !!error}
                            className="w-full rounded-xl bg-[#006B8F] hover:bg-[#005673] py-6 text-base"
                        >
                            {isSubmitting ? (
                                <>
                                    <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                                    Envoi en cours...
                                </>
                            ) : (
                                <>
                                    <Star className="w-5 h-5 mr-2" />
                                    Envoyer mon avis
                                </>
                            )}
                        </Button>
                    </form>
                </div>
            </main>
        </div>
    );
}
