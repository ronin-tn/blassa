"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import {
    ArrowLeft,
    MapPin,
    Calendar,
    Clock,
    Users,
    DollarSign,
    Cigarette,
    User,
    Loader2,
    CheckCircle,
    AlertCircle,
} from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { TUNISIA_CITIES, City } from "@/data/cities";
import { parseApiError } from "@/lib/api-utils";

type GenderPreference = "MALE_ONLY" | "FEMALE_ONLY" | "ANY";

interface RideFormData {
    originCode: string;
    destinationCode: string;
    departureDate: string;
    departureTime: string;
    totalSeats: number;
    pricePerSeat: string;
    allowsSmoking: boolean;
    genderPreference: GenderPreference;
}

interface RideResponse {
    id: string;
    originName: string;
    originLat: number;
    originLon: number;
    destinationName: string;
    destinationLat: number;
    destinationLon: number;
    departureTime: string;
    totalSeats: number;
    availableSeats: number;
    pricePerSeat: number;
    allowsSmoking: boolean;
    genderPreference: GenderPreference;
    status: string;
}

export default function EditRidePage() {
    const router = useRouter();
    const params = useParams();
    const rideId = params.id as string;
    const { isAuthenticated, isLoading: authLoading } = useAuth();

    const [isLoading, setIsLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);

    const [formData, setFormData] = useState<RideFormData>({
        originCode: "",
        destinationCode: "",
        departureDate: "",
        departureTime: "",
        totalSeats: 3,
        pricePerSeat: "",
        allowsSmoking: false,
        genderPreference: "ANY",
    });

    useEffect(() => {
        const fetchRide = async () => {
            if (!rideId) return;

            try {
                const response = await fetch(
                    `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}`,
                    { credentials: "include" }
                );

                if (!response.ok) {
                    throw new Error("Trajet non trouvé");
                }

                const ride: RideResponse = await response.json();

                const originCity = TUNISIA_CITIES.find(
                    (c) => c.name === ride.originName
                );
                const destCity = TUNISIA_CITIES.find(
                    (c) => c.name === ride.destinationName
                );

                const departureDate = new Date(ride.departureTime);
                const dateStr = departureDate.toISOString().split("T")[0];
                const timeStr = departureDate.toTimeString().slice(0, 5);

                setFormData({
                    originCode: originCity?.code || "",
                    destinationCode: destCity?.code || "",
                    departureDate: dateStr,
                    departureTime: timeStr,
                    totalSeats: ride.totalSeats,
                    pricePerSeat: ride.pricePerSeat.toString(),
                    allowsSmoking: ride.allowsSmoking || false,
                    genderPreference: ride.genderPreference || "ANY",
                });
            } catch (err) {
                setError(err instanceof Error ? err.message : "Erreur de chargement");
            } finally {
                setIsLoading(false);
            }
        };

        if (!authLoading) {
            if (!isAuthenticated) {
                router.replace(`/login?redirect=/rides/${rideId}/edit`);
            } else {
                fetchRide();
            }
        }
    }, [authLoading, isAuthenticated, rideId, router]);

    const getMinDate = () => {
        const today = new Date();
        return today.toISOString().split("T")[0];
    };

    const handleInputChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
    ) => {
        const { name, value, type } = e.target;

        if (type === "checkbox") {
            const checked = (e.target as HTMLInputElement).checked;
            setFormData((prev) => ({ ...prev, [name]: checked }));
        } else {
            setFormData((prev) => ({ ...prev, [name]: value }));
        }
    };

    const handleSeatsChange = (delta: number) => {
        setFormData((prev) => ({
            ...prev,
            totalSeats: Math.max(1, Math.min(8, prev.totalSeats + delta)),
        }));
    };

    const getCityByCode = (code: string): City | undefined => {
        return TUNISIA_CITIES.find((c) => c.code === code);
    };

    const validateForm = (): string | null => {
        if (!formData.originCode) return "Veuillez sélectionner une ville de départ";
        if (!formData.destinationCode) return "Veuillez sélectionner une ville d'arrivée";
        if (formData.originCode === formData.destinationCode) {
            return "La ville de départ et d'arrivée doivent être différentes";
        }
        if (!formData.departureDate) return "Veuillez sélectionner une date de départ";
        if (!formData.departureTime) return "Veuillez sélectionner une heure de départ";
        if (!formData.pricePerSeat || parseFloat(formData.pricePerSeat) <= 0) {
            return "Veuillez entrer un prix valide";
        }

        const departureDateTime = new Date(`${formData.departureDate}T${formData.departureTime}`);
        if (departureDateTime <= new Date()) {
            return "La date et l'heure de départ doivent être dans le futur";
        }

        return null;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        const validationError = validateForm();
        if (validationError) {
            setError(validationError);
            return;
        }

        setIsSubmitting(true);

        try {
            const origin = getCityByCode(formData.originCode);
            const destination = getCityByCode(formData.destinationCode);

            if (!origin || !destination) {
                throw new Error("Ville invalide");
            }

            const departureTime = `${formData.departureDate}T${formData.departureTime}:00+01:00`;

            const requestBody = {
                originName: origin.name,
                originLat: origin.lat,
                originLon: origin.lon,
                destinationName: destination.name,
                destinationLat: destination.lat,
                destinationLon: destination.lon,
                departureTime,
                totalSeats: formData.totalSeats,
                pricePerSeat: parseFloat(formData.pricePerSeat),
                allowsSmoking: formData.allowsSmoking,
                genderPreference: formData.genderPreference,
            };

            const response = await fetch(
                `${process.env.NEXT_PUBLIC_API_URL}/rides/${rideId}`,
                {
                    method: "PUT",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(requestBody),
                }
            );

            if (!response.ok) {
                const errorMessage = await parseApiError(response, "Erreur lors de la modification");
                throw new Error(errorMessage);
            }

            setSuccess(true);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (authLoading || isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="flex flex-col items-center gap-4">
                    <Loader2 className="w-10 h-10 text-[#0A8F8F] animate-spin" />
                    <span className="text-gray-600">Chargement...</span>
                </div>
            </div>
        );
    }

    if (success) {
        return (
            <div className="min-h-screen bg-gray-50 py-8 px-4">
                <div className="max-w-md mx-auto">
                    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 text-center">
                        <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-green-100 flex items-center justify-center">
                            <CheckCircle className="w-10 h-10 text-green-600" />
                        </div>
                        <h1 className="text-2xl font-bold text-gray-900 mb-2">
                            Trajet modifié !
                        </h1>
                        <p className="text-gray-600 mb-8">
                            Votre trajet a été mis à jour avec succès.
                        </p>
                        <div className="space-y-3">
                            <Link
                                href={`/rides/${rideId}`}
                                className="block w-full py-3 bg-[#0A8F8F] text-white font-medium rounded-xl hover:bg-[#0A8F8F]/90 transition-colors"
                            >
                                Voir mon trajet
                            </Link>
                            <Link
                                href="/dashboard/rides"
                                className="block w-full py-3 border border-gray-300 text-gray-700 font-medium rounded-xl hover:bg-gray-50 transition-colors"
                            >
                                Mes trajets
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4 pb-24">
            <div className="max-w-2xl mx-auto">
                <div className="flex items-center gap-4 mb-6">
                    <Link
                        href={`/rides/${rideId}`}
                        className="flex items-center gap-2 text-gray-600 hover:text-[#0A8F8F] transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5" />
                        <span>Retour</span>
                    </Link>
                    <h1 className="text-2xl font-bold text-gray-900">Modifier le trajet</h1>
                </div>

                {error && (
                    <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3">
                        <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                        <p className="text-red-700">{error}</p>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-6">
                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                        <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                            <MapPin className="w-5 h-5 text-[#0A8F8F]" />
                            Itinéraire
                        </h2>

                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Ville de départ
                                </label>
                                <select
                                    name="originCode"
                                    value={formData.originCode}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] bg-white"
                                >
                                    <option value="">Sélectionner une ville</option>
                                    {TUNISIA_CITIES.map((city) => (
                                        <option key={city.code} value={city.code}>
                                            {city.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Ville d&apos;arrivée
                                </label>
                                <select
                                    name="destinationCode"
                                    value={formData.destinationCode}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F] bg-white"
                                >
                                    <option value="">Sélectionner une ville</option>
                                    {TUNISIA_CITIES.map((city) => (
                                        <option key={city.code} value={city.code}>
                                            {city.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </section>

                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                        <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                            <Calendar className="w-5 h-5 text-[#0A8F8F]" />
                            Date et heure
                        </h2>

                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Date de départ
                                </label>
                                <input
                                    type="date"
                                    name="departureDate"
                                    value={formData.departureDate}
                                    onChange={handleInputChange}
                                    min={getMinDate()}
                                    className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F]"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Heure de départ
                                </label>
                                <div className="relative">
                                    <Clock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                                    <input
                                        type="time"
                                        name="departureTime"
                                        value={formData.departureTime}
                                        onChange={handleInputChange}
                                        className="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F]"
                                    />
                                </div>
                            </div>
                        </div>
                    </section>

                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                        <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                            <Users className="w-5 h-5 text-[#0A8F8F]" />
                            Places et prix
                        </h2>

                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Nombre de places disponibles
                                </label>
                                <div className="flex items-center gap-4">
                                    <button
                                        type="button"
                                        onClick={() => handleSeatsChange(-1)}
                                        disabled={formData.totalSeats <= 1}
                                        className="w-12 h-12 rounded-xl border border-gray-200 flex items-center justify-center text-xl font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        −
                                    </button>
                                    <span className="text-2xl font-bold text-gray-900 w-12 text-center">
                                        {formData.totalSeats}
                                    </span>
                                    <button
                                        type="button"
                                        onClick={() => handleSeatsChange(1)}
                                        disabled={formData.totalSeats >= 8}
                                        className="w-12 h-12 rounded-xl border border-gray-200 flex items-center justify-center text-xl font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        +
                                    </button>
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Prix par place (TND)
                                </label>
                                <div className="relative">
                                    <DollarSign className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                                    <input
                                        type="number"
                                        name="pricePerSeat"
                                        value={formData.pricePerSeat}
                                        onChange={handleInputChange}
                                        placeholder="15.00"
                                        step="0.5"
                                        min="0"
                                        className="w-full pl-12 pr-16 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0A8F8F]/20 focus:border-[#0A8F8F]"
                                    />
                                    <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500">
                                        TND
                                    </span>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                        <h2 className="flex items-center gap-2 text-lg font-semibold text-gray-900 mb-4">
                            <User className="w-5 h-5 text-[#0A8F8F]" />
                            Préférences
                        </h2>

                        <div className="space-y-4">
                            <label className="flex items-center justify-between p-4 border border-gray-200 rounded-xl cursor-pointer hover:bg-gray-50">
                                <div className="flex items-center gap-3">
                                    <Cigarette className="w-5 h-5 text-gray-400" />
                                    <div>
                                        <p className="font-medium text-gray-900">Fumeur autorisé</p>
                                        <p className="text-sm text-gray-500">Autoriser les fumeurs dans votre véhicule</p>
                                    </div>
                                </div>
                                <input
                                    type="checkbox"
                                    name="allowsSmoking"
                                    checked={formData.allowsSmoking}
                                    onChange={handleInputChange}
                                    className="w-5 h-5 text-[#0A8F8F] border-gray-300 rounded focus:ring-[#0A8F8F]"
                                />
                            </label>

                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Préférence de genre
                                </label>
                                <div className="grid grid-cols-3 gap-3">
                                    {[
                                        { value: "ANY", label: "Mixte" },
                                        { value: "MALE_ONLY", label: "Hommes" },
                                        { value: "FEMALE_ONLY", label: "Femmes" },
                                    ].map((option) => (
                                        <button
                                            key={option.value}
                                            type="button"
                                            onClick={() =>
                                                setFormData((prev) => ({
                                                    ...prev,
                                                    genderPreference: option.value as GenderPreference,
                                                }))
                                            }
                                            className={`py-3 px-4 rounded-xl border text-sm font-medium transition-colors ${formData.genderPreference === option.value
                                                ? "border-[#0A8F8F] bg-[#0A8F8F]/10 text-[#0A8F8F]"
                                                : "border-gray-200 text-gray-700 hover:bg-gray-50"
                                                }`}
                                        >
                                            {option.label}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </section>

                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className="w-full py-4 bg-[#0A8F8F] text-white font-semibold rounded-xl hover:bg-[#0A8F8F]/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                    >
                        {isSubmitting ? (
                            <>
                                <Loader2 className="w-5 h-5 animate-spin" />
                                Modification en cours...
                            </>
                        ) : (
                            "Enregistrer les modifications"
                        )}
                    </button>
                </form>
            </div>
        </div>
    );
}
