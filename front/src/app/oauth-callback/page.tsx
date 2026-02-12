"use client";

import { useEffect, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { API_URL } from "@/lib/config";
import { Loader2 } from "lucide-react";

function OAuthCallbackHandler() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const { login } = useAuth();

    useEffect(() => {
        const handleCallback = async () => {
            const error = searchParams.get("error");
            if (error) {
                console.error("OAuth error:", error);
                router.replace("/login?error=oauth_failed");
                return;
            }

            const code = searchParams.get("code");
            if (!code) {
                console.error("No authorization code received");
                router.replace("/login?error=oauth_failed");
                return;
            }

            try {
                // Send the code to our backend to exchange for a JWT
                const redirectUri = `${window.location.origin}/oauth-callback`;
                const response = await fetch(`${API_URL}/auth/google`, {
                    method: "POST",
                    credentials: "include",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ code, redirectUri }),
                });

                if (!response.ok) {
                    const data = await response.json().catch(() => ({}));
                    console.error("Google auth failed:", data);
                    router.replace("/login?error=oauth_failed");
                    return;
                }

                // Cookie is set by the backend, now fetch user profile
                await login();
                router.replace("/dashboard");
            } catch (err) {
                console.error("Failed to process OAuth login:", err);
                router.replace("/login?error=oauth_failed");
            }
        };

        handleCallback();
    }, [searchParams, login, router]);

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-[#F8FAFC]">
            <Loader2 className="w-10 h-10 animate-spin text-[#006B8F] mb-4" />
            <p className="text-slate-600 text-lg">Connexion en cours...</p>
            <p className="text-slate-400 text-sm mt-2">Veuillez patienter pendant que nous vous connectons</p>
        </div>
    );
}

export default function OAuthCallbackPage() {
    return (
        <Suspense
            fallback={
                <div className="min-h-screen flex flex-col items-center justify-center bg-[#F8FAFC]">
                    <Loader2 className="w-10 h-10 animate-spin text-[#006B8F] mb-4" />
                    <p className="text-slate-600 text-lg">Chargement...</p>
                </div>
            }
        >
            <OAuthCallbackHandler />
        </Suspense>
    );
}
