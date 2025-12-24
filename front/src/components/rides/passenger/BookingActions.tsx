"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Minus, Plus, Loader2, Ticket } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Ride } from "@/types/models";
import { UserProfile } from "@/types/models";
import { createBookingAction } from "@/app/actions/bookings";
import { useToast } from "@/contexts/ToastContext";

interface BookingActionsProps {
    ride: Ride;
    currentUser: UserProfile | null;
    onBookingSuccess: (seats: number) => void;
}

export default function BookingActions({ ride, currentUser, onBookingSuccess }: BookingActionsProps) {
    const router = useRouter();
    const { showError, showSuccess } = useToast();
    const [seats, setSeats] = useState(1);
    const [loading, setLoading] = useState(false);

    // Determines max seats user can book: min(4, availableSeats)
    const maxSeats = Math.min(4, ride.availableSeats);
    const totalPrice = seats * ride.pricePerSeat;

    const handleIncrement = () => {
        if (seats < maxSeats) setSeats(seats + 1);
    };

    const handleDecrement = () => {
        if (seats > 1) setSeats(seats - 1);
    };

    const handleBook = async () => {
        if (!currentUser) {
            router.push("/login");
            return;
        }

        setLoading(true);
        try {
            const result = await createBookingAction({
                rideId: ride.id,
                seatsRequested: seats,
            });

            if (result.success) {
                showSuccess("Demande de réservation envoyée au conducteur !");
                onBookingSuccess(seats);
                // Refresh data in background
                router.refresh();
            } else {
                throw new Error(result.error);
            }
        } catch (err) {
            console.error("Booking failed:", err);
            showError(err instanceof Error ? err.message : "Erreur lors de la réservation");
        } finally {
            setLoading(false);
        }
    };

    if (maxSeats === 0) {
        return (
            <div className="p-6 border-t border-slate-100 bg-slate-50 text-center">
                <p className="text-slate-500 font-medium">Ce trajet est complet.</p>
            </div>
        );
    }

    return (
        <div className="p-6 border-t border-slate-100 bg-white">
            <h3 className="text-sm font-medium text-slate-900 mb-4 flex items-center gap-2">
                <Ticket className="w-4 h-4 text-emerald-600" />
                Réserver votre place
            </h3>

            <div className="flex flex-col sm:flex-row gap-6">
                {/* Seat Selector */}
                <div className="flex-1">
                    <label className="block text-xs font-medium text-slate-500 mb-2">
                        Nombre de places
                    </label>
                    <div className="flex items-center gap-3">
                        <button
                            onClick={handleDecrement}
                            disabled={seats <= 1 || loading}
                            className="w-10 h-10 rounded-xl border border-slate-200 flex items-center justify-center text-slate-600 hover:bg-slate-50 disabled:opacity-50 transition-colors"
                        >
                            <Minus className="w-4 h-4" />
                        </button>
                        <span className="text-xl font-semibold w-8 text-center text-slate-900">
                            {seats}
                        </span>
                        <button
                            onClick={handleIncrement}
                            disabled={seats >= maxSeats || loading}
                            className="w-10 h-10 rounded-xl border border-slate-200 flex items-center justify-center text-slate-600 hover:bg-slate-50 disabled:opacity-50 transition-colors"
                        >
                            <Plus className="w-4 h-4" />
                        </button>
                    </div>
                </div>

                {/* Confirm Action */}
                <div className="flex-1">
                    <div className="flex justify-between items-baseline mb-2">
                        <span className="text-xs font-medium text-slate-500">Total</span>
                        <span className="text-xl font-bold text-emerald-700">
                            {totalPrice.toFixed(2)} <span className="text-sm font-normal">TND</span>
                        </span>
                    </div>
                    <Button
                        onClick={handleBook}
                        disabled={loading}
                        className="w-full h-12 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-semibold shadow-lg shadow-emerald-600/20"
                    >
                        {loading ? (
                            <Loader2 className="w-5 h-5 animate-spin" />
                        ) : (
                            "Confirmer la réservation"
                        )}
                    </Button>
                </div>
            </div>
        </div>
    );
}
