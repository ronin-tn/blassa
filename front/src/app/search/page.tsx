import { Suspense } from "react";
import type { Metadata } from "next";
import SearchClient from "@/components/search/SearchClient";
import { searchRides } from "@/lib/api/rides";
import { getMyBookedRideIds } from "@/lib/api/bookings";
import { getServerUser } from "@/lib/auth/server";
import { TUNISIA_CITIES } from "@/data/cities";
import { PagedResponse, Ride } from "@/types/models";
import { Loader2 } from "lucide-react";
import Navbar from "@/components/layout/Navbar";

interface SearchPageProps {
    searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}

/**
 * Generate dynamic metadata for search results SEO
 */
export async function generateMetadata({ searchParams }: SearchPageProps): Promise<Metadata> {
    const params = await searchParams;
    const from = (params.from as string) || "";
    const to = (params.to as string) || "";
    const date = (params.date as string) || "";

    // Build dynamic title and description
    if (from && to) {
        const formattedDate = date
            ? new Date(date).toLocaleDateString("fr-FR", { weekday: "long", day: "numeric", month: "long" })
            : "";

        const title = `Covoiturage ${from} → ${to}${formattedDate ? ` le ${formattedDate}` : ""} | Blassa`;
        const description = `Trouvez un covoiturage de ${from} à ${to}${formattedDate ? ` le ${formattedDate}` : ""}. Comparez les prix et réservez en quelques clics sur Blassa.`;

        return {
            title,
            description,
            openGraph: {
                title,
                description,
                type: "website",
            },
        };
    }

    return {
        title: "Recherche de covoiturage | Blassa",
        description: "Recherchez et comparez les trajets de covoiturage en Tunisie. Voyagez moins cher avec Blassa.",
    };
}

async function SearchPageContent({ searchParams }: { searchParams: Promise<{ [key: string]: string | string[] | undefined }> }) {
    const params = await searchParams;
    const from = (params.from as string) || "";
    const to = (params.to as string) || "";
    const date = (params.date as string) || "";
    const passengers = (params.p as string) || "1";
    const genderFilter = (params.g as string) || "";
    const page = parseInt((params.page as string) || "0");

    const user = await getServerUser();

    // Fetch rides
    let ridesData: PagedResponse<Ride> = {
        content: [],
        totalPages: 0,
        totalElements: 0,
        last: true,
        size: 10,
        number: 0,
        first: true,
        empty: true,
    };

    const originCity = TUNISIA_CITIES.find((c) => c.name.toLowerCase() === from.toLowerCase());
    const destCity = TUNISIA_CITIES.find((c) => c.name.toLowerCase() === to.toLowerCase());

    if (originCity && destCity) {
        try {
            ridesData = await searchRides({
                originLat: originCity.lat,
                originLon: originCity.lon,
                destLat: destCity.lat,
                destLon: destCity.lon,
                seats: parseInt(passengers),
                page,
                size: 10,
                departureTime: date ? `${date}T00:00:00` : undefined,
                genderFilter: genderFilter !== "ANY" ? genderFilter : undefined,
            });
        } catch {
            console.error("Search failed");
        }
    }

    // Fetch booked ride IDs if user is logged in
    let bookedRideIds: string[] = [];
    if (user) {
        try {
            bookedRideIds = await getMyBookedRideIds();
        } catch {
            console.error("Failed to fetch booked rides");
        }
    }

    return (
        <SearchClient
            initialRides={ridesData}
            initialBookedRideIds={bookedRideIds}
            currentUser={user}
            searchParams={{ from, to, date, passengers }}
        />
    );
}

export default async function SearchPage({ searchParams }: SearchPageProps) {
    return (
        <>
            <Navbar />
            <Suspense
                fallback={
                    <div className="min-h-screen flex items-center justify-center bg-slate-50 pt-16">
                        <div className="flex flex-col items-center gap-4">
                            <Loader2 className="w-10 h-10 text-[#0A8F8F] animate-spin" />
                            <span className="text-slate-600">Chargement...</span>
                        </div>
                    </div>
                }
            >
                <SearchPageContent searchParams={searchParams} />
            </Suspense>
        </>
    );
}
