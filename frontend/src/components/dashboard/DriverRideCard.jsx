import { memo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Calendar,
    ArrowRight,
    Users,
    Edit2,
    Eye,
    XOctagon,
    CheckCircle2,
    AlertCircle,
    UserX // Add UserX for "0 passager"
} from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { cancelRide } from '@/api/rideApi';

// Format time: "08:00"
const formatTime = (isoString) => {
    return new Date(isoString).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    });
};

// Format date: "Jeudi 11 décembre 2025"
const formatDate = (isoString) => {
    // Capitalize first letter of weekday and month
    const date = new Date(isoString).toLocaleDateString('fr-FR', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
    return date.charAt(0).toUpperCase() + date.slice(1);
};

const DriverRideCard = memo(({ ride, activeTab, onCancel }) => {
    const navigate = useNavigate();

    // Status Logic
    const isUpcoming = activeTab === 'upcoming';
    const isHistory = activeTab === 'history';
    const isCancelled = activeTab === 'cancelled';

    // Handle Cancel Action
    const handleCancel = async (e) => {
        e.stopPropagation();
        if (confirm("Êtes-vous sûr de vouloir annuler ce trajet ?")) {
            try {
                await cancelRide(ride.id);
                onCancel(); // Refresh list
            } catch (err) {
                alert("Erreur lors de l'annulation: " + err.message);
            }
        }
    };

    const handleEdit = (e) => {
        e.stopPropagation();
        navigate(`/ride/${ride.id}/edit`);
    };

    const handleView = () => {
        navigate(`/ride/${ride.id}`, { state: { from: '/my-rides' } });
    };

    return (
        <Card
            onClick={handleView}
            className={`
                group cursor-pointer transition-all duration-200 border-none shadow-[0_4px_12px_rgba(0,0,0,0.05)] hover:shadow-[0_8px_16px_rgba(0,0,0,0.08)]
                ${isCancelled ? 'opacity-80' : 'bg-white'}
                rounded-2xl overflow-hidden mb-6
            `}
        >
            {isCancelled && <div className="absolute inset-0 bg-gray-50/20 pointer-events-none" />}

            <CardContent className="p-6">

                {/* Top Row: Date | Badges */}
                <div className="flex justify-between items-start mb-6">
                    <div className="flex items-center gap-3 text-[#0A1B3C]">
                        <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
                            <Calendar className="w-5 h-5" />
                        </div>
                        <div>
                            <p className="font-bold text-lg leading-tight capitalize">
                                {formatDate(ride.departureTime)}
                            </p>
                            <p className="text-2xl font-bold text-[#FF7A00] mt-1">
                                {formatTime(ride.departureTime)}
                            </p>
                        </div>
                    </div>

                    <div className="flex flex-col items-end gap-2">
                        {/* Status Badges */}
                        {isHistory && (
                            <Badge className="bg-emerald-100 text-emerald-700 hover:bg-emerald-100 border-none px-3 py-1 rounded-full text-sm font-medium">
                                <CheckCircle2 className="w-3.5 h-3.5 mr-1.5" /> Terminé
                            </Badge>
                        )}
                        {isCancelled && (
                            <Badge className="bg-red-100 text-red-700 hover:bg-red-100 border-none px-3 py-1 rounded-full text-sm font-medium">
                                <AlertCircle className="w-3.5 h-3.5 mr-1.5" /> Annulé
                            </Badge>
                        )}

                        {/* Seat Info */}
                        {(isUpcoming || isHistory) && (
                            <>
                                <Badge variant="outline" className={`
                                border-blue-200 bg-blue-50 text-blue-700 px-3 py-1 rounded-full text-sm font-medium
                             `}>
                                    {ride.availableSeats} places restantes
                                </Badge>

                                {/* Passenger Count Badge (Mocked logic for now as Passengers aren't in DTO yet) */}
                                <Badge variant="outline" className="border-slate-200 bg-slate-50 text-slate-600 px-3 py-1 rounded-full text-sm font-medium">
                                    <Users className="w-3.5 h-3.5 mr-1.5" />
                                    {ride.totalSeats - ride.availableSeats} passagers
                                </Badge>
                            </>
                        )}
                    </div>
                </div>

                {/* Middle: Journey Path */}
                <div className="flex items-center gap-4 mb-6 relative">
                    {/* Path Visual */}
                    <div className="flex flex-col items-center self-stretch py-1">
                        <div className="w-3 h-3 rounded-full border-[3px] border-[#FF7A00] bg-white" />
                        <div className="w-0.5 flex-grow bg-slate-200 my-1 border-l border-dashed border-slate-300" />
                        <div className="w-3 h-3 rounded-full border-[3px] border-[#0A1B3C] bg-white" />
                    </div>

                    <div className="space-y-4 flex-grow">
                        <div>
                            <p className="text-lg font-bold text-[#0A1B3C]">{ride.originName}</p>
                        </div>
                        <div>
                            <p className="text-lg font-bold text-[#0A1B3C]">{ride.destinationName}</p>
                        </div>
                    </div>
                </div>

                {/* Map Link */}
                <div className="mb-6 pl-7">
                    <button
                        onClick={(e) => {
                            e.stopPropagation();
                            window.open(`https://www.google.com/maps/dir/?api=1&origin=${ride.originLat},${ride.originLon}&destination=${ride.destinationLat},${ride.destinationLon}`, '_blank');
                        }}
                        className="text-sm font-medium text-blue-600 hover:text-blue-700 flex items-center gap-1 hover:underline"
                    >
                        <ArrowRight className="w-4 h-4" /> Voir sur la carte
                    </button>
                </div>

                {/* Bottom Row: Actions */}
                <div className="flex justify-end items-center gap-3 pt-4 border-t border-slate-100">
                    {isUpcoming && (
                        <>
                            {/* Show Modifier only if no passengers booked */}
                            {(ride.totalSeats - ride.availableSeats) === 0 && (
                                <Button
                                    variant="outline"
                                    onClick={handleEdit}
                                    className="h-10 px-4 rounded-[10px] border-blue-200 text-blue-600 hover:bg-blue-50 hover:text-blue-700 hover:border-blue-300 font-medium transition-colors"
                                >
                                    <Edit2 className="w-4 h-4 mr-2" /> Modifier
                                </Button>
                            )}

                            <Button
                                variant="ghost"
                                onClick={handleView}
                                className="h-10 px-4 rounded-[10px] bg-slate-50 text-slate-700 hover:bg-slate-100 font-medium"
                            >
                                <Eye className="w-4 h-4 mr-2" /> Voir le trajet
                            </Button>

                            <Button
                                variant="outline"
                                onClick={handleCancel}
                                className="h-10 px-4 rounded-[10px] border-red-200 text-red-600 hover:bg-red-50 hover:text-red-700 hover:border-red-300 font-medium transition-colors"
                            >
                                <XOctagon className="w-4 h-4 mr-2" /> Annuler
                            </Button>
                        </>
                    )}

                    {isHistory && (
                        <Button
                            variant="ghost"
                            onClick={handleView}
                            className="h-10 px-4 rounded-[10px] bg-slate-50 text-slate-700 hover:bg-slate-100 font-medium"
                        >
                            <Eye className="w-4 h-4 mr-2" /> Voir détails
                        </Button>
                    )}

                    {isCancelled && (
                        <Button
                            variant="outline"
                            className="h-10 px-4 rounded-[10px] border-slate-200 text-slate-400 hover:bg-slate-50 hover:text-slate-500 font-medium"
                            disabled
                        >
                            <XOctagon className="w-4 h-4 mr-2" /> Supprimer définitivement
                        </Button>
                    )}
                </div>
            </CardContent>
        </Card>
    );
});

DriverRideCard.displayName = 'DriverRideCard';

export default DriverRideCard;
