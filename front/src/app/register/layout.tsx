import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Inscription - Blassa",
    description: "Créez votre compte Blassa pour commencer à covoiturer en Tunisie.",
};

export default function RegisterLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return children;
}
