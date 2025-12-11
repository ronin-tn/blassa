"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import {
    ArrowLeft,
    Bell,
    Shield,
    Globe,
    Moon,
    LogOut,
    Trash2,
    ChevronRight,
    Loader2,
    Lock,
    Mail,
    Smartphone,
} from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

interface SettingToggle {
    id: string;
    label: string;
    description: string;
    enabled: boolean;
}

export default function SettingsPage() {
    const router = useRouter();
    const { isAuthenticated, isLoading: authLoading, logout } = useAuth();

    // Local state for toggles (would normally sync with backend)
    const [notifications, setNotifications] = useState<SettingToggle[]>([
        {
            id: "email_bookings",
            label: "Réservations par email",
            description: "Recevoir les confirmations de réservation par email",
            enabled: true,
        },
        {
            id: "email_reminders",
            label: "Rappels de trajet",
            description: "Recevoir un rappel avant le départ",
            enabled: true,
        },
        {
            id: "email_promotions",
            label: "Actualités et promotions",
            description: "Recevoir des offres et nouveautés Blassa",
            enabled: false,
        },
    ]);

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);

    // Redirect if not authenticated
    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const handleToggle = (id: string) => {
        setNotifications((prev) =>
            prev.map((n) => (n.id === id ? { ...n, enabled: !n.enabled } : n))
        );
        // TODO: Sync with backend when endpoint is available
    };

    const handleLogout = () => {
        logout();
        router.replace("/");
    };

    const handleDeleteAccount = async () => {
        setIsDeleting(true);
        // TODO: Implement account deletion when backend endpoint is available
        // For now, just simulate and show message
        setTimeout(() => {
            setIsDeleting(false);
            setShowDeleteModal(false);
            alert("Cette fonctionnalité sera bientôt disponible.");
        }, 1000);
    };

    // Loading state
    if (authLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="flex flex-col items-center gap-4">
                    <Loader2 className="w-10 h-10 text-[#0A8F8F] animate-spin" />
                    <span className="text-gray-600">Chargement...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4">
            <div className="max-w-2xl mx-auto">
                {/* Header */}
                <div className="flex items-center gap-4 mb-6">
                    <Link
                        href="/dashboard"
                        className="flex items-center gap-2 text-gray-600 hover:text-[#0A8F8F] transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5" />
                        <span>Retour</span>
                    </Link>
                    <h1 className="text-2xl font-bold text-gray-900">Paramètres</h1>
                </div>

                <div className="space-y-6">
                    {/* Notifications Section */}
                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="px-6 py-4 border-b border-gray-100">
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900">
                                <Bell className="w-5 h-5 text-[#0A8F8F]" />
                                Notifications
                            </h2>
                        </div>
                        <div className="divide-y divide-gray-100">
                            {notifications.map((notification) => (
                                <div
                                    key={notification.id}
                                    className="flex items-center justify-between px-6 py-4"
                                >
                                    <div className="flex-1 pr-4">
                                        <p className="font-medium text-gray-900">
                                            {notification.label}
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            {notification.description}
                                        </p>
                                    </div>
                                    <button
                                        onClick={() => handleToggle(notification.id)}
                                        className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${notification.enabled ? "bg-[#0A8F8F]" : "bg-gray-200"
                                            }`}
                                    >
                                        <span
                                            className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${notification.enabled ? "translate-x-6" : "translate-x-1"
                                                }`}
                                        />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </section>

                    {/* Security Section */}
                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="px-6 py-4 border-b border-gray-100">
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900">
                                <Shield className="w-5 h-5 text-[#0A8F8F]" />
                                Sécurité
                            </h2>
                        </div>
                        <div className="divide-y divide-gray-100">
                            <Link
                                href="/forgot-password"
                                className="flex items-center justify-between px-6 py-4 hover:bg-gray-50 transition-colors"
                            >
                                <div className="flex items-center gap-3">
                                    <Lock className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">
                                            Changer le mot de passe
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            Modifier votre mot de passe actuel
                                        </p>
                                    </div>
                                </div>
                                <ChevronRight className="w-5 h-5 text-gray-400" />
                            </Link>

                            <div className="flex items-center justify-between px-6 py-4">
                                <div className="flex items-center gap-3">
                                    <Mail className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">
                                            Vérification email
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            Votre email est vérifié
                                        </p>
                                    </div>
                                </div>
                                <span className="px-2.5 py-1 text-xs font-medium text-green-700 bg-green-100 rounded-full">
                                    Vérifié
                                </span>
                            </div>

                            <div className="flex items-center justify-between px-6 py-4">
                                <div className="flex items-center gap-3">
                                    <Smartphone className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">
                                            Vérification téléphone
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            Ajoutez une vérification supplémentaire
                                        </p>
                                    </div>
                                </div>
                                <span className="px-2.5 py-1 text-xs font-medium text-amber-700 bg-amber-100 rounded-full">
                                    Non vérifié
                                </span>
                            </div>
                        </div>
                    </section>

                    {/* Preferences Section */}
                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="px-6 py-4 border-b border-gray-100">
                            <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900">
                                <Globe className="w-5 h-5 text-[#0A8F8F]" />
                                Préférences
                            </h2>
                        </div>
                        <div className="divide-y divide-gray-100">
                            <div className="flex items-center justify-between px-6 py-4">
                                <div className="flex items-center gap-3">
                                    <Globe className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">Langue</p>
                                        <p className="text-sm text-gray-500">
                                            Choisir la langue de l&apos;application
                                        </p>
                                    </div>
                                </div>
                                <span className="text-gray-600 font-medium">Français</span>
                            </div>

                            <div className="flex items-center justify-between px-6 py-4">
                                <div className="flex items-center gap-3">
                                    <Moon className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">Mode sombre</p>
                                        <p className="text-sm text-gray-500">
                                            Bientôt disponible
                                        </p>
                                    </div>
                                </div>
                                <span className="px-2.5 py-1 text-xs font-medium text-gray-500 bg-gray-100 rounded-full">
                                    Bientôt
                                </span>
                            </div>
                        </div>
                    </section>

                    {/* Account Actions */}
                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="divide-y divide-gray-100">
                            <button
                                onClick={handleLogout}
                                className="w-full flex items-center gap-3 px-6 py-4 hover:bg-gray-50 transition-colors text-left"
                            >
                                <LogOut className="w-5 h-5 text-gray-400" />
                                <span className="font-medium text-gray-900">Se déconnecter</span>
                            </button>

                            <button
                                onClick={() => setShowDeleteModal(true)}
                                className="w-full flex items-center gap-3 px-6 py-4 hover:bg-red-50 transition-colors text-left"
                            >
                                <Trash2 className="w-5 h-5 text-red-500" />
                                <span className="font-medium text-red-600">
                                    Supprimer mon compte
                                </span>
                            </button>
                        </div>
                    </section>

                    {/* App Info */}
                    <div className="text-center text-sm text-gray-400 py-4">
                        <p>Blassa v1.0.0</p>
                        <p className="mt-1">© 2024 Blassa. Tous droits réservés.</p>
                    </div>
                </div>
            </div>

            {/* Delete Account Modal */}
            {showDeleteModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl">
                        <div className="text-center mb-6">
                            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-red-100 flex items-center justify-center">
                                <Trash2 className="w-8 h-8 text-red-600" />
                            </div>
                            <h3 className="text-xl font-bold text-gray-900 mb-2">
                                Supprimer votre compte ?
                            </h3>
                            <p className="text-gray-600">
                                Cette action est irréversible. Toutes vos données seront
                                définitivement supprimées.
                            </p>
                        </div>

                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                disabled={isDeleting}
                                className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50"
                            >
                                Annuler
                            </button>
                            <button
                                onClick={handleDeleteAccount}
                                disabled={isDeleting}
                                className="flex-1 px-4 py-3 bg-red-600 text-white font-medium rounded-xl hover:bg-red-700 transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                            >
                                {isDeleting ? (
                                    <>
                                        <Loader2 className="w-4 h-4 animate-spin" />
                                        Suppression...
                                    </>
                                ) : (
                                    "Supprimer"
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
