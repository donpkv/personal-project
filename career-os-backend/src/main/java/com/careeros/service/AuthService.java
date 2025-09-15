package com.careeros.service;

import com.careeros.dto.auth.LoginRequest;
import com.careeros.dto.auth.LoginResponse;
import com.careeros.dto.auth.RegisterRequest;
import com.careeros.dto.auth.RegisterResponse;
import com.careeros.entity.User;
import com.careeros.exception.AuthenticationException;
import com.careeros.exception.UserAlreadyExistsException;
import com.careeros.repository.UserRepository;
import com.careeros.security.JwtUtil;
import com.careeros.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Authentication service for user registration, login, and token management
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    /**
     * Register a new user
     */
    public RegisterResponse register(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + request.getUsername() + " already exists");
        }

        try {
            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setAccountStatus(User.AccountStatus.ACTIVE);
            user.setEmailVerified(false);
            user.setLoginAttempts(0);

            // Add default USER role
            user.getRoles().add(User.Role.USER);

            // Save user
            User savedUser = userRepository.save(user);

            // Send verification email
            emailService.sendVerificationEmail(savedUser);

            logger.info("User registered successfully with ID: {}", savedUser.getId());

            RegisterResponse response = new RegisterResponse();
            response.setUserId(savedUser.getId());
            response.setUsername(savedUser.getUsername());
            response.setEmail(savedUser.getEmail());
            response.setMessage("Registration successful. Please check your email to verify your account.");

            return response;

        } catch (Exception e) {
            logger.error("Error during user registration", e);
            throw new RuntimeException("Registration failed", e);
        }
    }

    /**
     * Authenticate user and generate JWT tokens
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("Attempting login for user: {}", request.getUsername());

        try {
            // Find user by username or email
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByEmail(request.getUsername());
            }

            if (userOpt.isEmpty()) {
                throw new AuthenticationException("Invalid username or password");
            }

            User user = userOpt.get();

            // Check if account is locked
            if (user.isAccountLocked()) {
                throw new AuthenticationException("Account is temporarily locked due to too many failed login attempts");
            }

            // Check if account is active
            if (!user.isActive()) {
                throw new AuthenticationException("Account is inactive. Please contact support.");
            }

            try {
                // Authenticate user
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Reset login attempts on successful login
                user.setLoginAttempts(0);
                user.setLastLogin(LocalDateTime.now());
                user.setLockedUntil(null);
                userRepository.save(user);

                // Generate JWT tokens
                String accessToken = jwtUtil.generateAccessToken(authentication);
                String refreshToken = jwtUtil.generateRefreshToken(authentication);

                logger.info("User {} logged in successfully", user.getUsername());

                LoginResponse response = new LoginResponse();
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshToken);
                response.setTokenType("Bearer");
                response.setExpiresIn(jwtUtil.getTokenRemainingTime(accessToken));
                response.setUser(createUserInfo(user));

                return response;

            } catch (BadCredentialsException e) {
                // Handle failed login attempt
                handleFailedLoginAttempt(user);
                throw new AuthenticationException("Invalid username or password");
            }

        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", request.getUsername());
            throw e;
        } catch (Exception e) {
            logger.error("Error during login process", e);
            throw new RuntimeException("Login failed", e);
        }
    }

    /**
     * Refresh JWT access token
     */
    public LoginResponse refreshToken(String refreshToken) {
        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new AuthenticationException("Invalid refresh token");
            }

            String userId = jwtUtil.getUserIdFromToken(refreshToken);
            Optional<User> userOpt = userRepository.findById(java.util.UUID.fromString(userId));

            if (userOpt.isEmpty() || !userOpt.get().isActive()) {
                throw new AuthenticationException("User not found or inactive");
            }

            User user = userOpt.get();
            UserPrincipal userPrincipal = UserPrincipal.create(user);

            // Create authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities());

            // Generate new access token
            String newAccessToken = jwtUtil.generateAccessToken(authentication);

            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken); // Keep the same refresh token
            response.setTokenType("Bearer");
            response.setExpiresIn(jwtUtil.getTokenRemainingTime(newAccessToken));
            response.setUser(createUserInfo(user));

            return response;

        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            throw new AuthenticationException("Token refresh failed");
        }
    }

    /**
     * Logout user (invalidate tokens)
     */
    public void logout(String accessToken) {
        try {
            // In a production system, you would add the token to a blacklist
            // For now, we'll just log the logout
            String userId = jwtUtil.getUserIdFromToken(accessToken);
            logger.info("User {} logged out successfully", userId);
        } catch (Exception e) {
            logger.error("Error during logout", e);
        }
    }

    /**
     * Verify user email
     */
    public void verifyEmail(String token) {
        try {
            // Validate verification token (simplified - implement proper token validation)
            String userId = jwtUtil.getUserIdFromToken(token);
            Optional<User> userOpt = userRepository.findById(java.util.UUID.fromString(userId));

            if (userOpt.isEmpty()) {
                throw new AuthenticationException("Invalid verification token");
            }

            User user = userOpt.get();
            user.setEmailVerified(true);
            userRepository.save(user);

            logger.info("Email verified for user: {}", user.getUsername());

        } catch (Exception e) {
            logger.error("Error verifying email", e);
            throw new AuthenticationException("Email verification failed");
        }
    }

    /**
     * Request password reset
     */
    public void requestPasswordReset(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                // Don't reveal if email exists for security
                logger.warn("Password reset requested for non-existent email: {}", email);
                return;
            }

            User user = userOpt.get();
            
            // Generate reset token (simplified - implement proper token generation)
            UserPrincipal userPrincipal = UserPrincipal.create(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities());
            String resetToken = jwtUtil.generateAccessToken(auth);

            // Send password reset email
            emailService.sendPasswordResetEmail(user, resetToken);

            logger.info("Password reset email sent to: {}", email);

        } catch (Exception e) {
            logger.error("Error requesting password reset", e);
            throw new RuntimeException("Password reset request failed");
        }
    }

    /**
     * Reset password with token
     */
    public void resetPassword(String token, String newPassword) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new AuthenticationException("Invalid or expired reset token");
            }

            String userId = jwtUtil.getUserIdFromToken(token);
            Optional<User> userOpt = userRepository.findById(java.util.UUID.fromString(userId));

            if (userOpt.isEmpty()) {
                throw new AuthenticationException("User not found");
            }

            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);

            logger.info("Password reset successfully for user: {}", user.getUsername());

        } catch (Exception e) {
            logger.error("Error resetting password", e);
            throw new AuthenticationException("Password reset failed");
        }
    }

    private void handleFailedLoginAttempt(User user) {
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            logger.warn("User account locked due to {} failed login attempts: {}", attempts, user.getUsername());
        }

        userRepository.save(user);
    }

    private LoginResponse.UserInfo createUserInfo(User user) {
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setRoles(user.getRoles());
        userInfo.setEmailVerified(user.getEmailVerified());
        userInfo.setProfileImageUrl(user.getProfileImageUrl());
        return userInfo;
    }
}
