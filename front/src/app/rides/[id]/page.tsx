import { notFound } from "next/navigation";
import type { Metadata } from "next";
import RideDetailsClient from "@/components/rides/RideDetailsClient";
import ProfileGuard from "@/components/ProfileGuard";
import { getRide } from "@/lib/api/rides";
import { getRidePassengers, getMyBookingForRide } from "@/lib/api/bookings";
import { getServerUser } from "@/lib/auth/server";
import { PassengerInfo } from "@/types/models";

interface PageProps {
    params: Promise<{ id: string }>;
}

/**
 * Generate dynamic metadata for SEO
 */
export async function generateMetadata({ params }: PageProps): Promise<Metadata> {
    const { id } = await params;

    try {
        const ride = await getRide(id);

        const title = `${ride.originName} → ${ride.destinationName} | Blassa`;
        const description = `Covoiturage de ${ride.originName} à ${ride.destinationName} le ${new Date(ride.departureTime).toLocaleDateString("fr-FR")}. ${ride.availableSeats} place(s) disponible(s) • ${ride.pricePerSeat} TND/place`;

        return {
            title,
            description,
            openGraph: {
                title,
                description,
                type: "website",
            },
        };
    } catch {
        return {
            title: "Trajet introuvable | Blassa",
            description: "Ce trajet n'existe pas ou a été supprimé.",
        };
    }
}

export default async function RideDetailsPage({ params }: PageProps) {
    const { id } = await params;
    const user = await getServerUser();

    let ride;
    try {
        ride = await getRide(id);
    } catch {
        notFound();
    }

    if (!ride) {
        notFound();
    }

    let passengers: PassengerInfo[] = [];

    // If user is the driver, fetch passengers server-side
    // If user is a passenger, fetch their specific booking to show correct state
    if (user) {
        if (user.email === ride.driverEmail) {
            try {
                passengers = await getRidePassengers(id);
            } catch (e) {
                console.error("Failed to fetch passengers server-side", e);
            }
        } else {
            try {
                const myBooking = await getMyBookingForRide(id);
                if (myBooking) {
                    passengers.push({
                        bookingId: myBooking.id,
                        passengerName: `${user.firstName} ${user.lastName}`,
                        passengerEmail: user.email,
                        passengerPhone: user.phoneNumber || "",
                        facebookUrl: user.facebookUrl || null,
                        instagramUrl: user.instagramUrl || null,
                        seatsBooked: myBooking.seatsBooked,
                        status: myBooking.status
                    });
                }
            } catch (e) {
                // Ignore errors (e.g. 404 if no booking found)
            }
        }
    }

    return (
        <ProfileGuard>
            <RideDetailsClient
                ride={ride}
                initialPassengers={passengers}
                currentUser={user}
            />
        </ProfileGuard>
    );
}
