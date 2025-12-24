"use client";

import { useState, useEffect, useCallback } from "react";
import { clientGet } from "@/lib/api/client-api";
import { Ride, Booking, PagedResponse } from "@/types/models";

// Types for dashboard data
export interface DashboardStatsData {
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

interface UseDashboardDataResult {
    stats: DashboardStatsData;
    upcomingRides: DashboardRide[];
    isLoading: boolean;
    error: string | null;
    refetch: () => Promise<void>;
}

const defaultStats: DashboardStatsData = {
    totalTrips: 0,
    totalRides: 0,
    savedMoney: 0,
    rating: 0,
};

/**
 * Custom hook for fetching dashboard data
 * Handles loading, error states, and parallel data fetching
 */
export function useDashboardData(isAuthenticated: boolean): UseDashboardDataResult {
    const [stats, setStats] = useState<DashboardStatsData>(defaultStats);
    const [upcomingRides, setUpcomingRides] = useState<DashboardRide[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchDashboardData = useCallback(async () => {
        if (!isAuthenticated) {
            setIsLoading(false);
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            // Fetch all data in parallel using Promise.allSettled for resilience
            const [ridesData, bookingsData, reviewsData] = await Promise.allSettled([
                clientGet<PagedResponse<Ride>>("/rides/mine?page=0&size=50"),
                clientGet<PagedResponse<Booking>>("/bookings/mine?page=0&size=50"),
                clientGet<PagedResponse<{ rating: number }>>("/reviews/mine/received?page=0&size=100"),
            ]);

            let driverRides: DashboardRide[] = [];
            let passengerRides: DashboardRide[] = [];
            let completedTripsCount = 0;
            let publishedRidesCount = 0;
            let totalEarnings = 0;
            let avgRating = 0;

            // Process rides data
            if (ridesData.status === "fulfilled") {
                const ridesResponse = ridesData.value;
                publishedRidesCount = ridesResponse.totalElements ?? ridesResponse.content?.length ?? 0;

                driverRides = ridesResponse.content
                    .filter((r) => ["SCHEDULED", "FULL", "IN_PROGRESS"].includes(r.status))
                    .map((ride) => ({
                        id: ride.id,
                        type: "driver" as const,
                        origin: ride.originName,
                        destination: ride.destinationName,
                        departureTime: ride.departureTime,
                        seats: ride.totalSeats,
                        bookedSeats: ride.totalSeats - ride.availableSeats,
                        price: ride.pricePerSeat,
                        status: ride.status,
                    }));

                const completedDriverRides = ridesResponse.content.filter(
                    (r) => r.status === "COMPLETED"
                );
                completedTripsCount += completedDriverRides.length;
                completedDriverRides.forEach((r) => {
                    totalEarnings += (r.totalSeats - r.availableSeats) * r.pricePerSeat;
                });
            }

            // Process bookings data
            if (bookingsData.status === "fulfilled") {
                const bookingsResponse = bookingsData.value;

                completedTripsCount += bookingsResponse.content.filter(
                    (b) => b.status === "CONFIRMED" && b.rideStatus === "COMPLETED"
                ).length;

                passengerRides = bookingsResponse.content
                    .filter((b) => b.status === "CONFIRMED" || b.status === "PENDING")
                    .filter((b) => new Date(b.departureTime) > new Date())
                    .map((booking) => {
                        const [origin, destination] = booking.rideSummary.split(" -> ");
                        return {
                            id: booking.rideID,
                            type: "passenger" as const,
                            origin: origin || "",
                            destination: destination || "",
                            departureTime: booking.departureTime,
                            price: booking.priceTotal,
                            driverName: booking.driverName,
                            status: booking.status,
                        };
                    });
            }

            // Process reviews data
            if (reviewsData.status === "fulfilled" && reviewsData.value.content.length > 0) {
                const ratings = reviewsData.value.content.map((r) => r.rating);
                avgRating = Math.round((ratings.reduce((a, b) => a + b, 0) / ratings.length) * 10) / 10;
            }

            // Combine and sort rides
            const allRides = [...driverRides, ...passengerRides].sort(
                (a, b) => new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime()
            );

            setUpcomingRides(allRides);
            setStats({
                totalTrips: completedTripsCount,
                totalRides: publishedRidesCount,
                savedMoney: totalEarnings,
                rating: avgRating,
            });
        } catch (err) {
            console.error("Failed to fetch dashboard data:", err);
            setError("Impossible de charger vos données. Veuillez réessayer.");
        } finally {
            setIsLoading(false);
        }
    }, [isAuthenticated]);

    useEffect(() => {
        fetchDashboardData();
    }, [fetchDashboardData]);

    return {
        stats,
        upcomingRides,
        isLoading,
        error,
        refetch: fetchDashboardData,
    };
}
