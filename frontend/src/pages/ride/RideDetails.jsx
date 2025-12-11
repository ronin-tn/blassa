import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { getRideById, cancelRide } from '@/api/rideApi';
import { createBooking, getPassengersForRide, getMyBookedRideIds, cancelBookingByRide } from '@/api/bookingApi';
import { useAuth } from '@/hooks/useAuth';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
    MapPin,
    Calendar,
    Clock,
    Users,
    Car,
    CreditCard,
    User,
    Star,
    Cigarette,
    CigaretteOff,
    CheckCircle2,
    Shield,
    Share2,
    Copy,
    UserX,
    ArrowLeft,
    AlertCircle,
    XCircle,
    Facebook,
    Instagram,
    Phone
} from 'lucide-react';

const RideDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuth();

    const [ride, setRide] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isDriver, setIsDriver] = useState(false);
    const [hasBooked, setHasBooked] = useState(false);
    const [passengers, setPassengers] = useState([]);
    const [bookingLoading, setBookingLoading] = useState(false);
    const [cancelLoading, setCancelLoading] = useState(false);

    useEffect(() => {
        const fetchRide = async () => {
            try {
                const data = await getRideById(id);
                setRide(data);

                // Check if current user is the driver
                if (user && data.driverEmail === user.email) {
                    setIsDriver(true);
                    // Fetch passengers for driver
                    try {
                        const passengerData = await getPassengersForRide(id);
                        setPassengers(passengerData);
                    } catch (err) {
                        console.error('Error fetching passengers:', err);
                    }
                } else if (user) {
                    // Check if user has booked this ride
                    try {
                        const bookedRideIds = await getMyBookedRideIds();
                        if (bookedRideIds.includes(id)) {
                            setHasBooked(true);
                        }
                    } catch (err) {
                        console.error('Error checking booking status:', err);
                    }
                }
            } catch (err) {
                console.error('Error fetching ride:', err);
                setError('Impossible de charger les détails du trajet.');
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchRide();
        }
    }, [id, user]);

    const handleBack = () => {
        if (location.state?.from) {
            navigate(location.state.from);
        } else {
            navigate(-1);
        }
    };

    const handleBook = async () => {
        if (!user) {
            // Redirect to login with return URL
            const currentUrl = window.location.pathname;
            navigate(`/login?redirect=${encodeURIComponent(currentUrl)}`);
            return;
        }

        setBookingLoading(true);
        try {
            await createBooking(id, 1); // Book 1 seat by default
            // Navigate to bookings page with success message
            navigate('/my-bookings', {
                state: {
                    message: 'Réservation confirmée ! Vous recevrez un email de confirmation.',
                    type: 'success'
                }
            });
        } catch (err) {
            console.error('Booking error:', err);
            const errorMsg = err.response?.data?.message || 'Erreur lors de la réservation.';
            alert(errorMsg);
        } finally {
            setBookingLoading(false);
        }
    };

    const handleEdit = () => {
        navigate(`/ride/${id}/edit`);
    }

    const handleCancel = async () => {
        if (confirm("Voulez-vous vraiment annuler ce trajet ? Cette action est irréversible.")) {
            try {
                await cancelRide(id);
                // Refresh data to show cancelled state
                const data = await getRideById(id);
                setRide(data);
                // Optional: navigate back if preferred, but updating view is better feedback
                // navigate('/my-rides'); 
            } catch (err) {
                console.error("Error cancelling ride:", err);
                alert("Erreur lors de l'annulation du trajet.");
            }
        }
    }

    if (loading) {
        return (
            <div className="min-h-screen bg-slate-50 flex flex-col">
                <Navbar />
                <main className="flex-grow flex items-center justify-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-[#FF7A00]"></div>
                </main>
                <Footer />
            </div>
        );
    }

    if (error || !ride) {
        return (
            <div className="min-h-screen bg-slate-50 flex flex-col">
                <Navbar />
                <main className="flex-grow flex items-center justify-center p-4">
                    <Card className="max-w-md w-full text-center p-6">
                        <div className="mx-auto w-12 h-12 bg-red-100 rounded-full flex items-center justify-center mb-4">
                            <span className="text-2xl">⚠️</span>
                        </div>
                        <h2 className="text-xl font-bold text-[#0A1B3C] mb-2">Oups !</h2>
                        <p className="text-slate-600 mb-6">{error || 'Trajet introuvable'}</p>
                        <Button
                            onClick={() => navigate('/search')}
                            className="bg-[#0A1B3C] text-white hover:bg-[#0A1B3C]/90"
                        >
                            Retour à la recherche
                        </Button>
                    </Card>
                </main>
                <Footer />
            </div>
        );
    }

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('fr-FR', {
            weekday: 'long',
            day: 'numeric',
            month: 'long',
            year: 'numeric'
        });
    };

    const formatTime = (dateString) => {
        return new Date(dateString).toLocaleTimeString('fr-FR', {
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getGenderLabel = (preference) => {
        switch (preference) {
            case 'MALE_ONLY': return 'Hommes uniquement';
            case 'FEMALE_ONLY': return 'Femmes uniquement';
            default: return 'Mixte';
        }
    };

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            <Navbar />

            <main className="flex-grow pt-24 pb-12 px-4 sm:px-6 lg:px-8">
                {/* Back Button */}
                <div className="max-w-4xl mx-auto mb-6">
                    <Button
                        variant="ghost"
                        onClick={handleBack}
                        className="pl-0 hover:bg-transparent text-slate-500 hover:text-[#0A1B3C] transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5 mr-2" />
                        {location.state?.from === '/my-rides' ? 'Retour à mes trajets' : 'Retour'}
                    </Button>
                </div>

                <div className="max-w-4xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-8">

                    {/* Left Column: Ride Info */}
                    <div className="lg:col-span-2 space-y-6">
                        {/* Status Banner for Driver if Cancelled */}
                        {isDriver && ride.status === 'CANCELLED' && (
                            <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-center gap-3 text-red-700 animate-in fade-in slide-in-from-top-2">
                                <AlertCircle className="w-5 h-5 flex-shrink-0" />
                                <div>
                                    <h3 className="font-bold">Trajet annulé</h3>
                                    <p className="text-sm text-red-600">Ce trajet a été annulé et n'est plus visible dans la recherche.</p>
                                </div>
                            </div>
                        )}

                        <Card className="border-0 shadow-sm overflow-hidden">
                            <div className="bg-[#0A1B3C] p-5 text-white">
                                <div className="flex items-center justify-between flex-wrap gap-4">
                                    <div className="flex items-center gap-4">
                                        <Clock className="w-5 h-5 text-[#FF7A00]" />
                                        <span className="text-xl font-bold text-[#FF7A00]">
                                            {formatTime(ride.departureTime)}
                                        </span>
                                        <span className="text-slate-400">•</span>
                                        <span className="text-slate-200">
                                            {formatDate(ride.departureTime)}
                                        </span>
                                    </div>
                                    <div className="flex items-baseline gap-1">
                                        <span className="text-xl font-bold text-white">
                                            {ride.pricePerSeat} TND
                                        </span>
                                        <span className="text-sm text-slate-400">par place</span>
                                    </div>
                                </div>
                            </div>

                            <CardContent className="p-6">
                                {/* Route Visualization - Same as DriverRideCard */}
                                <div className="flex items-center gap-4 mb-4">
                                    {/* Path Visual */}
                                    <div className="flex flex-col items-center self-stretch py-1">
                                        <div className="w-3 h-3 rounded-full border-[3px] border-[#FF7A00] bg-white" />
                                        <div className="w-0.5 flex-grow bg-slate-200 my-1" />
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
                                <div className="pl-7">
                                    <a
                                        href={`https://www.google.com/maps/dir/?api=1&origin=${ride.originLat},${ride.originLon}&destination=${ride.destinationLat},${ride.destinationLon}`}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="text-sm font-medium text-blue-600 hover:text-blue-700 flex items-center gap-1 hover:underline"
                                    >
                                        <ArrowLeft className="w-4 h-4 rotate-180" /> Voir sur la carte
                                    </a>
                                </div>
                            </CardContent>
                        </Card>

                        {/* Car & Preferences */}
                        <Card className="border-0 shadow-sm">
                            <CardHeader className="pb-3">
                                <CardTitle className="text-base font-bold text-[#0A1B3C]">
                                    Détails du voyage
                                </CardTitle>
                            </CardHeader>
                            <CardContent className="grid grid-cols-2 gap-3">
                                <div className="flex items-center gap-3 p-2.5 bg-slate-50 rounded-lg border-l-2 border-blue-400">
                                    <Car className="w-4 h-4 text-blue-500" />
                                    <div>
                                        <p className="text-sm font-medium text-[#0A1B3C]">Véhicule</p>
                                        <p className="text-xs text-slate-500">Standard</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 p-2.5 bg-slate-50 rounded-lg border-l-2 border-emerald-400">
                                    <Users className="w-4 h-4 text-emerald-500" />
                                    <div>
                                        <p className="text-sm font-medium text-[#0A1B3C]">
                                            {ride.availableSeats} places
                                        </p>
                                        <p className="text-xs text-slate-500">restantes</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 p-2.5 bg-slate-50 rounded-lg border-l-2 border-amber-400">
                                    {ride.allowsSmoking ? (
                                        <Cigarette className="w-4 h-4 text-amber-500" />
                                    ) : (
                                        <CigaretteOff className="w-4 h-4 text-amber-500" />
                                    )}
                                    <div>
                                        <p className="text-sm font-medium text-[#0A1B3C]">
                                            {ride.allowsSmoking ? 'Fumeur' : 'Non fumeur'}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 p-2.5 bg-slate-50 rounded-lg border-l-2 border-purple-400">
                                    <Shield className="w-4 h-4 text-purple-500" />
                                    <div>
                                        <p className="text-sm font-medium text-[#0A1B3C]">
                                            {getGenderLabel(ride.genderPreference)}
                                        </p>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Right Column: Driver & Action OR Driver Management */}
                    <div className="space-y-6">

                        {isDriver ? (
                            /* DRIVER VIEW */
                            <div className="space-y-6">
                                {/* Combined Driver Role + Action Card */}
                                <Card className="border-0 shadow-sm overflow-hidden">
                                    <div className={`p-4 ${ride.status === 'CANCELLED' ? 'bg-red-50 text-red-800' : 'bg-blue-50 text-blue-800'}`}>
                                        <div className="flex items-center gap-3">
                                            {ride.status === 'CANCELLED' ? (
                                                <XCircle className="w-5 h-5" />
                                            ) : (
                                                <Star className="w-5 h-5 fill-current" />
                                            )}
                                            <span className="font-medium">
                                                {ride.status === 'CANCELLED'
                                                    ? 'Ce trajet a été annulé'
                                                    : 'Vous êtes le conducteur de ce trajet'}
                                            </span>
                                        </div>
                                    </div>
                                    {ride.status !== 'CANCELLED' && ride.status !== 'COMPLETED' && (
                                        <CardContent className="p-4 space-y-3">
                                            {/* Show Modifier only if no passengers booked */}
                                            {(ride.totalSeats - ride.availableSeats) === 0 ? (
                                                <Button
                                                    onClick={handleEdit}
                                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium h-11"
                                                >
                                                    Modifier le trajet
                                                </Button>
                                            ) : (
                                                <div className="text-center text-sm text-slate-500 bg-slate-50 rounded-lg p-3">
                                                    <span className="block text-slate-400">Modification impossible</span>
                                                    <span className="text-xs">Des passagers sont déjà réservés</span>
                                                </div>
                                            )}
                                            <button
                                                onClick={handleCancel}
                                                className="w-full text-sm text-red-600 hover:text-red-700 hover:underline font-medium py-2"
                                            >
                                                Annuler le trajet
                                            </button>
                                        </CardContent>
                                    )}
                                </Card>

                                {/* Passenger List with Actions */}
                                <Card className="border-0 shadow-sm">
                                    <CardHeader className="pb-2">
                                        <CardTitle className="text-lg font-bold text-[#0A1B3C]">Passagers ({passengers.length})</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        {passengers.length > 0 ? (
                                            <div className="space-y-3">
                                                {passengers.map((passenger) => (
                                                    <div key={passenger.bookingId} className="flex items-center gap-3 p-3 bg-slate-50 rounded-lg">
                                                        <div className="w-10 h-10 bg-gradient-to-br from-slate-200 to-slate-300 rounded-full flex items-center justify-center text-sm font-bold text-slate-600 uppercase">
                                                            {passenger.passengerName.charAt(0)}
                                                        </div>
                                                        <div className="flex-1 min-w-0">
                                                            <p className="font-medium text-[#0A1B3C] capitalize truncate">
                                                                {passenger.passengerName}
                                                            </p>
                                                            <p className="text-sm text-slate-500">{passenger.seatsBooked} place{passenger.seatsBooked > 1 ? 's' : ''}</p>
                                                        </div>
                                                        {/* Passenger Action Buttons */}
                                                        <div className="flex items-center gap-1">
                                                            <a
                                                                href={`tel:${passenger.passengerPhone || ''}`}
                                                                className="p-2 text-emerald-600 hover:bg-emerald-50 rounded-full transition-colors"
                                                                title="Appeler"
                                                            >
                                                                <Phone className="w-4 h-4" />
                                                            </a>
                                                            <button
                                                                className="p-2 text-blue-600 hover:bg-blue-50 rounded-full transition-colors"
                                                                title="Voir profil"
                                                            >
                                                                <User className="w-4 h-4" />
                                                            </button>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <div className="bg-gradient-to-br from-slate-50 to-slate-100 rounded-xl p-8 flex flex-col items-center justify-center text-center">
                                                <div className="w-16 h-16 bg-slate-200/80 rounded-full flex items-center justify-center mb-4">
                                                    <Users className="w-8 h-8 text-slate-400" />
                                                </div>
                                                <p className="font-medium text-slate-600 mb-1">En attente de passagers...</p>
                                                <p className="text-sm text-slate-400">Partagez votre trajet pour trouver des passagers</p>
                                            </div>
                                        )}
                                    </CardContent>
                                </Card>

                                {/* Share Section */}
                                <div className="space-y-2">
                                    <h4 className="font-medium text-[#0A1B3C]">Partager ce trajet</h4>
                                    <div className="flex gap-2">
                                        <Button size="icon" variant="outline" className="rounded-full bg-green-50 text-green-600 border-green-200 hover:bg-green-100">
                                            <Share2 className="w-4 h-4" />
                                        </Button>
                                        <Button size="icon" variant="outline" className="rounded-full bg-blue-50 text-blue-600 border-blue-200 hover:bg-blue-100">
                                            <Copy className="w-4 h-4" />
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            /* PASSENGER VIEW */
                            <div className="space-y-6">
                                {/* Driver Card */}
                                <Card className="border-0 shadow-sm">
                                    <CardHeader className="pb-2">
                                        <CardTitle className="text-lg font-bold text-[#0A1B3C]">Conducteur</CardTitle>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="flex items-center gap-4 mb-4">
                                            <div className="w-14 h-14 bg-gradient-to-br from-slate-200 to-slate-300 rounded-full flex items-center justify-center text-xl font-bold text-slate-600 ring-2 ring-white shadow-md">
                                                {ride.driverName.charAt(0)}
                                            </div>
                                            <div>
                                                <h3 className="font-bold text-[#0A1B3C] text-lg">
                                                    {ride.driverName}
                                                </h3>
                                                <div className="flex items-center gap-1 text-amber-500">
                                                    <Star className="w-4 h-4 fill-current" />
                                                    <span className="font-bold text-sm">
                                                        {ride.driverRating ? ride.driverRating.toFixed(1) : 'Nouveau'}
                                                    </span>
                                                    {ride.driverRating > 0 && (
                                                        <span className="text-xs text-slate-400 ml-1">
                                                            (avis)
                                                        </span>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                        {/* Social Media Links */}
                                        {(ride.driverFacebookUrl || ride.driverInstagramUrl) && (
                                            <div className="flex items-center gap-2 mt-3">
                                                {ride.driverFacebookUrl && (
                                                    <a
                                                        href={ride.driverFacebookUrl}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        className="p-2 bg-blue-50 text-blue-600 rounded-full hover:bg-blue-100 transition-colors"
                                                        title="Facebook"
                                                    >
                                                        <Facebook className="w-4 h-4" />
                                                    </a>
                                                )}
                                                {ride.driverInstagramUrl && (
                                                    <a
                                                        href={ride.driverInstagramUrl}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        className="p-2 bg-pink-50 text-pink-600 rounded-full hover:bg-pink-100 transition-colors"
                                                        title="Instagram"
                                                    >
                                                        <Instagram className="w-4 h-4" />
                                                    </a>
                                                )}
                                            </div>
                                        )}
                                    </CardContent>
                                </Card>

                                {/* Booking Card */}
                                <Card className="border-0 shadow-lg ring-1 ring-slate-100">
                                    <CardContent className="p-6 space-y-4">
                                        {/* Show role indicator for booked passengers */}
                                        {hasBooked && (
                                            <div className="flex items-center gap-3 p-3 bg-blue-50 rounded-lg border border-blue-100">
                                                <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                                                    <User className="w-4 h-4 text-blue-600" />
                                                </div>
                                                <span className="text-sm font-medium text-blue-800">
                                                    Vous êtes passager sur ce trajet
                                                </span>
                                            </div>
                                        )}

                                        <div className="flex justify-between items-center text-sm text-slate-500">
                                            <span>Prix par place</span>
                                            <span className="font-bold text-[#0A1B3C] text-lg">
                                                {ride.pricePerSeat} TND
                                            </span>
                                        </div>

                                        {hasBooked ? (
                                            <>
                                                {/* Booking confirmed badge - small, left-aligned */}
                                                <span className="inline-flex items-center gap-1.5 bg-emerald-100 text-emerald-700 px-2.5 py-1 rounded-full text-xs font-medium">
                                                    <CheckCircle2 className="w-3.5 h-3.5" />
                                                    Réservation confirmée
                                                </span>

                                                {/* Action buttons for booked passengers */}
                                                <Button
                                                    onClick={() => navigate('/my-bookings')}
                                                    variant="outline"
                                                    className="w-full h-10"
                                                >
                                                    Voir mes réservations
                                                </Button>
                                                <Button
                                                    onClick={async () => {
                                                        if (confirm('Êtes-vous sûr de vouloir annuler votre réservation ?')) {
                                                            setCancelLoading(true);
                                                            try {
                                                                await cancelBookingByRide(id);
                                                                setHasBooked(false);
                                                            } catch (err) {
                                                                console.error('Error canceling booking:', err);
                                                                alert('Erreur lors de l\'annulation');
                                                            } finally {
                                                                setCancelLoading(false);
                                                            }
                                                        }
                                                    }}
                                                    disabled={cancelLoading}
                                                    variant="ghost"
                                                    className="w-full h-10 text-red-600 hover:bg-red-50"
                                                >
                                                    <XCircle className="w-4 h-4 mr-2" />
                                                    {cancelLoading ? 'Annulation...' : 'Annuler ma réservation'}
                                                </Button>
                                            </>
                                        ) : ride.status === 'CANCELLED' ? (
                                            <Button
                                                disabled
                                                className="w-full bg-red-100 text-red-600 font-bold h-12 text-lg shadow-none cursor-not-allowed"
                                            >
                                                Trajet annulé
                                            </Button>
                                        ) : ride.status === 'COMPLETED' ? (
                                            <Button
                                                disabled
                                                className="w-full bg-emerald-100 text-emerald-600 font-bold h-12 text-lg shadow-none cursor-not-allowed"
                                            >
                                                Trajet terminé
                                            </Button>
                                        ) : (
                                            <Button
                                                onClick={handleBook}
                                                disabled={bookingLoading}
                                                className="w-full bg-[#FF7A00] hover:bg-[#FF7A00]/90 text-white font-bold h-12 text-lg shadow-md transition-transform active:scale-95 disabled:opacity-50"
                                            >
                                                {bookingLoading ? 'Réservation...' : 'Réserver ce trajet'}
                                            </Button>
                                        )}

                                        <p className="text-xs text-center text-slate-400 flex items-center justify-center gap-1">
                                            <CreditCard className="w-3 h-3" /> Paiement sécurisé en ligne ou espèce
                                        </p>
                                    </CardContent>
                                </Card>
                            </div>
                        )}
                    </div>
                </div>
            </main>

            <Footer />
        </div>
    );
};

export default RideDetails;
