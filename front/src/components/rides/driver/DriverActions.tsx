"use client";

import Link from "next/link";
import { Loader2, Play, CheckCircle, Edit, Ban } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Ride, RideStatus } from "@/types/models";

interface DriverActionsProps {
    ride: Ride;
    isLoading: boolean;
    onStart: () => void;
    onComplete: () => void;
    onCancel: () => void;
    isAcquisitionPhase: boolean;
    hasConfirmedBookings: boolean;
}

/**
 * Driver action buttons: Start, Complete, Edit, Cancel ride
 */
export default function DriverActions({
    ride,
    isLoading,
    onStart,
    onComplete,
    onCancel,
    isAcquisitionPhase,
    hasConfirmedBookings,
}: DriverActionsProps) {
    const status = ride.status as RideStatus;

    return (
        <div className="mt-6 pt-4 border-t border-emerald-200">
            <h3 className="text-sm font-medium text-emerald-800 mb-3">Actions</h3>
            <div className="flex flex-wrap gap-3">
                {/* Start button for SCHEDULED or FULL */}
                {(status === "SCHEDULED" || status === "FULL") && (
                    <div className="relative group">
                        <Button
                            onClick={onStart}
                            disabled={isLoading || isAcquisitionPhase}
                            className={`rounded-xl transition-all ${isAcquisitionPhase
                                ? "bg-slate-300 text-slate-500 cursor-not-allowed hover:bg-slate-300"
                                : "bg-emerald-600 hover:bg-emerald-700 text-white"
                                }`}
                        >
                            {isLoading ? (
                                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                            ) : (
                                <Play className="w-4 h-4 mr-2" />
                            )}
                            Démarrer le trajet
                        </Button>
                        {isAcquisitionPhase && (
                            <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 w-max max-w-[200px] px-2 py-1 bg-slate-800 text-white text-xs rounded opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10 text-center">
                                En attente de passagers ou 30min avant départ
                            </div>
                        )}
                    </div>
                )}

                {/* Complete button for IN_PROGRESS */}
                {status === "IN_PROGRESS" && (
                    <Button
                        onClick={onComplete}
                        disabled={isLoading}
                        className="bg-blue-600 hover:bg-blue-700 text-white rounded-xl"
                    >
                        {isLoading ? (
                            <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                        ) : (
                            <CheckCircle className="w-4 h-4 mr-2" />
                        )}
                        Terminer le trajet
                    </Button>
                )}

                {/* Edit button for SCHEDULED */}
                {/* Edit button for SCHEDULED */}
                {status === "SCHEDULED" && (
                    hasConfirmedBookings ? (
                        <div title="Impossible de modifier un trajet avec des réservations confirmées">
                            <Button
                                disabled
                                variant="outline"
                                className="rounded-xl border-slate-200 text-slate-400 cursor-not-allowed"
                            >
                                <Edit className="w-4 h-4 mr-2" />
                                Modifier
                            </Button>
                        </div>
                    ) : (
                        <Link href={`/rides/${ride.id}/edit`}>
                            <Button
                                variant={isAcquisitionPhase ? "default" : "outline"}
                                className={`rounded-xl ${isAcquisitionPhase
                                    ? "bg-[#006B8F] text-white hover:bg-[#005572]" // Primary emphasis
                                    : "border-slate-300" // Secondary emphasis
                                    }`}
                            >
                                <Edit className="w-4 h-4 mr-2" />
                                Modifier
                            </Button>
                        </Link>
                    )
                )}

                {/* Cancel button for SCHEDULED, FULL, or IN_PROGRESS */}
                {(status === "SCHEDULED" || status === "FULL" || status === "IN_PROGRESS") && (
                    <Button
                        onClick={onCancel}
                        disabled={isLoading}
                        variant="outline"
                        className="rounded-xl border border-red-500 bg-white text-red-600 shadow-sm hover:bg-red-50 hover:text-red-700 hover:border-red-600 ml-auto"
                    >
                        <Ban className="w-4 h-4 mr-2" />
                        <span className="hidden sm:inline">Annuler</span>
                    </Button>
                )}
            </div>
        </div>
    );
}
