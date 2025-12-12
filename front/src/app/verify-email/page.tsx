"use client";

import { useState, useEffect, Suspense } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import { Mail, Loader2, ArrowLeft, CheckCircle2, RefreshCw, Edit3, Eye, EyeOff, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { parseApiError } from "@/lib/api-utils";

function VerifyEmailContent() {
    const router = useRouter();
    const searchParams = useSearchParams();

    const [email, setEmail] = useState(searchParams.get("email") || "");
    const sentAtParam = searchParams.get("sentAt");

    const [isResending, setIsResending] = useState(false);
    const [resendSuccess, setResendSuccess] = useState(false);
    const [error, setError] = useState("");
    const [countdown, setCountdown] = useState(0);

    // Change email modal state
    const [showChangeEmail, setShowChangeEmail] = useState(false);
    const [newEmail, setNewEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [isChangingEmail, setIsChangingEmail] = useState(false);
    const [changeEmailError, setChangeEmailError] = useState("");

    // Calculate initial countdown from sentAt parameter
    useEffect(() => {
        if (sentAtParam) {
            const sentAt = new Date(sentAtParam);
            const now = new Date();
            const elapsed = Math.floor((now.getTime() - sentAt.getTime()) / 1000);
            const remaining = Math.max(0, 60 - elapsed);
            setCountdown(remaining);
        }
    }, [sentAtParam]);

    // Countdown timer
    useEffect(() => {
        if (countdown > 0) {
            const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
            return () => clearTimeout(timer);
        }
    }, [countdown]);

    const handleResend = async () => {
        if (countdown > 0 || !email) return;

        setIsResending(true);
        setError("");
        setResendSuccess(false);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/auth/resend-verification`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ email }),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de l'envoi");
                throw new Error(errorMessage);
            }

            setResendSuccess(true);
            setCountdown(60);

            // Hide success message after 5 seconds
            setTimeout(() => setResendSuccess(false), 5000);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsResending(false);
        }
    };

    const handleChangeEmail = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!newEmail || !password) {
            setChangeEmailError("Tous les champs sont requis");
            return;
        }

        setIsChangingEmail(true);
        setChangeEmailError("");

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/auth/change-email`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        currentEmail: email,
                        newEmail,
                        password,
                    }),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors du changement d'email");
                throw new Error(errorMessage);
            }

            const data = await response.json();

            // Update email and reset state
            setEmail(data.email);
            setShowChangeEmail(false);
            setNewEmail("");
            setPassword("");
            setCountdown(60);
            setResendSuccess(true);
            setError("");

            // Update URL without reload
            const params = new URLSearchParams({
                email: data.email,
                ...(data.verificationSentAt && { sentAt: data.verificationSentAt }),
            });
            window.history.replaceState({}, "", `/verify-email?${params.toString()}`);

            setTimeout(() => setResendSuccess(false), 5000);
        } catch (err) {
            setChangeEmailError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsChangingEmail(false);
        }
    };

    if (!email) {
        return (
            <div className="text-center">
                <p className="text-slate-600 mb-4">Email non spécifié</p>
                <Link href="/login">
                    <Button variant="outline" className="rounded-xl">
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Retour à la connexion
                    </Button>
                </Link>
            </div>
        );
    }

    return (
        <div className="text-center">
            {/* Email Icon */}
            <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-br from-[#0A8F8F]/20 to-[#006B8F]/10 flex items-center justify-center">
                <Mail className="w-10 h-10 text-[#0A8F8F]" />
            </div>

            {/* Title */}
            <h1 className="text-2xl font-bold text-slate-900 mb-3">
                Vérifiez votre email
            </h1>

            {/* Description */}
            <p className="text-slate-600 mb-2">
                Nous avons envoyé un lien de vérification à :
            </p>
            <div className="flex items-center justify-center gap-2 mb-6">
                <p className="font-medium text-slate-900">
                    {email}
                </p>
                <button
                    onClick={() => setShowChangeEmail(true)}
                    className="text-[#0A8F8F] hover:text-[#006B8F] transition-colors"
                    title="Modifier l'adresse email"
                >
                    <Edit3 className="w-4 h-4" />
                </button>
            </div>

            {/* Success Message */}
            {resendSuccess && (
                <div className="mb-6 p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm flex items-center justify-center gap-2">
                    <CheckCircle2 className="w-5 h-5" />
                    Un nouveau lien de vérification a été envoyé !
                </div>
            )}

            {/* Error Message */}
            {error && (
                <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                    {error}
                </div>
            )}

            {/* Countdown or Resend Button */}
            <div className="space-y-4">
                <Button
                    onClick={handleResend}
                    disabled={countdown > 0 || isResending}
                    className="w-full h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white disabled:opacity-50"
                >
                    {isResending ? (
                        <>
                            <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                            Envoi en cours...
                        </>
                    ) : countdown > 0 ? (
                        <>
                            <RefreshCw className="w-5 h-5 mr-2" />
                            Renvoyer dans {countdown}s
                        </>
                    ) : (
                        <>
                            <RefreshCw className="w-5 h-5 mr-2" />
                            Renvoyer l&apos;email de vérification
                        </>
                    )}
                </Button>

                <Link href="/login" className="block">
                    <Button variant="ghost" className="w-full h-12 rounded-xl text-slate-600">
                        <ArrowLeft className="w-4 h-4 mr-2" />
                        Retour à la connexion
                    </Button>
                </Link>
            </div>

            {/* Help text */}
            <div className="mt-8 p-4 bg-slate-50 rounded-xl">
                <p className="text-sm text-slate-500">
                    Vous n&apos;avez pas reçu l&apos;email ? Vérifiez votre dossier spam ou{" "}
                    <button
                        onClick={handleResend}
                        disabled={countdown > 0 || isResending}
                        className="text-[#0A8F8F] hover:underline disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        renvoyez-le
                    </button>.
                    <br />
                    <button
                        onClick={() => setShowChangeEmail(true)}
                        className="text-[#0A8F8F] hover:underline mt-1"
                    >
                        Mauvaise adresse ? Modifier l&apos;email
                    </button>
                </p>
            </div>

            {/* Change Email Modal */}
            {showChangeEmail && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl text-left">
                        <div className="flex items-center justify-between mb-6">
                            <h3 className="text-xl font-bold text-gray-900">
                                Modifier l&apos;adresse email
                            </h3>
                            <button
                                onClick={() => {
                                    setShowChangeEmail(false);
                                    setNewEmail("");
                                    setPassword("");
                                    setChangeEmailError("");
                                }}
                                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                            >
                                <X className="w-5 h-5 text-gray-500" />
                            </button>
                        </div>

                        <p className="text-sm text-slate-500 mb-4">
                            Entrez votre nouvelle adresse email et votre mot de passe pour confirmer le changement.
                        </p>

                        {changeEmailError && (
                            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-sm">
                                {changeEmailError}
                            </div>
                        )}

                        <form onSubmit={handleChangeEmail} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Email actuel
                                </label>
                                <input
                                    type="email"
                                    value={email}
                                    disabled
                                    className="w-full px-4 py-3 border border-gray-200 rounded-xl bg-gray-50 text-gray-500"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Nouvel email
                                </label>
                                <input
                                    type="email"
                                    value={newEmail}
                                    onChange={(e) => setNewEmail(e.target.value)}
                                    placeholder="nouveau@email.com"
                                    className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F]"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Mot de passe
                                </label>
                                <div className="relative">
                                    <input
                                        type={showPassword ? "text" : "password"}
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        placeholder="••••••••"
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] pr-12"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowPassword(!showPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                    >
                                        {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>

                            <div className="flex gap-3 pt-2">
                                <Button
                                    type="button"
                                    variant="outline"
                                    onClick={() => {
                                        setShowChangeEmail(false);
                                        setNewEmail("");
                                        setPassword("");
                                        setChangeEmailError("");
                                    }}
                                    disabled={isChangingEmail}
                                    className="flex-1 h-12 rounded-xl"
                                >
                                    Annuler
                                </Button>
                                <Button
                                    type="submit"
                                    disabled={isChangingEmail}
                                    className="flex-1 h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673]"
                                >
                                    {isChangingEmail ? (
                                        <>
                                            <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                                            Modification...
                                        </>
                                    ) : (
                                        "Confirmer"
                                    )}
                                </Button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default function VerifyEmailPage() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC] p-4">
            {/* Background Gradient Blobs */}
            <div className="fixed inset-0 pointer-events-none overflow-hidden">
                <div className="absolute -top-40 -left-40 w-[600px] h-[600px] bg-[#006B8F] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
                <div className="absolute -bottom-40 -right-40 w-[600px] h-[600px] bg-[#FF9A3E] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
            </div>

            {/* Card */}
            <div className="relative w-full max-w-[440px] bg-white rounded-[20px] p-8 shadow-[0_8px_20px_rgba(0,0,0,0.06)]">
                {/* Logo */}
                <div className="flex justify-center mb-6">
                    <Link href="/">
                        <Image
                            src="/LOGO.png"
                            alt="Blassa"
                            width={72}
                            height={72}
                            className="w-[72px] h-[72px]"
                        />
                    </Link>
                </div>

                {/* Content */}
                <Suspense
                    fallback={
                        <div className="h-48 flex items-center justify-center">
                            <Loader2 className="w-6 h-6 animate-spin text-slate-400" />
                        </div>
                    }
                >
                    <VerifyEmailContent />
                </Suspense>
            </div>
        </div>
    );
}
