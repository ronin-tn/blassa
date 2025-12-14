"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Gender, GenderLabels } from "@/lib/constants";
import { Phone, Calendar, User, Loader2, X } from "lucide-react";

interface CompleteProfileModalProps {
    isOpen: boolean;
    onComplete: () => void;
    token: string;
    currentData: {
        firstName: string;
        lastName: string;
        phoneNumber: string | null;
        dob: string | null;
        gender: "MALE" | "FEMALE" | null;
        bio: string | null;
        facebookUrl: string | null;
        instagramUrl: string | null;
    };
}

interface FormData {
    phoneNumber: string;
    birthDate: string;
    gender: string;
}

interface FormErrors {
    phoneNumber?: string;
    birthDate?: string;
    gender?: string;
}

export default function CompleteProfileModal({
    isOpen,
    onComplete,
    token,
    currentData,
}: CompleteProfileModalProps) {
    const [formData, setFormData] = useState<FormData>({
        phoneNumber: currentData.phoneNumber || "+216",
        birthDate: currentData.dob || "",
        gender: currentData.gender || "",
    });
    const [errors, setErrors] = useState<FormErrors>({});
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState("");

    if (!isOpen) return null;

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

        if (!formData.phoneNumber) {
            newErrors.phoneNumber = "Numéro de téléphone requis";
        } else if (!/^\+[1-9][0-9]{7,14}$/.test(formData.phoneNumber)) {
            newErrors.phoneNumber = "Format: +21612345678";
        }

        if (!formData.birthDate) {
            newErrors.birthDate = "Date de naissance requise";
        }

        if (!formData.gender) {
            newErrors.gender = "Genre requis";
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
            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/user/me`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        firstName: currentData.firstName,
                        lastName: currentData.lastName,
                        phoneNumber: formData.phoneNumber,
                        bio: currentData.bio || "",
                        facebookUrl: currentData.facebookUrl || "",
                        instagramUrl: currentData.instagramUrl || "",
                        dob: formData.birthDate,
                        gender: formData.gender,
                    }),
                }
            );

            if (!response.ok) {
                let errorMessage = "Erreur lors de la mise à jour du profil";
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch {
                    // Response is not JSON
                }
                throw new Error(errorMessage);
            }

            onComplete();
        } catch (error) {
            const message =
                error instanceof Error ? error.message : "Une erreur est survenue";
            setApiError(message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* Backdrop */}
            <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" />

            {/* Modal */}
            <div className="relative w-full max-w-md bg-white rounded-2xl shadow-2xl p-8 mx-4 animate-in fade-in zoom-in duration-200">
                {/* Header */}
                <div className="text-center mb-6">
                    <div className="w-16 h-16 bg-gradient-to-br from-[#006B8F] to-[#005673] rounded-full flex items-center justify-center mx-auto mb-4">
                        <User className="w-8 h-8 text-white" />
                    </div>
                    <h2 className="text-xl font-bold text-slate-900 font-[family-name:var(--font-poppins)]">
                        Complétez votre profil
                    </h2>
                    <p className="text-sm text-slate-500 mt-2">
                        Ces informations sont nécessaires pour utiliser Blassa
                    </p>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} className="space-y-5">
                    {/* API Error */}
                    {apiError && (
                        <div className="p-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm flex items-center gap-2">
                            <X className="w-4 h-4 shrink-0" />
                            {apiError}
                        </div>
                    )}

                    {/* Phone Number */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Numéro de téléphone
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

                    {/* Date of Birth */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Date de naissance
                        </label>
                        <div className="relative">
                            <div className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">
                                <Calendar className="w-5 h-5" strokeWidth={1.5} />
                            </div>
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
                                className={`w-full h-12 pl-12 pr-4 bg-slate-50 border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-[#006B8F] focus:border-transparent transition-all ${errors.birthDate ? "border-red-400" : "border-slate-200"
                                    }`}
                            />
                        </div>
                        {errors.birthDate && (
                            <p className="text-xs text-red-500">{errors.birthDate}</p>
                        )}
                    </div>

                    {/* Gender */}
                    <div className="space-y-2">
                        <label className="block text-sm font-medium text-slate-700">
                            Genre
                        </label>
                        <Select value={formData.gender} onValueChange={handleGenderChange}>
                            <SelectTrigger
                                className={`h-12 bg-slate-50 border rounded-xl text-sm focus:ring-2 focus:ring-[#006B8F] ${errors.gender ? "border-red-400" : "border-slate-200"
                                    }`}
                            >
                                <SelectValue placeholder="Sélectionnez votre genre" />
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

                    {/* Submit Button */}
                    <Button
                        type="submit"
                        className="w-full h-12 text-[15px] font-medium bg-[#006B8F] hover:bg-[#005673] text-white rounded-xl transition-all duration-200 shadow-sm"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <>
                                <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                                Enregistrement...
                            </>
                        ) : (
                            "Continuer"
                        )}
                    </Button>
                </form>

                {/* Note */}
                <p className="text-xs text-slate-400 text-center mt-4">
                    Pour les trajets, les conducteurs doivent pouvoir vous contacter
                </p>
            </div>
        </div>
    );
}
