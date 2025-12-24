export interface DashboardStats {
    totalTrips: number;
    totalRides: number;
    savedMoney: number;
    rating: number;
}

export interface DashboardRide {
    id: string;
    type: "driver" | "passenger";
    origin: string;
    destination: string;
    departureTime: string;
    seats?: number;
    bookedSeats?: number;
    price: number;
    driverName?: string;
    status: string;
}

export interface ReviewResponse {
    id: string;
    rating: number;
    comment?: string;
    reviewerName: string;
    createdAt: string;
}

import { PagedResponse } from "@/types/models";

export type { PagedResponse };
