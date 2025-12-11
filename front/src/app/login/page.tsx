"use client";

import { useState, useEffect, Suspense } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import { Mail, Eye, EyeOff, Loader2, CheckCircle2, Lock } from "lucide-react";

interface FormData {
    email: string;
    password: string;
}

interface FormErrors {
    email?: string;
    password?: string;
}

function LoginForm() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const { login, isAuthenticated, isLoading: authLoading } = useAuth();

    const [formData, setFormData] = useState<FormData>({
        email: "",
        password: "",
    });
    const [errors, setErrors] = useState<FormErrors>({});
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showSuccessMessage, setShowSuccessMessage] = useState(false);

    // Redirect if already authenticated
    useEffect(() => {
        if (!authLoading && isAuthenticated) {
            router.replace("/dashboard");
        }
    }, [authLoading, isAuthenticated, router]);

    useEffect(() => {
        if (searchParams.get("registered") === "true") {
            setShowSuccessMessage(true);
            window.history.replaceState({}, "", "/login");
        }
    }, [searchParams]);

    // Show loading while checking auth
    if (authLoading) {
        return (
            <div className="h-48 flex items-center justify-center">
                <Loader2 className="w-6 h-6 animate-spin text-slate-400" />
            </div>
        );
    }

    // Don't render form if authenticated (will redirect)
    if (isAuthenticated) {
        return (
            <div className="h-48 flex items-center justify-center">
                <Loader2 className="w-6 h-6 animate-spin text-slate-400" />
            </div>
        );
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
        if (errors[name as keyof FormErrors]) {
            setErrors((prev) => ({ ...prev, [name]: "" }));
        }
        setApiError("");
    };

    const validate = (): boolean => {
        const newErrors: FormErrors = {};

        if (!formData.email) {
            newErrors.email = "Email requis";
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = "Email invalide";
        }

        if (!formData.password) {
            newErrors.password = "Mot de passe requis";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validate()) return;

        setIsLoading(true);
        setApiError("");
        setShowSuccessMessage(false);

        try {
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/auth/login`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    credentials: "include",
                    body: JSON.stringify(formData),
                }
            );

            // Handle rate limiting
            if (response.status === 429) {
                throw new Error("Trop de tentatives. Veuillez réessayer plus tard.");
            }

            if (!response.ok) {
                let errorMessage = "Email ou mot de passe incorrect";
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch {
                    // Response is not JSON, use default message
                }
                throw new Error(errorMessage);
            }

            // Parse token from response
            const data = await response.json();

            // Check if we got a token (successful login) or a message (email verification needed)
            if (data.token && !data.token.includes("verification")) {
                // Store token and fetch user profile
                await login(data.token);

                // Clear sensitive data from state
                setFormData({ email: "", password: "" });

                router.push("/dashboard");
            } else {
                // Email verification required
                setApiError(data.token || "Veuillez vérifier votre email.");
            }
        } catch (error) {
            // Handle network errors specifically
            if (error instanceof TypeError && error.message.includes("fetch")) {
                setApiError("Erreur réseau. Vérifiez votre connexion internet.");
            } else {
                const message =
                    error instanceof Error ? error.message : "Une erreur est survenue";
                setApiError(message);
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            {/* Success Message */}
            {showSuccessMessage && (
                <div className="p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-sm flex items-center gap-3">
                    <CheckCircle2 className="w-5 h-5 shrink-0" />
                    <span>Inscription réussie ! Vous pouvez maintenant vous connecter.</span>
                </div>
            )}

            {/* API Error */}
            {apiError && (
                <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                    {apiError}
                </div>
            )}

            {/* Email Field */}
            <div className="space-y-2">
                <label
                    htmlFor="email"
                    className="block text-sm font-medium text-slate-700"
                >
                    Email
                </label>
                <div className="relative">
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                        <Mail className="w-5 h-5" strokeWidth={1.5} />
                    </div>
                    <input
                        id="email"
                        name="email"
                        type="email"
                        placeholder="votre@email.com"
                        value={formData.email}
                        onChange={handleChange}
                        autoComplete="email"
                        className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.email ? "border-red-400" : "border-slate-200"
                            }`}
                    />
                </div>
                {errors.email && (
                    <p className="text-sm text-red-500 mt-1">{errors.email}</p>
                )}
            </div>

            {/* Password Field */}
            <div className="space-y-2">
                <div className="flex items-center justify-between">
                    <label
                        htmlFor="password"
                        className="block text-sm font-medium text-slate-700"
                    >
                        Mot de passe
                    </label>
                    <Link
                        href="/forgot-password"
                        className="text-sm font-medium text-[#FF9A3E] hover:text-[#E88A35] transition-colors"
                    >
                        Mot de passe oublié ?
                    </Link>
                </div>
                <div className="relative">
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                        <Lock className="w-5 h-5" strokeWidth={1.5} />
                    </div>
                    <input
                        id="password"
                        name="password"
                        type={showPassword ? "text" : "password"}
                        placeholder="••••••••"
                        value={formData.password}
                        onChange={handleChange}
                        autoComplete="current-password"
                        className={`w-full h-12 pl-12 pr-12 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.password ? "border-red-400" : "border-slate-200"
                            }`}
                    />
                    <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                    >
                        {showPassword ? (
                            <EyeOff className="w-5 h-5" strokeWidth={1.5} />
                        ) : (
                            <Eye className="w-5 h-5" strokeWidth={1.5} />
                        )}
                    </button>
                </div>
                {errors.password && (
                    <p className="text-sm text-red-500 mt-1">{errors.password}</p>
                )}
            </div>

            {/* Submit Button */}
            <Button
                type="submit"
                className="w-full h-12 text-[15px] font-medium bg-[#006B8F] hover:bg-[#005673] text-white rounded-xl transition-all duration-200 shadow-sm"
                disabled={isLoading}
            >
                {isLoading ? (
                    <>
                        <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                        Connexion...
                    </>
                ) : (
                    "Se connecter"
                )}
            </Button>
        </form>
    );
}

export default function LoginPage() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC] p-4">
            {/* Background Gradient Blobs */}
            <div className="fixed inset-0 pointer-events-none overflow-hidden">
                <div className="absolute -top-40 -left-40 w-[600px] h-[600px] bg-[#006B8F] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
                <div className="absolute -bottom-40 -right-40 w-[600px] h-[600px] bg-[#FF9A3E] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
            </div>

            {/* Login Card */}
            <div className="relative w-full max-w-[400px] bg-white rounded-[20px] p-8 shadow-[0_8px_20px_rgba(0,0,0,0.06)]">
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

                {/* Header */}
                <div className="text-center mb-8">
                    <h1 className="text-[22px] font-bold text-slate-900 font-[family-name:var(--font-poppins)] mb-2">
                        Connexion
                    </h1>
                    <p className="text-sm text-slate-500">
                        Connectez-vous à votre compte Blassa
                    </p>

                    {/* Trust Indicator */}
                    <div className="flex items-center justify-center gap-1.5 mt-3 text-xs text-slate-400">
                        <Lock className="w-3.5 h-3.5" strokeWidth={1.5} />
                        <span>Sécurisé par Blassa</span>
                    </div>
                </div>

                {/* Form */}
                <Suspense
                    fallback={
                        <div className="h-48 flex items-center justify-center">
                            <Loader2 className="w-6 h-6 animate-spin text-slate-400" />
                        </div>
                    }
                >
                    <LoginForm />
                </Suspense>

                {/* Register Link */}
                <div className="mt-8 text-center">
                    <p className="text-sm text-slate-600">
                        Pas encore de compte ?{" "}
                        <Link
                            href="/register"
                            className="font-medium text-[#FF9A3E] hover:text-[#E88A35] transition-colors"
                        >
                            S&apos;inscrire
                        </Link>
                    </p>
                </div>

                {/* Back to Home */}
                <div className="mt-4 text-center">
                    <Link
                        href="/"
                        className="text-sm text-[#64748B] hover:text-slate-700 transition-colors"
                    >
                        ← Retour à l&apos;accueil
                    </Link>
                </div>
            </div>
        </div>
    );
}
