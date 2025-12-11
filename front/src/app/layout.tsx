import type { Metadata } from "next";
import { Poppins, Inter } from "next/font/google";
import { Providers } from "./providers";
import "./globals.css";

const poppins = Poppins({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700", "800"],
  variable: "--font-poppins",
  display: "swap",
});

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
  display: "swap",
});

export const metadata: Metadata = {
  title: "Blassa - Covoiturage en Tunisie",
  description:
    "Blassa révolutionne le transport en Tunisie en reliant conducteurs et passagers pour des trajets partagés. Rejoignez la communauté !",
  keywords: [
    "covoiturage",
    "Tunisie",
    "trajet",
    "partage",
    "conducteur",
    "passager",
    "Tunis",
    "Sousse",
    "Sfax",
  ],
  authors: [{ name: "Blassa" }],
  openGraph: {
    title: "Blassa - Covoiturage en Tunisie",
    description:
      "Rejoignez la plus grande communauté de covoiturage en Tunisie.",
    type: "website",
    locale: "fr_TN",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="fr" className={`${poppins.variable} ${inter.variable}`}>
      <body className="antialiased font-sans">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}

