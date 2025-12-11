/*
  MyBookings Page
  ---------------
  Protected page showing passenger's bookings.
  Shows upcoming and past bookings.
*/

import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { Calendar, MapPin, Clock, User, CheckCircle2, XCircle, ArrowRight } from 'lucide-react';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { getMyBookings, cancelBooking } from '@/api/bookingApi';

// Format date
const formatDate = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleDateString('fr-FR', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
};

// Format time
const formatTime = (isoString) => {
    return new Date(isoString).toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
    });
};

const MyBookings = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('upcoming');
    const [successMessage, setSuccessMessage] = useState(null);

    // Check for success message from navigation state
    useEffect(() => {
        if (location.state?.message) {
            setSuccessMessage(location.state.message);
            // Clear the state to prevent showing message on refresh
            window.history.replaceState({}, document.title);
            // Auto-hide after 5 seconds
            setTimeout(() => setSuccessMessage(null), 5000);
        }
    }, [location.state]);

    // Fetch bookings
    useEffect(() => {
        const fetchBookings = async () => {
            setLoading(true);
            try {
                const response = await getMyBookings(0, 50);
                setBookings(response.content || []);
            } catch (error) {
                console.error('Error fetching bookings:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchBookings();
    }, []);

    // Filter bookings based on tab
    const getFilteredBookings = () => {
        const now = new Date();
        return bookings.filter(booking => {
            const bookingDate = new Date(booking.departureTime);

            if (activeTab === 'upcoming') {
                return bookingDate >= now && booking.status !== 'CANCELLED';
            }
            if (activeTab === 'past') {
                return bookingDate < now || booking.status === 'COMPLETED';
            }
            if (activeTab === 'cancelled') {
                return booking.status === 'CANCELLED';
            }
            return false;
        });
    };

    const handleCancelBooking = async (bookingId) => {
        if (confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
            try {
                await cancelBooking(bookingId);
                // Refresh bookings
                const response = await getMyBookings(0, 50);
                setBookings(response.content || []);
            } catch (error) {
                console.error('Error cancelling booking:', error);
                alert('Erreur lors de l\'annulation');
            }
        }
    };

    const filteredBookings = getFilteredBookings();

    // Tab Button
    const TabButton = ({ id, label }) => (
        <button
            onClick={() => setActiveTab(id)}
            className={`px-6 py-2.5 rounded-full text-sm font-medium transition-all duration-200 
                ${activeTab === id
                    ? 'bg-[#0A1B3C] text-white shadow-md'
                    : 'bg-slate-100/50 text-slate-600 hover:bg-slate-200/50'
                }`}
        >
            {label}
        </button>
    );

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col">
            <Navbar />

            <main className="flex-grow pt-28 pb-16 px-4 sm:px-8 max-w-[1200px] mx-auto w-full">
                {/* Success Message Toast */}
                {successMessage && (
                    <div className="fixed top-24 left-1/2 -translate-x-1/2 z-50 animate-in fade-in slide-in-from-top-4">
                        <div className="bg-emerald-500 text-white px-6 py-3 rounded-lg shadow-lg flex items-center gap-3">
                            <CheckCircle2 className="w-5 h-5" />
                            <span className="font-medium">{successMessage}</span>
                        </div>
                    </div>
                )}

                {/* Header */}
                <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-10">
                    <div>
                        <h1 className="text-3xl font-bold text-[#0A1B3C] mb-2 tracking-tight">
                            Mes réservations
                        </h1>
                        <p className="text-slate-500 text-[15px]">
                            Gérez vos réservations de covoiturage.
                        </p>
                    </div>

                    <div className="flex items-center bg-white p-1.5 rounded-full shadow-sm border border-slate-100">
                        <TabButton id="upcoming" label="À venir" />
                        <TabButton id="past" label="Passées" />
                        <TabButton id="cancelled" label="Annulées" />
                    </div>
                </div>

                {/* Content */}
                <div className="max-w-3xl mx-auto">
                    {loading ? (
                        <div className="space-y-4">
                            {[1, 2, 3].map(i => (
                                <div key={i} className="h-40 bg-white rounded-2xl animate-pulse" />
                            ))}
                        </div>
                    ) : filteredBookings.length > 0 ? (
                        <div className="space-y-4">
                            {filteredBookings.map(booking => (
                                <Card
                                    key={booking.id}
                                    onClick={() => navigate(`/ride/${booking.rideID}`, { state: { from: '/my-bookings' } })}
                                    className="border-none shadow-sm hover:shadow-md transition-all cursor-pointer rounded-2xl overflow-hidden hover:scale-[1.01]"
                                >
                                    <CardContent className="p-6">
                                        <div className="flex flex-col sm:flex-row gap-4">
                                            {/* Date & Time */}
                                            <div className="flex items-start gap-3">
                                                <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
                                                    <Calendar className="w-5 h-5" />
                                                </div>
                                                <div>
                                                    <p className="font-bold text-[#0A1B3C] capitalize">
                                                        {formatDate(booking.departureTime)}
                                                    </p>
                                                    <p className="text-xl font-bold text-[#FF7A00]">
                                                        {formatTime(booking.departureTime)}
                                                    </p>
                                                </div>
                                            </div>

                                            {/* Route */}
                                            <div className="flex-1 sm:border-l sm:pl-4">
                                                <p className="text-lg font-medium text-[#0A1B3C]">
                                                    {booking.rideSummary}
                                                </p>
                                                <p className="text-sm text-slate-500">
                                                    Conducteur: {booking.driverName}
                                                </p>
                                            </div>

                                            {/* Status & Actions */}
                                            <div className="flex flex-col items-end gap-2">
                                                <Badge
                                                    className={`${booking.status === 'CONFIRMED'
                                                        ? 'bg-emerald-100 text-emerald-700'
                                                        : booking.status === 'CANCELLED'
                                                            ? 'bg-red-100 text-red-700'
                                                            : 'bg-slate-100 text-slate-700'
                                                        } border-none`}
                                                >
                                                    {booking.status === 'CONFIRMED' && <CheckCircle2 className="w-3 h-3 mr-1" />}
                                                    {booking.status === 'CANCELLED' && <XCircle className="w-3 h-3 mr-1" />}
                                                    {booking.status === 'CONFIRMED' ? 'Confirmée' :
                                                        booking.status === 'CANCELLED' ? 'Annulée' : booking.status}
                                                </Badge>

                                                <p className="text-lg font-bold text-[#0A1B3C]">
                                                    {booking.priceTotal} TND
                                                </p>
                                                <p className="text-sm text-slate-500">
                                                    {booking.seatsBooked} place{booking.seatsBooked > 1 ? 's' : ''}
                                                </p>

                                                {activeTab === 'upcoming' && booking.status === 'CONFIRMED' && (
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        className="text-red-600 hover:text-red-700 hover:bg-red-50"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            handleCancelBooking(booking.id);
                                                        }}
                                                    >
                                                        Annuler
                                                    </Button>
                                                )}
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    ) : (
                        <Card className="border-dashed">
                            <CardContent className="p-12 text-center">
                                <Calendar className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                                <h3 className="text-lg font-bold text-[#0A1B3C] mb-2">
                                    {activeTab === 'upcoming'
                                        ? 'Aucune réservation à venir'
                                        : activeTab === 'past'
                                            ? 'Aucune réservation passée'
                                            : 'Aucune réservation annulée'}
                                </h3>
                                <p className="text-slate-500 mb-6">
                                    {activeTab === 'upcoming' && "Recherchez un trajet pour commencer !"}
                                </p>
                                {activeTab === 'upcoming' && (
                                    <Link to="/">
                                        <Button className="bg-[#FF7A00] hover:bg-[#FF7A00]/90 text-white">
                                            Rechercher un trajet
                                            <ArrowRight className="w-4 h-4 ml-2" />
                                        </Button>
                                    </Link>
                                )}
                            </CardContent>
                        </Card>
                    )}
                </div>
            </main>

            <Footer />
        </div>
    );
};

export default MyBookings;
