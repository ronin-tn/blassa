package com.blassa.repository;

import com.blassa.model.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    boolean existsByRideIdAndPassengerId(UUID rideId, UUID passengerId);

    Optional<Booking> findByRideIdAndPassengerId(UUID rideId, UUID passengerId);

    @EntityGraph(attributePaths = { "ride", "ride.driver" })
    Page<Booking> findByPassengerId(UUID passengerId, Pageable pageable);

    // Get all ride IDs that a passenger has booked (excluding cancelled bookings)
    @Query("SELECT b.ride.id FROM Booking b WHERE b.passenger.id = :passengerId AND b.status != :status")
    List<UUID> findRideIdsByPassengerIdAndStatusNot(@Param("passengerId") UUID passengerId,
            @Param("status") com.blassa.model.enums.BookingStatus status);

    // Get all bookings for a specific ride (for driver to see passengers)
    @EntityGraph(attributePaths = { "passenger" })
    List<Booking> findByRideIdAndStatus(UUID rideId, com.blassa.model.enums.BookingStatus status);

    // Get all bookings for a specific ride (any status)
    List<Booking> findByRideId(UUID rideId);

    // Count bookings by ride and status
    long countByRideIdAndStatus(UUID rideId, com.blassa.model.enums.BookingStatus status);
}
