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
    Eye,
    EyeOff,
    X,
} from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { parseApiError } from "@/lib/api-utils";

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
    const [deleteError, setDeleteError] = useState<string | null>(null);

    // Password change modal state
    const [showPasswordModal, setShowPasswordModal] = useState(false);
    const [isChangingPassword, setIsChangingPassword] = useState(false);
    const [passwordError, setPasswordError] = useState<string | null>(null);
    const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
    const [showCurrentPassword, setShowCurrentPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [passwordForm, setPasswordForm] = useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    });

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

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setPasswordForm((prev) => ({ ...prev, [name]: value }));
        setPasswordError(null);
    };

    const handleChangePassword = async () => {
        setPasswordError(null);
        setPasswordSuccess(null);

        // Client-side validation
        if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
            setPasswordError("Tous les champs sont requis");
            return;
        }

        if (passwordForm.newPassword.length < 8) {
            setPasswordError("Le nouveau mot de passe doit contenir au moins 8 caractères");
            return;
        }

        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            setPasswordError("Les mots de passe ne correspondent pas");
            return;
        }

        setIsChangingPassword(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me/password`,
                {
                    method: "PUT",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(passwordForm),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors du changement de mot de passe");
                throw new Error(errorMessage);
            }

            setPasswordSuccess("Mot de passe modifié avec succès");
            setPasswordForm({ currentPassword: "", newPassword: "", confirmPassword: "" });

            // Close modal after 2 seconds
            setTimeout(() => {
                setShowPasswordModal(false);
                setPasswordSuccess(null);
            }, 2000);
        } catch (err) {
            setPasswordError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsChangingPassword(false);
        }
    };

    const handleDeleteAccount = async () => {
        setIsDeleting(true);
        setDeleteError(null);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me`,
                {
                    method: "DELETE",
                    credentials: "include",
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de la suppression du compte");
                throw new Error(errorMessage);
            }

            // Logout and redirect
            logout();
            router.replace("/");
        } catch (err) {
            setDeleteError(err instanceof Error ? err.message : "Erreur inconnue");
            setIsDeleting(false);
        }
    };

    const resetPasswordModal = () => {
        setShowPasswordModal(false);
        setPasswordForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
        setPasswordError(null);
        setPasswordSuccess(null);
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
                            <button
                                onClick={() => setShowPasswordModal(true)}
                                className="w-full flex items-center justify-between px-6 py-4 hover:bg-gray-50 transition-colors text-left"
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
                            </button>

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
                        <p className="mt-1">© 2025 Blassa. Tous droits réservés.</p>
                    </div>
                </div>
            </div>

            {/* Change Password Modal */}
            {showPasswordModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl">
                        <div className="flex items-center justify-between mb-6">
                            <h3 className="text-xl font-bold text-gray-900">
                                Changer le mot de passe
                            </h3>
                            <button
                                onClick={resetPasswordModal}
                                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                            >
                                <X className="w-5 h-5 text-gray-500" />
                            </button>
                        </div>

                        {passwordError && (
                            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                                {passwordError}
                            </div>
                        )}

                        {passwordSuccess && (
                            <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg text-sm">
                                {passwordSuccess}
                            </div>
                        )}

                        <div className="space-y-4">
                            {/* Current Password */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Mot de passe actuel
                                </label>
                                <div className="relative">
                                    <input
                                        type={showCurrentPassword ? "text" : "password"}
                                        name="currentPassword"
                                        value={passwordForm.currentPassword}
                                        onChange={handlePasswordChange}
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] pr-12"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                    >
                                        {showCurrentPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>

                            {/* New Password */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Nouveau mot de passe
                                </label>
                                <div className="relative">
                                    <input
                                        type={showNewPassword ? "text" : "password"}
                                        name="newPassword"
                                        value={passwordForm.newPassword}
                                        onChange={handlePasswordChange}
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] pr-12"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowNewPassword(!showNewPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                    >
                                        {showNewPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                                <p className="text-xs text-gray-500 mt-1">Minimum 8 caractères</p>
                            </div>

                            {/* Confirm Password */}
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Confirmer le mot de passe
                                </label>
                                <div className="relative">
                                    <input
                                        type={showConfirmPassword ? "text" : "password"}
                                        name="confirmPassword"
                                        value={passwordForm.confirmPassword}
                                        onChange={handlePasswordChange}
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] pr-12"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                    >
                                        {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div className="flex gap-3 mt-6">
                            <button
                                onClick={resetPasswordModal}
                                disabled={isChangingPassword}
                                className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50"
                            >
                                Annuler
                            </button>
                            <button
                                onClick={handleChangePassword}
                                disabled={isChangingPassword}
                                className="flex-1 px-4 py-3 bg-[#0A8F8F] text-white font-medium rounded-xl hover:bg-[#0A8F8F]/90 transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                            >
                                {isChangingPassword ? (
                                    <>
                                        <Loader2 className="w-4 h-4 animate-spin" />
                                        Modification...
                                    </>
                                ) : (
                                    "Modifier"
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            )}

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

                        {deleteError && (
                            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                                {deleteError}
                            </div>
                        )}

                        <div className="flex gap-3">
                            <button
                                onClick={() => {
                                    setShowDeleteModal(false);
                                    setDeleteError(null);
                                }}
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
