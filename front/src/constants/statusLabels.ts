/**
 * Status labels and styling constants
 * Shared across components for consistent UI
 */

import { RideStatus, BookingStatus, RideGenderPreference } from "@/types/models";

// ============================================================================
// Ride Status
// ============================================================================

export const RideStatusLabels: Record<RideStatus, string> = {
    SCHEDULED: "Prévu",
    IN_PROGRESS: "En cours",
    COMPLETED: "Terminé",
    CANCELLED: "Annulé",
    FULL: "Complet",
};

export const RideStatusColors: Record<RideStatus, string> = {
    SCHEDULED: "bg-blue-100 text-blue-700",
    IN_PROGRESS: "bg-yellow-100 text-yellow-700",
    COMPLETED: "bg-green-100 text-green-700",
    CANCELLED: "bg-red-100 text-red-700",
    FULL: "bg-purple-100 text-purple-700",
};

// ============================================================================
// Booking Status
// ============================================================================

export const BookingStatusLabels: Record<BookingStatus, string> = {
    PENDING: "En attente",
    CONFIRMED: "Confirmée",
    REJECTED: "Refusée",
    CANCELLED: "Annulée",
};

export const BookingStatusColors: Record<BookingStatus, string> = {
    PENDING: "bg-yellow-50 text-yellow-700 border-yellow-200",
    CONFIRMED: "bg-emerald-50 text-emerald-700 border-emerald-200",
    REJECTED: "bg-red-50 text-red-700 border-red-200",
    CANCELLED: "bg-slate-50 text-slate-700 border-slate-200",
};

// ============================================================================
// Gender Preference
// ============================================================================

export const GenderPreferenceLabels: Record<RideGenderPreference, string> = {
    MALE_ONLY: "Hommes uniquement",
    FEMALE_ONLY: "Femmes uniquement",
    ANY: "Peu importe",
};
