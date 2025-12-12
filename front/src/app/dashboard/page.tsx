"use client";

import { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    Car,
    Calendar,
    Clock,
    Users,
    Plus,
    Search,
    ChevronRight,
    Star,
    Ticket,
    TrendingUp,
    Loader2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";
import Navbar from "@/components/layout/Navbar";
import { API_URL } from "@/lib/config";

interface Ride {
    id: string;
    originName: string;
    destinationName: string;
    departureTime: string;
    totalSeats: number;
    availableSeats: number;
    pricePerSeat: number;
    status: string;
    driverName: string;
    driverRating: number | null;
}

interface Booking {
    id: string;
    rideId: string;
    rideSummary: string;
    driverName: string;
    departureTime: string;
    seatsBooked: number;
    priceTotal: number;
    status: string;
}

interface PagedResponse<T> {
    content: T[];
    totalElements: number;
}

interface DashboardRide {
    id: string;
    type: "driver" | "passenger";
    origin: string;
    destination: string;
    departureTime: string;
    seats?: number;
    bookedSeats?: number;
    price: number;
    driverName?: string;
    driverRating?: number | null;
    status: string;
}

export default function DashboardPage() {
    const router = useRouter();
    const { token, isAuthenticated, isLoading: authLoading } = useAuth();

    const [activeTab, setActiveTab] = useState<"all" | "driver" | "passenger">("all");
    const [upcomingRides, setUpcomingRides] = useState<DashboardRide[]>([]);
    const [stats, setStats] = useState({
        totalTrips: 0,
        totalRides: 0,
        savedMoney: 0,
        rating: 0,
    });
    const [isLoading, setIsLoading] = useState(true);

    // Redirect if not authenticated
    useEffect(() => {
        if (!authLoading && !isAuthenticated) {
            router.replace("/login");
        }
    }, [authLoading, isAuthenticated, router]);

    const fetchDashboardData = useCallback(async () => {
        if (!token) return;

        setIsLoading(true);

        try {
            // Fetch driver's rides
            const ridesResponse = await fetch(
                `${API_URL}/rides/mine?page=0&size=50`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            // Fetch passenger's bookings
            const bookingsResponse = await fetch(
                `${API_URL}/bookings/mine?page=0&size=50`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            // Fetch rating stats
            const reviewsResponse = await fetch(
                `${API_URL}/reviews/mine/received?page=0&size=100`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            let driverRides: DashboardRide[] = [];
            let passengerRides: DashboardRide[] = [];
            let completedTripsCount = 0;
            let publishedRidesCount = 0;
            let totalEarnings = 0;
            let avgRating = 0;

            if (ridesResponse.ok) {
                const ridesData: PagedResponse<Ride> = await ridesResponse.json();
                // Use totalElements if available, otherwise fallback to content.length
                publishedRidesCount = ridesData.totalElements ?? ridesData.content?.length ?? 0;

                // Map driver rides - filter for upcoming (SCHEDULED, FULL, IN_PROGRESS)
                driverRides = ridesData.content
                    .filter((r) => ["SCHEDULED", "FULL", "IN_PROGRESS"].includes(r.status))
                    .map((ride) => ({
                        id: ride.id,
                        type: "driver" as const,
                        origin: ride.originName,
                        destination: ride.destinationName,
                        departureTime: ride.departureTime,
                        seats: ride.totalSeats,
                        bookedSeats: ride.totalSeats - ride.availableSeats,
                        price: ride.pricePerSeat,
                        status: ride.status,
                    }));

                // Count completed rides for earnings estimate
                const completedDriverRides = ridesData.content.filter(
                    (r) => r.status === "COMPLETED"
                );
                // Count completed driver rides as trips
                completedTripsCount += completedDriverRides.length;
                completedDriverRides.forEach((r) => {
                    totalEarnings += (r.totalSeats - r.availableSeats) * r.pricePerSeat;
                });
            }

            if (bookingsResponse.ok) {
                const bookingsData: PagedResponse<Booking> = await bookingsResponse.json();

                // Count confirmed/completed bookings as "trips effectués"
                completedTripsCount += bookingsData.content.filter(
                    (b) => b.status === "CONFIRMED"
                ).length;

                // Map passenger bookings - filter for confirmed upcoming
                passengerRides = bookingsData.content
                    .filter((b) => b.status === "CONFIRMED" || b.status === "PENDING")
                    .filter((b) => new Date(b.departureTime) > new Date())
                    .map((booking) => {
                        const [origin, destination] = booking.rideSummary.split(" -> ");
                        return {
                            id: booking.rideId,
                            type: "passenger" as const,
                            origin: origin || "",
                            destination: destination || "",
                            departureTime: booking.departureTime,
                            price: booking.priceTotal,
                            driverName: booking.driverName,
                            status: booking.status,
                        };
                    });
            }

            if (reviewsResponse.ok) {
                const reviewsData = await reviewsResponse.json();
                if (reviewsData.content && reviewsData.content.length > 0) {
                    const ratings = reviewsData.content.map((r: { rating: number }) => r.rating);
                    avgRating = Math.round((ratings.reduce((a: number, b: number) => a + b, 0) / ratings.length) * 10) / 10;
                }
            }

            // Combine and sort by departure time
            const allRides = [...driverRides, ...passengerRides].sort(
                (a, b) => new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime()
            );

            setUpcomingRides(allRides);
            setStats({
                totalTrips: completedTripsCount,
                totalRides: publishedRidesCount,
                savedMoney: totalEarnings,
                rating: avgRating,
            });
        } catch (err) {
            console.error("Failed to fetch dashboard data:", err);
        } finally {
            setIsLoading(false);
        }
    }, [token]);

    useEffect(() => {
        if (token) {
            fetchDashboardData();
        }
    }, [token, fetchDashboardData]);

    const filteredRides = upcomingRides.filter((ride) => {
        if (activeTab === "all") return true;
        return ride.type === activeTab;
    });

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            weekday: "short",
            day: "numeric",
            month: "short",
        });
    };

    const formatTime = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("fr-FR", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    // Loading state
    if (authLoading || isLoading) {
        return (
            <div className="min-h-screen bg-[#F8FAFC]">
                <Navbar />
                <div className="h-16"></div>
                <div className="flex items-center justify-center py-20">
                    <Loader2 className="w-8 h-8 animate-spin text-[#006B8F]" />
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#F8FAFC]">
            <Navbar />
            <div className="h-16"></div>

            <main className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
                    <div>
                        <h1 className="text-2xl font-bold text-slate-900 font-[family-name:var(--font-poppins)]">
                            Tableau de bord
                        </h1>
                        <p className="text-slate-500 mt-1">
                            Bienvenue ! Voici un aperçu de vos activités.
                        </p>
                    </div>
                    <div className="flex gap-3">
                        <Link href="/#search">
                            <Button
                                variant="outline"
                                className="rounded-xl border-[#FF9A3E] text-[#FF9A3E] hover:bg-[#FF9A3E] hover:text-white"
                            >
                                <Search className="w-4 h-4 mr-2" />
                                Rechercher
                            </Button>
                        </Link>
                        <Link href="/publish">
                            <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673] text-white">
                                <Plus className="w-4 h-4 mr-2" />
                                Publier un trajet
                            </Button>
                        </Link>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                    <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100">
                        <div className="flex items-center gap-3 mb-3">
                            <div className="w-10 h-10 rounded-xl bg-blue-50 flex items-center justify-center">
                                <Ticket className="w-5 h-5 text-[#006B8F]" />
                            </div>
                        </div>
                        <p className="text-2xl font-bold text-slate-900">{stats.totalTrips}</p>
                        <p className="text-sm text-slate-500">Trajets effectués</p>
                    </div>

                    <div
                        onClick={() => router.push("/dashboard/rides")}
                        className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 hover:shadow-md transition-shadow cursor-pointer"
                    >
                        <div className="flex items-center gap-3 mb-3">
                            <div className="w-10 h-10 rounded-xl bg-orange-50 flex items-center justify-center">
                                <Car className="w-5 h-5 text-[#FF9A3E]" />
                            </div>
                        </div>
                        <p className="text-2xl font-bold text-slate-900">{stats.totalRides ?? 0}</p>
                        <p className="text-sm text-slate-500">Trajets publiés</p>
                    </div>

                    <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100">
                        <div className="flex items-center gap-3 mb-3">
                            <div className="w-10 h-10 rounded-xl bg-green-50 flex items-center justify-center">
                                <TrendingUp className="w-5 h-5 text-emerald-600" />
                            </div>
                        </div>
                        <p className="text-2xl font-bold text-slate-900">{stats.savedMoney} TND</p>
                        <p className="text-sm text-slate-500">Gagnés</p>
                    </div>

                    <div
                        onClick={() => router.push("/dashboard/reviews")}
                        className="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 hover:shadow-md transition-shadow cursor-pointer"
                    >
                        <div className="flex items-center gap-3 mb-3">
                            <div className="w-10 h-10 rounded-xl bg-yellow-50 flex items-center justify-center">
                                <Star className="w-5 h-5 text-yellow-500" />
                            </div>
                        </div>
                        <p className="text-2xl font-bold text-slate-900">{stats.rating || "-"}/5</p>
                        <p className="text-sm text-slate-500">Note moyenne</p>
                    </div>
                </div>

                {/* Upcoming Rides Section */}
                <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
                    <div className="p-6 border-b border-slate-100">
                        <div className="flex items-center justify-between">
                            <h2 className="text-lg font-bold text-slate-900">
                                Trajets à venir
                            </h2>
                            <div className="flex gap-1 bg-slate-100 p-1 rounded-xl">
                                {[
                                    { key: "all", label: "Tous" },
                                    { key: "driver", label: "Conducteur" },
                                    { key: "passenger", label: "Passager" },
                                ].map((tab) => (
                                    <button
                                        key={tab.key}
                                        onClick={() => setActiveTab(tab.key as typeof activeTab)}
                                        className={`px-4 py-1.5 text-sm font-medium rounded-lg transition-all ${activeTab === tab.key
                                            ? "bg-white text-slate-900 shadow-sm"
                                            : "text-slate-500 hover:text-slate-700"
                                            }`}
                                    >
                                        {tab.label}
                                    </button>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Rides List */}
                    <div className="divide-y divide-slate-100">
                        {filteredRides.length > 0 ? (
                            filteredRides.slice(0, 5).map((ride) => (
                                <Link
                                    key={`${ride.type}-${ride.id}`}
                                    href={`/rides/${ride.id}`}
                                    className="flex items-center justify-between p-6 hover:bg-slate-50 transition-colors"
                                >
                                    <div className="flex items-center gap-4">
                                        {/* Type Badge */}
                                        <div
                                            className={`w-12 h-12 rounded-xl flex items-center justify-center ${ride.type === "driver"
                                                ? "bg-[#006B8F]/10 text-[#006B8F]"
                                                : "bg-[#FF9A3E]/10 text-[#FF9A3E]"
                                                }`}
                                        >
                                            {ride.type === "driver" ? (
                                                <Car className="w-6 h-6" />
                                            ) : (
                                                <Ticket className="w-6 h-6" />
                                            )}
                                        </div>

                                        {/* Ride Info */}
                                        <div>
                                            <div className="flex items-center gap-2 text-slate-900 font-medium">
                                                <span>{ride.origin}</span>
                                                <ChevronRight className="w-4 h-4 text-slate-400" />
                                                <span>{ride.destination}</span>
                                            </div>
                                            <div className="flex items-center gap-4 mt-1 text-sm text-slate-500">
                                                <span className="flex items-center gap-1">
                                                    <Calendar className="w-4 h-4" />
                                                    {formatDate(ride.departureTime)}
                                                </span>
                                                <span className="flex items-center gap-1">
                                                    <Clock className="w-4 h-4" />
                                                    {formatTime(ride.departureTime)}
                                                </span>
                                                {ride.type === "driver" && ride.seats && (
                                                    <span className="flex items-center gap-1">
                                                        <Users className="w-4 h-4" />
                                                        {ride.bookedSeats}/{ride.seats} places
                                                    </span>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    {/* Price & Arrow */}
                                    <div className="flex items-center gap-4">
                                        <div className="text-right">
                                            <p className="text-lg font-bold text-slate-900">
                                                {ride.price} TND
                                            </p>
                                            {ride.type === "passenger" && ride.driverName && (
                                                <p className="text-sm text-slate-500 flex items-center gap-1 justify-end">
                                                    {ride.driverRating && (
                                                        <>
                                                            <Star className="w-3 h-3 text-yellow-500" />
                                                            {ride.driverRating} •
                                                        </>
                                                    )}
                                                    {ride.driverName}
                                                </p>
                                            )}
                                        </div>
                                        <ChevronRight className="w-5 h-5 text-slate-300" />
                                    </div>
                                </Link>
                            ))
                        ) : (
                            <div className="p-12 text-center">
                                <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <Calendar className="w-8 h-8 text-slate-400" />
                                </div>
                                <h3 className="text-lg font-medium text-slate-900 mb-2">
                                    Aucun trajet à venir
                                </h3>
                                <p className="text-slate-500 mb-6">
                                    Recherchez un trajet ou publiez le vôtre !
                                </p>
                                <div className="flex justify-center gap-3">
                                    <Link href="/#search">
                                        <Button
                                            variant="outline"
                                            className="rounded-xl border-[#FF9A3E] text-[#FF9A3E] hover:bg-[#FF9A3E] hover:text-white"
                                        >
                                            <Search className="w-4 h-4 mr-2" />
                                            Rechercher
                                        </Button>
                                    </Link>
                                    <Link href="/publish">
                                        <Button className="rounded-xl bg-[#006B8F] hover:bg-[#005673]">
                                            <Plus className="w-4 h-4 mr-2" />
                                            Publier
                                        </Button>
                                    </Link>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* View All Link */}
                    {filteredRides.length > 0 && (
                        <div className="p-4 border-t border-slate-100 text-center">
                            <Link
                                href="/dashboard/rides"
                                className="text-sm font-medium text-[#006B8F] hover:text-[#005673]"
                            >
                                Voir tous les trajets →
                            </Link>
                        </div>
                    )}
                </div>

                {/* Quick Actions */}
                <div className="grid md:grid-cols-2 gap-6 mt-8">
                    {/* For Drivers */}
                    <div className="bg-gradient-to-br from-[#006B8F] to-[#005673] rounded-2xl p-6 text-white">
                        <div className="flex items-center gap-3 mb-4">
                            <div className="w-12 h-12 bg-white/10 rounded-xl flex items-center justify-center">
                                <Car className="w-6 h-6" />
                            </div>
                            <div>
                                <h3 className="font-bold text-lg">Vous êtes conducteur ?</h3>
                                <p className="text-white/70 text-sm">
                                    Partagez vos frais de route
                                </p>
                            </div>
                        </div>
                        <Link href="/publish">
                            <Button className="w-full bg-white text-[#006B8F] hover:bg-white/90 rounded-xl font-medium">
                                <Plus className="w-4 h-4 mr-2" />
                                Publier un trajet
                            </Button>
                        </Link>
                    </div>

                    {/* For Passengers */}
                    <div className="bg-gradient-to-br from-[#FF9A3E] to-[#E88A35] rounded-2xl p-6 text-white">
                        <div className="flex items-center gap-3 mb-4">
                            <div className="w-12 h-12 bg-white/10 rounded-xl flex items-center justify-center">
                                <Search className="w-6 h-6" />
                            </div>
                            <div>
                                <h3 className="font-bold text-lg">Besoin d&apos;un trajet ?</h3>
                                <p className="text-white/70 text-sm">
                                    Trouvez votre covoiturage idéal
                                </p>
                            </div>
                        </div>
                        <Link href="/#search">
                            <Button className="w-full bg-white text-[#FF9A3E] hover:bg-white/90 rounded-xl font-medium">
                                <Search className="w-4 h-4 mr-2" />
                                Rechercher un trajet
                            </Button>
                        </Link>
                    </div>
                </div>
            </main>
        </div>
    );
}
