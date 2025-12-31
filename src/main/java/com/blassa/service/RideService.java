package com.blassa.service;

import com.blassa.dto.RideRequest;
import com.blassa.dto.RideResponse;
import com.blassa.dto.RideStatusResponse;
import com.blassa.dto.RideUpdateStatusRequest;
import com.blassa.model.entity.Booking;
import com.blassa.model.entity.Ride;
import com.blassa.model.entity.User;
import com.blassa.model.enums.*;
import com.blassa.repository.BookingRepository;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.blassa.model.enums.BookingStatus.CONFIRMED;
import static com.blassa.model.enums.RideStatus.COMPLETED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    private final com.blassa.service.VehicleService vehicleService; // Inject VehicleService

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public RideResponse createRide(RideRequest rideRequest) {
        User driver = getCurrentUser();

        // Verifi consistency t3 preference de genre (Fix #6)
        validateGenderPreference(driver, rideRequest.genderPreference());

        // Verifi u jib l vehicle
        if (rideRequest.vehicleId() == null) {
            throw new IllegalArgumentException("A vehicle must be selected for the ride");
        }
        com.blassa.model.entity.Vehicle vehicle = vehicleService.getVehicleEntity(rideRequest.vehicleId());
        if (!vehicle.getOwner().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("You can only use your own vehicles");
        }

        Point origin = geometryFactory.createPoint(new Coordinate(rideRequest.originLon(), rideRequest.originLat()));
        Point destination = geometryFactory
                .createPoint(new Coordinate(rideRequest.destinationLon(), rideRequest.destinationLat()));

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setVehicle(vehicle); // 7ott l vehicle
        ride.setOriginName(rideRequest.originName());
        ride.setOriginPoint(origin);
        ride.setDestinationName(rideRequest.destinationName());
        ride.setDestinationPoint(destination);
        ride.setDepartureTime(rideRequest.departureTime());
        ride.setTotalSeats(rideRequest.totalSeats());
        ride.setAvailableSeats(rideRequest.totalSeats());
        ride.setPricePerSeat(rideRequest.pricePerSeat());
        ride.setAllowsSmoking(rideRequest.allowsSmoking());
        ride.setAllowsMusic(rideRequest.allowsMusic());
        ride.setAllowsPets(rideRequest.allowsPets());
        ride.setLuggageSize(rideRequest.luggageSize());
        ride.setGenderPreference(rideRequest.genderPreference());
        ride.setStatus(RideStatus.SCHEDULED);
        Ride saved = rideRepository.save(ride);
        return mapToResponse(saved);
    }

    private RideResponse mapToResponse(Ride ride) {
        String carMake = ride.getVehicle() != null ? ride.getVehicle().getMake() : null;
        String carModel = ride.getVehicle() != null ? ride.getVehicle().getModel() : null;
        String carColor = ride.getVehicle() != null ? ride.getVehicle().getColor() : null;

        String carLicensePlate = null;
        User currentUser = getCurrentUserOrNull();

        // Show license plate only to the driver
        if (currentUser != null && ride.getDriver().getId().equals(currentUser.getId())) {
            carLicensePlate = ride.getVehicle() != null ? ride.getVehicle().getLicensePlate() : null;
        }

        return new RideResponse(
                ride.getId(),
                ride.getDriver().getId(),
                ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName(),
                ride.getDriver().getProfilePictureUrl(),
                ride.getDriver().getEmail(),
                0.0,
                ride.getDriver().getFacebookUrl(),
                ride.getDriver().getInstagramUrl(),
                ride.getDriver().getPhoneNumber(),
                ride.getOriginName(),
                ride.getOriginPoint().getY(),
                ride.getOriginPoint().getX(),
                ride.getDestinationName(),
                ride.getDestinationPoint().getY(),
                ride.getDestinationPoint().getX(),
                ride.getDepartureTime(),
                ride.getTotalSeats(),
                ride.getAvailableSeats(),
                ride.getPricePerSeat(),
                ride.getAllowsSmoking(),
                ride.getAllowsMusic(),
                ride.getAllowsPets(),
                ride.getLuggageSize(),
                ride.getGenderPreference(),
                ride.getStatus(),
                carMake,
                carModel,
                carColor,
                carLicensePlate);
    }

    /**
     * Endpoint search publique - yemchi lel authenticated u l anonymous users.
     * 
     * Lel anonymous users: Ywarri rides LKOL par défaut, walla mfiltria b
     * genderFilter...
     * Lel authenticated users: Ywarri rides li ymwaf9ou l genre te3hom.
     * 
     * @param genderFilter Filter optionnel lel genre (\"FEMALE_ONLY\" to show
     *                     women-only rides)
     */
    public Page<RideResponse> searchRides(
            Double originLat, Double originLon,
            Double destLat, Double destLon,
            OffsetDateTime departureTime,
            Integer seats,
            String genderFilter, // Optional gender filter
            Double radiusKm,
            int page,
            int size,
            String sortBy) {
        // 7added preferences masmou7a 7asb auth status u filter
        List<String> allowedPreferences;

        User currentUser = getCurrentUserOrNull();
        if (currentUser != null) {
            // Authenticated: Filtri b genre t3 user
            allowedPreferences = (currentUser.getGender() == Gender.MALE)
                    ? List.of("ANY", "MALE_ONLY")
                    : List.of("ANY", "FEMALE_ONLY");
        } else if (genderFilter != null && !genderFilter.isEmpty() && !genderFilter.equals("ANY")) {
            // Anonymous ma3 gender filter: Warri ANY + rides spécifiques lel genre
            if (genderFilter.equals("FEMALE_ONLY")) {
                allowedPreferences = List.of("ANY", "FEMALE_ONLY");
            } else if (genderFilter.equals("MALE_ONLY")) {
                allowedPreferences = List.of("ANY", "MALE_ONLY");
            } else {
                allowedPreferences = List.of("ANY", "MALE_ONLY", "FEMALE_ONLY");
            }
        } else {
            // Anonymous blech filter: Warri rides LKOL
            allowedPreferences = List.of("ANY", "MALE_ONLY", "FEMALE_ONLY");
        }

        // 2. Geometry
        Point origin = geometryFactory.createPoint(new Coordinate(originLon, originLat));
        Point destination = geometryFactory.createPoint(new Coordinate(destLon, destLat));

        // 3. Time Window
        OffsetDateTime start;
        OffsetDateTime end;

        if (departureTime != null) {
            start = departureTime.minusHours(2);
            end = departureTime.plusHours(2);
        } else {
            // Date Optionnelle: Lawej 3ala rides jayin lkol (men Taw 7atta l 3am jey)
            start = OffsetDateTime.now();
            end = OffsetDateTime.now().plusYears(1);
        }

        // 4. Logique rayon (Converti Km l Meters)
        double pickupRadiusMeters = (radiusKm != null) ? radiusKm * 1000 : 3000.0;
        double dropoffRadiusMeters = 5000.0;

        // 5. Pagination & Sorting Setup
        // Mappi frontend sort keys l colonnes DB (native query t7eb snake_case)
        Sort sort;
        if (sortBy == null)
            sortBy = "time_asc";

        switch (sortBy) {
            case "price_asc":
                sort = Sort.by("price_per_seat").ascending();
                break;
            case "price_desc":
                sort = Sort.by("price_per_seat").descending();
                break;
            case "time_desc":
                sort = Sort.by("departure_time").descending();
                break;
            case "time_asc":
            default:
                sort = Sort.by("departure_time").ascending();
                break;
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        // 6. Executi
        Page<Ride> ridePage = rideRepository.searchRides(
                origin, pickupRadiusMeters,
                destination, dropoffRadiusMeters,
                start, end,
                seats,
                allowedPreferences,
                pageable);

        // 7. Mappi Entity Page -> DTO Page
        return ridePage.map(this::mapToResponse);
    }

    /**
     * Jib user l 7ali walla null kene mch authenticated (lel public endpoints)
     */
    private User getCurrentUserOrNull() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return null;
            }
            String email = ((UserDetails) auth.getPrincipal()).getUsername();
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    private void validateGenderPreference(User driver, RideGenderPreference preference) {
        if (preference == null)
            return; // Par défaut ANY cv

        if (driver.getGender() == Gender.MALE && preference == RideGenderPreference.FEMALE_ONLY) {
            throw new IllegalArgumentException("Vous ne pouvez pas créer un trajet réservé aux femmes");
        }
        if (driver.getGender() == Gender.FEMALE && preference == RideGenderPreference.MALE_ONLY) {
            throw new IllegalArgumentException("Vous ne pouvez pas créer un trajet réservé aux hommes");
        }
    }

    public RideResponse getRideById(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

        return mapToResponse(ride);
    }

    public Page<RideResponse> getMyRides(int page, int size) {
        User driver = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").descending());
        Page<Ride> rides = rideRepository.findByDriverId(driver.getId(), pageable);
        return rides.map(this::mapToResponse);
    }

    @Transactional
    public RideResponse cancelRide(java.util.UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Security Check: Ya kene l caller huwwa l owner?
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to cancel this ride");
        }

        // Logic Check: Ya kene l ride saret déjà?
        if (ride.getStatus() == COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel a ride that is already completed or cancelled");
        }

        // Annuli bookings lkol l mraboutin b ride hedhi
        List<Booking> bookings = bookingRepository.findByRideId(rideId);
        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                booking.setStatus(BookingStatus.CANCELLED);
            }
        }
        bookingRepository.saveAll(bookings);
        ride.setStatus(RideStatus.CANCELLED);
        Ride saved = rideRepository.save(ride);

        // A3lem passengers lkol elli ride t'annulet
        for (Booking booking : bookings) {
            notificationService.sendNotification(
                    booking.getPassenger().getId(),
                    NotificationType.RIDE_CANCELLED,
                    "Trajet annulé",
                    "Le trajet " + ride.getOriginName() + " → " + ride.getDestinationName()
                            + " a été annulé par le conducteur",
                    null);

            // Ab3ath email lel passager
            emailService.sendRideCancelledEmail(
                    booking.getPassenger().getEmail(),
                    ride.getOriginName() + " → " + ride.getDestinationName());
        }

        return mapToResponse(saved);
    }

    @Transactional
    public RideStatusResponse updateRideStatus(UUID rideId, RideUpdateStatusRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Authorization: Ken l chauffeur ynajjem ybeddel statu t3 ride
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this ride's status");
        }

        // Verifi transition t3 status
        validateStatusTransition(ride.getStatus(), request.getStatus());

        ride.setStatus(request.getStatus());
        Ride saved = rideRepository.save(ride); // Sajjel bel7a9!!

        // Ab3ath emails 7asb tabdil status
        if (request.getStatus() == RideStatus.IN_PROGRESS) {
            // Ride bdet - A3lem passengers confirmés lkol
            List<Booking> confirmedBookings = bookingRepository.findByRideId(rideId).stream()
                    .filter(b -> b.getStatus() == CONFIRMED)
                    .toList();

            for (Booking booking : confirmedBookings) {
                emailService.sendRideStartedEmail(
                        booking.getPassenger().getEmail(),
                        ride.getOriginName() + " → " + ride.getDestinationName(),
                        currentUser.getFirstName() + " " + currentUser.getLastName(),
                        currentUser.getPhoneNumber());
            }
        } else if (request.getStatus() == COMPLETED) {
            // Ride kmelet - A3lem passengers lkol u otlob review
            List<Booking> allBookings = bookingRepository.findByRideId(rideId);

            for (Booking booking : allBookings) {
                emailService.sendRideCompletedEmail(
                        booking.getPassenger().getEmail(),
                        ride.getOriginName() + " → " + ride.getDestinationName(),
                        ride.getId().toString());
            }
        }

        return new RideStatusResponse(saved.getId(), saved.getStatus());
    }

    private void validateStatusTransition(RideStatus currentStatus, RideStatus newStatus) {
        // Transitions valides:
        // SCHEDULED -> IN_PROGRESS, CANCELLED
        // IN_PROGRESS -> COMPLETED, CANCELLED
        // FULL -> IN_PROGRESS, CANCELLED
        // COMPLETED -> (none, terminal)
        // CANCELLED -> (none, terminal)

        if (currentStatus == COMPLETED || currentStatus == RideStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot change status of a completed or cancelled ride");
        }

        if (currentStatus == RideStatus.SCHEDULED && newStatus == COMPLETED) {
            throw new IllegalArgumentException("Cannot complete a ride that hasn't started yet");
        }
    }

    @Transactional
    public RideStatusResponse startRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Authorization: Ken l chauffeur ynajjem ybda ride te3ou
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to start this ride");
        }

        // Validation: Tnajjem tabda ken rides SCHEDULED walla FULL
        if (ride.getStatus() != RideStatus.SCHEDULED && ride.getStatus() != RideStatus.FULL) {
            throw new IllegalArgumentException("Can only start a scheduled or full ride");
        }

        // Note: Chauffeur ynajjem ybda ride 7atta blech passengers confirmés
        // (wa9t depart wsel, passengers ynajjmou yjwu mb3d, walla tri7 wa7dou)

        ride.setStatus(RideStatus.IN_PROGRESS);
        Ride saved = rideRepository.save(ride);

        List<Booking> confirmedBookings = bookingRepository.findByRideIdAndStatus(rideId, CONFIRMED);
        for (Booking booking : confirmedBookings) {
            notificationService.sendNotification(
                    booking.getPassenger().getId(),
                    NotificationType.RIDE_STARTED,
                    "Trajet démarré",
                    "Le trajet " + saved.getOriginName() + " → " + saved.getDestinationName() + " a commencé",
                    "/rides/" + rideId);

            // Ab3ath email kima fi updateRideStatus
            emailService.sendRideStartedEmail(
                    booking.getPassenger().getEmail(),
                    ride.getOriginName() + " → " + ride.getDestinationName(),
                    currentUser.getFirstName() + " " + currentUser.getLastName(),
                    currentUser.getPhoneNumber());
        }

        return new RideStatusResponse(saved.getId(), saved.getStatus());
    }

    @Transactional
    public RideStatusResponse completeRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Authorization: Ken l chauffeur ynajjem ykammel ride te3ou
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to complete this ride");
        }

        // Validation: Tnajjem tkammel ken rides IN_PROGRESS
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Can only complete a ride that is in progress");
        }

        ride.setStatus(COMPLETED);
        Ride saved = rideRepository.save(ride);
        // A3lem passengers confirmés lkol elli ride kmelet
        List<Booking> confirmedBookings = bookingRepository.findByRideIdAndStatus(rideId, CONFIRMED);
        for (Booking booking : confirmedBookings) {
            notificationService.sendNotification(
                    booking.getPassenger().getId(),
                    NotificationType.RIDE_COMPLETED,
                    "Trajet terminé",
                    "Le trajet " + saved.getOriginName() + " → " + saved.getDestinationName()
                            + " est terminé. N'oubliez pas de laisser un avis !",
                    "/rides/" + rideId);

            // Ab3ath email kima fi updateRideStatus
            emailService.sendRideCompletedEmail(
                    booking.getPassenger().getEmail(),
                    ride.getOriginName() + " → " + ride.getDestinationName(),
                    ride.getId().toString());
        }

        // Mb3d completion, bookings lkol ywaliu eligible lel reviews
        // (ReviewService déjà yverifi ride.status == COMPLETED 9bal ma ykhalik ta3mel
        // review)

        return new RideStatusResponse(saved.getId(), saved.getStatus());
    }

    @Transactional
    public RideResponse updateRide(java.util.UUID rideId, RideRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Security Check
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this ride");
        }

        // Logic Check
        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new RuntimeException("Only scheduled rides can be edited");
        }

        // Verifi bookings mawjoudin - emna3 l update kene famma passengers booked
        boolean hasBookings = !bookingRepository.findByRideIdAndStatus(rideId, CONFIRMED).isEmpty();
        if (hasBookings) {
            throw new RuntimeException(
                    "Cannot modify a ride that already has passengers booked. Please cancel the ride instead.");
        }

        // Beddel champs (Esma7 ken b tabdil champs li maykassrouch logic s7i7a)
        // Note: Tabdil Origin/Dest ynajjem yest7aq 7sebet point jdida, aka 3lech
        // nbedlouhom zeda.
        Point origin = geometryFactory.createPoint(new Coordinate(request.originLon(), request.originLat()));
        Point destination = geometryFactory
                .createPoint(new Coordinate(request.destinationLon(), request.destinationLat()));

        ride.setOriginName(request.originName());
        ride.setOriginPoint(origin);
        ride.setDestinationName(request.destinationName());
        ride.setDestinationPoint(destination);
        ride.setDepartureTime(request.departureTime());
        ride.setPricePerSeat(request.pricePerSeat());
        ride.setAllowsSmoking(request.allowsSmoking());
        ride.setAllowsMusic(request.allowsMusic());
        ride.setAllowsPets(request.allowsPets());
        ride.setLuggageSize(request.luggageSize());
        ride.setGenderPreference(request.genderPreference());

        // Gerr l blays: Matn7ich blays a9al mli deja mawjoudin
        // (Logic avancée l Phase 5)
        // Tawwa, nbedlou total seats kahaw.
        ride.setTotalSeats(request.totalSeats());
        // Logic simple: Reset available seats 7asb total jdid (naseb 0 bookings taw)
        // Fi Phase 5, ligne hedhi lezm tkoun:
        // ride.setAvailableSeats(request.totalSeats() - currentBookingsCount);
        ride.setAvailableSeats(request.totalSeats());

        return mapToResponse(rideRepository.save(ride));
    }
}
