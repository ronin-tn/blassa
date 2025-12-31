import Link from "next/link";
import { Search, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function QuickActions() {
    return (
        <div className="grid md:grid-cols-2 gap-6 mt-8">
            <div className="bg-gradient-to-br from-[#006B8F] to-[#005673] rounded-2xl p-6 text-white">
                <div className="flex items-center gap-3 mb-4">
                    <div className="p-3 bg-[var(--color-blassa-teal)]/10 rounded-xl w-fit mb-3 group-hover:bg-[var(--color-blassa-teal)]/20 transition-colors">
                        <Plus className="w-6 h-6 text-[var(--color-blassa-teal)]" />
                    </div>
                    <div>
                        <h3 className="font-bold text-lg">Vous êtes conducteur ?</h3>
                        <p className="text-white/70 text-sm">
                            Partagez vos frais de route
                        </p>
                    </div>
                </div>
                <Link href="/publish">
                    <Button className="w-full bg-white text-[var(--color-blassa-teal)] hover:bg-white/90 rounded-xl font-medium">
                        <Plus className="w-4 h-4 mr-2" />
                        Publier un trajet
                    </Button>
                </Link>
                <Link href="/dashboard/vehicles" className="block mt-3">
                    <Button variant="ghost" className="w-full text-white/80 hover:text-white hover:bg-white/10 rounded-xl font-medium">
                        Gérer mes véhicules
                    </Button>
                </Link>
            </div>

            <div className="bg-gradient-to-br from-[#FF9A3E] to-[#E88A35] rounded-2xl p-6 text-white">
                <div className="flex items-center gap-3 mb-4">
                    <div className="p-3 bg-[var(--color-blassa-amber)]/10 rounded-xl w-fit mb-3 group-hover:bg-[var(--color-blassa-amber)]/20 transition-colors">
                        <Search className="w-6 h-6 text-[var(--color-blassa-amber)]" />
                    </div>
                    <div>
                        <h3 className="font-bold text-lg">Besoin d&apos;un trajet ?</h3>
                        <p className="text-white/70 text-sm">
                            Trouvez votre covoiturage idéal
                        </p>
                    </div>
                </div>
                <Link href="/#search">
                    <Button className="w-full bg-white text-[#FF9A3E] hover:bg-white/90 rounded-xl font-medium">
                        <Search className="w-4 h-4 mr-2" />
                        Rechercher un trajet
                    </Button>
                </Link>
            </div>
        </div>
    );
}
