import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Paramètres | Blassa",
    description: "Gérez vos paramètres et préférences",
};

export default function SettingsLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return <>{children}</>;
}
