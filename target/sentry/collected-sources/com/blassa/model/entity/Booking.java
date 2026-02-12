package com.blassa.model.entity;

import com.blassa.model.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @ColumnDefault("uuid_generate_v4()")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ColumnDefault("1")
    @Column(name = "seats_booked")
    private Integer seatsBooked;

    @NotNull
    @Column(name = "price_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceTotal;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "booking")
    private Set<Review> reviews = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;


    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}