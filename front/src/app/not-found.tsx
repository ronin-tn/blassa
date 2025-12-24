import { MapPin, Home, Search } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function NotFound() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100 px-4">
            <div className="max-w-md w-full text-center">
                {/* 404 Illustration */}
                <div className="relative mb-8">
                    <div className="text-[120px] font-black text-slate-200 leading-none select-none">
                        404
                    </div>
                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
                        <div className="w-20 h-20 rounded-full bg-gradient-to-r from-[#006B8F] to-[#FF9A3E] flex items-center justify-center shadow-lg">
                            <MapPin className="w-10 h-10 text-white" />
                        </div>
                    </div>
                </div>

                <h1 className="text-2xl font-bold text-slate-900 mb-3">
                    Page introuvable
                </h1>
                <p className="text-slate-600 mb-8">
                    Oups ! La page que vous recherchez n&apos;existe pas ou a été déplacée.
                    Peut-être cherchez-vous un trajet ?
                </p>

                <div className="flex flex-col sm:flex-row gap-3 justify-center">
                    <Link href="/">
                        <Button className="bg-[var(--color-blassa-teal)] hover:bg-[var(--color-blassa-teal-dark)] px-6 py-6 rounded-xl text-lg gap-2">
                            <Home className="w-5 h-5" />
                            Retour à l&apos;accueil
                        </Button>
                    </Link>
                    <Link
                        href="/search"
                        className="flex items-center justify-center gap-2 px-6 py-3 border border-slate-300 text-slate-700 font-medium rounded-xl hover:bg-slate-50 transition-colors"
                    >
                        <Search className="w-4 h-4" />
                        Rechercher un trajet
                    </Link>
                </div>
            </div>
        </div>
    );
}
