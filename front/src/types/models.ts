/**
 * Core API model types - Single Source of Truth
 * 
 * This file contains the canonical type definitions that match the backend API.
 * Other type files (ride.ts, booking.ts, etc.) may re-export these with UI helpers.
 */

// ============================================================================
// Common Types
// ============================================================================

export interface PagedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number; // current page (0-indexed)
    first?: boolean;
    last?: boolean;
    empty?: boolean;
}

// ============================================================================
// User Types
// ============================================================================

export interface UserProfile {
    firstName: string;
    lastName: string;
    email: string;
    dob: string | null;
    gender: "MALE" | "FEMALE" | null;
    phoneNumber: string | null;
    bio: string | null;
    profilePictureUrl: string | null;
    facebookUrl: string | null;
    instagramUrl: string | null;
    oauthProvider?: string | null;
}

export interface UserStats {
    totalRidesAsDriver: number;
    totalRidesAsPassenger: number;
    averageRating: number | null;
    totalReviews: number;
}

// ============================================================================
// Ride Types
// ============================================================================

export type RideGenderPreference = "MALE_ONLY" | "FEMALE_ONLY" | "ANY";

export type RideStatus =
    | "SCHEDULED"
    | "FULL"
    | "IN_PROGRESS"
    | "COMPLETED"
    | "CANCELLED";

export interface PublicProfile {
    id: string;
    firstName: string;
    lastName: string;
    bio: string;
    profilePictureUrl: string;
    gender: string;
    memberSince: string;
    completedRidesCount: number;
    averageRating: number | null;
}

export interface Ride {
    id: string;
    driverId: string;
    driverName: string;
    driverProfilePictureUrl?: string; // Add this
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
    driverEmail: string;
    driverPhoneNumber: string;
    driverRating: number | null;
    driverFacebookUrl?: string | null;
    driverInstagramUrl?: string | null;
    carMake?: string;
    carModel?: string;
    carColor?: string;
}

export interface CreateRideRequest {
    originName: string;
    originLat: number;
    originLon: number;
    destinationName: string;
    destinationLat: number;
    destinationLon: number;
    departureTime: string;
    totalSeats: number;
    pricePerSeat: number;
    allowsSmoking: boolean;
    genderPreference: RideGenderPreference;
}

export interface SearchRidesParams {
    from?: string;
    to?: string;
    date?: string;
    passengers?: number;
    genderFilter?: string;
    page?: number;
    size?: number;
    // Coordinate-based search params
    originLat?: number;
    originLon?: number;
    destLat?: number;
    destLon?: number;
    seats?: number;
    departureTime?: string;
}

// ============================================================================
// Booking Types
// ============================================================================

export type BookingStatus = "PENDING" | "CONFIRMED" | "REJECTED" | "CANCELLED";

export interface Booking {
    id: string;
    rideID: string;
    rideSummary: string;
    driverName: string;
    departureTime: string;
    seatsBooked: number;
    priceTotal: number;
    status: BookingStatus;
    rideStatus?: RideStatus;
    createdAt?: string;
    carLicensePlate?: string;
    carDescription?: string;
}

export interface PassengerInfo {
    bookingId: string;
    passengerId: string;
    passengerName: string;
    passengerEmail: string;
    passengerPhone: string;
    passengerProfilePictureUrl?: string | null;
    facebookUrl: string | null;
    instagramUrl: string | null;
    seatsBooked: number;
    status: BookingStatus;
}

export interface CreateBookingRequest {
    rideId: string;
    seatsRequested: number;
}

// ============================================================================
// Review Types
// ============================================================================

export interface ReviewRequest {
    bookingId: string;
    rating: number; // 1-5
    comment: string;
}

export interface ReviewResponse {
    id: string;
    bookingId: string;
    reviewerName: string;
    revieweeName: string;
    rating: number;
    comment: string;
    createdAt: string;
}
