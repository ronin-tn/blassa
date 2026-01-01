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

        // Shoufkene famma booking déjà mawjouda (active walla cancelled)
        Optional<Booking> existingBooking = bookingRepository.findByRideIdAndPassengerId(ride.getId(),
                passenger.getId());

        // Verifi les règles métier lkol
        validateBookingRules(ride, passenger, bookingRequest.seatsRequested(), existingBooking);

        // Beddel 3adad l blays
        int newAvailableSeats = ride.getAvailableSeats() - bookingRequest.seatsRequested();
        ride.setAvailableSeats(newAvailableSeats);

        // Beddel statu kene l ride t3abbat full
        if (newAvailableSeats == 0) {
            ride.setStatus(RideStatus.FULL);
        }

        // Sajjel l Ride (yb déclanchi optimistic lock check)
        rideRepository.save(ride);

        // A7seb soum lkol
        BigDecimal totalPrice = ride.getPricePerSeat().multiply(BigDecimal.valueOf(bookingRequest.seatsRequested()));

        // Asna3 walla rajja3 booking
        Booking booking = existingBooking.orElse(new Booking());
        if (booking.getId() == null) {
            booking.setRide(ride);
            booking.setPassenger(passenger);
        }

        booking.setSeatsBooked(bookingRequest.seatsRequested());
        booking.setPriceTotal(totalPrice);
        booking.setStatus(BookingStatus.PENDING); // L chauffeur lezm ya9bel

        // Sajjel l booking
        Booking savedBooking = bookingRepository.save(booking);

        // A3lem l chauffeur bel demande jdida
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
     * L chauffeur ya9bel booking pending.
     */
    @Transactional
    public BookingResponse acceptBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        User currentUser = getCurrentUser();
        // Ken l chauffeur ynajjem ya9bel
        if (!booking.getRide().getDriver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("BOOKING_NOT_PENDING");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        // Ab3ath notification
        notificationService.sendNotification(
                booking.getPassenger().getId(),
                NotificationType.BOOKING_ACCEPTED,
                "Réservation acceptée",
                "Votre réservation pour " + saved.getRide().getOriginName() + "→" + saved.getRide().getDestinationName()
                        + " a été confirmée",
                "/rides/" + booking.getRide().getId());

        // Ab3ath email
        emailService.sendBookingAcceptedEmail(
                booking.getPassenger().getEmail(),
                saved.getRide().getOriginName() + " → " + saved.getRide().getDestinationName(),
                saved.getRide().getId().toString());

        return mapToResponse(saved);
    }

    /**
     * L chauffeur yrfodh booking pending.
     * L blays yarj3u lel ride.
     */
    @Transactional
    public void rejectBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        User currentUser = getCurrentUser();
        // Ken l chauffeur ynajjem yrfodh
        if (!booking.getRide().getDriver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("BOOKING_NOT_PENDING");
        }

        // Rajja3 blays lel ride
        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }
        rideRepository.save(ride);

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        // Ab3ath notification
        notificationService.sendNotification(
                booking.getPassenger().getId(),
                NotificationType.BOOKING_REJECTED,
                "Réservation refusée",
                "Votre demande pour " + ride.getOriginName() + "→" + ride.getDestinationName() + " a été refusée",
                null);

        // Ab3ath email
        emailService.sendBookingRejectedEmail(
                booking.getPassenger().getEmail(),
                ride.getOriginName() + " → " + ride.getDestinationName());
    }

    // Map l response, kont najm nhotha fi DTO dossier ama mch lezm
    private BookingResponse mapToResponse(Booking booking) {
        String carLicensePlate = null;
        String carDescription = null;

        if (booking.getRide().getVehicle() != null) {
            com.blassa.model.entity.Vehicle v = booking.getRide().getVehicle();
            carDescription = v.getMake() + " " + v.getModel() + " (" + v.getColor() + ")";

            boolean showFullPlate = false;
            // Règle 1: Ride active walla completed
            if (booking.getRide().getStatus() == RideStatus.IN_PROGRESS
                    || booking.getRide().getStatus() == RideStatus.COMPLETED) {
                showFullPlate = true;
            }
            // Règle 2: 9bal b 60 d9i9a (T-60mn)
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
     * Mosta3mla lel review page bch nl9aw bookingId.
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
     * Mosta3mla mel frontend bch ydésactivi button "Réserver" lel rides li deja
     * booked.
     */
    @Transactional(readOnly = true)
    public java.util.List<UUID> getMyBookedRideIds() {
        User passenger = getCurrentUser();
        return bookingRepository.findRideIdsByPassengerIdAndStatusNot(passenger.getId(), BookingStatus.CANCELLED);
    }

    /**
     * Get all pending and confirmed passengers for a specific ride.
     * Ken l chauffeur t3 l ride ynajjem ychouf hadha.
     */
    @Transactional(readOnly = true)
    public List<RidePassengerResponse> getPassengersForRide(UUID rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("RIDE_NOT_FOUND"));

        User currentUser = getCurrentUser();
        if (!ride.getDriver().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED");
        }

        // Rajja3 l PENDING u l CONFIRMED bookings bch l chauffeur yetlahha bihom
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
        // ken l current user ynajjem yannuli el booking
        if (!booking.getPassenger().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED_TO_CANCEL");
        }
        // Verifi kene cancelled wla le
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("ALREADY_CANCELLED");
        }

        // Maynjmch yannuli ken l ride bdet walla kmelet
        Ride ride = booking.getRide();
        if (ride.getStatus() == RideStatus.IN_PROGRESS || ride.getStatus() == RideStatus.COMPLETED) {
            throw new IllegalArgumentException("CANNOT_CANCEL_ACTIVE_RIDE");
        }

        // 3adad l blays lezm yarj3ou kima kenou 9bal l booking
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        // raj3ha l scheduled ken kent m3abya
        // Kene l ride kent FULL, rajja3ha SCHEDULED
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }

        rideRepository.save(ride);

        // Beddel statu t3 booking
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // A3lem l chauffeur elli l passager annula
        notificationService.sendNotification(
                ride.getDriver().getId(),
                NotificationType.PASSENGER_CANCELLED,
                "Réservation annulée",
                currentUser.getFirstName() + " " + currentUser.getLastName() + " a annulé sa réservation pour "
                        + ride.getOriginName() + " → " + ride.getDestinationName(),
                "/rides/" + ride.getId());

        emailService.sendBookingCancelledByPassengerEmail(
                ride.getDriver().getEmail(),
                currentUser.getFirstName() + " " + currentUser.getLastName(),
                ride.getOriginName() + " → " + ride.getDestinationName(),
                ride.getId().toString());
    }

    /**
     * Cancel current user's booking for a specific ride.
     * Mosta3mla mel passengers bch yannuliw men ride details page.
     */
    @Transactional
    public void cancelBookingByRide(UUID rideId) {
        User currentUser = getCurrentUser();

        // Lawej 3ala booking active t3 l user fel ride hedhi
        Booking booking = bookingRepository.findByRideIdAndPassengerId(rideId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("ALREADY_CANCELLED");
        }

        // Rajja3 l blays l dispo
        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }
        rideRepository.save(ride);

        // Beddel statu t3 booking
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // A3lem l chauffeur elli l passager annula
        notificationService.sendNotification(
                ride.getDriver().getId(),
                NotificationType.PASSENGER_CANCELLED,
                "Réservation annulée",
                currentUser.getFirstName() + " " + currentUser.getLastName() + " a annulé sa réservation pour "
                        + ride.getOriginName() + " → " + ride.getDestinationName(),
                "/rides/" + ride.getId());

        emailService.sendBookingCancelledByPassengerEmail(
                ride.getDriver().getEmail(),
                currentUser.getFirstName() + " " + currentUser.getLastName(),
                ride.getOriginName() + " → " + ride.getDestinationName(),
                ride.getId().toString());
    }

    private void validateBookingRules(Ride ride, User passenger, Integer requestedSeats,
            Optional<Booking> existingBooking) {
        // Verifi kene l chauffeur 7ajez l rou7ou
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new IllegalArgumentException("DRIVER_CANNOT_BOOK");
        }

        // Verifi statu t3 ride, reservi ken rides scheduled
        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new IllegalArgumentException("RIDE_NOT_BOOKABLE");
        }

        // Verifi wa9t depart, reservi ken rides fl mosta9bel
        if (ride.getDepartureTime().isBefore(java.time.OffsetDateTime.now())) {
            throw new IllegalArgumentException("RIDE_ALREADY_DEPARTED");
        }

        // Verifi dispo t3 blays, shouf 3adad l seats
        if (ride.getAvailableSeats() < requestedSeats) {
            throw new IllegalArgumentException("NOT_ENOUGH_SEATS");
        }

        // Verifi preference t3 genre
        // Kene l ride FEMALE_ONLY, l passager lezm tkoun mra (FEMALE), u l 3aks bel
        // 3aks
        if (ride.getGenderPreference() == FEMALE_ONLY
                && passenger.getGender() != FEMALE) {
            throw new IllegalArgumentException("GENDER_NOT_ALLOWED");
        }
        // Kene l ride MALE_ONLY, l passager lezm ykoun rajel (MALE)
        if (ride.getGenderPreference() == MALE_ONLY
                && passenger.getGender() != MALE) {
            throw new IllegalArgumentException("GENDER_NOT_ALLOWED");
        }

        // Verifi booking double // maynjmch ireservi nafs ride marrtin
        // Bloki ken famma booking active
        if (existingBooking.isPresent() && existingBooking.get().getStatus() != BookingStatus.CANCELLED) {
            throw new IllegalStateException("PASSENGER_ALREADY_BOOKED");
        }
    }

    // njibu f current user
    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
