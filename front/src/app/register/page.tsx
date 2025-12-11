"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Gender, GenderLabels } from "@/lib/constants";
import { useAuth } from "@/contexts/AuthContext";
import { Mail, Eye, EyeOff, Loader2, User, Phone, Calendar, Lock } from "lucide-react";

interface FormData {
    email: string;
    password: string;
    confirmPassword: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    gender: string;
    birthDate: string;
}

interface FormErrors {
    email?: string;
    password?: string;
    confirmPassword?: string;
    firstName?: string;
    lastName?: string;
    phoneNumber?: string;
    gender?: string;
    birthDate?: string;
}

export default function RegisterPage() {
    const router = useRouter();
    const { isAuthenticated, isLoading: authLoading } = useAuth();

    const [formData, setFormData] = useState<FormData>({
        email: "",
        password: "",
        confirmPassword: "",
        firstName: "",
        lastName: "",
        phoneNumber: "+216",
        gender: "",
        birthDate: "",
    });
    const [errors, setErrors] = useState<FormErrors>({});
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    // Redirect if already authenticated
    useEffect(() => {
        if (!authLoading && isAuthenticated) {
            router.replace("/dashboard");
        }
    }, [authLoading, isAuthenticated, router]);

    // Show loading while checking auth
    if (authLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC]">
                <Loader2 className="w-8 h-8 animate-spin text-[#006B8F]" />
            </div>
        );
    }

    // Don't render form if authenticated (will redirect)
    if (isAuthenticated) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC]">
                <Loader2 className="w-8 h-8 animate-spin text-[#006B8F]" />
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

    const handleGenderChange = (value: string) => {
        setFormData((prev) => ({ ...prev, gender: value }));
        if (errors.gender) {
            setErrors((prev) => ({ ...prev, gender: "" }));
        }
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
        } else if (formData.password.length < 6) {
            newErrors.password = "Minimum 6 caractères";
        }

        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = "Les mots de passe ne correspondent pas";
        }

        if (!formData.firstName) {
            newErrors.firstName = "Prénom requis";
        }

        if (!formData.lastName) {
            newErrors.lastName = "Nom requis";
        }

        if (!formData.phoneNumber) {
            newErrors.phoneNumber = "Numéro requis";
        } else if (!/^\+[1-9][0-9]{7,14}$/.test(formData.phoneNumber)) {
            newErrors.phoneNumber = "Format: +21612345678";
        }

        if (!formData.gender) {
            newErrors.gender = "Genre requis";
        }

        if (!formData.birthDate) {
            newErrors.birthDate = "Date requise";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validate()) return;

        setIsLoading(true);
        setApiError("");

        try {
            const { confirmPassword: _confirmPassword, ...registerData } = formData;

            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/auth/register`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(registerData),
                }
            );

            // Handle rate limiting
            if (response.status === 429) {
                throw new Error("Trop de tentatives. Veuillez réessayer plus tard.");
            }

            // Handle conflict (email already exists)
            if (response.status === 409) {
                throw new Error("Cet email est déjà utilisé.");
            }

            if (!response.ok) {
                let errorMessage = "Erreur lors de l'inscription";
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch {
                    // Response is not JSON, use default message
                }
                throw new Error(errorMessage);
            }

            // Clear sensitive data from state
            setFormData({
                email: "",
                password: "",
                confirmPassword: "",
                firstName: "",
                lastName: "",
                phoneNumber: "+216",
                gender: "",
                birthDate: "",
            });

            router.push("/login?registered=true");
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
        <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC] p-4 py-8">
            {/* Background Gradient Blobs */}
            <div className="fixed inset-0 pointer-events-none overflow-hidden">
                <div className="absolute -top-40 -left-40 w-[600px] h-[600px] bg-[#006B8F] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
                <div className="absolute -bottom-40 -right-40 w-[600px] h-[600px] bg-[#FF9A3E] rounded-full mix-blend-multiply filter blur-[120px] opacity-[0.08]"></div>
            </div>

            {/* Register Card */}
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

                {/* Header */}
                <div className="text-center mb-6">
                    <h1 className="text-[22px] font-bold text-slate-900 font-[family-name:var(--font-poppins)] mb-2">
                        Créer un compte
                    </h1>
                    <p className="text-sm text-slate-500">
                        Rejoignez la communauté Blassa
                    </p>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="space-y-5">
                    {/* API Error */}
                    {apiError && (
                        <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                            {apiError}
                        </div>
                    )}

                    {/* Name Row */}
                    <div className="grid grid-cols-2 gap-3">
                        <div className="space-y-2">
                            <label className="block text-sm font-medium text-slate-700">
                                Prénom
                            </label>
                            <div className="relative">
                                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                    <User className="w-5 h-5" strokeWidth={1.5} />
                                </div>
                                <input
                                    name="firstName"
                                    placeholder="Ahmed"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.firstName ? "border-red-400" : "border-slate-200"
                                        }`}
                                />
                            </div>
                            {errors.firstName && (
                                <p className="text-xs text-red-500">{errors.firstName}</p>
                            )}
                        </div>
                        <div className="space-y-2">
                            <label className="block text-sm font-medium text-slate-700">
                                Nom
                            </label>
                            <div className="relative">
                                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                    <User className="w-5 h-5" strokeWidth={1.5} />
                                </div>
                                <input
                                    name="lastName"
                                    placeholder="Ben Ali"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.lastName ? "border-red-400" : "border-slate-200"
                                        }`}
                                />
                            </div>
                            {errors.lastName && (
                                <p className="text-xs text-red-500">{errors.lastName}</p>
                            )}
                        </div>
                    </div>

                    {/* Email */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Email
                        </label>
                        <div className="relative">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                <Mail className="w-5 h-5" strokeWidth={1.5} />
                            </div>
                            <input
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
                            <p className="text-xs text-red-500">{errors.email}</p>
                        )}
                    </div>

                    {/* Phone */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Téléphone
                        </label>
                        <div className="relative">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                <Phone className="w-5 h-5" strokeWidth={1.5} />
                            </div>
                            <input
                                name="phoneNumber"
                                type="tel"
                                placeholder="+21612345678"
                                value={formData.phoneNumber}
                                onChange={handleChange}
                                className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.phoneNumber ? "border-red-400" : "border-slate-200"
                                    }`}
                            />
                        </div>
                        {errors.phoneNumber && (
                            <p className="text-xs text-red-500">{errors.phoneNumber}</p>
                        )}
                    </div>

                    {/* Gender & Birth Date Row */}
                    <div className="grid grid-cols-2 gap-3">
                        <div className="space-y-2">
                            <label className="block text-sm font-medium text-slate-700">
                                Genre
                            </label>
                            <Select value={formData.gender} onValueChange={handleGenderChange}>
                                <SelectTrigger
                                    className={`h-12 bg-slate-50 border rounded-xl text-sm focus:ring-2 focus:ring-[#006B8F] ${errors.gender ? "border-red-400" : "border-slate-200"
                                        }`}
                                >
                                    <SelectValue placeholder="Sélectionnez" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={Gender.MALE}>
                                        {GenderLabels[Gender.MALE]}
                                    </SelectItem>
                                    <SelectItem value={Gender.FEMALE}>
                                        {GenderLabels[Gender.FEMALE]}
                                    </SelectItem>
                                </SelectContent>
                            </Select>
                            {errors.gender && (
                                <p className="text-xs text-red-500">{errors.gender}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <label className="block text-sm font-medium text-slate-700">
                                Date de naissance
                            </label>
                            <div className="relative">
                                <input
                                    name="birthDate"
                                    type="date"
                                    value={formData.birthDate}
                                    onChange={handleChange}
                                    max={
                                        new Date(
                                            new Date().setFullYear(new Date().getFullYear() - 18)
                                        )
                                            .toISOString()
                                            .split("T")[0]
                                    }
                                    className={`w-full h-12 px-4 bg-slate-50 border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.birthDate ? "border-red-400" : "border-slate-200"
                                        }`}
                                />
                            </div>
                            {errors.birthDate && (
                                <p className="text-xs text-red-500">{errors.birthDate}</p>
                            )}
                        </div>
                    </div>

                    {/* Password */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Mot de passe
                        </label>
                        <div className="relative">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                <Lock className="w-5 h-5" strokeWidth={1.5} />
                            </div>
                            <input
                                name="password"
                                type={showPassword ? "text" : "password"}
                                placeholder="••••••••"
                                value={formData.password}
                                onChange={handleChange}
                                autoComplete="new-password"
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
                            <p className="text-xs text-red-500">{errors.password}</p>
                        )}
                    </div>

                    {/* Confirm Password */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Confirmer le mot de passe
                        </label>
                        <div className="relative">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                <Lock className="w-5 h-5" strokeWidth={1.5} />
                            </div>
                            <input
                                name="confirmPassword"
                                type={showConfirmPassword ? "text" : "password"}
                                placeholder="••••••••"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                autoComplete="new-password"
                                className={`w-full h-12 pl-12 pr-12 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.confirmPassword ? "border-red-400" : "border-slate-200"
                                    }`}
                            />
                            <button
                                type="button"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                            >
                                {showConfirmPassword ? (
                                    <EyeOff className="w-5 h-5" strokeWidth={1.5} />
                                ) : (
                                    <Eye className="w-5 h-5" strokeWidth={1.5} />
                                )}
                            </button>
                        </div>
                        {errors.confirmPassword && (
                            <p className="text-xs text-red-500">{errors.confirmPassword}</p>
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
                                Inscription...
                            </>
                        ) : (
                            "S'inscrire"
                        )}
                    </Button>
                </form>

                {/* Login Link */}
                <div className="mt-6 text-center">
                    <p className="text-sm text-slate-600">
                        Déjà un compte ?{" "}
                        <Link
                            href="/login"
                            className="font-medium text-[#FF9A3E] hover:text-[#E88A35] transition-colors"
                        >
                            Se connecter
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
