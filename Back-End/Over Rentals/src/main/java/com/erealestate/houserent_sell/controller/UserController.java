package com.erealestate.houserent_sell.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erealestate.houserent_sell.model.Role;
import com.erealestate.houserent_sell.model.User;
import com.erealestate.houserent_sell.security.jwt.JwtUtil;
import com.erealestate.houserent_sell.service.IUserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    public UserController(IUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Endpoint for user registration
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        try {
            // Set default role if not provided
            if (user.getRole() == null) {
                user.setRole(Role.USER); // default to USER role
            }
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error during user registration: " + e.getMessage());
        }
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            boolean isAuthenticated = userService.authenticateUser(user.getEmail(), user.getPassword());
            if (isAuthenticated) {
                User authenticatedUser = userService.getUser(user.getEmail());
                String token = jwtUtil.generateToken(authenticatedUser);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful!");
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during login: " + e.getMessage());
        }
    }

    // Endpoint to fetch all users
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Remove "Bearer " from the token
            String jwtToken = authorizationHeader.substring(7);
            Claims claims = jwtUtil.extractClaims(jwtToken);
            String userRole = claims.get("role", String.class);
            if (!Role.ADMIN.name().equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Insufficient privileges");
            }
            List<User> users = userService.getUsers();
            return ResponseEntity.ok(users);
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the users");
        }
    }

    // Endpoint to fetch user by email
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        try {
            User theUser = userService.getUser(email);
            return ResponseEntity.ok(theUser);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user");
        }
    }

    // Endpoint to update user
    @PutMapping("/{email}")
    public ResponseEntity<String> updateUser(@PathVariable String email, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(email, user);
            return ResponseEntity.ok("User updated successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user");
        }
    }

    // Endpoint to delete a user by email
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("email") String email) {
        try {
            // Extract and validate JWT token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is required");
            }
            String jwtToken = authorizationHeader.substring(7);
            Claims claims = jwtUtil.extractClaims(jwtToken);
            String userRole = claims.get("role", String.class);
            String tokenUserEmail = claims.getSubject(); // Get email from token
            if (!Role.ADMIN.name().equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: Only administrators can delete users");
            }
            if (email.equals(tokenUserEmail)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot delete your own admin account");
            }
            userService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }
}
