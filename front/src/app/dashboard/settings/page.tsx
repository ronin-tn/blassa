"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import {
    ArrowLeft,
    Shield,
    LogOut,
    ChevronRight,
    Loader2,
    Lock,
    Mail,
    Eye,
    EyeOff,
    X,
} from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { parseApiError } from "@/lib/api-utils";

export default function SettingsPage() {
    const router = useRouter();
    const { isAuthenticated, isLoading: authLoading, logout } = useAuth();

    // Email change state
    const [showEmailModal, setShowEmailModal] = useState(false);
    const [isChangingEmail, setIsChangingEmail] = useState(false);
    const [emailError, setEmailError] = useState<string | null>(null);
    const [emailSuccess, setEmailSuccess] = useState<string | null>(null);
    const [showEmailPassword, setShowEmailPassword] = useState(false);
    const [emailForm, setEmailForm] = useState({
        newEmail: "",
        password: "",
    });

    // Password change state
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

    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const handleLogout = () => {
        logout();
        router.replace("/");
    };

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setPasswordForm((prev) => ({ ...prev, [name]: value }));
        setPasswordError(null);
    };

    const handleEmailFormChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setEmailForm((prev) => ({ ...prev, [name]: value }));
        setEmailError(null);
    };

    const handleChangeEmail = async () => {
        setEmailError(null);
        setEmailSuccess(null);

        if (!emailForm.newEmail || !emailForm.password) {
            setEmailError("Tous les champs sont requis");
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailForm.newEmail)) {
            setEmailError("Format d'email invalide");
            return;
        }

        setIsChangingEmail(true);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me/email`,
                {
                    method: "PUT",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(emailForm),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors du changement d'email");
                throw new Error(errorMessage);
            }

            setEmailSuccess("Email modifié avec succès. Veuillez vérifier votre nouvelle adresse email.");
            setEmailForm({ newEmail: "", password: "" });

            setTimeout(() => {
                setShowEmailModal(false);
                setEmailSuccess(null);
            }, 3000);
        } catch (err) {
            setEmailError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsChangingEmail(false);
        }
    };

    const resetEmailModal = () => {
        setShowEmailModal(false);
        setEmailForm({ newEmail: "", password: "" });
        setEmailError(null);
        setEmailSuccess(null);
    };

    const handleChangePassword = async () => {
        setPasswordError(null);
        setPasswordSuccess(null);


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

    const resetPasswordModal = () => {
        setShowPasswordModal(false);
        setPasswordForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
        setPasswordError(null);
        setPasswordSuccess(null);
    };


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

                            <button
                                onClick={() => setShowEmailModal(true)}
                                className="w-full flex items-center justify-between px-6 py-4 hover:bg-gray-50 transition-colors text-left"
                            >
                                <div className="flex items-center gap-3">
                                    <Mail className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">
                                            Changer l&apos;adresse email
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            Modifier votre adresse email
                                        </p>
                                    </div>
                                </div>
                                <ChevronRight className="w-5 h-5 text-gray-400" />
                            </button>
                        </div>
                    </section>


                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                        <div className="divide-y divide-gray-100">
                            <button
                                onClick={handleLogout}
                                className="w-full flex items-center gap-3 px-6 py-4 hover:bg-gray-50 transition-colors text-left"
                            >
                                <LogOut className="w-5 h-5 text-gray-400" />
                                <span className="font-medium text-gray-900">Se déconnecter</span>
                            </button>


                        </div>
                    </section>


                    <div className="text-center text-sm text-gray-400 py-4">
                        <p>Blassa v1.0.0</p>
                        <p className="mt-1">© 2025 Blassa. Tous droits réservés.</p>
                    </div>
                </div>
            </div>


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


            {showEmailModal && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl">
                        <div className="flex items-center justify-between mb-6">
                            <h3 className="text-xl font-bold text-gray-900">
                                Changer l&apos;adresse email
                            </h3>
                            <button
                                onClick={resetEmailModal}
                                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                            >
                                <X className="w-5 h-5 text-gray-500" />
                            </button>
                        </div>

                        {emailError && (
                            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                                {emailError}
                            </div>
                        )}

                        {emailSuccess && (
                            <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg text-sm">
                                {emailSuccess}
                            </div>
                        )}

                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Nouvel email
                                </label>
                                <div className="relative">
                                    <input
                                        type="email"
                                        name="newEmail"
                                        value={emailForm.newEmail}
                                        onChange={handleEmailFormChange}
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F]"
                                        placeholder="nouveau@email.com"
                                    />
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Mot de passe actuel
                                </label>
                                <div className="relative">
                                    <input
                                        type={showEmailPassword ? "text" : "password"}
                                        name="password"
                                        value={emailForm.password}
                                        onChange={handleEmailFormChange}
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] pr-12"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowEmailPassword(!showEmailPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                    >
                                        {showEmailPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                                <p className="text-xs text-gray-500 mt-1">Confirmez votre identité avec votre mot de passe</p>
                            </div>
                        </div>

                        <div className="flex gap-3 mt-6">
                            <button
                                onClick={resetEmailModal}
                                disabled={isChangingEmail}
                                className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors disabled:opacity-50"
                            >
                                Annuler
                            </button>
                            <button
                                onClick={handleChangeEmail}
                                disabled={isChangingEmail}
                                className="flex-1 px-4 py-3 bg-[#0A8F8F] text-white font-medium rounded-xl hover:bg-[#0A8F8F]/90 transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
                            >
                                {isChangingEmail ? (
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



        </div>
    );
}
