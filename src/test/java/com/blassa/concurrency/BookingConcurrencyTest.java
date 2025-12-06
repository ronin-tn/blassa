package com.blassa.concurrency;

import com.blassa.dto.BookingRequest;
import com.blassa.model.entity.Ride;
import com.blassa.model.entity.User;
import com.blassa.model.enums.Gender;
import com.blassa.model.enums.RideGenderPreference;
import com.blassa.model.enums.RideStatus;
import com.blassa.model.enums.UserRole;
import com.blassa.repository.RideRepository;
import com.blassa.repository.UserRepository;
import com.blassa.service.BookingService;
import org.junit.jupiter.api.Test; // ✅ JUnit 5
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private static final AtomicInteger phoneCounter = new AtomicInteger(20000000);

    @Test
    public void testDoubleBookingRaceCondition() throws InterruptedException {
        UUID rideId = setupRideWithOneSeat();

        User passenger1 = createDummyPassenger("p1@test.com");
        User passenger2 = createDummyPassenger("p2@test.com");

        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);

        // ✅ Track successes and failures
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Runnable task1 = () -> {
            try {
                mockSecurityContext(passenger1);
                latch.await();
                bookingService.createBooking(new BookingRequest(rideId, 1));
                successCount.incrementAndGet(); // ✅
            } catch (Exception e) {
                failureCount.incrementAndGet(); // ✅
                System.out.println("User 1 failed: " + e.getMessage());
            } finally {
                SecurityContextHolder.clearContext();
            }
        };

        Runnable task2 = () -> {
            try {
                mockSecurityContext(passenger2);
                latch.await();
                bookingService.createBooking(new BookingRequest(rideId, 1));
                successCount.incrementAndGet(); // ✅
            } catch (Exception e) {
                failureCount.incrementAndGet(); // ✅
                System.out.println("User 2 failed: " + e.getMessage());
            } finally {
                SecurityContextHolder.clearContext();
            }
        };

        executor.submit(task1);
        executor.submit(task2);

        System.out.println("Starting Race Condition Test...");
        latch.countDown();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Ride ride = rideRepository.findById(rideId).orElseThrow();

        System.out.println("Final Seats: " + ride.getAvailableSeats());
        System.out.println("Final Status: " + ride.getStatus());
        System.out.println("Successes: " + successCount.get() + ", Failures: " + failureCount.get());

        // ✅ Exactly 1 should succeed, 1 should fail
        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(1, failureCount.get(), "Exactly one booking should fail");
        assertEquals(0, ride.getAvailableSeats(), "Seats should be 0, not negative");
        assertEquals(RideStatus.FULL, ride.getStatus(), "Ride status should be FULL");

    }

    // --- HELPER METHODS ---

    private UUID setupRideWithOneSeat() {
        // 1. Create Driver
        User driver = new User();
        driver.setFirstName("Driver");
        driver.setLastName("Test");
        driver.setEmail("driver" + System.currentTimeMillis() + "@test.com");
        driver.setPasswordHash(passwordEncoder.encode("password"));
        driver.setPhoneNumber("+216" + phoneCounter.incrementAndGet()); // Valid format: +216 + 8 digits
        driver.setGender(Gender.MALE);
        driver.setDateOfBirth(LocalDate.of(1990, 1, 1));
        driver.setRole(UserRole.USER);
        userRepository.save(driver);

        // 2. Create Ride
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setOriginName("Test Origin");
        ride.setDestinationName("Test Dest");

        // Create dummy points
        Point p = geometryFactory.createPoint(new Coordinate(10.0, 36.0));
        ride.setOriginPoint(p);
        ride.setDestinationPoint(p);

        ride.setDepartureTime(OffsetDateTime.now().plusHours(2));
        ride.setTotalSeats(1);
        ride.setAvailableSeats(1); // <--- CRITICAL: ONLY 1 SEAT
        ride.setPricePerSeat(BigDecimal.TEN);
        ride.setGenderPreference(RideGenderPreference.ANY);
        ride.setStatus(RideStatus.SCHEDULED);
        ride.setAllowsSmoking(false);

        return rideRepository.save(ride).getId();
    }

    private User createDummyPassenger(String email) {
        User user = new User();
        user.setFirstName("Passenger");
        user.setLastName("Test");
        user.setEmail(email + System.currentTimeMillis()); // Ensure unique
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setPhoneNumber("+216" + phoneCounter.incrementAndGet()); // Valid format: +216 + 8 digits
        user.setGender(Gender.MALE);
        user.setDateOfBirth(LocalDate.of(1995, 1, 1));
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    private void mockSecurityContext(User user) {
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.Collections.emptyList());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null,
                principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}