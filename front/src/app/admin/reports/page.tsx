"use client";

import { useEffect, useState } from "react";
import { adminApi, Report } from "@/lib/api/admin";
import { Spinner } from "@/components/ui/Spinner";
import { AlertTriangle, CheckCircle, XCircle, Clock, ShieldCheck, User, Mail, Phone, MapPin, ChevronDown, ChevronUp, ExternalLink } from "lucide-react";
import toast from "react-hot-toast";
import Link from "next/link";

export default function ReportsPage() {
    const [reports, setReports] = useState<Report[]>([]);
    const [loading, setLoading] = useState(true);
    const [expandedReports, setExpandedReports] = useState<Set<number>>(new Set());

    useEffect(() => {
        loadReports();
    }, []);

    const loadReports = async () => {
        try {
            const data = await adminApi.getReports();
            setReports(data);
        } catch (e) {
            toast.error("Impossible de charger les signalements");
        } finally {
            setLoading(false);
        }
    };

    const handleResolve = async (id: number, status: string) => {
        try {
            await adminApi.resolveReport(id, status);
            toast.success("Signalement mis à jour");
            loadReports();
        } catch (e) {
            toast.error("Erreur lors de la mise à jour");
        }
    };

    const toggleExpanded = (id: number) => {
        const newExpanded = new Set(expandedReports);
        if (newExpanded.has(id)) {
            newExpanded.delete(id);
        } else {
            newExpanded.add(id);
        }
        setExpandedReports(newExpanded);
    };

    if (loading) return (
        <div className="flex justify-center p-12">
            <Spinner size="lg" className="text-[#0e7490]" />
        </div>
    );

    return (
        <div className="space-y-6 animate-in fade-in duration-500">
            <div>
                <h1 className="text-2xl font-bold text-gray-900">Gestion des Signalements</h1>
                <p className="text-gray-500">Traitez les retours des utilisateurs pour maintenir la sécurité.</p>
            </div>

            <div className="grid gap-6">
                {reports.length === 0 ? (
                    <div className="flex flex-col items-center justify-center rounded-xl bg-white p-12 shadow-sm border border-gray-100 text-center">
                        <div className="h-16 w-16 bg-green-50 rounded-full flex items-center justify-center mb-4">
                            <ShieldCheck className="h-8 w-8 text-green-600" />
                        </div>
                        <h3 className="text-lg font-medium text-gray-900">Tout est calme</h3>
                        <p className="max-w-md text-gray-500 mt-2">Aucun signalement en attente. Votre communauté se porte bien !</p>
                    </div>
                ) : (
                    reports.map((report) => (
                        <div key={report.id} className="overflow-hidden rounded-xl bg-white shadow-sm ring-1 ring-gray-900/5 transition-all hover:shadow-md">
                            {/* Header Section */}
                            <div
                                className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-6 cursor-pointer"
                                onClick={() => toggleExpanded(report.id)}
                            >
                                <div className="flex items-start gap-4">
                                    <div className={`p-2 rounded-lg ${report.status === 'PENDING' ? 'bg-amber-50 text-amber-600' : 'bg-gray-50 text-gray-500'}`}>
                                        <AlertTriangle className="h-6 w-6" />
                                    </div>
                                    <div>
                                        <div className="flex items-center gap-2">
                                            <h3 className="font-bold text-gray-900">{report.reason}</h3>
                                            <span className={`inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${report.status === 'PENDING' ? 'bg-yellow-50 text-yellow-800 ring-yellow-600/20' :
                                                report.status === 'RESOLVED' ? 'bg-green-50 text-green-700 ring-green-600/20' :
                                                    'bg-gray-50 text-gray-600 ring-gray-500/10'
                                                }`}>
                                                {report.status}
                                            </span>
                                        </div>
                                        <p className="mt-1 text-sm text-gray-600 leading-relaxed">{report.description}</p>
                                        <div className="mt-3 flex flex-wrap items-center gap-4 text-xs text-gray-500">
                                            <span className="flex items-center gap-1 text-gray-400">
                                                <Clock className="h-3 w-3" /> {new Date(report.createdAt).toLocaleDateString('fr-FR', {
                                                    day: 'numeric',
                                                    month: 'long',
                                                    year: 'numeric',
                                                    hour: '2-digit',
                                                    minute: '2-digit'
                                                })}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div className="flex items-center gap-2">
                                    {report.status === 'PENDING' && (
                                        <div className="flex gap-2 shrink-0">
                                            <button
                                                onClick={(e) => { e.stopPropagation(); handleResolve(report.id, 'RESOLVED'); }}
                                                className="flex items-center gap-1 rounded-lg bg-green-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-green-500 transition-colors"
                                            >
                                                <CheckCircle className="h-4 w-4" /> Résoudre
                                            </button>
                                            <button
                                                onClick={(e) => { e.stopPropagation(); handleResolve(report.id, 'DISMISSED'); }}
                                                className="flex items-center gap-1 rounded-lg bg-white px-3 py-2 text-sm font-semibold text-gray-700 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 transition-colors"
                                            >
                                                <XCircle className="h-4 w-4" /> Rejeter
                                            </button>
                                        </div>
                                    )}
                                    {expandedReports.has(report.id) ? (
                                        <ChevronUp className="h-5 w-5 text-gray-400" />
                                    ) : (
                                        <ChevronDown className="h-5 w-5 text-gray-400" />
                                    )}
                                </div>
                            </div>

                            {/* Expanded Details Section */}
                            {expandedReports.has(report.id) && (
                                <div className="border-t border-gray-100 bg-gray-50/50 p-6">
                                    <div className="grid md:grid-cols-2 gap-6">
                                        {/* Reporter Info */}
                                        <div className="bg-white rounded-lg p-4 ring-1 ring-gray-200">
                                            <div className="flex items-center justify-between mb-3">
                                                <h4 className="font-semibold text-gray-900 flex items-center gap-2">
                                                    <User className="h-4 w-4 text-blue-500" />
                                                    Signalé par
                                                </h4>
                                                <Link
                                                    href={`/users/${report.reporter.id}`}
                                                    target="_blank"
                                                    className="text-xs text-[#0e7490] hover:underline flex items-center gap-1"
                                                    onClick={(e) => e.stopPropagation()}
                                                >
                                                    <ExternalLink className="h-3 w-3" />
                                                    Voir profil
                                                </Link>
                                            </div>
                                            <div className="space-y-2 text-sm">
                                                <div className="flex items-center gap-2">
                                                    <span className="font-medium text-gray-700">
                                                        {report.reporter.firstName} {report.reporter.lastName}
                                                    </span>
                                                </div>
                                                <div className="flex items-center gap-2 text-gray-500">
                                                    <Mail className="h-4 w-4" />
                                                    <a href={`mailto:${report.reporter.email}`} className="hover:text-blue-600">
                                                        {report.reporter.email}
                                                    </a>
                                                </div>
                                                {report.reporter.phone && (
                                                    <div className="flex items-center gap-2 text-gray-500">
                                                        <Phone className="h-4 w-4" />
                                                        <a href={`tel:${report.reporter.phone}`} className="hover:text-blue-600">
                                                            {report.reporter.phone}
                                                        </a>
                                                    </div>
                                                )}
                                            </div>
                                        </div>

                                        {/* Reported User Info */}
                                        {report.reportedUser && (
                                            <div className="bg-white rounded-lg p-4 ring-1 ring-red-200">
                                                <div className="flex items-center justify-between mb-3">
                                                    <h4 className="font-semibold text-gray-900 flex items-center gap-2">
                                                        <AlertTriangle className="h-4 w-4 text-red-500" />
                                                        Utilisateur signalé
                                                    </h4>
                                                    <Link
                                                        href={`/users/${report.reportedUser.id}`}
                                                        target="_blank"
                                                        className="text-xs text-[#0e7490] hover:underline flex items-center gap-1"
                                                        onClick={(e) => e.stopPropagation()}
                                                    >
                                                        <ExternalLink className="h-3 w-3" />
                                                        Voir profil
                                                    </Link>
                                                </div>
                                                <div className="space-y-2 text-sm">
                                                    <div className="flex items-center gap-2">
                                                        <span className="font-medium text-gray-700">
                                                            {report.reportedUser.firstName} {report.reportedUser.lastName}
                                                        </span>
                                                    </div>
                                                    <div className="flex items-center gap-2 text-gray-500">
                                                        <Mail className="h-4 w-4" />
                                                        <a href={`mailto:${report.reportedUser.email}`} className="hover:text-blue-600">
                                                            {report.reportedUser.email}
                                                        </a>
                                                    </div>
                                                    {report.reportedUser.phone && (
                                                        <div className="flex items-center gap-2 text-gray-500">
                                                            <Phone className="h-4 w-4" />
                                                            <a href={`tel:${report.reportedUser.phone}`} className="hover:text-blue-600">
                                                                {report.reportedUser.phone}
                                                            </a>
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        )}
                                    </div>

                                    {/* Ride Info if applicable */}
                                    {report.ride && (
                                        <div className="mt-4 bg-white rounded-lg p-4 ring-1 ring-gray-200">
                                            <div className="flex items-center justify-between">
                                                <h4 className="font-semibold text-gray-900 flex items-center gap-2">
                                                    <MapPin className="h-4 w-4 text-cyan-500" />
                                                    Trajet concerné
                                                </h4>
                                                <Link
                                                    href={`/rides/${report.ride.id}`}
                                                    target="_blank"
                                                    className="text-xs text-[#0e7490] hover:underline flex items-center gap-1"
                                                    onClick={(e) => e.stopPropagation()}
                                                >
                                                    <ExternalLink className="h-3 w-3" />
                                                    Voir le trajet
                                                </Link>
                                            </div>
                                            <p className="text-sm text-gray-600 mt-2">
                                                {report.ride.originName} → {report.ride.destinationName}
                                            </p>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
