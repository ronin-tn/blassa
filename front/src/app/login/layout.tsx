import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Connexion - Blassa",
    description: "Connectez-vous à votre compte Blassa pour accéder à vos trajets.",
};

export default function LoginLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return children;
}
