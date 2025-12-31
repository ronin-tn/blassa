"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { ArrowLeft, Calendar, User, ShieldCheck, Star, Car, UserCircle } from "lucide-react";
import { PublicProfile, ReviewResponse, PagedResponse } from "@/types/models";
import Navbar from "@/components/layout/Navbar";
import { Button } from "@/components/ui/button";

export default function PublicProfilePage() {
    const params = useParams();
    const router = useRouter();
    const userId = params.id as string;

    const [profile, setProfile] = useState<PublicProfile | null>(null);
    const [reviews, setReviews] = useState<ReviewResponse[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const profileRes = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/user/${userId}/public`);
                if (!profileRes.ok) throw new Error("Profil introuvable");
                const profileData = await profileRes.json();
                setProfile(profileData);

                try {
                    const reviewsRes = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/reviews/user/${userId}?size=10`);
                    if (reviewsRes.ok) {
                        const reviewsData: PagedResponse<ReviewResponse> = await reviewsRes.json();
                        setReviews(reviewsData.content);
                    }
                } catch {
                }
            } catch {
                setError("Impossible de charger le profil");
            } finally {
                setIsLoading(false);
            }
        };

        if (userId) {
            fetchData();
        }
    }, [userId]);

    if (isLoading) {
        return (
            <div className="min-h-screen bg-slate-50">
                <Navbar />
                <div className="flex items-center justify-center h-[calc(100vh-64px)]">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-slate-900"></div>
                </div>
            </div>
        );
    }

    if (error || !profile) {
        return (
            <div className="min-h-screen bg-slate-50">
                <Navbar />
                <div className="max-w-md mx-auto mt-20 p-6 text-center">
                    <h2 className="text-xl font-bold text-slate-900 mb-2">Profil introuvable</h2>
                    <p className="text-slate-600 mb-6">Ce profil n&apos;existe pas ou n&apos;est plus disponible.</p>
                    <Link href="/dashboard">
                        <Button>Retour à l&apos;accueil</Button>
                    </Link>
                </div>
            </div>
        );
    }

    const formatDate = (dateStr: string) => {
        return new Date(dateStr).toLocaleDateString("fr-FR", {
            month: "long",
            year: "numeric"
        });
    };

    const formatReviewDate = (dateStr: string) => {
        return new Date(dateStr).toLocaleDateString("fr-FR", {
            day: "numeric",
            month: "short",
            year: "numeric"
        });
    };

    const getInitials = () => {
        return `${profile.firstName.charAt(0)}${profile.lastName.charAt(0)}`.toUpperCase();
    };

    const getDriverExperienceBadge = () => {
        const ridesCount = profile.completedRidesCount ?? 0;
        if (ridesCount < 5) {
            return {
                label: "Nouveau conducteur",
                color: "bg-blue-100 text-blue-700"
            };
        }
        return {
            label: "Conducteur expérimenté",
            color: "bg-emerald-100 text-emerald-700"
        };
    };

    const experienceBadge = getDriverExperienceBadge();

    return (
        <div className="min-h-screen bg-slate-50 pb-20">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-2xl mx-auto px-4 py-8">
                <button
                    onClick={() => router.back()}
                    className="flex items-center gap-2 text-slate-600 hover:text-slate-900 mb-6 transition-colors"
                >
                    <ArrowLeft className="w-5 h-5" />
                    <span>Retour</span>
                </button>

                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    <div className="h-32 bg-gradient-to-r from-[#006B8F] to-[#004e69]"></div>

                    <div className="px-8 pb-8">
                        <div className="relative flex justify-between items-end -mt-12 mb-6">
                            <div className="rounded-full p-1.5 bg-white shadow-sm">
                                {profile.profilePictureUrl ? (
                                    <Image
                                        src={profile.profilePictureUrl.replace("=s96-c", "=s400-c")}
                                        alt={profile.firstName}
                                        width={100}
                                        height={100}
                                        className="rounded-full w-24 h-24 object-cover border border-slate-100"
                                    />
                                ) : (
                                    <div className="w-24 h-24 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 text-2xl font-bold border border-slate-200">
                                        {getInitials()}
                                    </div>
                                )}
                            </div>
                            <span className={`px-3 py-1.5 rounded-full text-sm font-medium ${experienceBadge.color}`}>
                                {experienceBadge.label}
                            </span>
                        </div>

                        <div className="space-y-6">
                            <div>
                                <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
                                    {profile.firstName} {profile.lastName}
                                    <ShieldCheck className="w-5 h-5 text-emerald-500" />
                                </h1>
                                <p className="text-slate-500 flex items-center gap-2 text-sm mt-1">
                                    <Calendar className="w-4 h-4" />
                                    Membre depuis {formatDate(profile.memberSince)}
                                    <span className="text-slate-300">•</span>
                                    {profile.gender === 'MALE' ? 'Homme' : 'Femme'}
                                </p>
                            </div>

                            <div className="p-4 bg-slate-50 rounded-xl border border-slate-100">
                                <h2 className="font-semibold text-slate-900 mb-2 text-sm uppercase tracking-wide">À propos</h2>
                                <p className="text-slate-600 text-sm leading-relaxed">
                                    {profile.bio || "Aucune description renseignée."}
                                </p>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="p-4 rounded-xl border border-slate-100 bg-white text-center">
                                    <div className="flex items-center justify-center gap-1 text-slate-900 font-bold text-xl mb-1">
                                        <Star className="w-5 h-5 text-amber-400 fill-amber-400" />
                                        <span>{profile.averageRating ? profile.averageRating.toFixed(1) : "--"}</span>
                                    </div>
                                    <p className="text-xs text-slate-500 uppercase font-medium">Note moyenne</p>
                                </div>
                                <div className="p-4 rounded-xl border border-slate-100 bg-white text-center">
                                    <div className="flex items-center justify-center gap-1 text-slate-900 font-bold text-xl mb-1">
                                        <Car className="w-5 h-5 text-[#006B8F]" />
                                        <span>{profile.completedRidesCount}</span>
                                    </div>
                                    <p className="text-xs text-slate-500 uppercase font-medium">Trajets effectués</p>
                                </div>
                            </div>

                            <div className="border-t border-slate-100 pt-6">
                                <h2 className="font-semibold text-slate-900 mb-4 flex items-center gap-2">
                                    <Star className="w-5 h-5 text-amber-400" />
                                    Avis ({reviews.length})
                                </h2>

                                {reviews.length === 0 ? (
                                    <div className="text-center py-8 text-slate-500 bg-slate-50 rounded-xl border border-slate-100">
                                        <UserCircle className="w-10 h-10 mx-auto mb-2 text-slate-400" />
                                        <p>Aucun avis pour le moment</p>
                                    </div>
                                ) : (
                                    <div className="space-y-4">
                                        {reviews.map((review) => (
                                            <div key={review.id} className="p-4 bg-slate-50 rounded-xl border border-slate-100">
                                                <div className="flex items-center justify-between mb-2">
                                                    <div className="flex items-center gap-3">
                                                        <div className="w-10 h-10 rounded-full bg-[#006B8F]/10 flex items-center justify-center">
                                                            <User className="w-5 h-5 text-[#006B8F]" />
                                                        </div>
                                                        <div>
                                                            <p className="font-medium text-slate-900">{review.reviewerName}</p>
                                                            <p className="text-xs text-slate-500">{formatReviewDate(review.createdAt)}</p>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-0.5">
                                                        {[1, 2, 3, 4, 5].map((star) => (
                                                            <Star
                                                                key={star}
                                                                className={`w-4 h-4 ${star <= review.rating
                                                                    ? "text-amber-400 fill-amber-400"
                                                                    : "text-slate-200"
                                                                    }`}
                                                            />
                                                        ))}
                                                    </div>
                                                </div>
                                                {review.comment && (
                                                    <p className="text-slate-600 text-sm mt-2">{review.comment}</p>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}
