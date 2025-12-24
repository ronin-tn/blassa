"use client";

import { Ride, Booking } from "@/types/models";
import { Navigation, Phone, Facebook, Instagram, ShieldCheck, MapPin } from "lucide-react";

interface ActivePassengerViewProps {
    ride: Ride;
    booking?: Booking;
}

/**
 * A dedicated view for passengers when their confirmed ride is IN_PROGRESS.
 * Focuses on reassuring the passenger and providing easy access to driver contact.
 */
export default function ActivePassengerView({ ride, booking }: ActivePassengerViewProps) {
    // Construct Google Maps URL for tracking (or at least seeing the destination)
    const destEncoded = encodeURIComponent(ride.destinationName);
    const mapsUrl = `https://www.google.com/maps/dir/?api=1&destination=${destEncoded}`;

    return (
        <div className="p-6 md:p-8 space-y-8">
            {/* Active Status Header - Passenger Variant */}
            <div className="bg-emerald-600 rounded-2xl p-6 text-white shadow-lg relative overflow-hidden">
                <div className="relative z-10">
                    <div className="flex items-center gap-3 mb-3">
                        <span className="flex h-3 w-3 relative">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-200 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-3 w-3 bg-white"></span>
                        </span>
                        <span className="font-bold uppercase tracking-widest text-xs">En route</span>
                    </div>

                    <div className="space-y-1">
                        <div className="flex items-baseline gap-2 opacity-80 text-sm">
                            <span>Vers</span>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="text-3xl md:text-4xl font-extrabold tracking-tight">
                                {ride.destinationName}
                            </span>
                        </div>
                    </div>
                </div>
                {/* Background Decoration */}
                <div className="absolute -bottom-10 -right-10 opacity-10 text-white transform -rotate-12">
                    <Navigation className="w-48 h-48" />
                </div>
            </div>

            {/* Vehicle Info Card */}
            {booking?.carLicensePlate && (
                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 relative overflow-hidden">
                    <div className="absolute top-0 right-0 p-4 opacity-5">
                        <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2" /><circle cx="7" cy="17" r="2" /><path d="M9 17h6" /><circle cx="17" cy="17" r="2" /></svg>
                    </div>

                    <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wide mb-4 relative z-10">Véhicule</h3>

                    <div className="relative z-10">
                        <div className="flex flex-col gap-2">
                            <span className="text-xl font-bold text-slate-900 leading-tight">
                                {booking.carDescription || `${ride.carMake} ${ride.carModel} ${ride.carColor}`}
                            </span>
                            <div className="flex items-center gap-3 mt-1">
                                <span className="font-mono font-bold text-lg text-slate-800 bg-slate-100 px-3 py-1.5 rounded-lg border border-slate-200 tracking-wider">
                                    {booking.carLicensePlate}
                                </span>
                                <div className="flex items-center gap-1.5 text-xs font-medium text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-100">
                                    <ShieldCheck className="w-3 h-3" />
                                    <span>Plaque vérifiée</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Driver Info & Contact Card */}
            <div className="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
                <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wide mb-6">Votre Conducteur</h3>

                <div className="flex items-center gap-5 mb-8">
                    <div className="w-16 h-16 bg-[#006B8F] rounded-full flex items-center justify-center text-white text-2xl font-bold shadow-md">
                        {ride.driverName.charAt(0)}
                    </div>
                    <div>
                        <h4 className="text-xl font-bold text-slate-900">{ride.driverName}</h4>
                        <div className="flex items-center gap-2 text-emerald-600 bg-emerald-50 px-3 py-1 rounded-full text-xs font-medium w-fit mt-1">
                            <ShieldCheck className="w-3 h-3" />
                            <span>Conducteur vérifié</span>
                        </div>
                    </div>
                </div>

                {/* Contact Actions */}
                <div className="space-y-3">
                    <a
                        href={`tel:${ride.driverPhoneNumber}`}
                        className="flex items-center justify-between p-4 bg-emerald-50 border border-emerald-100 rounded-xl text-emerald-800 hover:bg-emerald-100 transition-colors group"
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center text-emerald-600 shadow-sm group-hover:scale-110 transition-transform">
                                <Phone className="w-5 h-5" />
                            </div>
                            <div className="flex flex-col">
                                <span className="font-bold">Appeler le conducteur</span>
                                <span className="text-xs opacity-80">{ride.driverPhoneNumber}</span>
                            </div>
                        </div>
                    </a>

                    <div className="grid grid-cols-2 gap-3">
                        {ride.driverFacebookUrl && (
                            <a
                                href={ride.driverFacebookUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="flex items-center justify-center gap-2 p-3 bg-[#1877F2]/10 text-[#1877F2] rounded-xl hover:bg-[#1877F2]/20 transition-colors font-medium"
                            >
                                <Facebook className="w-5 h-5" />
                                <span>Facebook</span>
                            </a>
                        )}
                        {ride.driverInstagramUrl && (
                            <a
                                href={ride.driverInstagramUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="flex items-center justify-center gap-2 p-3 bg-[#E4405F]/10 text-[#E4405F] rounded-xl hover:bg-[#E4405F]/20 transition-colors font-medium"
                            >
                                <Instagram className="w-5 h-5" />
                                <span>Instagram</span>
                            </a>
                        )}
                    </div>
                </div>
            </div>

            {/* Useful Links */}
            <a
                href={mapsUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="block p-4 bg-slate-50 border border-slate-200 rounded-2xl hover:border-emerald-300 hover:shadow-md transition-all group"
            >
                <div className="flex items-center gap-4">
                    <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center text-slate-400 group-hover:text-emerald-500 shadow-sm transition-colors">
                        <MapPin className="w-5 h-5" />
                    </div>
                    <div>
                        <h4 className="font-bold text-slate-700 group-hover:text-emerald-700 transition-colors">Suivre le trajet</h4>
                        <p className="text-sm text-slate-500">Voir la destination sur Maps</p>
                    </div>
                </div>
            </a>
        </div>
    );
}
