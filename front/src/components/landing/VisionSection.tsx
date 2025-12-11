import Image from "next/image";
import { Button } from "@/components/ui/button";

export default function VisionSection() {
    return (
        <section className="py-20 bg-white border-b border-slate-100 overflow-hidden">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="grid lg:grid-cols-2 gap-16 items-center">
                    <div className="order-2 lg:order-1 relative">
                        {/* Decorative Elements */}
                        <div className="absolute -top-10 -left-10 w-40 h-40 bg-orange-100 rounded-full mix-blend-multiply filter blur-2xl opacity-50"></div>
                        <div className="absolute bottom-0 right-0 w-40 h-40 bg-blue-100 rounded-full mix-blend-multiply filter blur-2xl opacity-50"></div>

                        <Image
                            src="https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&q=80&w=2070"
                            alt="Covoiturage Tunisie"
                            width={600}
                            height={400}
                            className="relative rounded-2xl shadow-xl border-4 border-white rotate-1 hover:rotate-0 transition duration-500"
                        />
                    </div>

                    <div className="order-1 lg:order-2 space-y-6 text-center lg:text-left">
                        <h2 className="text-3xl lg:text-4xl font-bold text-slate-900 leading-tight font-[family-name:var(--font-poppins)]">
                            DÉCOUVREZ L&apos;AVENIR DU <br />
                            <span className="text-orange-600">COVOITURAGE TUNISIEN</span>
                        </h2>
                        <div className="w-20 h-1.5 bg-orange-500 mx-auto lg:mx-0 rounded-full"></div>
                        <p className="text-slate-600 text-lg leading-relaxed">
                            Blassa révolutionne le transport en Tunisie en reliant conducteurs
                            et passagers pour des trajets partagés.
                        </p>
                        <p className="text-slate-600 leading-relaxed">
                            Notre plateforme favorise la communauté, la durabilité et
                            l&apos;accessibilité financière, facilitant ainsi les trajets pour
                            tous. Rejoignez notre communauté dès aujourd&apos;hui !
                        </p>
                        <Button
                            variant="outline"
                            className="mt-4 border-orange-500 text-orange-600 hover:bg-orange-50 px-8 py-6 rounded-full uppercase tracking-wider font-bold"
                        >
                            Pour en savoir plus
                        </Button>
                    </div>
                </div>
            </div>
        </section>
    );
}
