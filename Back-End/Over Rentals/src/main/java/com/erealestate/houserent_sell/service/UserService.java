package com.erealestate.houserent_sell.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.erealestate.houserent_sell.model.User;
import com.erealestate.houserent_sell.repository.UserRepository;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) throws Exception {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("User with this email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public boolean authenticateUser(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(String email) {
        User user = getUser(email);
        userRepository.delete(user);
    }

    @Override
    public User updateUser(String email, User user) throws Exception {
        // Find the existing user
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        // Update the existing user fields with the new values
        existingUser.setUsername(user.getUsername());
        existingUser.setRole(user.getRole());
        // Update other fields as necessary

        // Save the updated user back to the database
        return userRepository.save(existingUser);
    }

}