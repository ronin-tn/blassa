/*
  RideCard Component
  ------------------
  Displays a single ride result in a ticket-style card.
  
  Features:
  - Driver name & rating
  - Origin → Destination with times
  - Price per seat
  - Available seats
  - Gender preference badge (if not "ANY")
  - Smoking indicator
  - "Réserver" button (triggers auth check)
  
  Props:
  - ride: RideResponse object from backend
  - onBook: callback when user clicks "Réserver"
  
  Performance:
  - Wrapped with React.memo to prevent unnecessary re-renders
*/

import { memo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Clock, Users, Star, Cigarette, CigaretteOff, User, Heart, CheckCircle } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useAuth } from '@/hooks/useAuth';

// Format time from ISO string to "HH:MM"
const formatTime = (isoString) => {
  const date = new Date(isoString);
  return date.toLocaleTimeString('fr-FR', {
    hour: '2-digit',
    minute: '2-digit',
  });
};

// Format date from ISO string to "Lun 10 Dec"
const formatDate = (isoString) => {
  const date = new Date(isoString);
  return date.toLocaleDateString('fr-FR', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  });
};

const RideCard = memo(({ ride, onBook, isAlreadyBooked = false }) => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const {
    id,
    driverName,
    driverRating,
    originName,
    destinationName,
    departureTime,
    availableSeats,
    pricePerSeat,
    allowsSmoking,
    genderPreference,
  } = ride;

  // Handle "Réserver" button click
  const handleBook = () => {
    if (!user) {
      // Not logged in - redirect to login with return URL
      const currentUrl = window.location.pathname + window.location.search;
      navigate(`/login?redirect=${encodeURIComponent(currentUrl)}`);
      return;
    }

    // Logged in - proceed with booking callback
    if (onBook) {
      onBook(ride);
    } else {
      // Default: navigate to ride details for booking
      navigate(`/rides/${id}/book`);
    }
  };

  return (
    <Card className="hover:shadow-lg transition-shadow duration-200 border-l-4 border-l-primary">
      <CardContent className="p-4 sm:p-6">
        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
          {/* Left: Time & Route */}
          <div className="flex-1">
            {/* Time */}
            <div className="flex items-center gap-2 mb-3">
              <Clock className="h-5 w-5 text-primary" />
              <span className="text-2xl font-bold">{formatTime(departureTime)}</span>
              <span className="text-sm text-muted-foreground">{formatDate(departureTime)}</span>
            </div>

            {/* Route */}
            <div className="flex items-start gap-3">
              <div className="flex flex-col items-center mt-1">
                <div className="w-2 h-2 rounded-full bg-primary" />
                <div className="w-0.5 h-10 bg-border" />
                <div className="w-2 h-2 rounded-full bg-amber-500" />
              </div>
              <div className="flex flex-col gap-3">
                <div>
                  <p className="font-medium">{originName}</p>
                </div>
                <div>
                  <p className="font-medium">{destinationName}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Center: Driver Info */}
          <div className="flex items-center gap-3 sm:border-l sm:border-r sm:px-6 sm:mx-4">
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center">
              <User className="h-6 w-6 text-primary" />
            </div>
            <div>
              <p className="font-medium">{driverName}</p>
              <div className="flex items-center gap-1 text-sm text-muted-foreground">
                <Star className="h-4 w-4 text-amber-500 fill-amber-500" />
                <span>{driverRating?.toFixed(1) || 'Nouveau'}</span>
              </div>
            </div>
          </div>

          {/* Right: Price & Booking */}
          <div className="flex flex-col items-end gap-2 min-w-[120px]">
            {/* Price */}
            <div className="text-right">
              <p className="text-2xl font-bold text-primary">{pricePerSeat} DT</p>
              <p className="text-sm text-muted-foreground">par place</p>
            </div>

            {/* Seats available */}
            <div className="flex items-center gap-1 text-sm text-muted-foreground">
              <Users className="h-4 w-4" />
              <span>
                {availableSeats} place{availableSeats > 1 ? 's' : ''}
              </span>
            </div>

            {/* Book button or Already Booked badge */}
            {isAlreadyBooked ? (
              <Badge variant="outline" className="flex items-center gap-1 px-3 py-2 text-green-600 border-green-400 bg-green-50">
                <CheckCircle className="h-4 w-4" />
                Déjà réservé
              </Badge>
            ) : (
              <Button variant="action" size="sm" onClick={handleBook} className="w-full sm:w-auto">
                Réserver
              </Button>
            )}
          </div>
        </div>

        {/* Bottom: Badges */}
        <div className="flex items-center gap-2 mt-4 pt-4 border-t">
          {/* Gender Preference */}
          {genderPreference === 'FEMALE_ONLY' && (
            <Badge variant="female" className="flex items-center gap-1">
              <Heart className="h-3 w-3" />
              Femmes uniquement
            </Badge>
          )}
          {genderPreference === 'MALE_ONLY' && (
            <Badge variant="male" className="flex items-center gap-1">
              <User className="h-3 w-3" />
              Hommes uniquement
            </Badge>
          )}

          {/* Smoking */}
          {allowsSmoking ? (
            <Badge
              variant="outline"
              className="flex items-center gap-1 text-amber-600 border-amber-300"
            >
              <Cigarette className="h-3 w-3" />
              Fumeur accepté
            </Badge>
          ) : (
            <Badge
              variant="outline"
              className="flex items-center gap-1 text-green-600 border-green-300"
            >
              <CigaretteOff className="h-3 w-3" />
              Non-fumeur
            </Badge>
          )}
        </div>
      </CardContent>
    </Card>
  );
});

RideCard.displayName = 'RideCard';

export default RideCard;
