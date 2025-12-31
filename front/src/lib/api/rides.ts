import { revalidateTag } from "next/cache";
import { apiGet, apiPost, apiPut, apiDelete } from "./client";
import {
    Ride,
    PagedResponse,
    CreateRideRequest,
    SearchRidesParams
} from "@/types/models";

export type { Ride, PagedResponse, CreateRideRequest, SearchRidesParams };

export async function getRide(id: string): Promise<Ride> {
    return apiGet<Ride>(`/rides/${id}`, {
        next: { tags: [`ride-${id}`] }
    });
}

export async function searchRides(params: SearchRidesParams): Promise<PagedResponse<Ride>> {
    const searchParams = new URLSearchParams();

    if (params.from) searchParams.append("from", params.from);
    if (params.to) searchParams.append("to", params.to);
    if (params.date) searchParams.append("date", params.date);
    if (params.passengers) searchParams.append("passengers", params.passengers.toString());
    if (params.originLat !== undefined) searchParams.append("originLat", params.originLat.toString());
    if (params.originLon !== undefined) searchParams.append("originLon", params.originLon.toString());
    if (params.destLat !== undefined) searchParams.append("destLat", params.destLat.toString());
    if (params.destLon !== undefined) searchParams.append("destLon", params.destLon.toString());
    if (params.seats !== undefined) searchParams.append("seats", params.seats.toString());
    if (params.departureTime) searchParams.append("departureTime", params.departureTime);

    if (params.genderFilter) searchParams.append("genderFilter", params.genderFilter);
    if (params.page !== undefined) searchParams.append("page", params.page.toString());
    if (params.size !== undefined) searchParams.append("size", params.size.toString());
    if (params.sortBy) searchParams.append("sortBy", params.sortBy);

    return apiGet<PagedResponse<Ride>>(`/rides/search?${searchParams.toString()}`, {
        next: { tags: ["rides"], revalidate: 60 } // Cache searches for 60s
    });
}

export async function getMyRides(page = 0, size = 10): Promise<PagedResponse<Ride>> {
    return apiGet<PagedResponse<Ride>>(`/rides/mine?page=${page}&size=${size}`, {
        next: { tags: ["my-rides"] }
    });
}

export async function createRide(data: CreateRideRequest): Promise<Ride> {
    const ride = await apiPost<Ride>("/rides", data);
    revalidateTag("rides", "max");
    revalidateTag("my-rides", "max");
    return ride;
}

export async function updateRide(id: string, data: Partial<CreateRideRequest>): Promise<Ride> {
    const ride = await apiPut<Ride>(`/rides/${id}`, data);
    revalidateTag("rides", "max");
    revalidateTag("my-rides", "max");
    revalidateTag(`ride-${id}`, "max");
    return ride;
}

export async function startRide(id: string): Promise<Ride> {
    const ride = await apiPut<Ride>(`/rides/${id}/start`, undefined);
    revalidateTag(`ride-${id}`, "max");
    revalidateTag("my-rides", "max");
    return ride;
}

export async function completeRide(id: string): Promise<Ride> {
    const ride = await apiPut<Ride>(`/rides/${id}/complete`, undefined);
    revalidateTag(`ride-${id}`, "max");
    revalidateTag("my-rides", "max");
    return ride;
}

export async function deleteRide(id: string): Promise<void> {
    await apiDelete<void>(`/rides/${id}`);
    revalidateTag("rides", "max");
    revalidateTag("my-rides", "max");
    revalidateTag(`ride-${id}`, "max");
}
