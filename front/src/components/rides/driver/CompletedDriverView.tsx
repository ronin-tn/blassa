"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { Ride, PassengerInfo } from "@/types/models";
import { Button } from "@/components/ui/button";
import { CheckCircle, Home, PlusCircle, Coins, Users, Send } from "lucide-react";
import StarRatingInput from "@/components/ui/StarRatingInput";
import { submitReviewAction } from "@/app/actions/reviews";
import { useToast } from "@/contexts/ToastContext";

interface CompletedDriverViewProps {
    ride: Ride;
    passengers: PassengerInfo[];
}

export default function CompletedDriverView({ ride, passengers }: CompletedDriverViewProps) {
    const { showError, showSuccess } = useToast();
    const confirmedPassengers = passengers.filter(p => p.status === 'CONFIRMED');

    // Calculate simple stats
    const totalEarnings = confirmedPassengers.reduce((sum, p) => sum + (p.seatsBooked * ride.pricePerSeat), 0);

    // Track review status for each passenger
    const [reviewedPassengers, setReviewedPassengers] = useState<Set<string>>(new Set());
    const [ratings, setRatings] = useState<Record<string, number>>({});
    const [comments, setComments] = useState<Record<string, string>>({});
    const [submitting, setSubmitting] = useState<string | null>(null);

    // Check for existing reviews on mount
    useEffect(() => {
        const checkExistingReviews = async () => {
            try {
                const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/reviews/mine/sent?size=100`, {
                    credentials: 'include'
                });
                if (response.ok) {
                    const data = await response.json();
                    const existingBookingIds = new Set<string>();
                    data.content?.forEach((review: { bookingId: string }) => {
                        // Check if this review is for one of our passengers
                        const matchingPassenger = confirmedPassengers.find(p => p.bookingId === review.bookingId);
                        if (matchingPassenger) {
                            existingBookingIds.add(review.bookingId);
                        }
                    });
                    if (existingBookingIds.size > 0) {
                        setReviewedPassengers(existingBookingIds);
                    }
                }
            } catch {
                // Silently fail - we'll let the submit action catch duplicates
            }
        };
        checkExistingReviews();
    }, [confirmedPassengers]);

    const handleRatingChange = (passengerId: string, rating: number) => {
        setRatings(prev => ({ ...prev, [passengerId]: rating }));
    };

    const handleCommentChange = (passengerId: string, comment: string) => {
        setComments(prev => ({ ...prev, [passengerId]: comment }));
    };

    const handleSubmitReview = async (passenger: PassengerInfo) => {
        const rating = ratings[passenger.bookingId] || 0;
        const comment = comments[passenger.bookingId] || "";

        if (rating === 0) {
            showError("Veuillez sélectionner une note");
            return;
        }

        setSubmitting(passenger.bookingId);
        try {
            const result = await submitReviewAction({
                bookingId: passenger.bookingId,
                rating,
                comment
            });

            if (result.success) {
                showSuccess("Avis envoyé avec succès !");
                setReviewedPassengers(prev => new Set(prev).add(passenger.bookingId));
            } else {
                // Check if it's a duplicate review error
                if (result.error?.includes("déjà") || result.error?.includes("already")) {
                    setReviewedPassengers(prev => new Set(prev).add(passenger.bookingId));
                } else {
                    showError(result.error || "Une erreur est survenue");
                }
            }
        } catch {
            showError("Erreur de connexion");
        } finally {
            setSubmitting(null);
        }
    };

    return (
        <div className="p-6 md:p-8 space-y-8 animate-in fade-in duration-500">
            {/* Celebration Header */}
            <div className="text-center space-y-4 py-8">
                <div className="w-20 h-20 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-4 shadow-sm animate-bounce">
                    <CheckCircle className="w-10 h-10" />
                </div>
                <h1 className="text-3xl font-bold text-slate-900">Trajet terminé !</h1>
                <p className="text-slate-600 max-w-md mx-auto">
                    Bravo pour ce covoiturage. Voici le récapitulatif de votre trajet.
                </p>
            </div>

            {/* Summary Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm flex items-center gap-4">
                    <div className="w-12 h-12 bg-amber-100 text-amber-600 rounded-xl flex items-center justify-center">
                        <Coins className="w-6 h-6" />
                    </div>
                    <div>
                        <p className="text-sm text-slate-500 font-medium">Gain total estimé</p>
                        <p className="text-2xl font-bold text-slate-900">{totalEarnings} TND</p>
                    </div>
                </div>

                <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm flex items-center gap-4">
                    <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-xl flex items-center justify-center">
                        <Users className="w-6 h-6" />
                    </div>
                    <div>
                        <p className="text-sm text-slate-500 font-medium">Passagers transportés</p>
                        <p className="text-2xl font-bold text-slate-900">{confirmedPassengers.length}</p>
                    </div>
                </div>
            </div>

            {/* Passenger Reviews Section */}
            {confirmedPassengers.length > 0 && (
                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    <div className="p-6 border-b border-slate-100 bg-slate-50">
                        <h2 className="font-bold text-slate-900">Notez vos passagers</h2>
                        <p className="text-sm text-slate-500">Aidez la communauté en laissant un avis</p>
                    </div>

                    <div className="divide-y divide-slate-100">
                        {confirmedPassengers.map(passenger => {
                            const isReviewed = reviewedPassengers.has(passenger.bookingId);
                            const rating = ratings[passenger.bookingId] || 0;
                            const comment = comments[passenger.bookingId] || "";
                            const isSubmitting = submitting === passenger.bookingId;

                            return (
                                <div key={passenger.bookingId} className="p-6 transition-colors hover:bg-slate-50/50">
                                    <div className="flex items-start gap-4">
                                        {/* Passenger Profile Picture with Link */}
                                        <Link
                                            href={`/users/${passenger.passengerId}`}
                                            className="w-12 h-12 rounded-full overflow-hidden bg-slate-100 shrink-0 hover:ring-2 hover:ring-[#006B8F] transition-all"
                                        >
                                            {passenger.passengerProfilePictureUrl ? (
                                                <Image
                                                    src={passenger.passengerProfilePictureUrl.replace("=s96-c", "=s100-c")}
                                                    alt={passenger.passengerName}
                                                    width={48}
                                                    height={48}
                                                    className="w-full h-full object-cover"
                                                />
                                            ) : (
                                                <div className="w-full h-full flex items-center justify-center text-slate-600 font-bold">
                                                    {passenger.passengerName.charAt(0)}
                                                </div>
                                            )}
                                        </Link>
                                        <div className="flex-1 space-y-4">
                                            <div>
                                                <Link
                                                    href={`/users/${passenger.passengerId}`}
                                                    className="font-bold text-slate-900 hover:text-[#006B8F] transition-colors"
                                                >
                                                    {passenger.passengerName}
                                                </Link>
                                                {isReviewed ? (
                                                    <span className="inline-flex items-center text-xs font-medium text-emerald-600 bg-emerald-50 px-2 py-1 rounded-full mt-1 ml-2">
                                                        <CheckCircle className="w-3 h-3 mr-1" />
                                                        Avis envoyé
                                                    </span>
                                                ) : (
                                                    <p className="text-sm text-slate-500">Comment s&apos;est passé le voyage ?</p>
                                                )}
                                            </div>

                                            {!isReviewed && (
                                                <div className="space-y-4 animate-in fade-in slide-in-from-top-2">
                                                    <StarRatingInput
                                                        rating={rating}
                                                        onChange={(r) => handleRatingChange(passenger.bookingId, r)}
                                                    />

                                                    <textarea
                                                        placeholder="Laissez un commentaire (optionnel)..."
                                                        className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 resize-none transition-all"
                                                        rows={2}
                                                        value={comment}
                                                        onChange={(e) => handleCommentChange(passenger.bookingId, e.target.value)}
                                                    />

                                                    <div className="flex justify-end">
                                                        <Button
                                                            onClick={() => handleSubmitReview(passenger)}
                                                            disabled={rating === 0 || isSubmitting}
                                                            size="sm"
                                                            className="bg-slate-900 text-white hover:bg-slate-800 rounded-lg"
                                                        >
                                                            {isSubmitting ? "Envoi..." : (
                                                                <>
                                                                    Envoyer <Send className="w-3 h-3 ml-2" />
                                                                </>
                                                            )}
                                                        </Button>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}

            {/* Final Actions */}
            <div className="flex flex-col sm:flex-row gap-4 pt-4">
                <Link href="/dashboard" className="flex-1">
                    <Button variant="outline" className="w-full h-14 text-slate-700 border-slate-200 hover:bg-slate-50 rounded-xl gap-2 font-medium">
                        <Home className="w-5 h-5" />
                        Retour à l&apos;accueil
                    </Button>
                </Link>
                <Link href="/publish" className="flex-1">
                    <Button className="w-full h-14 bg-[#006B8F] hover:bg-[#005673] text-white rounded-xl gap-2 font-bold shadow-lg shadow-blue-900/10">
                        <PlusCircle className="w-5 h-5" />
                        Publier un trajet
                    </Button>
                </Link>
            </div>
        </div>
    );
}
