package com.blassa.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @ColumnDefault("uuid_generate_v4()")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Size(max = 50)
    @NotNull
    @Column(name = "make", nullable = false, length = 50)
    private String make;

    @Size(max = 50)
    @NotNull
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Size(max = 30)
    @NotNull
    @Column(name = "color", nullable = false, length = 30)
    private String color;

    @Size(max = 20)
    @NotNull
    @Column(name = "license_plate", nullable = false, length = 20)
    //Tunisian license Plate
    private String licensePlate;

    @Column(name = "production_year")
    private Integer productionYear;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}