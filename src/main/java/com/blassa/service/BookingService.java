package com.blassa.service;

import com.blassa.dto.BookingRequest;
import com.blassa.dto.BookingResponse;
import com.blassa.model.entity.Booking;
import com.blassa.model.entity.Ride;
import com.blassa.model.entity.User;
import com.blassa.model.enums.BookingStatus;
import com.blassa.model.enums.NotificationType;
import com.blassa.model.enums.RideStatus;
import com.blassa.repository.BookingRepository;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.blassa.dto.RidePassengerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.blassa.model.enums.Gender.FEMALE;
import static com.blassa.model.enums.Gender.MALE;
import static com.blassa.model.enums.RideGenderPreference.FEMALE_ONLY;
import static com.blassa.model.enums.RideGenderPreference.MALE_ONLY;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final VehicleService vehicleService;

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        User passenger = getCurrentUser();
        Ride ride = rideRepository.findById(bookingRequest.rideId())
                .orElseThrow(() -> new IllegalArgumentException("RIDE_NOT_FOUND"));

        // Check for existing booking (active or cancelled)
        Optional<Booking> existingBooking = bookingRepository.findByRideIdAndPassengerId(ride.getId(),
                passenger.getId());

        // Validate all business rules
        validateBookingRules(ride, passenger, bookingRequest.seatsRequested(), existingBooking);

        // Update seats
        int newAvailableSeats = ride.getAvailableSeats() - bookingRequest.seatsRequested();
        ride.setAvailableSeats(newAvailableSeats);

        // Update status if full
        if (newAvailableSeats == 0) {
            ride.setStatus(RideStatus.FULL);
        }

        // Save Ride (triggers optimistic lock check)
        rideRepository.save(ride);

        // Calculate total price
        BigDecimal totalPrice = ride.getPricePerSeat().multiply(BigDecimal.valueOf(bookingRequest.seatsRequested()));

        // Create or Revive Booking
        Booking booking = existingBooking.orElse(new Booking());
        if (booking.getId() == null) {
            booking.setRide(ride);
            booking.setPassenger(passenger);
        }

        booking.setSeatsBooked(bookingRequest.seatsRequested());
        booking.setPriceTotal(totalPrice);
        booking.setStatus(BookingStatus.PENDING); // Driver must accept

        // Save Booking
        Booking savedBooking = bookingRepository.save(booking);

        // Notify driver of new booking request
        notificationService.sendNotification(ride.getDriver().getId(), NotificationType.NEW_BOOKING,
                "Demande de réservation",
                "Nouvelle demande de réservation de " + passenger.getFirstName() + " " + passenger.getLastName(),
                "/rides/" + ride.getId());
        emailService.sendNewPassengerEmail(
                ride.getDriver().getEmail(),
                passenger.getFirstName() + " " + passenger.getLastName(),
                ride.getOriginName() + " → " + ride.getDestinationName());
        return mapToResponse(savedBooking);
    }

    /**
     * Driver accepts a pending booking.
     */
    @Transactional
    public BookingResponse acceptBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        User currentUser = getCurrentUser();
        // Only the driver can accept
        if (!booking.getRide().getDriver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("BOOKING_NOT_PENDING");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        notificationService.sendNotification(
                booking.getPassenger().getId(),
                NotificationType.BOOKING_ACCEPTED,
                "Réservation acceptée",
                "Votre réservation pour " + saved.getRide().getOriginName() + "→" + saved.getRide().getDestinationName()
                        + " a été confirmée",
                "/rides/" + booking.getRide().getId());
        return mapToResponse(saved);
    }

    /**
     * Driver rejects a pending booking.
     * Seats are restored to the ride.
     */
    @Transactional
    public void rejectBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        User currentUser = getCurrentUser();
        // Only the driver can reject
        if (!booking.getRide().getDriver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("BOOKING_NOT_PENDING");
        }

        // Restore seats to ride
        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }
        rideRepository.save(ride);

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        notificationService.sendNotification(
                booking.getPassenger().getId(),
                NotificationType.BOOKING_REJECTED,
                "Réservation refusée",
                "Votre demande pour " + ride.getOriginName() + "→" + ride.getDestinationName() + " a été refusée",
                null);

    }

    // Map l response, kont najm nhotha fi DTO dossier ama mch lezm
    private BookingResponse mapToResponse(Booking booking) {
        String carLicensePlate = null;
        String carDescription = null;

        if (booking.getRide().getVehicle() != null) {
            com.blassa.model.entity.Vehicle v = booking.getRide().getVehicle();
            carDescription = v.getMake() + " " + v.getModel() + " (" + v.getColor() + ")";

            boolean showFullPlate = false;
            // Rule 1: Ride is active/completed
            if (booking.getRide().getStatus() == RideStatus.IN_PROGRESS
                    || booking.getRide().getStatus() == RideStatus.COMPLETED) {
                showFullPlate = true;
            }
            // Rule 2: T-60 Minutes
            else if (booking.getRide().getDepartureTime().minusMinutes(60).isBefore(java.time.OffsetDateTime.now())) {
                showFullPlate = true;
            }

            if (showFullPlate) {
                carLicensePlate = v.getLicensePlate();
            } else if (booking.getStatus() == BookingStatus.CONFIRMED) {
                carLicensePlate = vehicleService.getMaskedPlate(v.getLicensePlate());
            }
        }

        return new BookingResponse(
                booking.getId(),
                booking.getRide().getId(),
                booking.getRide().getOriginName() + " -> " + booking.getRide().getDestinationName(),
                booking.getRide().getDriver().getFirstName() + " " + booking.getRide().getDriver().getLastName(),
                booking.getRide().getDepartureTime(),
                booking.getSeatsBooked(),
                booking.getPriceTotal(),
                booking.getStatus(),
                booking.getRide().getStatus(),
                booking.getCreatedAt(),
                carLicensePlate,
                carDescription);
    }

    @Transactional
    public Page<BookingResponse> getMyBookings(int page, int size) {
        User passenger = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookings = bookingRepository.findByPassengerId(passenger.getId(), pageable);
        return bookings.map(this::mapToResponse);
    }

    /**
     * Get current user's booking for a specific ride.
     * Used for the review page to find the bookingId.
     */
    @Transactional(readOnly = true)
    public BookingResponse getMyBookingForRide(UUID rideId) {
        User currentUser = getCurrentUser();
        Booking booking = bookingRepository.findByRideIdAndPassengerId(rideId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));
        return mapToResponse(booking);
    }

    /**
     * Get all ride IDs that the current user has booked (excluding cancelled
     * bookings).
     * Used by frontend to disable "Réserver" button for already-booked rides.
     */
    @Transactional(readOnly = true)
    public java.util.List<UUID> getMyBookedRideIds() {
        User passenger = getCurrentUser();
        return bookingRepository.findRideIdsByPassengerIdAndStatusNot(passenger.getId(), BookingStatus.CANCELLED);
    }

    /**
     * Get all pending and confirmed passengers for a specific ride.
     * Only the driver of the ride can access this.
     */
    @Transactional(readOnly = true)
    public List<RidePassengerResponse> getPassengersForRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("RIDE_NOT_FOUND"));

        User currentUser = getCurrentUser();
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED");
        }

        // Return PENDING and CONFIRMED bookings for driver to manage
        java.util.List<Booking> bookings = bookingRepository.findByRideId(rideId);
        return bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.CONFIRMED)
                .map(b -> new com.blassa.dto.RidePassengerResponse(
                        b.getId(),
                        b.getPassenger().getId(),
                        b.getPassenger().getFirstName() + " " + b.getPassenger().getLastName(),
                        b.getPassenger().getEmail(),
                        b.getPassenger().getPhoneNumber(),
                        b.getPassenger().getProfilePictureUrl(),
                        b.getPassenger().getFacebookUrl(),
                        b.getPassenger().getInstagramUrl(),
                        b.getSeatsBooked(),
                        b.getStatus()))
                .toList();
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        User currentUser = getCurrentUser();
        // only current usr can cancel el booking
        if (!booking.getPassenger().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED_TO_CANCEL");
        }
        // checki ken cancelled wla le
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("ALREADY_CANCELLED");
        }

        // Cannot cancel if ride is already in progress or completed
        Ride ride = booking.getRide();
        if (ride.getStatus() == RideStatus.IN_PROGRESS || ride.getStatus() == RideStatus.COMPLETED) {
            throw new IllegalArgumentException("CANNOT_CANCEL_ACTIVE_RIDE");
        }

        // number t3 blays lezm yarj3o kif makeno 9bal booking
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        // raj3ha l scheduled ken kent m3abya
        // If ride was FULL, set back to SCHEDULED
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }

        rideRepository.save(ride);

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Notify driver that passenger cancelled
        notificationService.sendNotification(
                ride.getDriver().getId(),
                NotificationType.PASSENGER_CANCELLED,
                "Réservation annulée",
                currentUser.getFirstName() + " " + currentUser.getLastName() + " a annulé sa réservation pour "
                        + ride.getOriginName() + " → " + ride.getDestinationName(),
                "/rides/" + ride.getId());
    }

    /**
     * Cancel current user's booking for a specific ride.
     * Used by passengers to cancel from ride details page.
     */
    @Transactional
    public void cancelBookingByRide(UUID rideId) {
        User currentUser = getCurrentUser();

        // Find the user's active booking for this ride
        Booking booking = bookingRepository.findByRideIdAndPassengerId(rideId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("ALREADY_CANCELLED");
        }

        // Restore available seats
        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }
        rideRepository.save(ride);

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Notify driver that passenger cancelled
        notificationService.sendNotification(
                ride.getDriver().getId(),
                NotificationType.PASSENGER_CANCELLED,
                "Réservation annulée",
                currentUser.getFirstName() + " " + currentUser.getLastName() + " a annulé sa réservation pour "
                        + ride.getOriginName() + " → " + ride.getDestinationName(),
                "/rides/" + ride.getId());
    }

    private void validateBookingRules(Ride ride, User passenger, Integer requestedSeats,
            Optional<Booking> existingBooking) {
        // Self-booking check
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new IllegalArgumentException("DRIVER_CANNOT_BOOK");
        }

        // Ride Status check, reservi ken scheduled rides
        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new IllegalArgumentException("RIDE_NOT_BOOKABLE");
        }

        // Departure Time check, reservi ken rides fi mosta9bl
        if (ride.getDepartureTime().isBefore(java.time.OffsetDateTime.now())) {
            throw new IllegalArgumentException("RIDE_ALREADY_DEPARTED");
        }

        // Seat Availability check, verify number t3 seats
        if (ride.getAvailableSeats() < requestedSeats) {
            throw new IllegalArgumentException("NOT_ENOUGH_SEATS");
        }

        // Gender Preference check
        // If ride is FEMALE_ONLY, passenger must be FEMALE, w 3aks bel 3aks
        if (ride.getGenderPreference() == FEMALE_ONLY
                && passenger.getGender() != FEMALE) {
            throw new IllegalArgumentException("GENDER_NOT_ALLOWED");
        }
        // If ride is MALE_ONLY, passenger must be MALE
        if (ride.getGenderPreference() == MALE_ONLY
                && passenger.getGender() != MALE) {
            throw new IllegalArgumentException("GENDER_NOT_ALLOWED");
        }

        // Duplicate Booking check // maynjmch ireservi nafs ride martin
        // Only block if an active booking exists
        if (existingBooking.isPresent() && existingBooking.get().getStatus() != BookingStatus.CANCELLED) {
            throw new IllegalStateException("PASSENGER_ALREADY_BOOKED");
        }
    }

    // nraj3o fi current user
    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
