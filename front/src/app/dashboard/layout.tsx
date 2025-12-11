import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Tableau de bord - Blassa",
    description: "Gérez vos trajets et réservations sur Blassa.",
};

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return children;
}
