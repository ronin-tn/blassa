import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Détails du trajet - Blassa",
    description: "Consultez les détails de ce trajet sur Blassa.",
};

export default function RideDetailsLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return children;
}
