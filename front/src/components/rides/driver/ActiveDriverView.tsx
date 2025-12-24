"use client";

import { Ride, PassengerInfo } from "@/types/models";
import { Button } from "@/components/ui/button";
import { Navigation, CheckCircle, Ban, Phone, ExternalLink, MapPin, Facebook, Instagram } from "lucide-react";
import { Loader2 } from "lucide-react";

interface ActiveDriverViewProps {
    ride: Ride;
    passengers: PassengerInfo[];
    onComplete: () => void;
    onCancel: () => void;
    isLoading: boolean;
}

/**
 * A dedicated, focused view for the driver when the ride is currently in progress.
 * Prioritizes navigation, passenger information, and ride completion.
 */
export default function ActiveDriverView({
    ride,
    passengers,
    onComplete,
    onCancel,
    isLoading
}: ActiveDriverViewProps) {
    const confirmedPassengers = passengers.filter(p => p.status === 'CONFIRMED');

    // Encode coordinates/addresses for Google Maps
    // Using origin and destination names. Ideally would use coordinates if available, but names work.
    const originEncoded = encodeURIComponent(ride.originName);
    const destEncoded = encodeURIComponent(ride.destinationName);

    // Construct Google Maps Directions URL
    const mapsUrl = `https://www.google.com/maps/dir/?api=1&origin=${originEncoded}&destination=${destEncoded}&travelmode=driving`;

    return (
        <div className="p-6 md:p-8 space-y-8">
            {/* Active Status Header */}
            <div className="bg-emerald-600 rounded-2xl p-6 text-white shadow-lg relative overflow-hidden">
                <div className="relative z-10">
                    <div className="flex items-center gap-3 mb-3">
                        <span className="flex h-3 w-3 relative">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-200 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-3 w-3 bg-white"></span>
                        </span>
                        <span className="font-bold uppercase tracking-widest text-xs">Voyage en cours</span>
                    </div>

                    <div className="space-y-1">
                        <div className="flex items-baseline gap-2 opacity-80 text-sm">
                            <span>De</span>
                            <span className="font-medium">{ride.originName}</span>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="text-3xl md:text-4xl font-extrabold tracking-tight">
                                Vers {ride.destinationName}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Background Decoration */}
                <div className="absolute -bottom-12 -right-12 opacity-10 text-white transform rotate-12">
                    <Navigation className="w-48 h-48" />
                </div>
            </div>

            {/* Navigation Action */}
            <a
                href={mapsUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="group flex items-center p-4 bg-white border-2 border-slate-100 hover:border-emerald-500 rounded-2xl transition-all shadow-sm hover:shadow-md"
            >
                <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center mr-4 group-hover:scale-110 transition-transform">
                    <MapPin className="w-6 h-6" />
                </div>
                <div className="flex-1">
                    <h3 className="font-bold text-slate-900 group-hover:text-emerald-700 transition-colors">Navigation GPS</h3>
                    <p className="text-sm text-slate-500">Ouvrir l'itinéraire dans Google Maps</p>
                </div>
                <ExternalLink className="w-5 h-5 text-slate-400 group-hover:text-emerald-500" />
            </a>

            {/* Passengers List */}
            <div className="space-y-4">
                <h3 className="text-lg font-bold text-slate-900 flex items-center justify-between">
                    <span>Passagers à déposer</span>
                    <span className="bg-emerald-100 text-emerald-700 px-3 py-1 rounded-full text-xs">
                        {confirmedPassengers.length}
                    </span>
                </h3>

                {confirmedPassengers.length === 0 ? (
                    <div className="p-6 bg-slate-50 rounded-2xl text-center text-slate-500 border border-slate-200 border-dashed">
                        <p>Aucun passager confirmé à bord.</p>
                    </div>
                ) : (
                    <div className="grid gap-3">
                        {confirmedPassengers.map(passenger => (
                            <div key={passenger.bookingId} className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-50 border border-slate-200 rounded-2xl gap-4">
                                <div className="flex items-center gap-4">
                                    <div className="w-12 h-12 bg-white border border-slate-200 rounded-full flex items-center justify-center text-slate-700 font-bold shadow-sm shrink-0">
                                        {passenger.passengerName.charAt(0)}
                                    </div>
                                    <div className="min-w-0">
                                        <p className="font-bold text-slate-900 truncate">{passenger.passengerName}</p>
                                        <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-sm text-slate-500">
                                            <span className="font-medium text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded text-xs whitespace-nowrap">
                                                {passenger.seatsBooked} {passenger.seatsBooked > 1 ? 'places' : 'place'}
                                            </span>
                                            {passenger.passengerPhone && (
                                                <span className="font-mono text-xs flex items-center gap-1">
                                                    <Phone className="w-3 h-3" />
                                                    {passenger.passengerPhone}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                </div>

                                <div className="flex items-center gap-2 self-end sm:self-center">
                                    {passenger.facebookUrl && (
                                        <a
                                            href={passenger.facebookUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="w-10 h-10 flex items-center justify-center rounded-xl bg-blue-50 text-blue-600 hover:bg-blue-100 transition-colors"
                                            title="Facebook"
                                        >
                                            <Facebook className="w-5 h-5" />
                                        </a>
                                    )}
                                    {passenger.instagramUrl && (
                                        <a
                                            href={passenger.instagramUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="w-10 h-10 flex items-center justify-center rounded-xl bg-pink-50 text-pink-600 hover:bg-pink-100 transition-colors"
                                            title="Instagram"
                                        >
                                            <Instagram className="w-5 h-5" />
                                        </a>
                                    )}
                                    {passenger.passengerPhone && (
                                        <a
                                            href={`tel:${passenger.passengerPhone}`}
                                            className="w-10 h-10 flex items-center justify-center rounded-xl bg-emerald-100 text-emerald-600 hover:bg-emerald-200 transition-colors shadow-sm"
                                            title="Appeler"
                                        >
                                            <Phone className="w-5 h-5" />
                                        </a>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            <hr className="border-slate-100" />

            {/* Actions */}
            <div className="space-y-4">
                <Button
                    onClick={onComplete}
                    disabled={isLoading}
                    className="w-full py-8 text-xl font-bold bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl shadow-emerald-200 shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-0.5"
                >
                    {isLoading ? (
                        <Loader2 className="w-6 h-6 mr-3 animate-spin" />
                    ) : (
                        <CheckCircle className="w-6 h-6 mr-3" />
                    )}
                    Terminer le trajet
                </Button>

                <div className="flex justify-center">
                    <button
                        onClick={onCancel}
                        disabled={isLoading}
                        className="text-sm font-medium text-red-500 hover:text-red-700 hover:bg-red-50 px-4 py-2 rounded-lg transition-colors flex items-center gap-2"
                    >
                        <Ban className="w-4 h-4" />
                        Annuler le trajet (Urgence)
                    </button>
                </div>
            </div>
        </div>
    );
}
