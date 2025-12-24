import { MapPin } from "lucide-react";

export default function RideDetailsLoading() {
    return (
        <div className="min-h-screen bg-[#F8FAFC] pb-20 lg:pb-8">
            {/* Placeholder for Navbar space */}
            <div className="h-16"></div>

            <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {/* Back Button Skeleton */}
                <div className="h-6 w-20 bg-slate-200 rounded animate-pulse mb-6"></div>

                <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
                    {/* Route Header Skeleton */}
                    <div className="p-6 border-b border-slate-100 bg-gradient-to-r from-[#006B8F]/5 to-[#FF9A3E]/5">
                        <div className="flex items-center gap-3 mb-4">
                            <div className="w-12 h-12 bg-[#006B8F]/20 rounded-xl flex items-center justify-center">
                                <MapPin className="w-6 h-6 text-[#006B8F]/40" />
                            </div>
                            <div className="flex-1">
                                <div className="h-6 w-64 bg-slate-200 rounded animate-pulse mb-2"></div>
                                <div className="h-5 w-20 bg-slate-100 rounded-full animate-pulse"></div>
                            </div>
                        </div>
                        <div className="flex flex-wrap items-center gap-6">
                            <div className="h-5 w-32 bg-slate-200 rounded animate-pulse"></div>
                            <div className="h-5 w-16 bg-slate-200 rounded animate-pulse"></div>
                        </div>
                    </div>

                    {/* Details Grid Skeleton */}
                    <div className="p-6 grid sm:grid-cols-2 gap-6">
                        {[1, 2, 3].map((i) => (
                            <div key={i} className="flex items-center gap-3">
                                <div className="w-10 h-10 bg-slate-100 rounded-lg animate-pulse"></div>
                                <div>
                                    <div className="h-4 w-24 bg-slate-100 rounded animate-pulse mb-1"></div>
                                    <div className="h-5 w-20 bg-slate-200 rounded animate-pulse"></div>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Driver Section Skeleton */}
                    <div className="p-6 border-t border-slate-100 bg-slate-50">
                        <div className="h-4 w-20 bg-slate-200 rounded animate-pulse mb-4"></div>
                        <div className="flex items-center gap-4 mb-4">
                            <div className="w-14 h-14 bg-[#006B8F]/20 rounded-full animate-pulse"></div>
                            <div>
                                <div className="h-5 w-32 bg-slate-200 rounded animate-pulse mb-2"></div>
                                <div className="h-4 w-16 bg-slate-100 rounded animate-pulse"></div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Loading Indicator */}
                <div className="flex items-center justify-center py-8">
                    <div className="w-8 h-8 border-4 border-slate-200 border-t-[#006B8F] rounded-full animate-spin"></div>
                </div>
            </main>
        </div>
    );
}
