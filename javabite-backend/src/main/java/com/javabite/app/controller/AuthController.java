package com.javabite.app.controller;

import com.javabite.app.model.User;
import com.javabite.app.payload.ApiResponse;
import com.javabite.app.payload.LoginRequest;
import com.javabite.app.payload.SignupRequest;
import com.javabite.app.service.CustomUserDetails;
import com.javabite.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Customer Signup - POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User user = userService.registerCustomer(signupRequest);
            log.info("Customer registered successfully: {}", user.getEmail());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Registration successful! Please login with your credentials.")
            );
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Login - POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpSession session
    ) {
        try {
            log.info("Login attempt for user: {}", loginRequest.getEmail());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // CRITICAL: Set authentication BEFORE creating session attribute
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Now store in session
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );

            // Force session creation
            session.setAttribute("AUTHENTICATED", true);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", userDetails.getId());
            userData.put("name", userDetails.getName());
            userData.put("email", userDetails.getEmail());
            userData.put("role", userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .orElse("CUSTOMER"));

            log.info("User {} logged in successfully with role: {}",
                    userDetails.getEmail(), userData.get("role"));

            return ResponseEntity.ok(
                    new ApiResponse(true, "Login successful", userData)
            );
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid email or password"));
        }
    }

    /**
     * Get current user - GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Not authenticated"));
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userDetails.getId());
        userData.put("name", userDetails.getName());
        userData.put("email", userDetails.getEmail());
        userData.put("role", userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("CUSTOMER"));

        return ResponseEntity.ok(
                new ApiResponse(true, "User retrieved successfully", userData)
        );
    }
}