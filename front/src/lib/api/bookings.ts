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

export async function getMyBookings(page = 0, size = 10): Promise<PagedResponse<Booking>> {
    return apiGet<PagedResponse<Booking>>(`/bookings/mine?page=${page}&size=${size}`, {
        next: { tags: ["my-bookings"] }
    });
}

export async function getMyBookedRideIds(): Promise<string[]> {
    return apiGet<string[]>("/bookings/mine/ride-ids", {
        next: { tags: ["my-bookings"] }
    });
}

export async function getMyBookingForRide(rideId: string): Promise<Booking> {
    return apiGet<Booking>(`/bookings/ride/${rideId}/mine`, {
        next: { tags: ["my-bookings", `ride-${rideId}-booking`] }
    });
}

export async function getRidePassengers(rideId: string): Promise<PassengerInfo[]> {
    return apiGet<PassengerInfo[]>(`/bookings/ride/${rideId}/passengers`, {
        next: { tags: [`ride-${rideId}-passengers`] }
    });
}

export async function createBooking(data: CreateBookingRequest): Promise<Booking> {
    const booking = await apiPost<Booking>("/bookings", data);
    revalidateTag("my-bookings", "max");
    revalidateTag(`ride-${data.rideId}`, "max");
    revalidateTag(`ride-${data.rideId}-passengers`, "max");
    revalidateTag(`ride-${data.rideId}-booking`, "max");
    return booking;
}

export async function acceptBooking(bookingId: string): Promise<Booking> {
    const booking = await apiPost<Booking>(`/bookings/${bookingId}/accept`, undefined);
    revalidateTag("my-bookings", "max");
    if (booking.rideID) {
        revalidateTag(`ride-${booking.rideID}`, "max");
        revalidateTag(`ride-${booking.rideID}-passengers`, "max");
        revalidateTag(`ride-${booking.rideID}-booking`, "max");
    }
    return booking;
}

export async function rejectBooking(bookingId: string): Promise<void> {
    await apiPost<void>(`/bookings/${bookingId}/reject`, undefined);
    revalidateTag("my-bookings", "max");
}


export async function cancelBooking(bookingId: string): Promise<void> {
    await apiPost<void>(`/bookings/${bookingId}/cancel`, undefined);
    revalidateTag("my-bookings", "max");
}
