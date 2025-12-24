"use server";

import { startRide, completeRide, deleteRide } from "@/lib/api/rides";

export async function startRideAction(rideId: string) {
    try {
        const ride = await startRide(rideId);
        return { success: true, ride };
    } catch (error) {
        return { success: false, error: error instanceof Error ? error.message : "Failed to start ride" };
    }
}

export async function completeRideAction(rideId: string) {
    try {
        const ride = await completeRide(rideId);
        return { success: true, ride };
    } catch (error) {
        return { success: false, error: error instanceof Error ? error.message : "Failed to complete ride" };
    }
}

export async function deleteRideAction(rideId: string) {
    try {
        await deleteRide(rideId);
        return { success: true };
    } catch (error) {
        return { success: false, error: error instanceof Error ? error.message : "Failed to delete ride" };
    }
}
