"use server";

import { revalidatePath } from "next/cache";
import { cookies } from "next/headers";
import { CreateBookingRequest } from "@/types/models";
import { AUTH_COOKIE_NAME } from "@/lib/config";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8088/api/v1";

async function getAuthToken() {
    const cookieStore = await cookies();
    return cookieStore.get(AUTH_COOKIE_NAME)?.value;
}

export async function createBookingAction(data: CreateBookingRequest) {
    const token = await getAuthToken();
    if (!token) return { success: false, error: "Non authentifié" };

    try {
        const res = await fetch(`${API_URL}/bookings`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(data),
        });

        if (!res.ok) {
            const error = await res.json().catch(() => ({}));
            return { success: false, error: error.message || "Erreur de réservation" };
        }

        revalidatePath(`/rides/${data.rideId}`);
        return { success: true };
    } catch {
        return { success: false, error: "Erreur serveur" };
    }
}

export async function acceptBookingAction(bookingId: string) {
    const token = await getAuthToken();
    if (!token) return { success: false, error: "Non authentifié" };

    try {
        const res = await fetch(`${API_URL}/bookings/${bookingId}/accept`, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) return { success: false, error: "Impossible d'accepter" };

        revalidatePath("/dashboard/rides");
        return { success: true };
    } catch {
        return { success: false, error: "Erreur serveur" };
    }
}

export async function rejectBookingAction(bookingId: string) {
    const token = await getAuthToken();
    if (!token) return { success: false, error: "Non authentifié" };

    try {
        const res = await fetch(`${API_URL}/bookings/${bookingId}/reject`, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) return { success: false, error: "Impossible de refuser" };

        revalidatePath("/dashboard/rides");
        return { success: true };
    } catch {
        return { success: false, error: "Erreur serveur" };
    }
}

export async function getMyBookingForRideAction(rideId: string) {
    const token = await getAuthToken();
    if (!token) return { success: false, error: "Non authentifié" };

    try {
        const res = await fetch(`${API_URL}/bookings/ride/${rideId}/mine`, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (!res.ok) return { success: false, error: "Non trouvé" };

        const booking = await res.json();
        return { success: true, booking };
    } catch {
        return { success: false, error: "Erreur serveur" };
    }
}
