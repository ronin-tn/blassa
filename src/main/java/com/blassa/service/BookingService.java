package com.blassa.service;

import com.blassa.dto.BookingRequest;
import com.blassa.dto.BookingResponse;
import com.blassa.model.entity.Booking;
import com.blassa.model.entity.Ride;
import com.blassa.model.entity.User;
import com.blassa.model.enums.BookingStatus;
import com.blassa.model.enums.RideStatus;
import com.blassa.repository.BookingRepository;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        User passenger = getCurrentUser();
        Ride ride = rideRepository.findById(bookingRequest.rideId())
                .orElseThrow(() -> new IllegalArgumentException("RIDE_NOT_FOUND"));

        // Validate all business rules
        validateBookingRules(ride, passenger, bookingRequest.seatsRequested());

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

        // Create Booking
        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeatsBooked(bookingRequest.seatsRequested());
        booking.setPriceTotal(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);

        // Save Booking
        Booking savedBooking = bookingRepository.save(booking);

        emailService.sendNewPassengerEmail(ride.getDriver().getEmail(), passenger.getEmail());
        return mapToResponse(savedBooking);
    }
    //Map l response, kont najm nhotha fi DTO dossier ama mch lezm
    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRide().getId(),
                booking.getRide().getOriginName() + " -> " + booking.getRide().getDestinationName(),
                booking.getRide().getDriver().getFirstName() + " " + booking.getRide().getDriver().getLastName(),
                booking.getRide().getDepartureTime(),
                booking.getSeatsBooked(),
                booking.getPriceTotal(),
                booking.getStatus(),
                booking.getCreatedAt());
    }
    @Transactional
    public Page<BookingResponse> getMyBookings(int page, int size) {
        User passenger = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Booking> bookings= bookingRepository.findByPassengerId(passenger.getId(), pageable);
        return bookings.map(this::mapToResponse);
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("BOOKING_NOT_FOUND"));

        User currentUser = getCurrentUser();
        //only current usr can cancel el booking
        if (!booking.getPassenger().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("NOT_AUTHORIZED_TO_CANCEL");
        }
        //checki ken cancelled wla le
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("ALREADY_CANCELLED");
        }

        // number t3 blays lezm yarj3o kif makeno 9bal booking
        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        // raj3ha l scheduled ken kent m3abya
        // If ride was FULL, set back to SCHEDULED
        if (ride.getStatus() == RideStatus.FULL) {
            ride.setStatus(RideStatus.SCHEDULED);
        }

        rideRepository.save(ride);

        //Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private void validateBookingRules(Ride ride, User passenger, Integer requestedSeats) {
        //Self-booking check
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new IllegalArgumentException("DRIVER_CANNOT_BOOK");
        }

        //Ride Status check, reservi ken scheduled rides
        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new IllegalArgumentException("RIDE_NOT_BOOKABLE");
        }

        //Departure Time check, reservi ken rides fi mosta9bl
        if (ride.getDepartureTime().isBefore(java.time.OffsetDateTime.now())) {
            throw new IllegalArgumentException("RIDE_ALREADY_DEPARTED");
        }

        //Seat Availability check, verify number t3 seats
        if (ride.getAvailableSeats() < requestedSeats) {
            throw new IllegalArgumentException("NOT_ENOUGH_SEATS");
        }

        //Gender Preference check
        //If ride is FEMALE_ONLY, passenger must be FEMALE, w 3aks bel 3aks
        if (ride.getGenderPreference() == FEMALE_ONLY
                && passenger.getGender() != FEMALE) {
            throw new IllegalArgumentException("GENDER_NOT_ALLOWED");
        }
        //If ride is MALE_ONLY, passenger must be MALE
        if (ride.getGenderPreference() == MALE_ONLY
                && passenger.getGender() != MALE) {
            throw new IllegalArgumentException("GENDER_NOT_ALLOWED");
        }

        //Duplicate Booking check // maynjmch ireservi nafs ride martin
        if (bookingRepository.existsByRideIdAndPassengerId(ride.getId(), passenger.getId())) {
            // Ttnjm tkon throwi bel DataIntegrityViolationException, but good to
            // check explicitly
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
