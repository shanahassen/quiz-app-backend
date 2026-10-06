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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

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

        log.info("Registration attempt for email: {}", request.email());

        if (userRepository.findByEmailAndUserStatus(request.email(), UserStatus.ACTIVE).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role participantRole = roleRepository.findByRoleName(RoleName.PARTICIPANT)
                .orElseThrow(() -> new RuntimeException("Participant role not found"));

        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        user.setPassword(passwordEncoder.encode(request.password()));

        user.setUserStatus(UserStatus.ACTIVE);
        user.setRoleId(participantRole.getRefId());

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        log.info("User registered successfully: {}", user.getEmail());

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}", request.email());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        log.info("Authentication successful for email: {}", request.email());

        String email = authentication.getName();
        User user = userRepository.findByEmailAndUserStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

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
