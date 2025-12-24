"use client";

import { useState, useMemo } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    ArrowLeft,
    Users,
    Cigarette,
    User,
    Loader2,
    Search,
    Star,
    ChevronRight,
    Shield,
    UserCheck,
    Filter,
} from "lucide-react";
import { Ride, RideGenderPreference, PagedResponse } from "@/types/models";
import { createBookingAction } from "@/app/actions/bookings";
import { UserProfile } from "@/lib/api/user";
import SearchFiltersComponent, {
    SearchFilters,
    DEFAULT_FILTERS,
} from "./SearchFilters";

interface SearchClientProps {
    initialRides: PagedResponse<Ride>;
    initialBookedRideIds: string[];
    currentUser: UserProfile | null;
    searchParams: {
        from: string;
        to: string;
        date: string;
        passengers: string;
    };
}

export default function SearchClient({
    initialRides,
    initialBookedRideIds,
    currentUser,
    searchParams,
}: SearchClientProps) {
    const router = useRouter();
    const { from, to, date, passengers } = searchParams;
    const rides = initialRides.content;
    const { totalPages, number: currentPage, totalElements } = initialRides;

    const [bookingRideId, setBookingRideId] = useState<string | null>(null);
    const [bookingSeats, setBookingSeats] = useState<number>(1);
    const [isBooking, setIsBooking] = useState(false);
    const [bookingError, setBookingError] = useState<string | null>(null);
    const [bookingSuccess, setBookingSuccess] = useState<string | null>(null);
    const [myBookedRideIds, setMyBookedRideIds] = useState<string[]>(initialBookedRideIds);
    const [filters, setFilters] = useState<SearchFilters>(DEFAULT_FILTERS);
    const [showMobileFilters, setShowMobileFilters] = useState(false);

    // Calculate max price from rides for filter
    const maxPriceInResults = useMemo(() => {
        if (rides.length === 0) return 100;
        return Math.ceil(Math.max(...rides.map((r) => r.pricePerSeat)));
    }, [rides]);

    // Apply client-side filtering
    const filteredRides = useMemo(() => {
        let result = [...rides];

        // Filter by time of day
        if (filters.timeOfDay.length > 0) {
            result = result.filter((ride) => {
                const hour = new Date(ride.departureTime).getHours();
                if (filters.timeOfDay.includes("morning") && hour >= 6 && hour < 12) return true;
                if (filters.timeOfDay.includes("afternoon") && hour >= 12 && hour < 18) return true;
                if (filters.timeOfDay.includes("evening") && (hour >= 18 || hour < 6)) return true;
                return false;
            });
        }

        // Filter by max price
        if (filters.maxPrice !== null) {
            result = result.filter((ride) => ride.pricePerSeat <= filters.maxPrice!);
        }

        // Filter by ladies only
        if (filters.ladiesOnly) {
            result = result.filter((ride) => ride.genderPreference === "FEMALE_ONLY");
        }

        // Filter by no smoking
        if (filters.noSmoking) {
            result = result.filter((ride) => !ride.allowsSmoking);
        }

        // Sort
        if (filters.sortBy) {
            result.sort((a, b) => {
                switch (filters.sortBy) {
                    case "price_asc":
                        return a.pricePerSeat - b.pricePerSeat;
                    case "price_desc":
                        return b.pricePerSeat - a.pricePerSeat;
                    case "time_asc":
                        return new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime();
                    case "time_desc":
                        return new Date(b.departureTime).getTime() - new Date(a.departureTime).getTime();
                    default:
                        return 0;
                }
            });
        }

        return result;
    }, [rides, filters]);

    const handleBooking = async (rideId: string, seats: number) => {
        if (!currentUser) {
            const redirectParams = new URLSearchParams(searchParams);
            router.push(`/login?redirect=/search?${redirectParams.toString()}`);
            return;
        }
        setIsBooking(true);
        setBookingError(null);
        setBookingSuccess(null);

        try {
            const result = await createBookingAction({ rideId, seatsRequested: seats });
            if (!result.success) throw new Error(result.error);

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

    const isOwnRide = (ride: Ride) => currentUser?.email === ride.driverEmail;
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

    // Helper to generate pagination links
    const getPageLink = (page: number) => {
        const params = new URLSearchParams(searchParams);
        params.set("page", page.toString());
        return `/search?${params.toString()}`;
    };

    return (
        <div className="min-h-screen bg-slate-50 pt-16">
            {/* Search Info Bar - Below main Navbar */}
            <div className="bg-white border-b border-slate-200">
                <div className="max-w-6xl mx-auto px-4 py-4">
                    <div className="flex items-center gap-4">
                        <Link href="/search-form" className="p-2 -ml-2 rounded-lg hover:bg-slate-100">
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
                        <button
                            onClick={() => setShowMobileFilters(true)}
                            className="lg:hidden p-2 rounded-lg border border-slate-200 hover:bg-slate-50"
                        >
                            <Filter className="w-5 h-5 text-slate-600" />
                        </button>
                    </div>
                </div>
            </div>

            {/* Mobile Filter Modal */}
            {showMobileFilters && (
                <SearchFiltersComponent
                    filters={filters}
                    onFiltersChange={setFilters}
                    maxPriceInResults={maxPriceInResults}
                    onClose={() => setShowMobileFilters(false)}
                    isMobile
                />
            )}

            <div className="max-w-6xl mx-auto px-4 py-6">
                {bookingSuccess && (
                    <div className="mb-6 p-4 bg-emerald-50 border border-emerald-200 text-emerald-700 rounded-xl flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center">✓</div>
                        <p className="font-medium">{bookingSuccess}</p>
                    </div>
                )}

                {/* Grid Layout: Sidebar + Results */}
                <div className="flex gap-6">
                    {/* Desktop Sidebar */}
                    <div className="hidden lg:block w-72 flex-shrink-0">
                        <SearchFiltersComponent
                            filters={filters}
                            onFiltersChange={setFilters}
                            maxPriceInResults={maxPriceInResults}
                        />
                    </div>

                    {/* Results */}
                    <div className="flex-1">
                        {rides.length === 0 ? (
                            <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
                                <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-slate-100 flex items-center justify-center">
                                    <Search className="w-10 h-10 text-slate-400" />
                                </div>
                                <h2 className="text-xl font-semibold text-slate-900 mb-2">Aucun trajet trouvé</h2>
                                <p className="text-slate-600 mb-6 max-w-sm mx-auto">
                                    Essayez avec d&apos;autres dates ou destinations.
                                </p>
                                <Link
                                    href="/search-form"
                                    className="inline-flex items-center gap-2 px-6 py-3 bg-[#0A8F8F] text-white font-medium rounded-xl hover:bg-[#0A8F8F]/90"
                                >
                                    Nouvelle recherche
                                </Link>
                            </div>
                        ) : filteredRides.length === 0 ? (
                            <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
                                <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-amber-50 flex items-center justify-center">
                                    <Filter className="w-10 h-10 text-amber-400" />
                                </div>
                                <h2 className="text-xl font-semibold text-slate-900 mb-2">Aucun résultat avec ces filtres</h2>
                                <p className="text-slate-600 mb-6 max-w-sm mx-auto">
                                    Essayez de modifier vos critères de recherche.
                                </p>
                                <button
                                    onClick={() => setFilters(DEFAULT_FILTERS)}
                                    className="inline-flex items-center gap-2 px-6 py-3 bg-slate-100 text-slate-700 font-medium rounded-xl hover:bg-slate-200"
                                >
                                    Réinitialiser les filtres
                                </button>
                            </div>
                        ) : (
                            <>
                                <p className="text-sm text-slate-500 mb-4">
                                    {filteredRides.length} trajet{filteredRides.length > 1 ? "s" : ""}
                                    {filteredRides.length !== totalElements && ` (sur ${totalElements})`}
                                </p>

                                <div className="space-y-4">
                                    {filteredRides.map((ride) => {
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
                                                                        <div className="w-12 h-12 rounded-full overflow-hidden bg-gradient-to-br from-[#0A8F8F]/20 to-[#0A8F8F]/10 flex items-center justify-center">
                                                                            {ride.driverProfilePictureUrl ? (
                                                                                <img
                                                                                    src={ride.driverProfilePictureUrl.replace("=s96-c", "=s400-c")}
                                                                                    alt={ride.driverName}
                                                                                    className="w-full h-full object-cover"
                                                                                />
                                                                            ) : (
                                                                                <User className="w-6 h-6 text-[#0A8F8F]" />
                                                                            )}
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
                                            </div>
                                        );
                                    })}
                                </div>

                                {totalPages > 1 && (
                                    <div className="flex items-center justify-center gap-2 mt-8">
                                        <Link
                                            href={getPageLink(currentPage - 1)}
                                            className={`px-4 py-2 border border-slate-200 rounded-lg text-slate-700 hover:bg-slate-50 ${currentPage === 0 ? "pointer-events-none opacity-50" : ""}`}
                                        >
                                            Précédent
                                        </Link>
                                        <span className="px-4 py-2 text-slate-600">Page {currentPage + 1} sur {totalPages}</span>
                                        <Link
                                            href={getPageLink(currentPage + 1)}
                                            className={`px-4 py-2 border border-slate-200 rounded-lg text-slate-700 hover:bg-slate-50 ${currentPage >= totalPages - 1 ? "pointer-events-none opacity-50" : ""}`}
                                        >
                                            Suivant
                                        </Link>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
