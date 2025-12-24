import { Ticket } from "lucide-react";

export default function BookingsLoading() {
    return (
        <div className="min-h-screen bg-[#F8FAFC]">
            {/* Placeholder for Navbar space */}
            <div className="h-16"></div>

            <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Header Skeleton */}
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-slate-200 rounded-lg animate-pulse"></div>
                        <div>
                            <div className="h-6 w-40 bg-slate-200 rounded animate-pulse mb-2"></div>
                            <div className="h-4 w-56 bg-slate-100 rounded animate-pulse"></div>
                        </div>
                    </div>
                    <div className="h-10 w-36 bg-slate-200 rounded-xl animate-pulse"></div>
                </div>

                {/* Filter Tabs Skeleton */}
                <div className="flex gap-2 mb-6">
                    {[1, 2, 3, 4, 5].map((i) => (
                        <div key={i} className="h-10 w-24 bg-slate-200 rounded-full animate-pulse"></div>
                    ))}
                </div>

                {/* Loading Indicator */}
                <div className="flex flex-col items-center justify-center py-20">
                    <div className="relative">
                        <div className="w-16 h-16 border-4 border-slate-200 border-t-[#006B8F] rounded-full animate-spin"></div>
                        <div className="absolute inset-0 flex items-center justify-center">
                            <Ticket className="w-6 h-6 text-[#006B8F]" />
                        </div>
                    </div>
                    <p className="text-slate-600 mt-4 font-medium">Chargement des réservations...</p>
                </div>
            </main>
        </div>
    );
}
