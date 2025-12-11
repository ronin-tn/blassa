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

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public RideResponse createRide(RideRequest rideRequest) {
        User driver = getCurrentUser();

        // Validate gender preference consistency (Fix #6)
        validateGenderPreference(driver, rideRequest.genderPreference());

        Point origin = geometryFactory.createPoint(new Coordinate(rideRequest.originLon(), rideRequest.originLat()));
        Point destination = geometryFactory
                .createPoint(new Coordinate(rideRequest.destinationLon(), rideRequest.destinationLat()));

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setOriginName(rideRequest.originName());
        ride.setOriginPoint(origin);
        ride.setDestinationName(rideRequest.destinationName());
        ride.setDestinationPoint(destination);
        ride.setDepartureTime(rideRequest.departureTime());
        ride.setTotalSeats(rideRequest.totalSeats());
        ride.setAvailableSeats(rideRequest.totalSeats());
        ride.setPricePerSeat(rideRequest.pricePerSeat());
        ride.setAllowsSmoking(rideRequest.allowsSmoking());
        ride.setGenderPreference(rideRequest.genderPreference());
        ride.setStatus(RideStatus.SCHEDULED);
        Ride saved = rideRepository.save(ride);
        return mapToResponse(saved);
    }

    private RideResponse mapToResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName(),
                ride.getDriver().getEmail(),
                0.0, // Placeholder for rating calculation
                ride.getDriver().getFacebookUrl(),
                ride.getDriver().getInstagramUrl(),
                ride.getDriver().getPhoneNumber(),
                ride.getOriginName(),
                ride.getOriginPoint().getY(), // Latitude
                ride.getOriginPoint().getX(), // Longitude
                ride.getDestinationName(),
                ride.getDestinationPoint().getY(),
                ride.getDestinationPoint().getX(),
                ride.getDepartureTime(),
                ride.getTotalSeats(),
                ride.getAvailableSeats(),
                ride.getPricePerSeat(),
                ride.getAllowsSmoking(),
                ride.getGenderPreference(),
                ride.getStatus());
    }

    /**
     * Public search endpoint - works for both authenticated and anonymous users.
     * 
     * For anonymous users: Shows ALL rides by default, or filtered if genderFilter
     * is provided.
     * For authenticated users: Shows rides matching their gender preference.
     * 
     * @param genderFilter Optional gender filter (\"FEMALE_ONLY\" to show
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
            int size) {
        // Determine allowed preferences based on auth status and filter
        List<String> allowedPreferences;

        User currentUser = getCurrentUserOrNull();
        if (currentUser != null) {
            // Authenticated: Filter by user's gender
            allowedPreferences = (currentUser.getGender() == Gender.MALE)
                    ? List.of("ANY", "MALE_ONLY")
                    : List.of("ANY", "FEMALE_ONLY");
        } else if (genderFilter != null && !genderFilter.isEmpty() && !genderFilter.equals("ANY")) {
            // Anonymous with gender filter: Show ANY + matching gender-specific rides
            if (genderFilter.equals("FEMALE_ONLY")) {
                allowedPreferences = List.of("ANY", "FEMALE_ONLY");
            } else if (genderFilter.equals("MALE_ONLY")) {
                allowedPreferences = List.of("ANY", "MALE_ONLY");
            } else {
                allowedPreferences = List.of("ANY", "MALE_ONLY", "FEMALE_ONLY");
            }
        } else {
            // Anonymous without filter: Show ALL rides
            allowedPreferences = List.of("ANY", "MALE_ONLY", "FEMALE_ONLY");
        }

        // 2. Geometry
        Point origin = geometryFactory.createPoint(new Coordinate(originLon, originLat));
        Point destination = geometryFactory.createPoint(new Coordinate(destLon, destLat));

        // 3. Time Window
        // 3. Time Window
        OffsetDateTime start;
        OffsetDateTime end;

        if (departureTime != null) {
            start = departureTime.minusHours(2);
            end = departureTime.plusHours(2);
        } else {
            // Optional Date: Search for ALL future rides (from Now until next year)
            start = OffsetDateTime.now();
            end = OffsetDateTime.now().plusYears(1);
        }

        // 4. Radius Logic (Convert Km to Meters)
        double pickupRadiusMeters = (radiusKm != null) ? radiusKm * 1000 : 3000.0;
        double dropoffRadiusMeters = 5000.0;

        // 5. Pagination & Sorting Setup
        // For native queries, we must sort by DB column name (snake_case)
        Pageable pageable = PageRequest.of(page, size, Sort.by("departure_time").ascending());

        // 6. Execute
        Page<Ride> ridePage = rideRepository.searchRides(
                origin, pickupRadiusMeters,
                destination, dropoffRadiusMeters,
                start, end,
                seats,
                allowedPreferences,
                pageable);

        // 7. Map Entity Page -> DTO Page
        return ridePage.map(this::mapToResponse);
    }

    /**
     * Get current user or null if not authenticated (for public endpoints)
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
            return; // Default to ANY is fine

        if (driver.getGender() == Gender.MALE && preference == RideGenderPreference.FEMALE_ONLY) {
            throw new IllegalArgumentException("Male drivers cannot create FEMALE_ONLY rides");
        }
        if (driver.getGender() == Gender.FEMALE && preference == RideGenderPreference.MALE_ONLY) {
            throw new IllegalArgumentException("Female drivers cannot create MALE_ONLY rides");
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

        // Security Check: Is the caller the owner?
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to cancel this ride");
        }

        // Logic Check: Is the ride already happened?
        if (ride.getStatus() == COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel a ride that is already completed or cancelled");
        }

        // Cancel all bookings associated with this ride
        List<Booking> bookings = bookingRepository.findByRideId(rideId);
        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                booking.setStatus(BookingStatus.CANCELLED);
            }
        }
        bookingRepository.saveAll(bookings);
        ride.setStatus(RideStatus.CANCELLED);
        Ride saved = rideRepository.save(ride);

        // Notify all passengers that ride was cancelled
        for (Booking booking : bookings) {
            notificationService.sendNotification(
                    booking.getPassenger().getId(),
                    NotificationType.RIDE_CANCELLED,
                    "Trajet annulé",
                    "Le trajet " + ride.getOriginName() + " → " + ride.getDestinationName()
                            + " a été annulé par le conducteur",
                    null);
        }

        return mapToResponse(saved);
    }

    @Transactional
    public RideStatusResponse updateRideStatus(UUID rideId, RideUpdateStatusRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Authorization: Only driver can update their ride status
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this ride's status");
        }

        // Validate status transition
        validateStatusTransition(ride.getStatus(), request.getStatus());

        ride.setStatus(request.getStatus());
        Ride saved = rideRepository.save(ride); // Actually persist!

        return new RideStatusResponse(saved.getId(), saved.getStatus());
    }

    private void validateStatusTransition(RideStatus currentStatus, RideStatus newStatus) {
        // Valid transitions:
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

        // Authorization: Only driver can start their ride
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to start this ride");
        }

        // Validation: Can only start SCHEDULED or FULL rides
        if (ride.getStatus() != RideStatus.SCHEDULED && ride.getStatus() != RideStatus.FULL) {
            throw new IllegalArgumentException("Can only start a scheduled or full ride");
        }

        // Note: Driver can start a ride even without confirmed passengers
        // (e.g., departure time arrived, passengers may join later, or solo trip)

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
        }

        return new RideStatusResponse(saved.getId(), saved.getStatus());
    }

    @Transactional
    public RideStatusResponse completeRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User currentUser = getCurrentUser();

        // Authorization: Only driver can complete their ride
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to complete this ride");
        }

        // Validation: Can only complete IN_PROGRESS rides
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Can only complete a ride that is in progress");
        }

        ride.setStatus(COMPLETED);
        Ride saved = rideRepository.save(ride);
        // Notify all confirmed passengers that ride is completed
        List<Booking> confirmedBookings = bookingRepository.findByRideIdAndStatus(rideId, CONFIRMED);
        for (Booking booking : confirmedBookings) {
            notificationService.sendNotification(
                    booking.getPassenger().getId(),
                    NotificationType.RIDE_COMPLETED,
                    "Trajet terminé",
                    "Le trajet " + saved.getOriginName() + " → " + saved.getDestinationName()
                            + " est terminé. N'oubliez pas de laisser un avis !",
                    "/rides/" + rideId + "/review");
        }

        // After completion, all bookings for this ride become eligible for reviews
        // (ReviewService already checks ride.status == COMPLETED before allowing
        // reviews)

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

        // Check for existing bookings - prevent update if passengers are booked
        boolean hasBookings = !bookingRepository.findByRideIdAndStatus(rideId, CONFIRMED).isEmpty();
        if (hasBookings) {
            throw new RuntimeException(
                    "Cannot modify a ride that already has passengers booked. Please cancel the ride instead.");
        }

        // Update Fields (Only allow updating fields that don't break strict logic)
        // Note: Changing Origin/Dest might require re-calculating points, so we update
        // those too.
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
        ride.setGenderPreference(request.genderPreference());

        // Handle Seats: Don't allow reducing seats below what is already booked
        // (Advanced logic for Phase 5)
        // For now, we just update total seats.
        ride.setTotalSeats(request.totalSeats());
        // Simple logic: Reset available seats based on new total (assuming 0 bookings
        // for now)
        // In Phase 5, this line will need to be:
        // ride.setAvailableSeats(request.totalSeats() - currentBookingsCount);
        ride.setAvailableSeats(request.totalSeats());

        return mapToResponse(rideRepository.save(ride));
    }
}
