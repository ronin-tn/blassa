"use client";

import Link from "next/link";
import { Loader2, Check, X, Phone, Mail, Facebook, Instagram } from "lucide-react";
import { PassengerInfo, BookingStatus } from "@/types/models";
import { BookingStatusLabels, BookingStatusColors } from "@/constants/statusLabels";

interface PassengerCardProps {
    passenger: PassengerInfo;
    onAccept: (bookingId: string) => void;
    onReject: (bookingId: string) => void;
    isLoading: boolean;
}

/**
 * Displays a single passenger card with contact info and accept/reject actions
 */
export default function PassengerCard({
    passenger,
    onAccept,
    onReject,
    isLoading
}: PassengerCardProps) {
    const profileUrl = `/users/${passenger.passengerId}`;

    return (
        <div
            className={`bg-white rounded-xl p-4 border ${passenger.status === "PENDING"
                ? "border-yellow-300 ring-2 ring-yellow-100"
                : "border-emerald-200"
                }`}
        >
            <div className="flex items-start gap-4">
                {/* Avatar - Clickable */}
                <Link href={profileUrl} className="w-12 h-12 rounded-full overflow-hidden bg-emerald-100 flex-shrink-0 border border-slate-100 relative hover:ring-2 hover:ring-[#0A8F8F] transition-all">
                    {passenger.passengerProfilePictureUrl ? (
                        // eslint-disable-next-line @next/next/no-img-element
                        <img
                            src={passenger.passengerProfilePictureUrl.replace("=s96-c", "=s100-c")}
                            alt={passenger.passengerName}
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-emerald-700 font-bold text-lg bg-emerald-100">
                            {passenger.passengerName.charAt(0).toUpperCase()}
                        </div>
                    )}
                </Link>

                <div className="flex-1 min-w-0">
                    {/* Header: Name, Status, Seats */}
                    <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center gap-2">
                            <Link href={profileUrl} className="font-semibold text-slate-900 hover:text-[#0A8F8F] transition-colors">
                                {passenger.passengerName}
                            </Link>
                            <span className={`text-xs px-2 py-0.5 rounded-full ${BookingStatusColors[passenger.status as BookingStatus]}`}>
                                {BookingStatusLabels[passenger.status as BookingStatus]}
                            </span>
                        </div>
                        <span className="text-sm bg-slate-100 text-slate-700 px-2 py-0.5 rounded-full">
                            {passenger.seatsBooked} place{passenger.seatsBooked > 1 ? "s" : ""}
                        </span>
                    </div>

                    {/* Contact Info */}
                    <div className="space-y-2">
                        <a
                            href={`tel:${passenger.passengerPhone}`}
                            className="flex items-center gap-2 text-sm text-slate-600 hover:text-emerald-600"
                        >
                            <Phone className="w-4 h-4" />
                            <span>{passenger.passengerPhone}</span>
                        </a>
                        <a
                            href={`mailto:${passenger.passengerEmail}`}
                            className="flex items-center gap-2 text-sm text-slate-600 hover:text-emerald-600"
                        >
                            <Mail className="w-4 h-4" />
                            <span className="truncate">{passenger.passengerEmail}</span>
                        </a>

                        {/* Social Links */}
                        {(passenger.facebookUrl || passenger.instagramUrl) && (
                            <div className="flex items-center gap-2 pt-1">
                                {passenger.facebookUrl && (
                                    <a
                                        href={passenger.facebookUrl}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="flex items-center gap-1.5 px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs hover:bg-blue-200"
                                    >
                                        <Facebook className="w-3 h-3" />
                                        Facebook
                                    </a>
                                )}
                                {passenger.instagramUrl && (
                                    <a
                                        href={passenger.instagramUrl}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="flex items-center gap-1.5 px-2 py-1 bg-pink-100 text-pink-700 rounded text-xs hover:bg-pink-200"
                                    >
                                        <Instagram className="w-3 h-3" />
                                        Instagram
                                    </a>
                                )}
                            </div>
                        )}
                    </div>

                    {/* Accept/Reject buttons for PENDING bookings */}
                    {passenger.status === "PENDING" && (
                        <div className="flex items-center gap-2 mt-4 pt-3 border-t border-slate-100">
                            <button
                                onClick={() => onAccept(passenger.bookingId)}
                                disabled={isLoading}
                                className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-emerald-600 text-white rounded-lg hover:bg-emerald-700 disabled:opacity-50 font-medium"
                            >
                                {isLoading ? (
                                    <Loader2 className="w-4 h-4 animate-spin" />
                                ) : (
                                    <Check className="w-4 h-4" />
                                )}
                                Accepter
                            </button>
                            <button
                                onClick={() => onReject(passenger.bookingId)}
                                disabled={isLoading}
                                className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 disabled:opacity-50 font-medium"
                            >
                                <X className="w-4 h-4" />
                                Refuser
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
