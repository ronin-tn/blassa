package com.blassa.model.entity;

import com.blassa.model.enums.RideGenderPreference;
import com.blassa.model.enums.RideStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "rides")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @ColumnDefault("uuid_generate_v4()")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Size(max = 255)
    @NotNull
    @Column(name = "origin_name", nullable = false)
    private String originName;

    @Size(max = 255)
    @NotNull
    @Column(name = "destination_name", nullable = false)
    private String destinationName;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private OffsetDateTime departureTime;

    @NotNull
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @NotNull
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @NotNull
    @Column(name = "price_per_seat", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSeat;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ColumnDefault("0")
    @Column(name = "version")
    @Version
    private Integer version;

    @ColumnDefault("false")
    @Column(name = "allows_smoking")
    private Boolean allowsSmoking;

    @ColumnDefault("false")
    @Column(name = "allows_music")
    private Boolean allowsMusic;

    @ColumnDefault("false")
    @Column(name = "allows_pets")
    private Boolean allowsPets;

    @Size(max = 20)
    @Column(name = "luggage_size") // SMALL, MEDIUM, LARGE
    private String luggageSize;

    @OneToMany(mappedBy = "ride")
    private Set<Booking> bookings = new LinkedHashSet<>();

    @Column(name = "origin_point", columnDefinition = "geography(Point, 4326)")
    private Point originPoint;

    @Column(name = "destination_point", columnDefinition = "geography(Point, 4326)")
    private Point destinationPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_preference")
    private RideGenderPreference genderPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RideStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}