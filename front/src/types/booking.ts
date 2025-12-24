/**
 * Booking types with UI helpers
 * Core types are imported from models.ts
 */

import {
    Booking as BaseBooking,
    BookingStatus,
    PassengerInfo,
    CreateBookingRequest,
    RideStatus,
} from "./models";

// Extended Booking with additional fields used in the bookings page
export interface Booking extends BaseBooking {
    rideID: string; // Alias for rideId (backend uses camelCase, but some components use this)
    rideStatus: RideStatus;
    createdAt: string;
}

// Re-export types
export type { BookingStatus, PassengerInfo, CreateBookingRequest };

// UI Helper Constants
export const BookingStatusLabels: Record<BookingStatus, string> = {
    PENDING: "En attente",
    CONFIRMED: "Confirmée",
    REJECTED: "Refusée",
    CANCELLED: "Annulée",
};

export const BookingStatusColors: Record<BookingStatus, string> = {
    PENDING: "bg-yellow-100 text-yellow-700",
    CONFIRMED: "bg-emerald-100 text-emerald-700",
    REJECTED: "bg-red-100 text-red-700",
    CANCELLED: "bg-slate-100 text-slate-600",
};
