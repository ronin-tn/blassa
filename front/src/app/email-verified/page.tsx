"use client";

import { Suspense } from "react";
import Link from "next/link";
import Image from "next/image";
import { useSearchParams } from "next/navigation";
import { CheckCircle2, XCircle, Loader2, ArrowRight, Home } from "lucide-react";
import { Button } from "@/components/ui/button";

function EmailVerifiedContent() {
    const searchParams = useSearchParams();
    const status = searchParams.get("status");
    const errorMessage = searchParams.get("message");

    const isSuccess = status === "success";

    return (
        <div className="text-center">

            <div className={`w-20 h-20 mx-auto mb-6 rounded-full flex items-center justify-center ${isSuccess
                ? "bg-gradient-to-br from-emerald-100 to-emerald-50"
                : "bg-gradient-to-br from-red-100 to-red-50"
                }`}>
                {isSuccess ? (
                    <CheckCircle2 className="w-10 h-10 text-emerald-600" />
                ) : (
                    <XCircle className="w-10 h-10 text-red-600" />
                )}
            </div>

            <h1 className={`text-2xl font-bold mb-3 ${isSuccess ? "text-slate-900" : "text-red-700"
                }`}>
                {isSuccess ? "Email vérifié !" : "Échec de la vérification"}
            </h1>

            <p className="text-slate-600 mb-8">
                {isSuccess
                    ? "Votre adresse email a été vérifiée avec succès. Vous pouvez maintenant vous connecter à votre compte."
                    : errorMessage
                        ? decodeURIComponent(errorMessage)
                        : "Le lien de vérification est invalide ou a expiré. Veuillez demander un nouveau lien."
                }
            </p>

            <div className="space-y-3">
                {isSuccess ? (
                    <>
                        <Link href="/login" className="block">
                            <Button className="w-full h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                                Se connecter
                                <ArrowRight className="w-5 h-5 ml-2" />
                            </Button>
                        </Link>
                        <Link href="/" className="block">
                            <Button variant="ghost" className="w-full h-12 rounded-xl text-slate-600">
                                <Home className="w-4 h-4 mr-2" />
                                Retour à l&apos;accueil
                            </Button>
                        </Link>
                    </>
                ) : (
                    <>
                        <Link href="/login" className="block">
                            <Button className="w-full h-12 rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                                Retour à la connexion
                            </Button>
                        </Link>
                        <p className="text-sm text-slate-500 mt-4">
                            Connectez-vous pour demander un nouveau lien de vérification.
                        </p>
                    </>
                )}
            </div>

            {isSuccess && (
                <div className="mt-8 p-4 bg-emerald-50 rounded-xl border border-emerald-100">
                    <p className="text-sm text-emerald-700">
                        🎉 Bienvenue dans la communauté Blassa ! Voyagez ensemble.
                    </p>
                </div>
            )}
        </div>
    );
}

export default function EmailVerifiedPage() {
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

                <Suspense
                    fallback={
                        <div className="h-48 flex items-center justify-center">
                            <Loader2 className="w-6 h-6 animate-spin text-slate-400" />
                        </div>
                    }
                >
                    <EmailVerifiedContent />
                </Suspense>
            </div>
        </div>
    );
}
