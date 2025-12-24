"use client";

import { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { useRouter } from "next/navigation";
import {
    Search,
    Wallet,
    Star,
    ArrowRight,
    CheckCircle2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import CitySelect from "@/components/ui/city-select";
import { City } from "@/data/cities";
import { useRecentSearches } from "@/hooks/useRecentSearches";

const popularRoutes = [
    { from: "Tunis", to: "Sousse" },
    { from: "Sousse", to: "Monastir" },
    { from: "Tunis", to: "Bizerte" },
    { from: "Sfax", to: "Tunis" },
];

interface HeroSectionProps {
    onTabChange?: (tab: "search" | "publish") => void;
}

export default function HeroSection({ onTabChange }: HeroSectionProps) {
    const router = useRouter();
    const { addSearch } = useRecentSearches();

    const [departure, setDeparture] = useState<City | null>(null);
    const [destination, setDestination] = useState<City | null>(null);
    const [date, setDate] = useState("");
    const [passengers, setPassengers] = useState("1");
    const [ladiesOnly, setLadiesOnly] = useState(false);
    const [activeTab, setActiveTab] = useState<"search" | "publish">("search");

    const handleTabChange = (tab: "search" | "publish") => {
        setActiveTab(tab);
        onTabChange?.(tab);
    };

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        if (!departure || !destination) {
            alert("Veuillez sélectionner un point de départ et une destination.");
            return;
        }

        // Save to recent searches
        addSearch({
            from: departure.name,
            to: destination.name,
            date: date || undefined,
            passengers: parseInt(passengers),
        });

        // Build clean, user-friendly URL
        const params = new URLSearchParams();
        params.set("from", departure.name);
        params.set("to", destination.name);
        if (date) params.set("date", date);
        if (passengers !== "1") params.set("p", passengers);
        if (ladiesOnly) params.set("g", "FEMALE_ONLY");

        router.push(`/search?${params.toString()}`);
    };

    return (
        <section className="relative pt-12 pb-20 lg:pt-16 lg:pb-32 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
            {/* Background Blobs */}
            <div className="absolute inset-0 pointer-events-none overflow-hidden">
                <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-amber-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
                <div className="absolute top-0 -right-4 w-[500px] h-[500px] bg-orange-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
                <div className="absolute -bottom-8 left-20 w-[500px] h-[500px] bg-pink-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>
            </div>

            <div className="grid lg:grid-cols-2 gap-12 lg:gap-20 items-center">
                {/* Left: Content & Search */}
                <div className="relative z-10 space-y-8">
                    <div className="space-y-4">
                        <div className="inline-flex items-center rounded-full border border-orange-200 bg-orange-50 px-3 py-1 text-sm text-orange-600">
                            <span className="flex h-2 w-2 rounded-full bg-orange-600 mr-2"></span>
                            Le covoiturage nouvelle génération
                        </div>
                        <h1 className="text-4xl lg:text-6xl font-bold tracking-tight text-slate-900 leading-[1.05] font-[family-name:var(--font-poppins)]">
                            Voyagez{" "}
                            <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-500 to-amber-500">
                                ensemble
                            </span>
                            .
                        </h1>
                        <p className="text-xl text-slate-600 font-medium">
                            Conducteur ou passager ? Rejoignez la communauté Blassa.
                        </p>
                    </div>

                    {/* Glassmorphic Search/Publish Card */}
                    <div className="relative group" id="search">
                        <div className="absolute -inset-1 bg-gradient-to-r from-orange-500 to-amber-500 rounded-[1.5rem] blur opacity-20 group-hover:opacity-30 transition duration-1000"></div>
                        <div className="relative bg-white/90 backdrop-blur-xl border border-slate-200 shadow-[0_8px_30px_rgb(0,0,0,0.04)] rounded-[1.25rem] overflow-hidden">
                            {/* Tabs */}
                            <div className="flex border-b border-slate-100">
                                <button
                                    onClick={() => handleTabChange("search")}
                                    className={`flex-1 py-4 text-center font-bold text-sm uppercase tracking-wide transition-colors ${activeTab === "search"
                                        ? "text-orange-600 bg-orange-50/50"
                                        : "text-slate-500 hover:text-slate-700"
                                        }`}
                                >
                                    Passager
                                </button>
                                <button
                                    onClick={() => handleTabChange("publish")}
                                    className={`flex-1 py-4 text-center font-bold text-sm uppercase tracking-wide transition-colors ${activeTab === "publish"
                                        ? "text-orange-600 bg-orange-50/50"
                                        : "text-slate-500 hover:text-slate-700"
                                        }`}
                                >
                                    Conducteur
                                </button>
                            </div>

                            <div className="p-6 lg:p-8">
                                {activeTab === "search" ? (
                                    <form onSubmit={handleSearch} className="space-y-5">
                                        <div className="space-y-4">
                                            <CitySelect
                                                label="Départ"
                                                placeholder="D'où partez-vous ?"
                                                value={departure}
                                                onChange={setDeparture}
                                                excludeCity={destination?.code}
                                            />
                                            <CitySelect
                                                label="Destination"
                                                placeholder="Où allez-vous ?"
                                                value={destination}
                                                onChange={setDestination}
                                                excludeCity={departure?.code}
                                            />

                                            <div className="grid grid-cols-2 gap-4">
                                                <div className="space-y-2">
                                                    <label className="text-sm font-medium text-slate-700">
                                                        Date
                                                    </label>
                                                    <Input
                                                        type="date"
                                                        min={new Date().toISOString().split("T")[0]}
                                                        value={date}
                                                        onChange={(e) => setDate(e.target.value)}
                                                        className="bg-slate-50 border-slate-200 focus:border-orange-500 rounded-xl px-4 py-3 h-auto"
                                                    />
                                                </div>
                                                <div className="space-y-2">
                                                    <label className="text-sm font-medium text-slate-700">
                                                        Passagers
                                                    </label>
                                                    <select
                                                        value={passengers}
                                                        onChange={(e) => setPassengers(e.target.value)}
                                                        className="w-full bg-slate-50 border border-slate-200 focus:border-orange-500 focus:ring-2 focus:ring-orange-500 rounded-xl px-4 py-3 text-sm appearance-none"
                                                    >
                                                        {[1, 2, 3, 4].map((num) => (
                                                            <option key={num} value={num}>
                                                                {num} {num === 1 ? "passager" : "passagers"}
                                                            </option>
                                                        ))}
                                                    </select>
                                                </div>
                                            </div>

                                            {/* Ladies Only Toggle */}
                                            <div
                                                role="checkbox"
                                                aria-checked={ladiesOnly}
                                                aria-label="Trajets entre femmes uniquement"
                                                tabIndex={0}
                                                className="flex items-center space-x-2 bg-pink-50 p-3 rounded-xl border border-pink-100 cursor-pointer focus:outline-none focus:ring-2 focus:ring-pink-500 focus:ring-offset-2"
                                                onClick={() => setLadiesOnly(!ladiesOnly)}
                                                onKeyDown={(e) => {
                                                    if (e.key === " " || e.key === "Enter") {
                                                        e.preventDefault();
                                                        setLadiesOnly(!ladiesOnly);
                                                    }
                                                }}
                                            >
                                                <div
                                                    className={`w-5 h-5 rounded border flex items-center justify-center transition-colors ${ladiesOnly
                                                        ? "bg-pink-500 border-pink-500"
                                                        : "bg-white border-slate-300"
                                                        }`}
                                                    aria-hidden="true"
                                                >
                                                    {ladiesOnly && (
                                                        <CheckCircle2 className="w-3.5 h-3.5 text-white" />
                                                    )}
                                                </div>
                                                <span className="text-sm font-bold text-pink-700 select-none">
                                                    Ladies Only (Entre femmes)
                                                </span>
                                            </div>
                                        </div>

                                        <Button
                                            type="submit"
                                            className="w-full h-14 text-lg font-bold bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 shadow-xl shadow-orange-500/20 transition-all duration-300 transform hover:-translate-y-0.5 rounded-xl"
                                        >
                                            <Search className="w-5 h-5 mr-3" />
                                            Rechercher
                                        </Button>
                                    </form>
                                ) : (
                                    <div className="text-center space-y-6 py-6">
                                        <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto text-orange-600">
                                            <Wallet className="w-8 h-8" />
                                        </div>
                                        <div>
                                            <h3 className="text-xl font-bold text-slate-900 mb-2">
                                                Gagnez de l&apos;argent
                                            </h3>
                                            <p className="text-slate-600">
                                                Partagez vos frais et rencontrez des gens sympas. C&apos;est
                                                simple et rapide.
                                            </p>
                                        </div>
                                        <Link href="/publish">
                                            <Button className="w-full h-14 text-lg font-bold bg-slate-900 hover:bg-slate-800 shadow-xl transition-all duration-300 transform hover:-translate-y-0.5 rounded-xl">
                                                Publier un trajet
                                                <ArrowRight className="w-5 h-5 ml-2" />
                                            </Button>
                                        </Link>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Popular Routes */}
                    <div className="pt-2">
                        <p className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">
                            Trajets populaires
                        </p>
                        <div className="flex flex-wrap gap-2">
                            {popularRoutes.map((route, idx) => (
                                <button
                                    key={idx}
                                    className="bg-white border border-slate-200 rounded-full px-4 py-1.5 text-sm font-medium text-slate-600 hover:border-orange-300 hover:text-orange-600 transition-colors shadow-sm"
                                >
                                    {route.from}{" "}
                                    <span className="text-slate-300 mx-1">→</span> {route.to}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right: Lifestyle Image */}
                <div className="relative hidden lg:block h-full min-h-[600px]">
                    {/* Background Shapes */}
                    <div className="absolute top-10 right-0 w-4/5 h-full bg-orange-50 rounded-[3rem] -rotate-3 z-0"></div>
                    <div className="absolute top-0 right-10 w-4/5 h-full rounded-[3rem] overflow-hidden shadow-2xl rotate-3 transition-transform duration-700 hover:rotate-2 z-10">
                        <Image
                            src="/images/hero.png"
                            alt="Voyageurs heureux"
                            fill
                            sizes="(max-width: 1024px) 0vw, 50vw"
                            className="object-cover transform hover:scale-105 transition duration-700"
                            priority
                        />
                    </div>

                    {/* Floating Card: Economy */}
                    <div className="absolute bottom-20 -left-12 bg-white/95 backdrop-blur-sm p-4 rounded-2xl shadow-xl flex items-center gap-4 animate-bounce-slow z-20 border border-slate-50">
                        <div className="p-3 bg-green-100 rounded-xl text-green-600">
                            <Wallet className="w-6 h-6" />
                        </div>
                        <div>
                            <p className="text-xs text-slate-500 font-medium">
                                Économie réalisée
                            </p>
                            <p className="text-lg font-bold text-slate-900">45.00 TND</p>
                        </div>
                    </div>

                    {/* Floating Card: Trust */}
                    <div className="absolute top-24 -left-8 bg-white/95 backdrop-blur-sm p-3 pr-6 rounded-full shadow-lg flex items-center gap-3 animate-bounce-slow animation-delay-2000 z-20 border border-slate-50">
                        <div className="w-10 h-10 bg-yellow-100 rounded-full flex items-center justify-center text-yellow-600">
                            <Star className="w-5 h-5 fill-current" />
                        </div>
                        <div>
                            <p className="text-sm font-bold text-slate-900">4.9/5 Note</p>
                            <p className="text-xs text-slate-500 font-medium">
                                Conducteurs vérifiés
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
