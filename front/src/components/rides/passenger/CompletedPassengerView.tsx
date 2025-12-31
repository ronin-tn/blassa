"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { Ride, PassengerInfo } from "@/types/models";
import { Button } from "@/components/ui/button";
import { CheckCircle, Home, Send, ShieldCheck, MapPin } from "lucide-react";
import StarRatingInput from "@/components/ui/StarRatingInput";
import { submitReviewAction } from "@/app/actions/reviews";
import { useToast } from "@/contexts/ToastContext";

interface CompletedPassengerViewProps {
    ride: Ride;
    currentPassenger: PassengerInfo;
}

export default function CompletedPassengerView({ ride, currentPassenger }: CompletedPassengerViewProps) {
    const { showError, showSuccess } = useToast();

    // Manage review state locally
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [hasExistingReview, setHasExistingReview] = useState(false);

    // Check if a review was already submitted for this booking
    useEffect(() => {
        const checkExistingReview = async () => {
            try {
                // Check if review already exists via the sent reviews endpoint
                const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/reviews/mine/sent?size=100`, {
                    credentials: 'include'
                });
                if (response.ok) {
                    const data = await response.json();
                    const existingReview = data.content?.find(
                        (review: { bookingId: string }) => review.bookingId === currentPassenger.bookingId
                    );
                    if (existingReview) {
                        setHasExistingReview(true);
                        setIsSubmitted(true);
                    }
                }
            } catch {
                // Silently fail - we'll let the submit action catch duplicates
            }
        };
        checkExistingReview();
    }, [currentPassenger.bookingId]);

    const handleSubmitReview = async () => {
        if (rating === 0) {
            showError("Veuillez sélectionner une note");
            return;
        }

        setIsSubmitting(true);
        try {
            const result = await submitReviewAction({
                bookingId: currentPassenger.bookingId,
                rating,
                comment
            });

            if (result.success) {
                showSuccess("Votre avis a été envoyé !");
                setIsSubmitted(true);
            } else {
                // Check if it's a duplicate review error
                if (result.error?.includes("déjà") || result.error?.includes("already")) {
                    setIsSubmitted(true);
                    setHasExistingReview(true);
                } else {
                    showError(result.error || "Une erreur est survenue");
                }
            }
        } catch {
            showError("Erreur de connexion");
        } finally {
            setIsSubmitting(false);
        }
    };

    const reviewAlreadyDone = isSubmitted || hasExistingReview;

    return (
        <div className="p-6 md:p-8 space-y-8 animate-in fade-in duration-500">
            {/* Celebration Header */}
            <div className="text-center space-y-4 py-8">
                <div className="w-20 h-20 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-4 shadow-sm animate-bounce">
                    <MapPin className="w-10 h-10" />
                </div>
                <h1 className="text-3xl font-bold text-slate-900">Vous êtes bien arrivé !</h1>
                <p className="text-slate-600 max-w-md mx-auto">
                    Merci d&apos;avoir voyagé avec Blassa. Nous espérons que votre trajet vers <span className="font-semibold text-slate-900">{ride.destinationName}</span> s&apos;est bien passé.
                </p>
            </div>

            {/* Driver Review Card */}
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden max-w-lg mx-auto">
                <div className="p-6 border-b border-slate-100 bg-slate-50 flex items-center justify-between">
                    <div>
                        <h2 className="font-bold text-slate-900">Notez votre conducteur</h2>
                        <p className="text-sm text-slate-500">Votre avis compte pour la communauté</p>
                    </div>
                </div>

                <div className="p-8">
                    <Link
                        href={`/users/${ride.driverId}`}
                        className="flex flex-col items-center gap-4 mb-8 group"
                    >
                        {/* Driver Profile Picture */}
                        <div className="w-20 h-20 rounded-full overflow-hidden bg-[#006B8F] shadow-md group-hover:ring-2 group-hover:ring-[#006B8F] transition-all">
                            {ride.driverProfilePictureUrl ? (
                                <Image
                                    src={ride.driverProfilePictureUrl.replace("=s96-c", "=s200-c")}
                                    alt={ride.driverName}
                                    width={80}
                                    height={80}
                                    className="w-full h-full object-cover"
                                />
                            ) : (
                                <div className="w-full h-full flex items-center justify-center text-white text-2xl font-bold">
                                    {ride.driverName.charAt(0)}
                                </div>
                            )}
                        </div>
                        <div className="text-center">
                            <h3 className="text-xl font-bold text-slate-900 group-hover:text-[#006B8F] transition-colors">
                                {ride.driverName}
                            </h3>
                            <div className="flex items-center justify-center gap-1.5 text-emerald-600 mt-1">
                                <ShieldCheck className="w-4 h-4" />
                                <span className="text-xs font-semibold uppercase tracking-wide">Conducteur</span>
                            </div>
                        </div>
                    </Link>

                    {reviewAlreadyDone ? (
                        <div className="text-center py-8 space-y-2 bg-emerald-50 rounded-xl border border-emerald-100">
                            <div className="inline-flex items-center justify-center w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 mb-2">
                                <CheckCircle className="w-6 h-6" />
                            </div>
                            <h3 className="text-lg font-bold text-emerald-900">
                                {hasExistingReview ? "Avis déjà envoyé" : "Merci pour votre avis !"}
                            </h3>
                            <p className="text-emerald-700 text-sm">Votre retour a bien été pris en compte.</p>
                        </div>
                    ) : (
                        <div className="space-y-6">
                            <div className="flex justify-center">
                                <StarRatingInput
                                    rating={rating}
                                    onChange={setRating}
                                    size="lg"
                                />
                            </div>

                            <textarea
                                placeholder={`Un commentaire sur la conduite de ${ride.driverName.split(' ')[0]} ? (optionnel)`}
                                className="w-full p-4 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#006B8F] resize-none transition-all placeholder:text-slate-400"
                                rows={3}
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                            />

                            <Button
                                onClick={handleSubmitReview}
                                disabled={rating === 0 || isSubmitting}
                                className="w-full bg-slate-900 hover:bg-slate-800 text-white rounded-xl h-12 text-base shadow-lg shadow-slate-900/10"
                            >
                                {isSubmitting ? "Envoi en cours..." : (
                                    <>
                                        Envoyer mon avis <Send className="w-4 h-4 ml-2" />
                                    </>
                                )}
                            </Button>
                        </div>
                    )}
                </div>
            </div>

            {/* Navigation Actions */}
            <div className="flex justify-center pt-4">
                <Link href="/dashboard" className="w-full max-w-xs">
                    <Button variant="outline" className="w-full h-12 text-slate-600 border-slate-200 hover:bg-slate-50 rounded-xl gap-2">
                        <Home className="w-4 h-4" />
                        Retour à l&apos;accueil
                    </Button>
                </Link>
            </div>
        </div>
    );
}
