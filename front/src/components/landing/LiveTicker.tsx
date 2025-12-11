"use client";

import { useState, useEffect } from "react";

const tickers = [
    "Ahmed vient de publier Tunis → Sousse",
    "Sarra a réservé une place pour Nabeul",
    "Youssef cherche un trajet vers Bizerte",
    "34 trajets réservés cette dernière heure",
];

export default function LiveTicker() {
    const [tickerIndex, setTickerIndex] = useState(0);

    useEffect(() => {
        const interval = setInterval(() => {
            setTickerIndex((prev) => (prev + 1) % tickers.length);
        }, 3000);
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="bg-slate-900/95 text-slate-300 text-xs sm:text-sm py-2 overflow-hidden border-b border-white/5 relative z-40">
            <div className="max-w-7xl mx-auto px-4 flex items-center justify-center gap-2">
                <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                <span className="font-medium tracking-wide">{tickers[tickerIndex]}</span>
            </div>
        </div>
    );
}
