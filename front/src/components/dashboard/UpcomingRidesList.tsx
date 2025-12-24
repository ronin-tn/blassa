"use client";

import { useState } from "react";
import Link from "next/link";
import {
    Car,
    Calendar,
    Clock,
    Users,
    ChevronRight,
    Star,
    Ticket,
    Search,
    Plus,
} from "lucide-react";
import { Button } from "@/components/ui/button";

export interface DashboardRide {
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

interface UpcomingRidesListProps {
    rides: DashboardRide[];
}

export default function UpcomingRidesList({ rides }: UpcomingRidesListProps) {
    const [activeTab, setActiveTab] = useState<"all" | "driver" | "passenger">("all");

    const filteredRides = rides.filter((ride) => {
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

    return (
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
            <div className="p-4 sm:p-6 border-b border-slate-100">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                    <h2 className="text-lg font-bold text-slate-900">
                        Trajets à venir
                    </h2>
                    <div className="flex gap-1 bg-slate-100 p-1 rounded-xl w-full sm:w-auto overflow-x-auto">
                        {[
                            { key: "all", label: "Tous" },
                            { key: "driver", label: "Conducteur" },
                            { key: "passenger", label: "Passager" },
                        ].map((tab) => (
                            <button
                                key={tab.key}
                                onClick={() => setActiveTab(tab.key as typeof activeTab)}
                                className={`flex-1 sm:flex-initial px-3 sm:px-4 py-1.5 text-xs sm:text-sm font-medium rounded-lg transition-all whitespace-nowrap ${activeTab === tab.key
                                    ? "bg-white text-[var(--color-blassa-teal)] shadow-sm"
                                    : "text-slate-500 hover:text-slate-700 hover:bg-slate-50"
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
                            <Link href="/search-form">
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
    );
}
