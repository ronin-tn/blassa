// Booking types matching backend BookingResponse
export interface Booking {
    id: string;
    rideID: string;
    rideSummary: string;
    driverName: string;
    departureTime: string;
    seatsBooked: number;
    priceTotal: number;
    status: BookingStatus;
    createdAt: string;
}

export type BookingStatus = "PENDING" | "CONFIRMED" | "REJECTED" | "CANCELLED";

// Status labels and colors
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
