package com.gastrogeniusai.application.service;

import com.gastrogeniusai.domain.entity.User;
import com.gastrogeniusai.domain.entity.UserRole;
import com.gastrogeniusai.infrastructure.repository.UserRepository;
import com.gastrogeniusai.infrastructure.security.JwtTokenUtil;
import com.gastrogeniusai.presentation.dto.AuthResponse;
import com.gastrogeniusai.presentation.dto.LoginRequest;
import com.gastrogeniusai.presentation.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service class for handling user authentication operations.
 * Manages user registration, login, and token generation.
 */
@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Registers a new user in the system.
     * 
     * @param registerRequest the registration request
     * @return authentication response with tokens and user info
     * @throws IllegalArgumentException if username or email already exists
     */
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenUtil.generateEnhancedToken(
                savedUser, savedUser.getId(), savedUser.getRole().name());
        String refreshToken = jwtTokenUtil.generateRefreshToken(savedUser);

        // Create user info for response
        AuthResponse.UserInfo userInfo = createUserInfo(savedUser);

        return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userInfo);
    }

    /**
     * Authenticates a user and generates tokens.
     * 
     * @param loginRequest the login request
     * @return authentication response with tokens and user info
     * @throws BadCredentialsException if credentials are invalid
     */
    public AuthResponse loginUser(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Update last login timestamp
            user.updateLastLogin();
            userRepository.save(user);

            // Generate tokens
            String accessToken = jwtTokenUtil.generateEnhancedToken(
                    userDetails, user.getId(), user.getRole().name());
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            // Create user info for response
            AuthResponse.UserInfo userInfo = createUserInfo(user);

            return new AuthResponse(accessToken, refreshToken, jwtExpiration / 1000, userInfo);

        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username/email or password", e);
        }
    }

    /**
     * Refreshes an access token using a refresh token.
     * 
     * @param refreshToken the refresh token
     * @return new authentication response with fresh tokens
     * @throws IllegalArgumentException if refresh token is invalid
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenUtil.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String tokenType = jwtTokenUtil.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }

        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate new tokens
        String newAccessToken = jwtTokenUtil.generateEnhancedToken(
                user, user.getId(), user.getRole().name());
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(user);

        // Create user info for response
        AuthResponse.UserInfo userInfo = createUserInfo(user);

        return new AuthResponse(newAccessToken, newRefreshToken, jwtExpiration / 1000, userInfo);
    }

    /**
     * Validates if a username is available.
     * 
     * @param username the username to check
     * @return true if username is available
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Validates if an email is available.
     * 
     * @param email the email to check
     * @return true if email is available
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Validates a JWT token.
     * 
     * @param token the token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        try {
            if (!jwtTokenUtil.isTokenValid(token)) {
                return false;
            }

            String username = jwtTokenUtil.getUsernameFromToken(token);
            User user = userRepository.findByUsernameOrEmail(username)
                    .orElse(null);

            return user != null && user.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets user information from a JWT token.
     * 
     * @param token the JWT token
     * @return user information
     * @throws UsernameNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public AuthResponse.UserInfo getUserFromToken(String token) {
        if (!jwtTokenUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return createUserInfo(user);
    }

    /**
     * Creates UserInfo DTO from User entity.
     * 
     * @param user the user entity
     * @return user info DTO
     */
    private AuthResponse.UserInfo createUserInfo(User user) {
        return new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole());
    }

    /**
     * Changes user password (requires current password verification).
     * 
     * @param userId          the user ID
     * @param currentPassword the current password
     * @param newPassword     the new password
     * @throws BadCredentialsException if current password is incorrect
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Disables a user account.
     * 
     * @param userId the user ID
     */
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Enables a user account.
     * 
     * @param userId the user ID
     */
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }
}
