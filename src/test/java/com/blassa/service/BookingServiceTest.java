package com.blassa.service;

import com.blassa.model.entity.Booking;
import com.blassa.model.entity.Ride;
import com.blassa.model.entity.User;
import com.blassa.model.enums.*;
import com.blassa.repository.BookingRepository;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private BookingService bookingService;

    private User driver;
    private User passenger;
    private Ride ride;

    @BeforeEach
    void setUp() {
        driver = User.builder()
                .id(UUID.randomUUID())
                .email("driver@blassa.tn")
                .firstName("Ahmed")
                .lastName("Driver")
                .gender(Gender.MALE)
                .build();

        passenger = User.builder()
                .id(UUID.randomUUID())
                .email("passenger@blassa.tn")
                .firstName("Sara")
                .lastName("Passenger")
                .gender(Gender.FEMALE)
                .build();

        ride = Ride.builder()
                .id(UUID.randomUUID())
                .driver(driver)
                .originName("Tunis")
                .destinationName("Sousse")
                .departureTime(OffsetDateTime.now().plusDays(1))
                .totalSeats(4)
                .availableSeats(3)
                .pricePerSeat(BigDecimal.valueOf(15))
                .status(RideStatus.SCHEDULED)
                .genderPreference(RideGenderPreference.ANY)
                .build();

        mockSecurityContext(passenger);
    }

    private void mockSecurityContext(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("password")
                .authorities("USER")
                .build();

        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext ctx = mock(SecurityContext.class);
        lenient().when(ctx.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(ctx);

        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void createBooking_shouldThrow_whenDriverTriesToBookOwnRide() {

        mockSecurityContext(driver);

        when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        when(bookingRepository.findByRideIdAndPassengerId(ride.getId(), driver.getId()))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(
                        new com.blassa.dto.BookingRequest(ride.getId(), 1)));

        assertEquals("DRIVER_CANNOT_BOOK", ex.getMessage());
    }

    @Test
    void createBooking_shouldThrow_whenNotEnoughSeats() {
        ride.setAvailableSeats(1);

        when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        when(bookingRepository.findByRideIdAndPassengerId(ride.getId(), passenger.getId()))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(
                        new com.blassa.dto.BookingRequest(ride.getId(), 3)));

        assertEquals("NOT_ENOUGH_SEATS", ex.getMessage());
    }

    @Test
    void createBooking_shouldThrow_whenRideIsNotScheduled() {
        ride.setStatus(RideStatus.COMPLETED);

        when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        when(bookingRepository.findByRideIdAndPassengerId(ride.getId(), passenger.getId()))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(
                        new com.blassa.dto.BookingRequest(ride.getId(), 1)));

        assertEquals("RIDE_NOT_BOOKABLE", ex.getMessage());
    }

    @Test
    void createBooking_shouldThrow_whenGenderNotAllowed() {
        ride.setGenderPreference(RideGenderPreference.MALE_ONLY);

        when(rideRepository.findById(ride.getId())).thenReturn(Optional.of(ride));
        when(bookingRepository.findByRideIdAndPassengerId(ride.getId(), passenger.getId()))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(
                        new com.blassa.dto.BookingRequest(ride.getId(), 1)));

        assertEquals("GENDER_NOT_ALLOWED", ex.getMessage());
    }

    @Test
    void cancelBooking_shouldThrow_whenNotOwner() {
        Booking booking = Booking.builder()
                .id(UUID.randomUUID())
                .ride(ride)
                .passenger(driver)
                .seatsBooked(1)
                .priceTotal(BigDecimal.valueOf(15))
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.cancelBooking(booking.getId()));

        assertEquals("NOT_AUTHORIZED_TO_CANCEL", ex.getMessage());
    }
}
