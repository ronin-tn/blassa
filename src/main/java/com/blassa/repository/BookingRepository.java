package com.blassa.repository;

import com.blassa.model.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    boolean existsByRideIdAndPassengerId(UUID rideId, UUID passengerId);

    @EntityGraph(attributePaths = { "ride", "ride.driver" })
    Page<Booking> findByPassengerId(UUID passengerId, Pageable pageable);
}
