"use client";

import Link from "next/link";
import { Star, MessageCircle, Phone, Facebook, Instagram } from "lucide-react";
import { Ride } from "@/types/models";

interface DriverContactViewProps {
    ride: Ride;
}

/**
 * Passenger view: displays driver info and contact methods
 */
export default function DriverContactView({ ride }: DriverContactViewProps) {
    return (
        <div className="p-6 border-t border-slate-100 bg-slate-50">
            <h3 className="text-sm font-medium text-slate-500 mb-4">Conducteur</h3>

            {/* Driver Info */}
            <Link href={`/users/${ride.driverId}`} className="flex items-center gap-4 mb-4 hover:bg-slate-100 p-2 -mx-2 rounded-xl transition-colors group">
                <div className="relative w-14 h-14 bg-[#006B8F] rounded-full flex items-center justify-center text-white text-lg font-bold group-hover:bg-[#005673] transition-colors overflow-hidden">
                    {ride.driverProfilePictureUrl ? (
                        <img
                            src={ride.driverProfilePictureUrl.replace("=s96-c", "=s400-c")} // Fix low quality google images
                            alt={ride.driverName}
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        ride.driverName.charAt(0)
                    )}
                </div>
                <div>
                    <p className="font-medium text-slate-900 text-lg group-hover:text-[#006B8F] flex items-center gap-2">
                        {ride.driverName}
                        <span className="text-xs font-normal text-slate-500 bg-slate-200 px-2 py-0.5 rounded-full">Voir profil</span>
                    </p>
                    {ride.driverRating !== null && ride.driverRating > 0 && (
                        <div className="flex items-center gap-1 text-sm text-slate-500">
                            <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                            <span>{ride.driverRating.toFixed(1)}</span>
                        </div>
                    )}
                </div>
            </Link>

            {/* Contact Methods */}
            <div className="mt-4 pt-4 border-t border-slate-200">
                <h4 className="text-sm font-medium text-slate-700 mb-3 flex items-center gap-2">
                    <MessageCircle className="w-4 h-4" />
                    Contacter le conducteur
                </h4>

                <div className="space-y-3">
                    {/* Phone */}
                    <a
                        href={`tel:${ride.driverPhoneNumber}`}
                        className="flex items-center gap-3 w-full px-4 py-3 bg-emerald-600 text-white rounded-xl hover:bg-emerald-700 transition-colors"
                    >
                        <div className="w-10 h-10 bg-white/20 rounded-full flex items-center justify-center">
                            <Phone className="w-5 h-5" />
                        </div>
                        <div>
                            <p className="font-semibold text-lg">{ride.driverPhoneNumber}</p>
                            <p className="text-emerald-100 text-sm">Appuyer pour appeler</p>
                        </div>
                    </a>

                    {/* Social Links */}
                    <div className="flex flex-wrap gap-3">
                        {ride.driverFacebookUrl && (
                            <a
                                href={ride.driverFacebookUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors"
                            >
                                <Facebook className="w-5 h-5" />
                                <span className="font-medium">Facebook</span>
                            </a>
                        )}
                        {ride.driverInstagramUrl && (
                            <a
                                href={ride.driverInstagramUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="flex items-center gap-2 px-4 py-2.5 bg-gradient-to-r from-purple-600 to-pink-500 text-white rounded-xl hover:from-purple-700 hover:to-pink-600 transition-all"
                            >
                                <Instagram className="w-5 h-5" />
                                <span className="font-medium">Instagram</span>
                            </a>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
