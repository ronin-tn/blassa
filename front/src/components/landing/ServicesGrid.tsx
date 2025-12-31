import { Search, ShieldCheck, Wallet, Heart } from "lucide-react";

const services = [
    {
        number: 1,
        title: "Trouver un trajet",
        description:
            "Trouvez instantanément des trajets disponibles, simples et rapides.",
        icon: Search,
        color: "slate",
        bgColor: "bg-white",
        numberBg: "bg-slate-900",
        iconColor: "text-slate-700",
        shadowColor: "shadow-slate-200/60",
        borderColor: "border-slate-100",
        hoverBg: "group-hover:bg-slate-900",
        hoverText: "group-hover:text-white",
    },
    {
        number: 2,
        title: "Sécurité & Vérification",
        description:
            "Identités validées, avis authentiques et assistance 24/7.",
        icon: ShieldCheck,
        color: "blue",
        bgColor: "bg-white",
        numberBg: "bg-blue-600",
        iconColor: "text-blue-600",
        shadowColor: "shadow-blue-100/60",
        borderColor: "border-blue-50",
        hoverBg: "group-hover:bg-blue-600",
        hoverText: "group-hover:text-white",
    },
    {
        number: 3,
        title: "Prix abordables",
        description:
            "Économisez sur chaque kilomètre avec des prix justes.",
        icon: Wallet,
        color: "emerald",
        bgColor: "bg-white",
        numberBg: "bg-emerald-500",
        iconColor: "text-emerald-600",
        shadowColor: "shadow-emerald-100/60",
        borderColor: "border-emerald-50",
        hoverBg: "group-hover:bg-emerald-500",
        hoverText: "group-hover:text-white",
    },
    {
        number: 4,
        title: "Ladies Only",
        description:
            "L'option exclusive 100% féminine pour voyager sereine.",
        icon: Heart,
        color: "rose",
        bgColor: "bg-white",
        numberBg: "bg-rose-500",
        iconColor: "text-rose-500",
        shadowColor: "shadow-rose-100/60",
        borderColor: "border-rose-50",
        hoverBg: "group-hover:bg-rose-500",
        hoverText: "group-hover:text-white",
    },
];

export default function ServicesGrid() {
    return (
        <section className="py-24 bg-slate-50 relative overflow-hidden">
            <div className="absolute inset-0 bg-gradient-to-b from-white via-slate-50 to-slate-100 opacity-80 pointer-events-none"></div>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 w-full">
                <div className="text-center mb-20 space-y-4">
                    <h2 className="text-3xl md:text-4xl font-bold text-slate-900 uppercase tracking-tight font-[family-name:var(--font-poppins)]">
                        L&apos;excellence à chaque trajet
                    </h2>
                    <div className="w-24 h-1.5 bg-gradient-to-r from-orange-400 to-amber-500 mx-auto rounded-full shadow-sm"></div>
                </div>

                <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8 lg:gap-12">
                    {services.map((service) => {
                        const Icon = service.icon;
                        return (
                            <div key={service.number} className="group text-center space-y-5">
                                <div className="relative mx-auto w-24 h-24">
                                    <span
                                        className={`absolute -top-3 -right-3 w-8 h-8 ${service.numberBg} rounded-full text-white text-sm font-bold flex items-center justify-center border-4 border-slate-50 z-10 shadow-lg`}
                                    >
                                        {service.number}
                                    </span>
                                    <div
                                        className={`w-full h-full ${service.bgColor} rounded-[2rem] flex items-center justify-center ${service.iconColor} shadow-xl ${service.shadowColor} ${service.borderColor} border group-hover:scale-105 ${service.hoverBg} ${service.hoverText} transition-all duration-300`}
                                    >
                                        <Icon className="w-10 h-10 stroke-[2.5]" />
                                    </div>
                                </div>
                                <div>
                                    <h3 className="text-xl font-bold text-slate-900 mb-3">
                                        {service.title}
                                    </h3>
                                    <p className="text-base text-slate-600 font-medium leading-relaxed px-2">
                                        {service.description}
                                    </p>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </section>
    );
}
