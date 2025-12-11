import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Mon Profil | Blassa",
    description: "Gérez votre profil et vos informations personnelles",
};

export default function ProfileLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return <>{children}</>;
}
