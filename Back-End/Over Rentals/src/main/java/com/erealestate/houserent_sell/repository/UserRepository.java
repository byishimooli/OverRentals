package com.erealestate.houserent_sell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.erealestate.houserent_sell.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Check if a user exists by email
    boolean existsByEmail(String email);

    // Delete a user by email
    void deleteByEmail(String email);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Find a user by reset password token
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
}
