import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Bell, User } from 'lucide-react';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import { Button } from '@/components/ui/button';
import { getMyRides } from '@/api/rideApi';
import DriverRideCard from '@/components/dashboard/DriverRideCard';
import { useAuth } from '@/hooks/useAuth';

const MyRides = () => {
    const navigate = useNavigate();
    const { user } = useAuth();

    // State
    const [rides, setRides] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('upcoming'); // upcoming, history, cancelled

    // Fetch rides
    const fetchRides = async () => {
        setLoading(true);
        try {
            const response = await getMyRides(0, 100); // Fetch plenty for now
            setRides(response.content || []);
        } catch (error) {
            console.error("Error fetching rides:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRides();
    }, []);

    // Filter rides based on tab
    const getFilteredRides = () => {
        const now = new Date();

        return rides.filter(ride => {
            const rideDate = new Date(ride.departureTime);

            if (activeTab === 'cancelled') {
                return ride.status === 'CANCELLED';
            }

            // If filtering for Upcoming or History, ensure not cancelled first
            if (ride.status === 'CANCELLED') return false;

            if (activeTab === 'upcoming') {
                return rideDate >= now && ride.status !== 'COMPLETED';
            }
            if (activeTab === 'history') {
                return rideDate < now || ride.status === 'COMPLETED';
            }
            return false;
        }).sort((a, b) => {
            // Sort upcoming ascending (soonest first)
            // Sort history descending (most recent first)
            const dateA = new Date(a.departureTime);
            const dateB = new Date(b.departureTime);
            return activeTab === 'upcoming'
                ? dateA - dateB
                : dateB - dateA;
        });
    };

    const filteredRides = getFilteredRides();

    // Tab Button Component
    const TabButton = ({ id, label }) => (
        <button
            onClick={() => setActiveTab(id)}
            className={`
                px-6 py-2.5 rounded-full text-sm font-medium transition-all duration-200
                ${activeTab === id
                    ? 'bg-[#0A1B3C] text-white shadow-md'
                    : 'bg-slate-100/50 text-slate-600 hover:bg-slate-200/50'
                }
            `}
        >
            {label}
        </button>
    );

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
            <Navbar />

            {/* Main Content Area */}
            <main className="flex-grow pt-28 pb-16 px-4 sm:px-8 max-w-[1440px] mx-auto w-full">

                {/* Header Section */}
                <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 mb-10">
                    <div>
                        <h1 className="text-3xl font-bold text-[#0A1B3C] mb-2 tracking-tight">
                            Mes trajets
                        </h1>
                        <p className="text-slate-500 text-[15px]">
                            Vue d'ensemble de tous vos trajets de covoiturage.
                        </p>
                    </div>

                    <div className="flex items-center bg-white p-1.5 rounded-full shadow-sm border border-slate-100">
                        <TabButton id="upcoming" label="À venir" />
                        <TabButton id="history" label="Historique" />
                        <TabButton id="cancelled" label="Annulés" />
                    </div>
                </div>

                {/* Content Grid */}
                <div className="grid grid-cols-12 gap-8">
                    {/* Main List */}
                    <div className="col-span-12 lg:col-span-8 lg:col-start-2"> {/* Centered layout max 8 cols */}

                        {loading ? (
                            <div className="space-y-6">
                                {[1, 2, 3].map(i => (
                                    <div key={i} className="h-64 bg-white rounded-2xl animate-pulse" />
                                ))}
                            </div>
                        ) : filteredRides.length > 0 ? (
                            <div className="space-y-6">
                                {filteredRides.map(ride => (
                                    <DriverRideCard
                                        key={ride.id}
                                        ride={ride}
                                        activeTab={activeTab}
                                        onCancel={fetchRides}
                                    />
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-20 bg-white rounded-3xl border border-dashed border-slate-200">
                                <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <Plus className="w-8 h-8 text-slate-300" />
                                </div>
                                <h3 className="text-lg font-bold text-[#0A1B3C] mb-2">Aucun trajet trouvé</h3>
                                <p className="text-slate-500 mb-6 max-w-sm mx-auto">
                                    {activeTab === 'upcoming'
                                        ? "Vous n'avez aucun trajet à venir. C'est le moment de proposer votre prochain voyage !"
                                        : activeTab === 'history'
                                            ? "Votre historique de trajets est vide."
                                            : "Aucun trajet annulé."
                                    }
                                </p>
                                {activeTab === 'upcoming' && (
                                    <Button
                                        onClick={() => navigate('/publish')}
                                        className="bg-[#FF7A00] hover:bg-[#FF7A00]/90 text-white font-bold h-12 px-8 rounded-xl shadow-lg shadow-orange-200 transition-all hover:translate-y-[-2px]"
                                    >
                                        Publier un trajet
                                    </Button>
                                )}
                            </div>
                        )}

                    </div>
                </div>
            </main>

            <Footer />
        </div>
    );
};

export default MyRides;
