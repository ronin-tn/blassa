"use client";

import { Users, Cigarette, CigaretteOff, Shield, Music, Dog, Briefcase } from "lucide-react";
import { Ride } from "@/types/models";
import { GenderPreferenceLabels } from "@/constants/statusLabels";

interface RideDetailsGridProps {
    ride: Ride;
}

/**
 * Displays the ride details grid: seats, smoking preference, gender preference
 */
export default function RideDetailsGrid({ ride }: RideDetailsGridProps) {
    return (
        <div className="p-6 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Available Seats */}
            <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center">
                    <Users className="w-5 h-5 text-slate-600" />
                </div>
                <div>
                    <p className="text-sm text-slate-500">Places disponibles</p>
                    <p className="font-medium text-slate-900">{ride.availableSeats} / {ride.totalSeats}</p>
                </div>
            </div>

            {/* Smoking Preference */}
            <div className="flex items-center gap-3">
                {ride.allowsSmoking ? (
                    <div className="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center">
                        <Cigarette className="w-5 h-5 text-orange-600" />
                    </div>
                ) : (
                    <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center">
                        <CigaretteOff className="w-5 h-5 text-slate-600" />
                    </div>
                )}
                <div>
                    <p className="text-sm text-slate-500">Fumeur</p>
                    <p className="font-medium text-slate-900">{ride.allowsSmoking ? "Autorisé" : "Non autorisé"}</p>
                </div>
            </div>

            {/* Gender Preference */}
            <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                    <Shield className="w-5 h-5 text-purple-600" />
                </div>
                <div>
                    <p className="text-sm text-slate-500">Préférence</p>
                    <p className="font-medium text-slate-900">{GenderPreferenceLabels[ride.genderPreference]}</p>
                </div>
            </div>

            {/* Music */}
            {ride.allowsMusic && (
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                        <Music className="w-5 h-5 text-green-600" />
                    </div>
                    <div>
                        <p className="text-sm text-slate-500">Musique</p>
                        <p className="font-medium text-slate-900">Autorisée</p>
                    </div>
                </div>
            )}

            {/* Pets */}
            {ride.allowsPets && (
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                        <Dog className="w-5 h-5 text-purple-600" />
                    </div>
                    <div>
                        <p className="text-sm text-slate-500">Animaux</p>
                        <p className="font-medium text-slate-900">Autorisés</p>
                    </div>
                </div>
            )}

            {/* Luggage */}
            <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-slate-100 rounded-lg flex items-center justify-center">
                    <Briefcase className="w-5 h-5 text-slate-600" />
                </div>
                <div>
                    <p className="text-sm text-slate-500">Bagage</p>
                    <p className="font-medium text-slate-900">
                        {ride.luggageSize === "SMALL" && "Petit"}
                        {ride.luggageSize === "MEDIUM" && "Moyen"}
                        {ride.luggageSize === "LARGE" && "Grand"}
                    </p>
                </div>
            </div>
        </div>
    );
}
