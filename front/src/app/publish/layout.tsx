import type { Metadata } from "next";

export const metadata: Metadata = {
    title: "Publier un trajet | Blassa",
    description: "Publiez votre trajet et trouvez des passagers pour partager les frais",
};

export default function PublishLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return <>{children}</>;
}
