import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Mes Trajets - Blassa",
    description: "Gérez vos trajets publiés sur Blassa.",
};

export default function MyRidesLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return children;
}
