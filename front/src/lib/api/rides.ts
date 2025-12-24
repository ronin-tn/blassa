/**
 * Rides API service for server-side data fetching
 */

import { revalidateTag } from "next/cache";
import { apiGet, apiPost, apiPut, apiDelete } from "./client";
import {
    Ride,
    PagedResponse,
    CreateRideRequest,
    SearchRidesParams
} from "@/types/models";

// Re-export types if needed, or consumers can import from models directly
export type { Ride, PagedResponse, CreateRideRequest, SearchRidesParams };

// API Functions

/**
 * Get a single ride by ID
 */
export async function getRide(id: string): Promise<Ride> {
    return apiGet<Ride>(`/rides/${id}`, {
        next: { tags: [`ride-${id}`] }
    });
}

/**
 * Search for rides with filters
 */
export async function searchRides(params: SearchRidesParams): Promise<PagedResponse<Ride>> {
    const searchParams = new URLSearchParams();

    // Support both name-based and coordinate-based search
    if (params.from) searchParams.append("from", params.from);
    if (params.to) searchParams.append("to", params.to);
    if (params.date) searchParams.append("date", params.date);
    if (params.passengers) searchParams.append("passengers", params.passengers.toString());

    // Coordinate-based params
    if (params.originLat !== undefined) searchParams.append("originLat", params.originLat.toString());
    if (params.originLon !== undefined) searchParams.append("originLon", params.originLon.toString());
    if (params.destLat !== undefined) searchParams.append("destLat", params.destLat.toString());
    if (params.destLon !== undefined) searchParams.append("destLon", params.destLon.toString());
    if (params.seats !== undefined) searchParams.append("seats", params.seats.toString());
    if (params.departureTime) searchParams.append("departureTime", params.departureTime);

    if (params.genderFilter) searchParams.append("genderFilter", params.genderFilter);
    if (params.page !== undefined) searchParams.append("page", params.page.toString());
    if (params.size !== undefined) searchParams.append("size", params.size.toString());

    return apiGet<PagedResponse<Ride>>(`/rides/search?${searchParams.toString()}`, {
        next: { tags: ["rides"], revalidate: 60 } // Cache searches for 60s
    });
}

/**
 * Get rides created by the current user (as driver)
 */
export async function getMyRides(page = 0, size = 10): Promise<PagedResponse<Ride>> {
    return apiGet<PagedResponse<Ride>>(`/rides/mine?page=${page}&size=${size}`, {
        next: { tags: ["my-rides"] }
    });
}

/**
 * Create a new ride
 */
export async function createRide(data: CreateRideRequest): Promise<Ride> {
    const ride = await apiPost<Ride>("/rides", data);
    revalidateTag("rides", "max");
    revalidateTag("my-rides", "max");
    return ride;
}

/**
 * Update a ride
 */
export async function updateRide(id: string, data: Partial<CreateRideRequest>): Promise<Ride> {
    const ride = await apiPut<Ride>(`/rides/${id}`, data);
    revalidateTag("rides", "max");
    revalidateTag("my-rides", "max");
    revalidateTag(`ride-${id}`, "max");
    return ride;
}

/**
 * Start a ride (driver action)
 */
export async function startRide(id: string): Promise<Ride> {
    const ride = await apiPut<Ride>(`/rides/${id}/start`, undefined);
    revalidateTag(`ride-${id}`, "max");
    revalidateTag("my-rides", "max");
    return ride;
}

/**
 * Complete a ride (driver action)
 */
export async function completeRide(id: string): Promise<Ride> {
    const ride = await apiPut<Ride>(`/rides/${id}/complete`, undefined);
    revalidateTag(`ride-${id}`, "max");
    revalidateTag("my-rides", "max");
    return ride;
}

/**
 * Cancel/delete a ride
 */
export async function deleteRide(id: string): Promise<void> {
    await apiDelete<void>(`/rides/${id}`);
    revalidateTag("rides", "max");
    revalidateTag("my-rides", "max");
    revalidateTag(`ride-${id}`, "max");
}
