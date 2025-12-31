package com.blassa.repository;

import com.blassa.model.entity.Ride;
import com.blassa.model.enums.RideStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {

    @Query(value = """
            SELECT * FROM rides r
            WHERE
                ST_DWithin(r.origin_point, :origin, :pickupRadius)
            AND ST_DWithin(r.destination_point, :destination, :dropoffRadius)
            AND r.departure_time BETWEEN :startTime AND :endTime
            AND r.status = 'SCHEDULED'
            AND r.available_seats >= :requiredSeats
            AND r.gender_preference IN (:allowedPreferences)
            """, countQuery = """
            SELECT count(*) FROM rides r
            WHERE
                ST_DWithin(r.origin_point, :origin, :pickupRadius)
            AND ST_DWithin(r.destination_point, :destination, :dropoffRadius)
            AND r.departure_time BETWEEN :startTime AND :endTime
            AND r.status = 'SCHEDULED'
            AND r.available_seats >= :requiredSeats
            AND r.gender_preference IN (:allowedPreferences)
            """, nativeQuery = true)
    Page<Ride> searchRides(
            @Param("origin") Point origin,
            @Param("pickupRadius") double pickupRadius,
            @Param("destination") Point destination,
            @Param("dropoffRadius") double dropoffRadius,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime,
            @Param("requiredSeats") int requiredSeats,
            @Param("allowedPreferences") List<String> allowedPreferences,
            Pageable pageable);

    @EntityGraph(attributePaths = { "driver" })
    Page<Ride> findByDriverId(UUID id, Pageable pageable);

    List<Ride> findByIdAndStatus(UUID id, RideStatus status);

    int countByDriverIdAndStatus(UUID driverId, RideStatus status);

    long countByDriverIdAndStatusIn(UUID driverId, List<RideStatus> statuses);

    List<Ride> findByDriverId(UUID driverId);
}
