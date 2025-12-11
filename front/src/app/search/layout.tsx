import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Recherche de trajets | Blassa",
    description: "Trouvez un trajet partagé vers votre destination",
};

export default function SearchLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return <>{children}</>;
}
