"use client";

import { Users, UserCheck } from "lucide-react";
import { PassengerInfo } from "@/types/models";
import PassengerCard from "./PassengerCard";

interface PassengerListProps {
    passengers: PassengerInfo[];
    onAccept: (bookingId: string) => void;
    onReject: (bookingId: string) => void;
    loadingBookingId: string | null;
}

/**
 * Displays the list of passengers with counts and empty state
 */
export default function PassengerList({
    passengers,
    onAccept,
    onReject,
    loadingBookingId,
}: PassengerListProps) {
    const pendingCount = passengers.filter((p) => p.status === "PENDING").length;
    const confirmedCount = passengers.filter((p) => p.status === "CONFIRMED").length;

    return (
        <div className="p-6 border-t border-slate-100 bg-emerald-50">
            {/* Header with counts */}
            <div className="flex items-center justify-between mb-4">
                <h3 className="text-sm font-medium text-emerald-800 flex items-center gap-2">
                    <Users className="w-4 h-4" />
                    Réservations
                </h3>
                <div className="flex items-center gap-2 text-xs">
                    {pendingCount > 0 && (
                        <span className="px-2 py-1 bg-yellow-100 text-yellow-700 rounded-full">
                            {pendingCount} en attente
                        </span>
                    )}
                    {confirmedCount > 0 && (
                        <span className="px-2 py-1 bg-emerald-100 text-emerald-700 rounded-full">
                            {confirmedCount} confirmée{confirmedCount > 1 ? "s" : ""}
                        </span>
                    )}
                </div>
            </div>

            {/* Empty state or passenger list */}
            {passengers.length === 0 ? (
                <div className="text-center py-8">
                    <div className="w-16 h-16 bg-emerald-100 rounded-full flex items-center justify-center mx-auto mb-3">
                        <UserCheck className="w-8 h-8 text-emerald-600" />
                    </div>
                    <p className="text-emerald-700 font-medium">Aucune réservation</p>
                    <p className="text-emerald-600 text-sm mt-1">Les demandes de réservation apparaîtront ici</p>
                </div>
            ) : (
                <div className="space-y-4">
                    {passengers.map((passenger) => (
                        <PassengerCard
                            key={passenger.bookingId}
                            passenger={passenger}
                            onAccept={onAccept}
                            onReject={onReject}
                            isLoading={loadingBookingId === passenger.bookingId}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}
