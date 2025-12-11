// Ride types matching backend RideResponse
export interface Ride {
    id: string;
    driverName: string;
    driverEmail: string;
    driverRating: number | null;
    driverFacebookUrl: string | null;
    driverInstagramUrl: string | null;
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
    | "IN_PROGRESS"
    | "COMPLETED"
    | "CANCELLED";

// Booking types matching backend BookingResponse
export interface Booking {
    id: string;
    rideId: string;
    rideSummary: string;
    driverName: string;
    departureTime: string;
    seatsBooked: number;
    priceTotal: number;
    status: BookingStatus;
    createdAt: string;
}

export type BookingStatus =
    | "PENDING"
    | "CONFIRMED"
    | "CANCELLED"
    | "COMPLETED";

// User types
export interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    gender: "MALE" | "FEMALE";
    birthDate: string;
    rating: number | null;
    totalRides: number;
    totalTrips: number;
}
