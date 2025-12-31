package com.blassa.repository;

import com.blassa.model.entity.Booking;
import com.blassa.model.enums.BookingStatus;

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

    @Query("SELECT b.ride.id FROM Booking b WHERE b.passenger.id = :passengerId AND b.status != :status")
    List<UUID> findRideIdsByPassengerIdAndStatusNot(@Param("passengerId") UUID passengerId,
            @Param("status") BookingStatus status);

    @EntityGraph(attributePaths = { "passenger" })
    List<Booking> findByRideIdAndStatus(UUID rideId, BookingStatus status);

    List<Booking> findByRideId(UUID rideId);

    long countByRideIdAndStatus(UUID rideId, BookingStatus status);

    long countByPassengerIdAndStatusIn(UUID passengerId, List<BookingStatus> statuses);

    List<Booking> findByPassengerId(UUID passengerId);
}
