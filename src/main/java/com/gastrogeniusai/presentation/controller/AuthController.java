package com.gastrogeniusai.presentation.controller;

import com.gastrogeniusai.application.service.AuthenticationService;
import com.gastrogeniusai.presentation.dto.AuthResponse;
import com.gastrogeniusai.presentation.dto.LoginRequest;
import com.gastrogeniusai.presentation.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 * Handles user registration, login, token refresh, and validation.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Registers a new user account.
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided information", responses = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "409", description = "Username or email already taken", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(description = "User registration details", required = true) @Valid @RequestBody RegisterRequest registerRequest) {

        try {
            AuthResponse authResponse = authenticationService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Authenticates a user and returns JWT tokens.
     */
    @Operation(summary = "Login user", description = "Authenticates user credentials and returns JWT access and refresh tokens", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Account disabled", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Parameter(description = "User login credentials", required = true) @Valid @RequestBody LoginRequest loginRequest) {

        try {
            AuthResponse authResponse = authenticationService.loginUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", "Invalid username/email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Refreshes access token using refresh token.
     */
    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token", responses = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Parameter(description = "Refresh token", required = true) @RequestBody Map<String, String> request) {

        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid request");
                errorResponse.put("message", "Refresh token is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            AuthResponse authResponse = authenticationService.refreshToken(refreshToken);
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token refresh failed");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token refresh failed");
            errorResponse.put("message", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Validates JWT token.
     */
    @Operation(summary = "Validate token", description = "Validates if a JWT token is valid and returns user information", responses = {
            @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(schema = @Schema(implementation = AuthResponse.UserInfo.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @Parameter(description = "JWT token to validate", required = true) @RequestBody Map<String, String> request) {

        try {
            String token = request.get("token");
            if (token == null || token.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid request");
                errorResponse.put("message", "Token is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (authenticationService.validateToken(token)) {
                AuthResponse.UserInfo userInfo = authenticationService.getUserFromToken(token);
                return ResponseEntity.ok(userInfo);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid token");
                errorResponse.put("message", "Token is invalid or expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token validation failed");
            errorResponse.put("message", "An unexpected error occurred during validation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Checks if username is available.
     */
    @Operation(summary = "Check username availability", description = "Checks if a username is available for registration", responses = {
            @ApiResponse(responseCode = "200", description = "Username availability checked", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(
            @Parameter(description = "Username to check", required = true) @RequestParam @NotBlank String username) {

        boolean available = authenticationService.isUsernameAvailable(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("available", available);

        return ResponseEntity.ok(response);
    }

    /**
     * Checks if email is available.
     */
    @Operation(summary = "Check email availability", description = "Checks if an email address is available for registration", responses = {
            @ApiResponse(responseCode = "200", description = "Email availability checked", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(
            @Parameter(description = "Email to check", required = true) @RequestParam @NotBlank String email) {

        boolean available = authenticationService.isEmailAvailable(email);

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", available);

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for authentication service.
     */
    @Operation(summary = "Authentication service health check", description = "Returns the health status of the authentication service", responses = {
            @ApiResponse(responseCode = "200", description = "Service is healthy", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "authentication");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
