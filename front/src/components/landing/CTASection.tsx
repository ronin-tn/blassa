"use client";

import { Button } from "@/components/ui/button";

interface CTASectionProps {
    onPublishClick?: () => void;
    onSearchClick?: () => void;
}

export default function CTASection({
    onPublishClick,
    onSearchClick,
}: CTASectionProps) {
    const handlePublishClick = () => {
        onPublishClick?.();
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const handleSearchClick = () => {
        onSearchClick?.();
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    return (
        <section className="pb-32 pt-10 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
            <div className="relative rounded-[2.5rem] overflow-hidden bg-gradient-to-r from-orange-500 to-amber-600 text-center py-20 px-6 lg:px-20 shadow-2xl">
                {/* Background Pattern */}
                <div
                    className="absolute inset-0 opacity-10"
                    style={{
                        backgroundImage:
                            "url('https://www.transparenttextures.com/patterns/cubes.png')",
                    }}
                ></div>
                <div className="absolute top-0 right-0 w-[600px] h-[600px] bg-white opacity-10 rounded-full mix-blend-overlay filter blur-[80px] animate-blob"></div>

                <div className="relative z-10 space-y-8">
                    <h2 className="text-4xl md:text-6xl font-bold text-white tracking-tight font-[family-name:var(--font-poppins)]">
                        Conducteur ou Passager ?
                    </h2>
                    <p className="text-orange-50 text-xl font-medium max-w-2xl mx-auto">
                        Rejoignez la plus grande communauté de covoiturage en Tunisie.
                    </p>
                    <div className="flex flex-col sm:flex-row gap-5 justify-center pt-6">
                        {/* Primary Button (Driver Focus) */}
                        <Button
                            size="lg"
                            onClick={handlePublishClick}
                            className="h-16 px-10 rounded-full text-xl font-bold bg-white text-orange-600 hover:bg-orange-50 border-0 shadow-xl hover:scale-105 transition-transform w-full sm:w-auto"
                        >
                            Publier un trajet
                        </Button>

                        {/* Secondary Button (Passenger Focus) */}
                        <Button
                            size="lg"
                            variant="outline"
                            onClick={handleSearchClick}
                            className="h-16 px-10 rounded-full text-xl font-bold border-2 border-white text-white bg-transparent hover:bg-white/10 hover:text-white transition-all w-full sm:w-auto"
                        >
                            Rechercher un trajet
                        </Button>
                    </div>
                </div>
            </div>
        </section>
    );
}
