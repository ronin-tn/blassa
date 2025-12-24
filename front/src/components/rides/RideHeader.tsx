"use client";

import { MapPin, Calendar, Clock } from "lucide-react";
import { Ride, RideStatus } from "@/types/models";
import { RideStatusLabels, RideStatusColors } from "@/constants/statusLabels";

interface RideHeaderProps {
    ride: Ride;
}

/**
 * Displays the ride route header with origin, destination, status, date and time
 */
export default function RideHeader({ ride }: RideHeaderProps) {
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("fr-FR", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric",
        });
    };

    const formatTime = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
    };

    return (
        <div className="p-6 border-b border-slate-100 bg-gradient-to-r from-[#006B8F]/5 to-[#FF9A3E]/5">
            <div className="flex items-center gap-3 mb-4">
                <div className="w-12 h-12 bg-[#006B8F] rounded-xl flex items-center justify-center">
                    <MapPin className="w-6 h-6 text-white" />
                </div>
                <div>
                    <div className="flex items-center gap-2 text-xl font-bold text-slate-900">
                        <span>{ride.originName}</span>
                        <span className="text-slate-400">→</span>
                        <span>{ride.destinationName}</span>
                    </div>
                    <span className={`inline-block mt-1 px-3 py-0.5 rounded-full text-xs font-medium ${RideStatusColors[ride.status as RideStatus]}`}>
                        {RideStatusLabels[ride.status as RideStatus]}
                    </span>
                </div>
            </div>
            <div className="flex flex-wrap items-center gap-6 text-slate-600">
                {ride.carMake && (
                    <div className="flex items-center gap-2 px-3 py-1 bg-slate-100 rounded-lg text-slate-700 font-medium">
                        <span>{ride.carMake} {ride.carModel}</span>
                        <span className="w-2 h-2 rounded-full bg-slate-400" />
                        <span className="text-slate-500">{ride.carColor}</span>
                    </div>
                )}
                <div className="flex items-center gap-2">
                    <Calendar className="w-5 h-5 text-[#006B8F]" />
                    <span>{formatDate(ride.departureTime)}</span>
                </div>
                <div className="flex items-center gap-2">
                    <Clock className="w-5 h-5 text-[#006B8F]" />
                    <span>{formatTime(ride.departureTime)}</span>
                </div>
            </div>
        </div>
    );
}
