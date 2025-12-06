/*
  Search Results Page
  -------------------
  Displays ride search results.
  
  Features:
  - PUBLIC: No authentication required (lazy registration)
  - Parses URL search params from Landing page
  - Calls backend /api/v1/rides/search
  - Displays RideCard components
  - Loading state with skeleton loaders
  - Empty state when no rides found
  - Search summary header
*/

import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { SearchX, RefreshCw, MapPin, Calendar, Users, ArrowLeft } from 'lucide-react';
import Navbar from '@/components/layout/Navbar';
import Footer from '@/components/layout/Footer';
import RideCard from '@/components/rides/RideCard';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { searchRides } from '@/api/rideApi';

// Format date for display
const formatSearchDate = (dateStr) => {
    if (!dateStr) return 'Toutes les dates';
    const date = new Date(dateStr);
    return date.toLocaleDateString('fr-FR', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
};

// Skeleton loader for ride cards
const RideCardSkeleton = () => (
    <Card className="border-l-4 border-l-gray-200">
        <CardContent className="p-6">
            <div className="flex flex-col sm:flex-row sm:items-center gap-4">
                <div className="flex-1 space-y-3">
                    <Skeleton className="h-8 w-24" />
                    <div className="space-y-2">
                        <Skeleton className="h-4 w-48" />
                        <Skeleton className="h-4 w-40" />
                    </div>
                </div>
                <div className="flex items-center gap-3 sm:px-6">
                    <Skeleton className="h-12 w-12 rounded-full" />
                    <div className="space-y-2">
                        <Skeleton className="h-4 w-24" />
                        <Skeleton className="h-3 w-16" />
                    </div>
                </div>
                <div className="flex flex-col items-end gap-2 min-w-[120px]">
                    <Skeleton className="h-8 w-20" />
                    <Skeleton className="h-4 w-16" />
                    <Skeleton className="h-9 w-24" />
                </div>
            </div>
        </CardContent>
    </Card>
);

const Search = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    // State
    const [rides, setRides] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [totalResults, setTotalResults] = useState(0);

    // Extract search params
    const origin = searchParams.get('origin') || '';
    const originLat = searchParams.get('originLat');
    const originLon = searchParams.get('originLon');
    const dest = searchParams.get('dest') || '';
    const destLat = searchParams.get('destLat');
    const destLon = searchParams.get('destLon');
    const date = searchParams.get('date');
    const passengers = searchParams.get('passengers') || '1';

    // Fetch rides on mount or when params change
    useEffect(() => {
        const fetchRides = async () => {
            // Validate required params (Date is now OPTIONAL)
            if (!originLat || !originLon || !destLat || !destLon) {
                setError('Paramètres de recherche manquants (Départ et Arrivée requis).');
                setLoading(false);
                return;
            }

            setLoading(true);
            setError(null);

            try {
                // Build departure time only if date exists
                let departureTime = '';
                if (date) {
                    departureTime = date.includes('T')
                        ? date.split('+')[0].split('Z')[0]
                        : `${date}T08:00:00`;
                }

                const response = await searchRides({
                    originLat: parseFloat(originLat),
                    originLon: parseFloat(originLon),
                    destLat: parseFloat(destLat),
                    destLon: parseFloat(destLon),
                    departureTime,
                    seats: parseInt(passengers, 10),
                    radius: 20, // 20km radius (covers typical city-size differences)
                    page: 0,
                    size: 20,
                });

                setRides(response.content || []);
                setTotalResults(response.totalElements || 0);
            } catch (err) {
                console.error('Search error:', err);
                setError('Une erreur est survenue lors de la recherche. Veuillez réessayer.');
            } finally {
                setLoading(false);
            }
        };

        fetchRides();
    }, [originLat, originLon, destLat, destLon, date, passengers]);

    // Handle booking (callback from RideCard)
    const handleBook = (ride) => {
        // Navigate to booking page with ride ID
        navigate(`/rides/${ride.id}/book`);
    };

    return (
        <div className="min-h-screen flex flex-col bg-slate-50">
            <Navbar />

            <main className="flex-1">
                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

                    {/* Back button */}
                    <Button
                        variant="ghost"
                        onClick={() => navigate('/')}
                        className="mb-4 -ml-2"
                    >
                        <ArrowLeft className="h-4 w-4 mr-2" />
                        Nouvelle recherche
                    </Button>

                    {/* Search Summary Header */}
                    <Card className="mb-6">
                        <CardContent className="p-4">
                            <div className="flex flex-wrap items-center gap-4 text-sm">
                                <div className="flex items-center gap-2">
                                    <MapPin className="h-4 w-4 text-primary" />
                                    <span className="font-medium">{origin || 'Départ'}</span>
                                    <span className="text-muted-foreground">→</span>
                                    <span className="font-medium">{dest || 'Destination'}</span>
                                </div>
                                <div className="flex items-center gap-2 text-muted-foreground">
                                    <Calendar className="h-4 w-4" />
                                    <span>{formatSearchDate(date)}</span>
                                </div>
                                <div className="flex items-center gap-2 text-muted-foreground">
                                    <Users className="h-4 w-4" />
                                    <span>{passengers} passager{passengers > 1 ? 's' : ''}</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Results count */}
                    {!loading && !error && (
                        <p className="text-muted-foreground mb-4">
                            {totalResults === 0
                                ? 'Aucun trajet trouvé'
                                : `${totalResults} trajet${totalResults > 1 ? 's' : ''} trouvé${totalResults > 1 ? 's' : ''}`}
                        </p>
                    )}

                    {/* Loading State */}
                    {loading && (
                        <div className="space-y-4">
                            <RideCardSkeleton />
                            <RideCardSkeleton />
                            <RideCardSkeleton />
                        </div>
                    )}

                    {/* Error State */}
                    {error && (
                        <Card className="border-destructive/50">
                            <CardContent className="p-8 text-center">
                                <SearchX className="h-12 w-12 text-destructive mx-auto mb-4" />
                                <h2 className="text-lg font-semibold mb-2">Erreur de recherche</h2>
                                <p className="text-muted-foreground mb-4">{error}</p>
                                <Button onClick={() => navigate('/')}>
                                    Retour à l'accueil
                                </Button>
                            </CardContent>
                        </Card>
                    )}

                    {/* Empty State */}
                    {!loading && !error && rides.length === 0 && (
                        <Card>
                            <CardContent className="p-8 text-center">
                                <SearchX className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                                <h2 className="text-lg font-semibold mb-2">Aucun trajet disponible</h2>
                                <p className="text-muted-foreground mb-6">
                                    Nous n'avons pas trouvé de trajets correspondant à votre recherche.
                                    <br />
                                    Essayez de modifier votre date ou d'élargir votre zone de recherche.
                                </p>
                                <div className="flex flex-col sm:flex-row gap-3 justify-center">
                                    <Button onClick={() => navigate('/')}>
                                        <RefreshCw className="h-4 w-4 mr-2" />
                                        Modifier la recherche
                                    </Button>
                                    <Button variant="outline" onClick={() => navigate('/rides/create')}>
                                        Proposer ce trajet
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    )}

                    {/* Results List */}
                    {!loading && !error && rides.length > 0 && (
                        <div className="space-y-4">
                            {rides.map((ride) => (
                                <RideCard
                                    key={ride.id}
                                    ride={ride}
                                    onBook={handleBook}
                                />
                            ))}
                        </div>
                    )}

                </div>
            </main>

            <Footer />
        </div>
    );
};

export default Search;
