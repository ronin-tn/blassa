/**
 * Ride types with UI helpers
 * Core types are imported from models.ts
 */

import {
    Ride,
    RideStatus,
    RideGenderPreference,
    PagedResponse,
    CreateRideRequest,
    SearchRidesParams,
} from "./models";

// Re-export core types
export type { Ride, RideStatus, RideGenderPreference, PagedResponse, CreateRideRequest, SearchRidesParams };

// UI Helper Constants
export const RideStatusLabels: Record<RideStatus, string> = {
    SCHEDULED: "Planifié",
    FULL: "Complet",
    IN_PROGRESS: "En cours",
    COMPLETED: "Terminé",
    CANCELLED: "Annulé",
};

export const RideStatusColors: Record<RideStatus, string> = {
    SCHEDULED: "bg-blue-100 text-blue-700",
    FULL: "bg-orange-100 text-orange-700",
    IN_PROGRESS: "bg-emerald-100 text-emerald-700",
    COMPLETED: "bg-slate-100 text-slate-600",
    CANCELLED: "bg-red-100 text-red-700",
};

export const GenderPreferenceLabels: Record<RideGenderPreference, string> = {
    ANY: "Mixte",
    MALE_ONLY: "Hommes uniquement",
    FEMALE_ONLY: "Femmes uniquement",
};
