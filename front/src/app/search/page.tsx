"use client";

import { useState, useEffect, useCallback, Suspense, useMemo } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";
import {
    ArrowLeft,
    Users,
    Cigarette,
    User,
    Loader2,
    AlertCircle,
    Search,
    Star,
    ChevronRight,
    Shield,
    UserCheck,
    Filter,
} from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { Ride, PagedResponse, RideGenderPreference } from "@/types/ride";
import { TUNISIA_CITIES } from "@/data/cities";

function SearchPageContent() {
    const searchParams = useSearchParams();
    const router = useRouter();
    const { user, isAuthenticated } = useAuth();

    const [rides, setRides] = useState<Ride[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const [bookingRideId, setBookingRideId] = useState<string | null>(null);
    const [bookingSeats, setBookingSeats] = useState<number>(1);
    const [isBooking, setIsBooking] = useState(false);
    const [bookingError, setBookingError] = useState<string | null>(null);
    const [bookingSuccess, setBookingSuccess] = useState<string | null>(null);
    const [myBookedRideIds, setMyBookedRideIds] = useState<string[]>([]);

    // Clean URL params
    const from = searchParams.get("from") || "";
    const to = searchParams.get("to") || "";
    const date = searchParams.get("date") || "";
    const passengers = searchParams.get("p") || "1";
    const genderFilter = searchParams.get("g") || "";

    // Look up coordinates from city names
    const originCity = useMemo(
        () => TUNISIA_CITIES.find((c) => c.name.toLowerCase() === from.toLowerCase()),
        [from]
    );
    const destCity = useMemo(
        () => TUNISIA_CITIES.find((c) => c.name.toLowerCase() === to.toLowerCase()),
        [to]
    );

    useEffect(() => {
        const fetchMyBookedRides = async () => {
            if (!isAuthenticated) return;
            try {
                const token = localStorage.getItem("blassa_token");
                if (!token) return;
                const response = await fetch(
                    `${process.env.NEXT_PUBLIC_API_URL}/bookings/mine/ride-ids`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                if (response.ok) {
                    const ids = await response.json();
                    setMyBookedRideIds(ids);
                }
            } catch (err) {
                console.error("Failed to fetch booked ride IDs:", err);
            }
        };
        fetchMyBookedRides();
    }, [isAuthenticated]);

    const fetchRides = useCallback(
        async (page: number) => {
            if (!originCity || !destCity) {
                setError("Ville de départ ou d'arrivée non reconnue");
                setIsLoading(false);
                return;
            }
            setIsLoading(true);
            setError(null);

            try {
                const params = new URLSearchParams({
                    originLat: originCity.lat.toString(),
                    originLon: originCity.lon.toString(),
                    destLat: destCity.lat.toString(),
                    destLon: destCity.lon.toString(),
                    seats: passengers,
                    page: page.toString(),
                    size: "10",
                });
                if (date) params.append("departureTime", `${date}T00:00:00`);
                if (genderFilter && genderFilter !== "ANY") {
                    params.append("genderFilter", genderFilter);
                }

                const headers: HeadersInit = {};
                const token = localStorage.getItem("blassa_token");
                if (token) headers["Authorization"] = `Bearer ${token}`;

                const response = await fetch(
                    `${process.env.NEXT_PUBLIC_API_URL}/rides/search?${params.toString()}`,
                    { headers }
                );

                if (!response.ok) throw new Error("Erreur lors de la recherche");

                const data: PagedResponse<Ride> = await response.json();
                setRides(data.content);
                setTotalPages(data.totalPages);
                setTotalElements(data.totalElements);
                setCurrentPage(data.number);
            } catch (err) {
                setError(err instanceof Error ? err.message : "Erreur inconnue");
            } finally {
                setIsLoading(false);
            }
        },
        [originCity, destCity, date, passengers, genderFilter]
    );

    useEffect(() => {
        fetchRides(0);
    }, [fetchRides]);

    const handleBooking = async (rideId: string, seats: number) => {
        if (!isAuthenticated) {
            router.push(`/login?redirect=/search?${searchParams.toString()}`);
            return;
        }
        setIsBooking(true);
        setBookingError(null);
        setBookingSuccess(null);

        try {
            const token = localStorage.getItem("blassa_token");
            if (!token) throw new Error("Non authentifié");

            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/bookings`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ rideId, seatsRequested: seats }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || "Erreur lors de la réservation");
            }

            setBookingSuccess("Réservation effectuée avec succès !");
            setMyBookedRideIds((prev) => [...prev, rideId]);
            setBookingRideId(null);
            setTimeout(() => setBookingSuccess(null), 3000);
        } catch (err) {
            setBookingError(err instanceof Error ? err.message : "Erreur inconnue");
        } finally {
            setIsBooking(false);
        }
    };

    const formatTime = (dateStr: string) => {
        const d = new Date(dateStr);
        return d.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
    };

    const isOwnRide = (ride: Ride) => user?.email === ride.driverEmail;
    const hasAlreadyBooked = (rideId: string) => myBookedRideIds.includes(rideId);
    const canBook = (ride: Ride) => {
        if (isOwnRide(ride)) return false;
        if (hasAlreadyBooked(ride.id)) return false;
        if (ride.availableSeats <= 0) return false;
        if (ride.status !== "SCHEDULED") return false;
        return true;
    };

    const getGenderBadge = (pref: RideGenderPreference) => {
        if (pref === "ANY") return null;
        const isWomen = pref === "FEMALE_ONLY";
        return {
            bg: isWomen ? "bg-pink-50 border-pink-200" : "bg-blue-50 border-blue-200",
            text: isWomen ? "text-pink-700" : "text-blue-700",
            icon: isWomen ? "♀" : "♂",
            label: isWomen ? "Femmes uniquement" : "Hommes uniquement",
        };
    };

    return (
        <div className="min-h-screen bg-slate-50">
            <div className="bg-white border-b border-slate-200 sticky top-0 z-10">
                <div className="max-w-5xl mx-auto px-4 py-4">
                    <div className="flex items-center gap-4">
                        <Link href="/" className="p-2 -ml-2 rounded-lg hover:bg-slate-100">
                            <ArrowLeft className="w-5 h-5 text-slate-600" />
                        </Link>
                        <div className="flex-1">
                            <h1 className="font-semibold text-slate-900">{from} → {to}</h1>
                            <p className="text-sm text-slate-500">
                                {date
                                    ? new Date(date).toLocaleDateString("fr-FR", {
                                        weekday: "long",
                                        day: "numeric",
                                        month: "long",
                                    })
                                    : "Toutes les dates"}{" "}
                                · {passengers} passager{parseInt(passengers) > 1 ? "s" : ""}
                            </p>
                        </div>
                        <button className="p-2 rounded-lg border border-slate-200 hover:bg-slate-50">
                            <Filter className="w-5 h-5 text-slate-600" />
                        </button>
                    </div>
                </div>
            </div>

            <div className="max-w-5xl mx-auto px-4 py-6">
                {bookingSuccess && (
                    <div className="mb-6 p-4 bg-emerald-50 border border-emerald-200 text-emerald-700 rounded-xl flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center">✓</div>
                        <p className="font-medium">{bookingSuccess}</p>
                    </div>
                )}

                {error && (
                    <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3">
                        <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                        <p className="text-red-700">{error}</p>
                    </div>
                )}

                {isLoading ? (
                    <div className="flex flex-col items-center justify-center py-20">
                        <Loader2 className="w-10 h-10 text-[#0A8F8F] animate-spin mb-4" />
                        <p className="text-slate-600">Recherche en cours...</p>
                    </div>
                ) : rides.length === 0 ? (
                    <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
                        <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-slate-100 flex items-center justify-center">
                            <Search className="w-10 h-10 text-slate-400" />
                        </div>
                        <h2 className="text-xl font-semibold text-slate-900 mb-2">Aucun trajet trouvé</h2>
                        <p className="text-slate-600 mb-6 max-w-sm mx-auto">
                            Essayez avec d&apos;autres dates ou destinations.
                        </p>
                        <Link
                            href="/"
                            className="inline-flex items-center gap-2 px-6 py-3 bg-[#0A8F8F] text-white font-medium rounded-xl hover:bg-[#0A8F8F]/90"
                        >
                            Nouvelle recherche
                        </Link>
                    </div>
                ) : (
                    <>
                        <p className="text-sm text-slate-500 mb-4">
                            {totalElements} trajet{totalElements > 1 ? "s" : ""} disponible{totalElements > 1 ? "s" : ""}
                        </p>

                        <div className="space-y-4">
                            {rides.map((ride) => {
                                const genderBadge = getGenderBadge(ride.genderPreference);
                                const isOwn = isOwnRide(ride);
                                const alreadyBooked = hasAlreadyBooked(ride.id);
                                const bookable = canBook(ride);

                                return (
                                    <div
                                        key={ride.id}
                                        className="bg-white rounded-2xl border border-slate-200 overflow-hidden hover:border-slate-300 hover:shadow-md transition-all"
                                    >
                                        {bookingRideId === ride.id ? (
                                            <div className="p-6">
                                                <div className="flex items-center justify-between mb-4">
                                                    <h3 className="font-semibold text-slate-900">Confirmer la réservation</h3>
                                                    <button
                                                        onClick={() => {
                                                            setBookingRideId(null);
                                                            setBookingError(null);
                                                            setBookingSeats(1);
                                                        }}
                                                        className="text-slate-500 hover:text-slate-700 text-xl"
                                                    >
                                                        ×
                                                    </button>
                                                </div>
                                                <div className="bg-slate-50 rounded-xl p-4 mb-4">
                                                    <div className="flex items-center justify-between mb-3">
                                                        <span className="text-slate-600">Nombre de places</span>
                                                        <div className="flex items-center gap-3">
                                                            <button
                                                                onClick={() => setBookingSeats((s) => Math.max(1, s - 1))}
                                                                className="w-8 h-8 rounded-full border-2 border-slate-300 flex items-center justify-center"
                                                            >
                                                                −
                                                            </button>
                                                            <span className="text-xl font-bold w-8 text-center">{bookingSeats}</span>
                                                            <button
                                                                onClick={() => setBookingSeats((s) => Math.min(ride.availableSeats, Math.min(4, s + 1)))}
                                                                className="w-8 h-8 rounded-full border-2 border-slate-300 flex items-center justify-center"
                                                            >
                                                                +
                                                            </button>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center justify-between pt-3 border-t border-slate-200">
                                                        <span className="font-medium">Total à payer</span>
                                                        <span className="text-2xl font-bold text-[#0A8F8F]">
                                                            {(ride.pricePerSeat * bookingSeats).toFixed(0)} TND
                                                        </span>
                                                    </div>
                                                </div>
                                                {bookingError && <p className="text-sm text-red-600 mb-4">{bookingError}</p>}
                                                <button
                                                    onClick={() => handleBooking(ride.id, bookingSeats)}
                                                    disabled={isBooking}
                                                    className="w-full py-3.5 bg-[#0A8F8F] text-white font-semibold rounded-xl disabled:opacity-50 flex items-center justify-center gap-2"
                                                >
                                                    {isBooking ? (
                                                        <>
                                                            <Loader2 className="w-5 h-5 animate-spin" />
                                                            Réservation...
                                                        </>
                                                    ) : (
                                                        "Confirmer la réservation"
                                                    )}
                                                </button>
                                            </div>
                                        ) : (
                                            <Link href={`/rides/${ride.id}`} className="block">
                                                <div className="p-5">
                                                    <div className="flex items-start gap-4 mb-4">
                                                        <div className="text-center flex-shrink-0 w-16">
                                                            <div className="text-2xl font-bold text-slate-900">{formatTime(ride.departureTime)}</div>
                                                            <div className="text-xs text-slate-500 mt-0.5">
                                                                {new Date(ride.departureTime).toLocaleDateString("fr-FR", { day: "numeric", month: "short" })}
                                                            </div>
                                                        </div>
                                                        <div className="flex-1 min-w-0">
                                                            <div className="flex items-center gap-3">
                                                                <div className="flex flex-col items-center">
                                                                    <div className="w-2.5 h-2.5 rounded-full bg-[#0A8F8F]" />
                                                                    <div className="w-0.5 h-8 bg-gradient-to-b from-[#0A8F8F] to-orange-400" />
                                                                    <div className="w-2.5 h-2.5 rounded-full bg-orange-400" />
                                                                </div>
                                                                <div className="flex-1 space-y-2">
                                                                    <p className="font-medium text-slate-900 truncate">{ride.originName}</p>
                                                                    <p className="font-medium text-slate-900 truncate">{ride.destinationName}</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div className="text-right flex-shrink-0">
                                                            <div className="text-2xl font-bold text-[#0A8F8F]">
                                                                {ride.pricePerSeat.toFixed(0)} <span className="text-base">TND</span>
                                                            </div>
                                                            <div className="text-xs text-slate-500">par place</div>
                                                        </div>
                                                    </div>
                                                    <div className="border-t border-slate-100 my-4" />
                                                    <div className="flex items-center justify-between">
                                                        <div className="flex items-center gap-3">
                                                            <div className="relative">
                                                                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-[#0A8F8F]/20 to-[#0A8F8F]/10 flex items-center justify-center">
                                                                    <User className="w-6 h-6 text-[#0A8F8F]" />
                                                                </div>
                                                                <div className="absolute -bottom-0.5 -right-0.5 w-5 h-5 bg-emerald-500 rounded-full flex items-center justify-center border-2 border-white">
                                                                    <Shield className="w-3 h-3 text-white" />
                                                                </div>
                                                            </div>
                                                            <div>
                                                                <p className="font-medium text-slate-900">{ride.driverName}</p>
                                                                <div className="flex items-center gap-2 text-sm">
                                                                    {ride.driverRating && ride.driverRating > 0 ? (
                                                                        <span className="flex items-center gap-1 text-amber-600">
                                                                            <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
                                                                            <span className="font-medium">{ride.driverRating.toFixed(1)}</span>
                                                                        </span>
                                                                    ) : (
                                                                        <span className="text-slate-500 flex items-center gap-1">
                                                                            <UserCheck className="w-3.5 h-3.5" />
                                                                            Nouveau
                                                                        </span>
                                                                    )}
                                                                    <span className="text-slate-300">·</span>
                                                                    <span className="text-emerald-600 flex items-center gap-1">
                                                                        <Shield className="w-3.5 h-3.5" />
                                                                        Vérifié
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div className="flex items-center gap-3">
                                                            <div className="flex items-center gap-2">
                                                                {ride.allowsSmoking && (
                                                                    <span className="px-2 py-1 text-xs font-medium bg-slate-100 text-slate-600 rounded-lg">
                                                                        <Cigarette className="w-3 h-3" />
                                                                    </span>
                                                                )}
                                                                {genderBadge && (
                                                                    <span className={`px-2.5 py-1 text-xs font-medium border rounded-lg flex items-center gap-1 ${genderBadge.bg} ${genderBadge.text}`}>
                                                                        <span>{genderBadge.icon}</span>
                                                                        {genderBadge.label}
                                                                    </span>
                                                                )}
                                                            </div>
                                                            <span className="flex items-center gap-1.5 text-sm text-slate-600 bg-slate-50 px-2.5 py-1.5 rounded-lg">
                                                                <Users className="w-4 h-4" />
                                                                {ride.availableSeats}
                                                            </span>
                                                            <ChevronRight className="w-5 h-5 text-slate-400" />
                                                        </div>
                                                    </div>
                                                </div>
                                                {(isOwn || alreadyBooked || !bookable) && (
                                                    <div className="bg-slate-50 px-5 py-3 border-t border-slate-100">
                                                        <span className={`text-sm font-medium ${isOwn ? "text-slate-500" : alreadyBooked ? "text-emerald-600" : "text-orange-600"}`}>
                                                            {isOwn
                                                                ? "Vous êtes le conducteur"
                                                                : alreadyBooked
                                                                    ? "✓ Déjà réservé"
                                                                    : ride.availableSeats <= 0
                                                                        ? "Complet"
                                                                        : "Non disponible"}
                                                        </span>
                                                    </div>
                                                )}
                                            </Link>
                                        )}
                                        {bookable && bookingRideId !== ride.id && (
                                            <div className="px-5 pb-5">
                                                <button
                                                    onClick={(e) => {
                                                        e.preventDefault();
                                                        setBookingRideId(ride.id);
                                                        setBookingSeats(Math.min(parseInt(passengers), ride.availableSeats));
                                                    }}
                                                    className="w-full py-3 bg-[#0A8F8F] text-white font-semibold rounded-xl hover:bg-[#0A8F8F]/90 flex items-center justify-center gap-2"
                                                >
                                                    Réserver
                                                    <ChevronRight className="w-5 h-5" />
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>

                        {totalPages > 1 && (
                            <div className="flex items-center justify-center gap-2 mt-8">
                                <button
                                    onClick={() => fetchRides(currentPage - 1)}
                                    disabled={currentPage === 0}
                                    className="px-4 py-2 border border-slate-200 rounded-lg text-slate-700 hover:bg-slate-50 disabled:opacity-50"
                                >
                                    Précédent
                                </button>
                                <span className="px-4 py-2 text-slate-600">Page {currentPage + 1} sur {totalPages}</span>
                                <button
                                    onClick={() => fetchRides(currentPage + 1)}
                                    disabled={currentPage >= totalPages - 1}
                                    className="px-4 py-2 border border-slate-200 rounded-lg text-slate-700 hover:bg-slate-50 disabled:opacity-50"
                                >
                                    Suivant
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

export default function SearchPage() {
    return (
        <Suspense
            fallback={
                <div className="min-h-screen flex items-center justify-center bg-slate-50">
                    <div className="flex flex-col items-center gap-4">
                        <Loader2 className="w-10 h-10 text-[#0A8F8F] animate-spin" />
                        <span className="text-slate-600">Chargement...</span>
                    </div>
                </div>
            }
        >
            <SearchPageContent />
        </Suspense>
    );
}
