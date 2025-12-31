"use client";

import { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { Mail, Loader2, ArrowLeft, CheckCircle2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { parseApiError } from "@/lib/api-utils";

export default function ForgotPasswordPage() {
    const [email, setEmail] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!email) {
            setError("Veuillez entrer votre adresse email");
            return;
        }

        setIsLoading(true);
        setError("");

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL?.replace('/api/v1', '')}/forgot`,
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

            setIsSuccess(true);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Une erreur est survenue");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC] p-4">

            <div className="fixed inset-0 pointer-events-none overflow-hidden">
                <div className="absolute -top-40 -left-40 w-[600px] h-[600px] bg-[#006B8F] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
                <div className="absolute -bottom-40 -right-40 w-[600px] h-[600px] bg-[#FF9A3E] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
            </div>

            <div className="relative w-full max-w-[440px] bg-white rounded-[20px] p-8 shadow-[0_8px_20px_rgba(0,0,0,0.06)]">

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

                {isSuccess ? (

                    <div className="text-center">
                        <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-br from-emerald-100 to-emerald-50 flex items-center justify-center">
                            <CheckCircle2 className="w-10 h-10 text-emerald-600" />
                        </div>
                        <h1 className="text-2xl font-bold text-slate-900 mb-3">
                            Email envoyé !
                        </h1>
                        <p className="text-slate-600 mb-6">
                            Si un compte existe avec l&apos;adresse <strong>{email}</strong>, vous recevrez un lien de réinitialisation.
                        </p>
                        <p className="text-sm text-slate-500 mb-6">
                            Vérifiez votre boîte de réception et votre dossier spam.
                        </p>
                        <Link href="/login">
                            <Button className="w-full h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                                Retour à la connexion
                            </Button>
                        </Link>
                    </div>
                ) : (

                    <div>

                        <div className="text-center mb-6">
                            <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-gradient-to-br from-[#0A8F8F]/20 to-[#006B8F]/10 flex items-center justify-center">
                                <Mail className="w-8 h-8 text-[#0A8F8F]" />
                            </div>
                            <h1 className="text-2xl font-bold text-slate-900 mb-2">
                                Mot de passe oublié ?
                            </h1>
                            <p className="text-sm text-slate-500">
                                Entrez votre email et nous vous enverrons un lien pour réinitialiser votre mot de passe.
                            </p>
                        </div>

                        {error && (
                            <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                                {error}
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="space-y-5">
                            <div className="space-y-2">
                                <label className="block text-sm font-medium text-slate-700">
                                    Adresse email
                                </label>
                                <div className="relative">
                                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                        <Mail className="w-5 h-5" strokeWidth={1.5} />
                                    </div>
                                    <input
                                        type="email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        placeholder="votre@email.com"
                                        className="w-full h-12 pl-12 pr-4 bg-slate-50 border border-slate-200 rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all"
                                    />
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
                                        Envoi en cours...
                                    </>
                                ) : (
                                    "Envoyer le lien"
                                )}
                            </Button>
                        </form>

                        <div className="mt-6 text-center">
                            <Link
                                href="/login"
                                className="inline-flex items-center text-sm text-slate-600 hover:text-[#006B8F] transition-colors"
                            >
                                <ArrowLeft className="w-4 h-4 mr-2" />
                                Retour à la connexion
                            </Link>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
