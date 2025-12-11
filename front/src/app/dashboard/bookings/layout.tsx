import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Mes Réservations - Blassa",
    description: "Consultez et gérez vos réservations de trajets sur Blassa.",
};

export default function MyBookingsLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return children;
}
