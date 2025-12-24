"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import {
    Search,
    MapPin,
    Clock,
    X,
    Trash2,
    ArrowRight,
    ArrowLeft,
    Users,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import CitySelect from "@/components/ui/city-select";
import { City, TUNISIA_CITIES } from "@/data/cities";
import { useRecentSearches, RecentSearch } from "@/hooks/useRecentSearches";
import Navbar from "@/components/layout/Navbar";
import Footer from "@/components/layout/Footer";

const POPULAR_ROUTES = [
    { from: "Tunis", to: "Sousse" },
    { from: "Sousse", to: "Monastir" },
    { from: "Tunis", to: "Bizerte" },
    { from: "Sfax", to: "Tunis" },
    { from: "Tunis", to: "Sfax" },
    { from: "Sousse", to: "Tunis" },
];

export default function SearchFormPage() {
    const router = useRouter();
    const { searches, isLoaded, addSearch, removeSearch, clearSearches } =
        useRecentSearches();

    const [departure, setDeparture] = useState<City | null>(null);
    const [destination, setDestination] = useState<City | null>(null);
    const [date, setDate] = useState("");
    const [passengers, setPassengers] = useState("1");

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();

        if (!departure || !destination) {
            return;
        }

        // Save to recent searches
        addSearch({
            from: departure.name,
            to: destination.name,
            date: date || undefined,
            passengers: parseInt(passengers),
        });

        // Navigate to search results
        const params = new URLSearchParams();
        params.set("from", departure.name);
        params.set("to", destination.name);
        if (date) params.set("date", date);
        if (passengers !== "1") params.set("p", passengers);

        router.push(`/search?${params.toString()}`);
    };

    const handleQuickSearch = (search: RecentSearch | { from: string; to: string }) => {
        const fromCity = TUNISIA_CITIES.find(
            (c) => c.name.toLowerCase() === search.from.toLowerCase()
        );
        const toCity = TUNISIA_CITIES.find(
            (c) => c.name.toLowerCase() === search.to.toLowerCase()
        );

        if (fromCity && toCity) {
            // Save to recent searches
            addSearch({
                from: fromCity.name,
                to: toCity.name,
                passengers: "passengers" in search ? search.passengers : 1,
            });

            const params = new URLSearchParams();
            params.set("from", fromCity.name);
            params.set("to", toCity.name);
            if ("passengers" in search && search.passengers > 1) {
                params.set("p", search.passengers.toString());
            }

            router.push(`/search?${params.toString()}`);
        }
    };

    const formatRelativeTime = (timestamp: number) => {
        const diff = Date.now() - timestamp;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (days > 0) return `Il y a ${days}j`;
        if (hours > 0) return `Il y a ${hours}h`;
        if (minutes > 0) return `Il y a ${minutes}min`;
        return "À l'instant";
    };

    return (
        <>
            <Navbar />
            <div className="min-h-screen bg-slate-50 pt-16">
                {/* Header */}
                <div className="bg-white border-b border-slate-200">
                    <div className="max-w-2xl mx-auto px-4 py-6">
                        <div className="flex items-center gap-4 mb-4">
                            <Link
                                href="/"
                                className="flex items-center gap-2 text-slate-600 hover:text-[#0A8F8F] transition-colors"
                            >
                                <ArrowLeft className="w-5 h-5" />
                                <span>Retour à l'accueil</span>
                            </Link>
                            <h1 className="text-2xl font-bold text-slate-900">
                                Rechercher un trajet
                            </h1>
                        </div>
                    </div>
                </div>

                {/* Search Form */}
                <div className="max-w-2xl mx-auto px-4 py-6">
                    <form
                        onSubmit={handleSearch}
                        className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 space-y-4"
                    >
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
                                    className="bg-slate-50 border-slate-200 rounded-xl px-4 py-3 h-auto"
                                />
                            </div>
                            <div className="space-y-2">
                                <label className="text-sm font-medium text-slate-700">
                                    Passagers
                                </label>
                                <select
                                    value={passengers}
                                    onChange={(e) => setPassengers(e.target.value)}
                                    className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm"
                                >
                                    {[1, 2, 3, 4].map((num) => (
                                        <option key={num} value={num}>
                                            {num} {num === 1 ? "passager" : "passagers"}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <Button
                            type="submit"
                            disabled={!departure || !destination}
                            className="w-full h-12 text-base font-semibold bg-[#0A8F8F] hover:bg-[#0A8F8F]/90 rounded-xl"
                        >
                            <Search className="w-5 h-5 mr-2" />
                            Rechercher
                        </Button>
                    </form>

                    {/* Recent Searches */}
                    {isLoaded && searches.length > 0 && (
                        <div className="mt-8">
                            <div className="flex items-center justify-between mb-4">
                                <h2 className="text-sm font-semibold text-slate-900 flex items-center gap-2">
                                    <Clock className="w-4 h-4 text-slate-400" />
                                    Recherches récentes
                                </h2>
                                <button
                                    onClick={clearSearches}
                                    className="text-xs text-slate-500 hover:text-red-600 flex items-center gap-1"
                                >
                                    <Trash2 className="w-3 h-3" />
                                    Effacer
                                </button>
                            </div>

                            <div className="bg-white rounded-2xl border border-slate-200 divide-y divide-slate-100">
                                {searches.map((search) => (
                                    <div
                                        key={search.id}
                                        className="flex items-center justify-between p-4 hover:bg-slate-50 transition-colors group"
                                    >
                                        <button
                                            onClick={() => handleQuickSearch(search)}
                                            className="flex items-center gap-3 flex-1 text-left"
                                        >
                                            <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center text-slate-500 group-hover:bg-[#0A8F8F]/10 group-hover:text-[#0A8F8F] transition-colors">
                                                <MapPin className="w-5 h-5" />
                                            </div>
                                            <div>
                                                <p className="font-medium text-slate-900">
                                                    {search.from}{" "}
                                                    <span className="text-slate-400">→</span>{" "}
                                                    {search.to}
                                                </p>
                                                <p className="text-xs text-slate-500 flex items-center gap-2">
                                                    <span>{formatRelativeTime(search.timestamp)}</span>
                                                    {search.passengers > 1 && (
                                                        <span className="flex items-center gap-0.5">
                                                            <Users className="w-3 h-3" />
                                                            {search.passengers}
                                                        </span>
                                                    )}
                                                </p>
                                            </div>
                                        </button>
                                        <div className="flex items-center gap-2">
                                            <button
                                                onClick={() => removeSearch(search.id)}
                                                className="p-2 text-slate-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-opacity"
                                                aria-label="Supprimer"
                                            >
                                                <X className="w-4 h-4" />
                                            </button>
                                            <button
                                                onClick={() => handleQuickSearch(search)}
                                                className="p-2 text-slate-300 hover:text-[#0A8F8F] transition-colors"
                                                aria-label="Rechercher"
                                            >
                                                <ArrowRight className="w-5 h-5" />
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Popular Routes */}
                    <div className="mt-8">
                        <h2 className="text-sm font-semibold text-slate-900 mb-4">
                            Trajets populaires
                        </h2>
                        <div className="flex flex-wrap gap-2">
                            {POPULAR_ROUTES.map((route, idx) => (
                                <button
                                    key={idx}
                                    onClick={() => handleQuickSearch(route)}
                                    className="bg-white border border-slate-200 rounded-full px-4 py-2 text-sm font-medium text-slate-600 hover:border-[#0A8F8F] hover:text-[#0A8F8F] transition-colors"
                                >
                                    {route.from}{" "}
                                    <span className="text-slate-300 mx-1">→</span>{" "}
                                    {route.to}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Back to Home */}
                    <div className="mt-8 text-center">
                        <Link
                            href="/"
                            className="text-sm text-slate-500 hover:text-[#0A8F8F]"
                        >
                            ← Retour à l&apos;accueil
                        </Link>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
}
