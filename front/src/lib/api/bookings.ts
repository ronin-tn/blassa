/**
 * Bookings API service for server-side data fetching
 */

import { revalidateTag } from "next/cache";
import { apiGet, apiPost } from "./client";
import {
    Booking,
    BookingStatus,
    PassengerInfo,
    PagedResponse,
    CreateBookingRequest
} from "@/types/models";

export type { Booking, BookingStatus, PassengerInfo, PagedResponse, CreateBookingRequest };

// API Functions

/**
 * Get current user's bookings
 */
export async function getMyBookings(page = 0, size = 10): Promise<PagedResponse<Booking>> {
    return apiGet<PagedResponse<Booking>>(`/bookings/mine?page=${page}&size=${size}`, {
        next: { tags: ["my-bookings"] }
    });
}

/**
 * Get ride IDs that the current user has booked
 */
export async function getMyBookedRideIds(): Promise<string[]> {
    return apiGet<string[]>("/bookings/mine/ride-ids", {
        next: { tags: ["my-bookings"] }
    });
}

/**
 * Get user's booking for a specific ride
 */
export async function getMyBookingForRide(rideId: string): Promise<Booking> {
    return apiGet<Booking>(`/bookings/ride/${rideId}/mine`, {
        next: { tags: ["my-bookings", `ride-${rideId}-booking`] }
    });
}

/**
 * Get passengers for a ride (driver only)
 */
export async function getRidePassengers(rideId: string): Promise<PassengerInfo[]> {
    return apiGet<PassengerInfo[]>(`/bookings/ride/${rideId}/passengers`, {
        next: { tags: [`ride-${rideId}-passengers`] }
    });
}

/**
 * Create a new booking
 */
export async function createBooking(data: CreateBookingRequest): Promise<Booking> {
    const booking = await apiPost<Booking>("/bookings", data);
    revalidateTag("my-bookings", "max");
    revalidateTag(`ride-${data.rideId}`, "max"); // Update ride availability
    revalidateTag(`ride-${data.rideId}-passengers`, "max");
    revalidateTag(`ride-${data.rideId}-booking`, "max");
    return booking;
}

/**
 * Accept a booking (driver action)
 */
export async function acceptBooking(bookingId: string): Promise<Booking> {
    const booking = await apiPost<Booking>(`/bookings/${bookingId}/accept`, undefined);
    revalidateTag("my-bookings", "max");
    if (booking.rideId) {
        revalidateTag(`ride-${booking.rideId}`, "max");
        revalidateTag(`ride-${booking.rideId}-passengers`, "max");
        revalidateTag(`ride-${booking.rideId}-booking`, "max");
    }
    return booking;
}

/**
 * Reject a booking (driver action)
 */
export async function rejectBooking(bookingId: string): Promise<void> {
    await apiPost<void>(`/bookings/${bookingId}/reject`, undefined);
    revalidateTag("my-bookings", "max");
}

/**
 * Cancel a booking (passenger action)
 */
export async function cancelBooking(bookingId: string): Promise<void> {
    await apiPost<void>(`/bookings/${bookingId}/cancel`, undefined);
    revalidateTag("my-bookings", "max");
}
