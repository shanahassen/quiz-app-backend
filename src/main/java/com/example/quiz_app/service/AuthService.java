package com.example.quiz_app.service;

import com.example.quiz_app.dto.AuthResponse;
import com.example.quiz_app.dto.LoginRequest;
import com.example.quiz_app.dto.RegisterRequest;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.RoleName;
import com.example.quiz_app.model.User;
import com.example.quiz_app.model.UserStatus;
import com.example.quiz_app.repository.RoleRepository;
import com.example.quiz_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
<<<<<<< Updated upstream
=======
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
>>>>>>> Stashed changes

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        log.info("Registration attempt for email: {}", email);

<<<<<<< Updated upstream
        if (userRepository.findByEmailAndStatus(request.email(), UserStatus.ACTIVE).isPresent()) {
            throw new RuntimeException("Email already registered");
=======
        // confirmPassword is only checked here, never stored
        if (!request.password().equals(request.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
>>>>>>> Stashed changes
        }

        // any status counts as already registered
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered");
        }

        Role participantRole = roleRepository.findByRoleName(RoleName.PARTICIPANT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Participant role not found"));

<<<<<<< Updated upstream
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
=======
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUserStatus(UserStatus.ACTIVE);
>>>>>>> Stashed changes
        user.setRoleId(participantRole.getRefId());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

<<<<<<< Updated upstream
        log.info("User registered successfully: {}", user.getEmail());
=======
        User saved = userRepository.save(user);

        log.info("User registered successfully: {}", saved.getEmail());
>>>>>>> Stashed changes

        return saved;
    }

    public AuthResponse login(LoginRequest request) {

        String requestEmail = request.email().trim().toLowerCase();

        log.info("Login attempt for email: {}", requestEmail);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestEmail,
                                request.password()
                        )
                );

        log.info("Authentication successful for email: {}", requestEmail);

        String email = authentication.getName();
<<<<<<< Updated upstream
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
=======
        User user = userRepository.findByEmailAndUserStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found"));
>>>>>>> Stashed changes
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        log.info("User {} logged in with role {}", user.getEmail(), role.getRoleName());

        String token = jwtService.generateToken(email);

        return new AuthResponse(
                token,
                user.getRefId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                role.getRoleName().name()
        );
    }
}