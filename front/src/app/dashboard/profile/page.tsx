"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import {
    ArrowLeft,
    Phone,
    Calendar,
    User as UserIcon,
    Edit3,
    Save,
    X,
    Loader2,
    Facebook,
    Instagram,
    Settings,
    Star,
    LogOut,
    ChevronRight,
} from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { parseApiError } from "@/lib/api-utils";
import ProfilePictureUpload from "@/components/profile/ProfilePictureUpload";

interface ProfileFormData {
    firstName: string;
    lastName: string;
    phoneNumber: string;
    bio: string;
    facebookUrl: string;
    instagramUrl: string;
}

export default function ProfilePage() {
    const router = useRouter();
    const { user, isAuthenticated, isLoading: authLoading, refreshUser, logout } = useAuth();

    const [isEditing, setIsEditing] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const [formData, setFormData] = useState<ProfileFormData>({
        firstName: "",
        lastName: "",
        phoneNumber: "",
        bio: "",
        facebookUrl: "",
        instagramUrl: "",
    });


    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);


    useEffect(() => {
        if (user) {
            setFormData({
                firstName: user.firstName || "",
                lastName: user.lastName || "",
                phoneNumber: user.phoneNumber || "",
                bio: user.bio || "",
                facebookUrl: user.facebookUrl || "",
                instagramUrl: user.instagramUrl || "",
            });
        }
    }, [user]);

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleCancel = () => {
        if (user) {
            setFormData({
                firstName: user.firstName || "",
                lastName: user.lastName || "",
                phoneNumber: user.phoneNumber || "",
                bio: user.bio || "",
                facebookUrl: user.facebookUrl || "",
                instagramUrl: user.instagramUrl || "",
            });
        }
        setIsEditing(false);
        setError(null);
    };

    const handleSave = async () => {
        setError(null);
        setSuccess(null);
        setIsSaving(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me`,
                {
                    method: "PUT",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(formData),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de la mise à jour du profil");
                throw new Error(errorMessage);
            }


            await refreshUser();

            setSuccess("Profil mis à jour avec succès");
            setIsEditing(false);


            setTimeout(() => setSuccess(null), 3000);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsSaving(false);
        }
    };


    if (authLoading || !user) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="flex flex-col items-center gap-4">
                    <Loader2 className="w-10 h-10 text-[#0A8F8F] animate-spin" />
                    <span className="text-gray-600">Chargement du profil...</span>
                </div>
            </div>
        );
    }

    const getInitials = () => {
        const first = user.firstName?.charAt(0)?.toUpperCase() || "";
        const last = user.lastName?.charAt(0)?.toUpperCase() || "";
        return first + last || "?";
    };

    const formatDate = (dateStr: string | null) => {
        if (!dateStr) return "Non renseigné";
        const date = new Date(dateStr);
        return date.toLocaleDateString("fr-FR", {
            day: "numeric",
            month: "long",
            year: "numeric",
        });
    };

    const formatGender = (gender: string | null) => {
        if (!gender) return "Non renseigné";
        return gender === "MALE" ? "Homme" : "Femme";
    };

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-2xl mx-auto">
                <div className="flex items-center justify-between mb-6">
                    <Link
                        href="/dashboard"
                        className="flex items-center gap-2 text-gray-600 hover:text-[#0A8F8F] transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5" />
                        <span>Retour</span>
                    </Link>

                    {!isEditing ? (
                        <button
                            onClick={() => setIsEditing(true)}
                            className="flex items-center gap-2 px-4 py-2 bg-[#0A8F8F] text-white rounded-lg hover:bg-[#0A8F8F]/90 transition-colors"
                        >
                            <Edit3 className="w-4 h-4" />
                            <span>Modifier</span>
                        </button>
                    ) : (
                        <div className="flex items-center gap-2">
                            <button
                                onClick={handleCancel}
                                disabled={isSaving}
                                className="flex items-center gap-2 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition-colors disabled:opacity-50"
                            >
                                <X className="w-4 h-4" />
                                <span>Annuler</span>
                            </button>
                            <button
                                onClick={handleSave}
                                disabled={isSaving}
                                className="flex items-center gap-2 px-4 py-2 bg-[#0A8F8F] text-white rounded-lg hover:bg-[#0A8F8F]/90 transition-colors disabled:opacity-50"
                            >
                                {isSaving ? (
                                    <Loader2 className="w-4 h-4 animate-spin" />
                                ) : (
                                    <Save className="w-4 h-4" />
                                )}
                                <span>{isSaving ? "Enregistrement..." : "Enregistrer"}</span>
                            </button>
                        </div>
                    )}
                </div>

                {error && (
                    <div className="mb-4 p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg">
                        {error}
                    </div>
                )}
                {success && (
                    <div className="mb-4 p-4 bg-green-50 border border-green-200 text-green-700 rounded-lg">
                        {success}
                    </div>
                )}

                {/* Main Card */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="bg-gradient-to-r from-[#0A8F8F] to-[#0A7070] p-8 text-center">
                        <div className="flex justify-center mb-4">
                            <ProfilePictureUpload
                                currentImageUrl={user.profilePictureUrl || null}
                                initials={getInitials()}
                                onUploadSuccess={refreshUser}
                            />
                        </div>

                        {isEditing ? (
                            <div className="flex gap-3 justify-center max-w-md mx-auto">
                                <input
                                    type="text"
                                    name="firstName"
                                    value={formData.firstName}
                                    onChange={handleInputChange}
                                    placeholder="Prénom"
                                    className="flex-1 px-4 py-2 rounded-lg bg-white/20 backdrop-blur text-white placeholder-white/60 border border-white/30 focus:outline-none focus:ring-2 focus:ring-white/50"
                                />
                                <input
                                    type="text"
                                    name="lastName"
                                    value={formData.lastName}
                                    onChange={handleInputChange}
                                    placeholder="Nom"
                                    className="flex-1 px-4 py-2 rounded-lg bg-white/20 backdrop-blur text-white placeholder-white/60 border border-white/30 focus:outline-none focus:ring-2 focus:ring-white/50"
                                />
                            </div>
                        ) : (
                            <>
                                <h1 className="text-2xl font-bold text-white mb-1">
                                    {user.firstName} {user.lastName}
                                </h1>
                                <p className="text-white/80">{user.email}</p>
                            </>
                        )}
                    </div>

                    <div className="p-6 space-y-6">
                        <section>
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                                <Phone className="w-5 h-5 text-[#0A8F8F]" />
                                Informations personnelles
                            </h2>
                            <div className="space-y-4">
                                <div className="flex items-center justify-between py-3 border-b border-gray-100">
                                    <span className="text-gray-600">Téléphone</span>
                                    {isEditing ? (
                                        <input
                                            type="tel"
                                            name="phoneNumber"
                                            value={formData.phoneNumber}
                                            onChange={handleInputChange}
                                            placeholder="+216 XX XXX XXX"
                                            className="text-right px-3 py-1.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F]"
                                        />
                                    ) : (
                                        <span className="text-gray-900 font-medium">
                                            {user.phoneNumber || "Non renseigné"}
                                        </span>
                                    )}
                                </div>

                                <div className="flex items-center justify-between py-3 border-b border-gray-100">
                                    <span className="text-gray-600 flex items-center gap-2">
                                        <Calendar className="w-4 h-4" />
                                        Date de naissance
                                    </span>
                                    <span className="text-gray-900 font-medium">
                                        {formatDate(user.dob)}
                                    </span>
                                </div>

                                <div className="flex items-center justify-between py-3 border-b border-gray-100">
                                    <span className="text-gray-600 flex items-center gap-2">
                                        <UserIcon className="w-4 h-4" />
                                        Genre
                                    </span>
                                    <span className="text-gray-900 font-medium">
                                        {formatGender(user.gender)}
                                    </span>
                                </div>
                            </div>
                        </section>

                        <section>
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                                <Edit3 className="w-5 h-5 text-[#0A8F8F]" />
                                À propos
                            </h2>
                            {isEditing ? (
                                <textarea
                                    name="bio"
                                    value={formData.bio}
                                    onChange={handleInputChange}
                                    placeholder="Parlez un peu de vous..."
                                    rows={4}
                                    className="w-full px-4 py-3 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] resize-none"
                                />
                            ) : (
                                <p className="text-gray-700 bg-gray-50 p-4 rounded-lg">
                                    {user.bio || "Aucune description ajoutée"}
                                </p>
                            )}
                        </section>

                        {/* Social Links */}
                        <section>
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                                <Instagram className="w-5 h-5 text-[#0A8F8F]" />
                                Réseaux sociaux
                            </h2>
                            <div className="space-y-4">
                                <div className="flex items-center justify-between py-3 border-b border-gray-100">
                                    <span className="text-gray-600 flex items-center gap-2">
                                        <Facebook className="w-4 h-4 text-blue-600" />
                                        Facebook
                                    </span>
                                    {isEditing ? (
                                        <input
                                            type="url"
                                            name="facebookUrl"
                                            value={formData.facebookUrl}
                                            onChange={handleInputChange}
                                            placeholder="https://facebook.com/..."
                                            className="text-right px-3 py-1.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] w-64"
                                        />
                                    ) : user.facebookUrl ? (
                                        <a
                                            href={user.facebookUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-blue-600 hover:underline"
                                        >
                                            Voir le profil
                                        </a>
                                    ) : (
                                        <span className="text-gray-400">Non renseigné</span>
                                    )}
                                </div>

                                <div className="flex items-center justify-between py-3 border-b border-gray-100">
                                    <span className="text-gray-600 flex items-center gap-2">
                                        <Instagram className="w-4 h-4 text-pink-600" />
                                        Instagram
                                    </span>
                                    {isEditing ? (
                                        <input
                                            type="url"
                                            name="instagramUrl"
                                            value={formData.instagramUrl}
                                            onChange={handleInputChange}
                                            placeholder="https://instagram.com/..."
                                            className="text-right px-3 py-1.5 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] w-64"
                                        />
                                    ) : user.instagramUrl ? (
                                        <a
                                            href={user.instagramUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-pink-600 hover:underline"
                                        >
                                            Voir le profil
                                        </a>
                                    ) : (
                                        <span className="text-gray-400">Non renseigné</span>
                                    )}
                                </div>
                            </div>
                        </section>
                    </div>

                    <div className="border-t border-gray-100 p-6 bg-gray-50/50 space-y-8">

                        <div>
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                                <Star className="w-5 h-5 text-[#0A8F8F]" />
                                Mes activités
                            </h2>
                            <div className="space-y-3">
                                <Link
                                    href="/dashboard/reviews"
                                    className="flex items-center justify-between p-4 bg-white border border-gray-200 rounded-xl hover:border-[#0A8F8F]/50 hover:shadow-sm transition-all group"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-amber-50 rounded-full flex items-center justify-center text-amber-600 group-hover:bg-amber-100 transition-colors">
                                            <Star className="w-5 h-5" />
                                        </div>
                                        <span className="font-medium text-gray-700 group-hover:text-gray-900">Mes avis</span>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-gray-400 group-hover:text-gray-600" />
                                </Link>
                            </div>
                        </div>

                        <div>
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                                <Settings className="w-5 h-5 text-[#0A8F8F]" />
                                Paramètres du compte
                            </h2>
                            <div className="space-y-3">
                                <Link
                                    href="/dashboard/settings"
                                    className="flex items-center justify-between p-4 bg-white border border-gray-200 rounded-xl hover:border-[#0A8F8F]/50 hover:shadow-sm transition-all group"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-slate-50 rounded-full flex items-center justify-center text-slate-600 group-hover:bg-slate-100 transition-colors">
                                            <Settings className="w-5 h-5" />
                                        </div>
                                        <span className="font-medium text-gray-700 group-hover:text-gray-900">Paramètres</span>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-gray-400 group-hover:text-gray-600" />
                                </Link>

                                <button
                                    onClick={() => {
                                        if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
                                            logout();
                                            router.push("/login"); // or home
                                        }
                                    }}
                                    className="w-full flex items-center justify-between p-4 bg-white border border-gray-200 rounded-xl hover:bg-red-50 hover:border-red-200 transition-all group mt-4 text-red-600"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-red-50 rounded-full flex items-center justify-center text-red-600 group-hover:bg-red-100 transition-colors">
                                            <LogOut className="w-5 h-5" />
                                        </div>
                                        <span className="font-medium">Déconnexion</span>
                                    </div>
                                    <ChevronRight className="w-5 h-5 text-red-300 group-hover:text-red-500" />
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
