package com.blassa.repository;

import com.blassa.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetToken(String token);

    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}
