"use client";



export default function Loading() {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100">
            <div className="flex flex-col items-center gap-4">
                <div className="relative">
                    <div className="w-16 h-16 border-4 border-slate-200 border-t-[var(--color-blassa-teal)] rounded-full animate-spin"></div>
                    <div className="absolute inset-0 flex items-center justify-center">
                        <div className="w-2 h-2 bg-[var(--color-blassa-teal)] rounded-full"></div>
                    </div>
                </div>
                <div className="text-center">
                    <p className="text-lg font-medium text-slate-700">Chargement...</p>
                    <p className="text-sm text-slate-500">Veuillez patienter</p>
                </div>
            </div>
        </div>
    );
}
