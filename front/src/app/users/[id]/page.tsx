"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { ArrowLeft, Calendar, User, ShieldCheck, Star } from "lucide-react";
import { PublicProfile } from "@/types/models";
import Navbar from "@/components/layout/Navbar";
import { Button } from "@/components/ui/button";

export default function PublicProfilePage() {
    const params = useParams();
    const router = useRouter();
    const userId = params.id as string;

    const [profile, setProfile] = useState<PublicProfile | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/user/${userId}/public`);
                if (!response.ok) throw new Error("Profil introuvable");
                const data = await response.json();
                setProfile(data);
            } catch (err) {
                setError("Impossible de charger le profil");
            } finally {
                setIsLoading(false);
            }
        };

        if (userId) {
            fetchProfile();
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
                    <p className="text-slate-600 mb-6">Ce profil n'existe pas ou n'est plus disponible.</p>
                    <Link href="/dashboard">
                        <Button>Retour à l'accueil</Button>
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

    const getInitials = () => {
        return `${profile.firstName.charAt(0)}${profile.lastName.charAt(0)}`.toUpperCase();
    };

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
                    {/* Header Banner */}
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
                            {/* Future: Add 'Verified' badge here */}
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

                            {/* Stats */}
                            <div className="grid grid-cols-2 gap-4">
                                <div className="p-4 rounded-xl border border-slate-100 bg-white text-center">
                                    <div className="flex items-center justify-center gap-1 text-slate-900 font-bold text-xl mb-1">
                                        <Star className="w-5 h-5 text-amber-400 fill-amber-400" />
                                        <span>{profile.averageRating ? profile.averageRating.toFixed(1) : "--"}</span>
                                    </div>
                                    <p className="text-xs text-slate-500 uppercase font-medium">Note moyenne</p>
                                </div>
                                <div className="p-4 rounded-xl border border-slate-100 bg-white text-center">
                                    <div className="text-slate-900 font-bold text-xl mb-1">
                                        {profile.completedRidesCount}
                                    </div>
                                    <p className="text-xs text-slate-500 uppercase font-medium">Trajets effectués</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}
