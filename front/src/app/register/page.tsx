"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import Image from "next/image";
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
import { Mail, Eye, EyeOff, Loader2, User, Phone, Lock } from "lucide-react";

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
    const [isGoogleLoading, setIsGoogleLoading] = useState(false);
    const [apiError, setApiError] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);


    useEffect(() => {
        if (!authLoading && isAuthenticated) {
            router.replace("/dashboard");
        }
    }, [authLoading, isAuthenticated, router]);

    if (authLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#F8FAFC]">
                <Loader2 className="w-8 h-8 animate-spin text-[#006B8F]" />
            </div>
        );
    }

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
            const registerData = {
                email: formData.email,
                password: formData.password,
                firstName: formData.firstName,
                lastName: formData.lastName,
                phoneNumber: formData.phoneNumber,
                gender: formData.gender,
                birthDate: formData.birthDate,
            };

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

            if (response.status === 429) {
                throw new Error("Trop de tentatives. Veuillez réessayer plus tard.");
            }

            if (response.status === 409) {
                throw new Error("Cet email est déjà utilisé.");
            }

            if (!response.ok) {
                let errorMessage = "Erreur lors de l'inscription";
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch {

                }
                throw new Error(errorMessage);
            }
            const data = await response.json();
            const registeredEmail = data.email || formData.email;

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

            const params = new URLSearchParams({
                email: registeredEmail,
                ...(data.verificationSentAt && { sentAt: data.verificationSentAt }),
            });
            router.push(`/verify-email?${params.toString()}`);
        } catch (error) {

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

                <div className="text-center mb-6">
                    <h1 className="text-[22px] font-bold text-slate-900 font-[family-name:var(--font-poppins)] mb-2">
                        Créer un compte
                    </h1>
                    <p className="text-sm text-slate-500">
                        Rejoignez la communauté Blassa
                    </p>
                </div>

                <Button
                    type="button"
                    onClick={() => {
                        setIsGoogleLoading(true);
                        window.location.href = `${process.env.NEXT_PUBLIC_API_URL?.replace('/api/v1', '')}/oauth2/authorization/google`;
                    }}
                    variant="outline"
                    className="w-full h-12 flex items-center justify-center gap-3 border-slate-200 hover:bg-slate-50 text-slate-700 font-medium rounded-xl transition-all duration-200 mb-5"
                    disabled={isLoading || isGoogleLoading}
                >
                    {isGoogleLoading ? (
                        <>
                            <Loader2 className="w-5 h-5 animate-spin" />
                            Redirection...
                        </>
                    ) : (
                        <>

                            <svg className="w-5 h-5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
                                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
                                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
                                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
                            </svg>
                            Continuer avec Google
                        </>
                    )}
                </Button>

                <div className="relative mb-5">
                    <div className="absolute inset-0 flex items-center">
                        <span className="w-full border-t border-slate-200" />
                    </div>
                    <div className="relative flex justify-center text-xs uppercase">
                        <span className="bg-white px-3 text-slate-400">ou inscrivez-vous par email</span>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5">

                    {apiError && (
                        <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
                            {apiError}
                        </div>
                    )}

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
                                    className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.firstName ? "border-red-400" : "border-slate-200"
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
                                    className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.lastName ? "border-red-400" : "border-slate-200"
                                        }`}
                                />
                            </div>
                            {errors.lastName && (
                                <p className="text-xs text-red-500">{errors.lastName}</p>
                            )}
                        </div>
                    </div>

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
                                className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.email ? "border-red-400" : "border-slate-200"
                                    }`}
                            />
                        </div>
                        {errors.email && (
                            <p className="text-xs text-red-500">{errors.email}</p>
                        )}
                    </div>

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
                                className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.phoneNumber ? "border-red-400" : "border-slate-200"
                                    }`}
                            />
                        </div>
                        {errors.phoneNumber && (
                            <p className="text-xs text-red-500">{errors.phoneNumber}</p>
                        )}
                    </div>

                    <div className="grid grid-cols-2 gap-3">
                        <div className="space-y-2">
                            <label className="block text-sm font-medium text-slate-700">
                                Genre
                            </label>
                            <Select value={formData.gender} onValueChange={handleGenderChange}>
                                <SelectTrigger
                                    className={`h-12 bg-slate-50 border rounded-xl text-sm focus:ring-2 focus:ring-[var(--color-blassa-teal)] ${errors.gender ? "border-red-400" : "border-slate-200"
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
                                    className={`w-full h-12 px-4 bg-slate-50 border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.birthDate ? "border-red-400" : "border-slate-200"
                                        }`}
                                />
                            </div>
                            {errors.birthDate && (
                                <p className="text-xs text-red-500">{errors.birthDate}</p>
                            )}
                        </div>
                    </div>

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
                                className={`w-full h-12 pl-12 pr-12 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.password ? "border-red-400" : "border-slate-200"
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
                                className={`w-full h-12 pl-12 pr-12 bg-slate-50 border rounded-xl text-sm placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-[var(--color-blassa-teal)] focus:border-transparent transition-all ${errors.confirmPassword ? "border-red-400" : "border-slate-200"
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

                    <Button
                        type="submit"
                        className="w-full h-12 text-[15px] font-medium bg-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal-dark)] text-white rounded-xl transition-all duration-200 shadow-sm"
                        disabled={isLoading || isGoogleLoading}
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
