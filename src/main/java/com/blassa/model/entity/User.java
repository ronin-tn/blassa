package com.blassa.model.entity;

import com.blassa.model.enums.Gender;
import com.blassa.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @ColumnDefault("uuid_generate_v4()")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "bio", length = Integer.MAX_VALUE)
    private String bio;

    @Column(name = "profile_picture_url", length = Integer.MAX_VALUE)
    private String profilePictureUrl;

    @Size(max = 20)
    @Column(name = "cin_number", length = 20)
    private String cinNumber;

    @ColumnDefault("false")
    @Column(name = "email_verified")
    private Boolean emailVerified;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "passenger")
    private Set<Booking> bookings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reviewer")
    private Set<Review> reviewsWritten = new LinkedHashSet<>();
    @OneToMany(mappedBy = "driver")
    private Set<Ride> rides = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reviewee")
    private Set<Review> reviewsReceived = new LinkedHashSet<>();
    @OneToMany(mappedBy = "owner")
    private Set<Vehicle> vehicles = new LinkedHashSet<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    private String verificationToken;
    private boolean isVerified;
    @Column(name="reset_token")
    private String resetToken;

}