"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    Star,
    ArrowLeft,
    Loader2,
    AlertCircle,
    MessageSquare,
    User,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import { cn } from "@/lib/utils";

interface Review {
    id: string;
    bookingId: string;
    reviewerName: string;
    revieweeName: string;
    rating: number;
    comment: string | null;
    createdAt: string;
}

interface PagedResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
}

type TabType = "received" | "sent";

function ReviewCard({ review, type }: { review: Review; type: TabType }) {
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            day: "numeric",
            month: "short",
            year: "numeric",
        });
    };

    const personName = type === "received" ? review.reviewerName : review.revieweeName;
    const personLabel = type === "received" ? "De" : "Pour";

    return (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-5 hover:shadow-md transition-shadow">
            <div className="flex items-start gap-4">
                {/* Avatar */}
                <div className="w-12 h-12 bg-[#006B8F]/10 rounded-full flex items-center justify-center text-[#006B8F] font-bold text-lg">
                    {personName.charAt(0).toUpperCase()}
                </div>

                <div className="flex-1 min-w-0">
                    {/* Header */}
                    <div className="flex items-center justify-between mb-2">
                        <div>
                            <p className="text-sm text-slate-500">{personLabel}</p>
                            <p className="font-semibold text-slate-900">{personName}</p>
                        </div>
                        <div className="text-right">
                            <div className="flex items-center gap-0.5">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <Star
                                        key={star}
                                        className={cn(
                                            "w-4 h-4",
                                            star <= review.rating
                                                ? "text-yellow-400 fill-yellow-400"
                                                : "text-slate-200"
                                        )}
                                    />
                                ))}
                            </div>
                            <p className="text-xs text-slate-400 mt-1">
                                {formatDate(review.createdAt)}
                            </p>
                        </div>
                    </div>

                    {/* Comment */}
                    {review.comment ? (
                        <p className="text-slate-600 text-sm mt-3 bg-slate-50 rounded-lg p-3">
                            &quot;{review.comment}&quot;
                        </p>
                    ) : (
                        <p className="text-slate-400 text-sm italic mt-3">
                            Aucun commentaire
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default function ReviewsPage() {
    const router = useRouter();
    const { isAuthenticated, isLoading: authLoading } = useAuth();

    const [activeTab, setActiveTab] = useState<TabType>("received");
    const [receivedReviews, setReceivedReviews] = useState<Review[]>([]);
    const [sentReviews, setSentReviews] = useState<Review[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [stats, setStats] = useState({ averageRating: 0, totalReviews: 0 });

    // Redirect if not authenticated
    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const fetchReviews = useCallback(async () => {
        if (!isAuthenticated) return;

        setIsLoading(true);
        setError("");

        try {
            // Fetch received reviews
            const receivedResponse = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/reviews/mine/received?page=0&size=50`,
                {
                    credentials: "include", // Send cookies
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            if (receivedResponse.ok) {
                const data: PagedResponse<Review> = await receivedResponse.json();
                setReceivedReviews(data.content);

                // Calculate stats
                if (data.content.length > 0) {
                    const avg = data.content.reduce((sum, r) => sum + r.rating, 0) / data.content.length;
                    setStats({
                        averageRating: Math.round(avg * 10) / 10,
                        totalReviews: data.totalElements,
                    });
                }
            }

            // Fetch sent reviews
            const sentResponse = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/reviews/mine/sent?page=0&size=50`,
                {
                    credentials: "include", // Send cookies
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            if (sentResponse.ok) {
                const data: PagedResponse<Review> = await sentResponse.json();
                setSentReviews(data.content);
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : "Erreur lors du chargement");
        } finally {
            setIsLoading(false);
        }
    }, [isAuthenticated]);

    useEffect(() => {
        if (isAuthenticated) {
            fetchReviews();
        }
    }, [isAuthenticated, fetchReviews]);

    const currentReviews = activeTab === "received" ? receivedReviews : sentReviews;

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

    return (
        <div className="min-h-screen bg-[#F8FAFC] pb-20 lg:pb-8">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
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
                                Mes Avis
                            </h1>
                            <p className="text-slate-500 text-sm">
                                Consultez vos avis reçus et envoyés
                            </p>
                        </div>
                    </div>

                    {/* Stats Card */}
                    {stats.totalReviews > 0 && (
                        <div className="flex items-center gap-3 bg-white border border-slate-100 rounded-xl px-4 py-3 shadow-sm">
                            <div className="flex items-center gap-1">
                                <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                                <span className="text-lg font-bold text-slate-900">
                                    {stats.averageRating}
                                </span>
                            </div>
                            <div className="h-8 w-px bg-slate-200"></div>
                            <div className="text-sm text-slate-500">
                                {stats.totalReviews} avis reçu{stats.totalReviews > 1 ? "s" : ""}
                            </div>
                        </div>
                    )}
                </div>

                {/* Tabs */}
                <div className="flex gap-2 mb-6">
                    <button
                        onClick={() => setActiveTab("received")}
                        className={cn(
                            "flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-medium transition-all",
                            activeTab === "received"
                                ? "bg-[#006B8F] text-white"
                                : "bg-white text-slate-600 hover:bg-slate-50 border border-slate-200"
                        )}
                    >
                        <MessageSquare className="w-4 h-4" />
                        Reçus ({receivedReviews.length})
                    </button>
                    <button
                        onClick={() => setActiveTab("sent")}
                        className={cn(
                            "flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-medium transition-all",
                            activeTab === "sent"
                                ? "bg-[#006B8F] text-white"
                                : "bg-white text-slate-600 hover:bg-slate-50 border border-slate-200"
                        )}
                    >
                        <User className="w-4 h-4" />
                        Envoyés ({sentReviews.length})
                    </button>
                </div>

                {/* Error State */}
                {error && (
                    <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 flex items-center gap-3 mb-6">
                        <AlertCircle className="w-5 h-5 shrink-0" />
                        <span>{error}</span>
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={fetchReviews}
                            className="ml-auto"
                        >
                            Réessayer
                        </Button>
                    </div>
                )}

                {/* Reviews List */}
                {currentReviews.length > 0 ? (
                    <div className="space-y-4">
                        {currentReviews.map((review) => (
                            <ReviewCard key={review.id} review={review} type={activeTab} />
                        ))}
                    </div>
                ) : (
                    // Empty State
                    <div className="bg-white rounded-2xl border border-slate-100 p-12 text-center">
                        <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Star className="w-8 h-8 text-slate-400" />
                        </div>
                        <h3 className="text-lg font-medium text-slate-900 mb-2">
                            {activeTab === "received"
                                ? "Aucun avis reçu"
                                : "Aucun avis envoyé"}
                        </h3>
                        <p className="text-slate-500 mb-6">
                            {activeTab === "received"
                                ? "Les avis que vous recevez apparaîtront ici."
                                : "Les avis que vous laissez apparaîtront ici."}
                        </p>
                        <Link href="/dashboard/bookings">
                            <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673]">
                                Voir mes réservations
                            </Button>
                        </Link>
                    </div>
                )}
            </main>
        </div>
    );
}
