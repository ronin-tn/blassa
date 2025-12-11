// Ride types matching backend RideResponse
export interface Ride {
    id: string;
    driverName: string;
    driverEmail: string;
    driverRating: number | null;
    driverFacebookUrl: string | null;
    driverInstagramUrl: string | null;
    driverPhoneNumber: string;
    originName: string;
    originLat: number;
    originLon: number;
    destinationName: string;
    destinationLat: number;
    destinationLon: number;
    departureTime: string;
    totalSeats: number;
    availableSeats: number;
    pricePerSeat: number;
    allowsSmoking: boolean;
    genderPreference: RideGenderPreference;
    status: RideStatus;
}

export type RideGenderPreference = "ANY" | "MALE_ONLY" | "FEMALE_ONLY";

export type RideStatus =
    | "SCHEDULED"
    | "FULL"
    | "IN_PROGRESS"
    | "COMPLETED"
    | "CANCELLED";

// Paginated response
export interface PagedResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number; // current page (0-indexed)
    first: boolean;
    last: boolean;
    empty: boolean;
}

// Status labels and colors
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
