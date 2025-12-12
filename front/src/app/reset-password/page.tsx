"use client";

import { useState, Suspense } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import { Lock, Loader2, Eye, EyeOff, CheckCircle2, XCircle, KeyRound } from "lucide-react";
import { Button } from "@/components/ui/button";
import { parseApiError } from "@/lib/api-utils";

function ResetPasswordContent() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const token = searchParams.get("token");

    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!password || !confirmPassword) {
            setError("Veuillez remplir tous les champs");
            return;
        }

        if (password.length < 6) {
            setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (password !== confirmPassword) {
            setError("Les mots de passe ne correspondent pas");
            return;
        }

        setIsLoading(true);
        setError("");

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL?.replace('/api/v1', '')}/reset/email?token=${token}`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ password: password }),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de la réinitialisation");
                throw new Error(errorMessage);
            }

            setIsSuccess(true);
            setTimeout(() => router.push("/login"), 3000);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Une erreur est survenue");
        } finally {
            setIsLoading(false);
        }
    };

    if (!token) {
        return (
            <div className="text-center">
                <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-br from-red-100 to-red-50 flex items-center justify-center">
                    <XCircle className="w-10 h-10 text-red-600" />
                </div>
                <h1 className="text-2xl font-bold text-red-700 mb-3">
                    Lien invalide
                </h1>
                <p className="text-slate-600 mb-6">
                    Ce lien de réinitialisation est invalide ou a expiré.
                </p>
                <Link href="/forgot-password">
                    <Button className="w-full h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                        Demander un nouveau lien
                    </Button>
                </Link>
            </div>
        );
    }

    if (isSuccess) {
        return (
            <div className="text-center">
                <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-br from-emerald-100 to-emerald-50 flex items-center justify-center">
                    <CheckCircle2 className="w-10 h-10 text-emerald-600" />
                </div>
                <h1 className="text-2xl font-bold text-slate-900 mb-3">
                    Mot de passe modifié !
                </h1>
                <p className="text-slate-600 mb-6">
                    Votre mot de passe a été réinitialisé avec succès. Vous allez être redirigé vers la page de connexion...
                </p>
                <Link href="/login">
                    <Button className="w-full h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                        Se connecter maintenant
                    </Button>
                </Link>
            </div>
        );
    }

    return (
        <div>
            {/* Header */}
            <div className="text-center mb-6">
                <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-br from-[#0A8F8F]/20 to-[#006B8F]/10 flex items-center justify-center">
                    <KeyRound className="w-8 h-8 text-[#0A8F8F]" />
                </div>
                <h1 className="text-2xl font-bold text-slate-900 mb-2">
                    Nouveau mot de passe
                </h1>
                <p className="text-sm text-slate-500">
                    Choisissez un nouveau mot de passe sécurisé pour votre compte.
                </p>
            </div>

            {/* Error */}
            {error && (
                <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                    {error}
                </div>
            )}

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-5">
                <div className="space-y-2">
                    <label className="block text-sm font-medium text-slate-700">
                        Nouveau mot de passe
                    </label>
                    <div className="relative">
                        <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                            <Lock className="w-5 h-5" strokeWidth={1.5} />
                        </div>
                        <input
                            type={showPassword ? "text" : "password"}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            className="w-full h-12 pl-12 pr-12 bg-slate-50 border border-slate-200 rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all"
                        />
                        <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                        >
                            {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                        </button>
                    </div>
                </div>

                <div className="space-y-2">
                    <label className="block text-sm font-medium text-slate-700">
                        Confirmer le mot de passe
                    </label>
                    <div className="relative">
                        <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                            <Lock className="w-5 h-5" strokeWidth={1.5} />
                        </div>
                        <input
                            type={showConfirmPassword ? "text" : "password"}
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            placeholder="••••••••"
                            className="w-full h-12 pl-12 pr-12 bg-slate-50 border border-slate-200 rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all"
                        />
                        <button
                            type="button"
                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                        >
                            {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                        </button>
                    </div>
                </div>

                <Button
                    type="submit"
                    disabled={isLoading}
                    className="w-full h-12 text-[15px] font-medium bg-[#006B8F] hover:bg-[#005673] text-white rounded-xl transition-all duration-200 shadow-sm"
                >
                    {isLoading ? (
                        <>
                            <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                            Réinitialisation...
                        </>
                    ) : (
                        "Réinitialiser le mot de passe"
                    )}
                </Button>
            </form>
        </div>
    );
}

export default function ResetPasswordPage() {
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
                    <ResetPasswordContent />
                </Suspense>
            </div>
        </div>
    );
}
