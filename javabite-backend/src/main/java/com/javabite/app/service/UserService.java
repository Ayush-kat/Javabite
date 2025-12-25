package com.javabite.app.service;

import com.javabite.app.model.Role;
import com.javabite.app.model.User;
import com.javabite.app.payload.CreateChefRequest;
import com.javabite.app.payload.CreateWaiterRequest;
import com.javabite.app.payload.SignupRequest;
import com.javabite.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int PASSWORD_LENGTH = 12;

    @Transactional
    public User registerCustomer(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Customer registered: {}", savedUser.getEmail());

        return savedUser;
    }

    @Transactional
    public User createChef(CreateChefRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        String password = request.getPassword();
        if (password == null || password.isBlank()) {
            password = generateRandomPassword();
        }

        User chef = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(Role.CHEF)
                .enabled(true)
                .build();

        User saved = userRepository.save(chef);
        saved.setPassword(password); // For response only (not saved to DB)

        log.info("Chef created: {}", saved.getEmail());
        return saved;
    }

    @Transactional
    public User createWaiter(CreateWaiterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        String password = request.getPassword();
        if (password == null || password.isBlank()) {
            password = generateRandomPassword();
        }

        User waiter = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(Role.WAITER)
                .enabled(true)
                .build();

        User saved = userRepository.save(waiter);
        saved.setPassword(password); // For response only (not saved to DB)

        log.info("Waiter created: {}", saved.getEmail());
        return saved;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public List<User> getAllChefs() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.CHEF)
                .collect(Collectors.toList());
    }

    public List<User> getAllWaiters() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.WAITER)
                .collect(Collectors.toList());
    }

    @Transactional
    public User toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        userRepository.delete(user);
        log.info("User deleted: {}", user.getEmail());
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    // ========== INVITATION-BASED REGISTRATION ==========

    @Transactional
    public String inviteChef(String name, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        // Generate invitation token
        String token = java.util.UUID.randomUUID().toString();

        User chef = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // temp
                .role(Role.CHEF)
                .enabled(false)  // Not enabled until they accept
                .invitationToken(token)
                .invitationSentAt(java.time.LocalDateTime.now())
                .maxActiveOrders(10)
                .currentActiveOrders(0)
                .isAvailable(true)
                .build();

        userRepository.save(chef);

        // TODO: Send email with link: /accept-invite?token={token}
        log.info("Chef invitation sent to {}", email);

        return token;  // Return for testing
    }

    @Transactional
    public String inviteWaiter(String name, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists!");
        }

        // Generate invitation token
        String token = java.util.UUID.randomUUID().toString();

        User waiter = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // temp
                .role(Role.WAITER)
                .enabled(false)  // Not enabled until they accept
                .invitationToken(token)
                .invitationSentAt(java.time.LocalDateTime.now())
                .maxActiveOrders(1)  // Waiters can serve 1 table at a time
                .currentActiveOrders(0)
                .isAvailable(true)
                .build();

        userRepository.save(waiter);

        log.info("Waiter invitation sent to {}", email);

        return token;
    }

    @Transactional
    public User acceptInvitation(String token, String password) {
        User user = userRepository.findByInvitationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (user.getInvitationAcceptedAt() != null) {
            throw new RuntimeException("Invitation already accepted");
        }

        // Set hashed password
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setInvitationAcceptedAt(java.time.LocalDateTime.now());
        user.setInvitationToken(null);  // Clear token

        return userRepository.save(user);
    }
}