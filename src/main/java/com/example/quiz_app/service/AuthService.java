package com.example.quiz_app.service;

import com.example.quiz_app.dto.AuthResponse;
import com.example.quiz_app.dto.LoginRequest;
import com.example.quiz_app.dto.RegisterRequest;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.RoleName;
import com.example.quiz_app.model.User;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.RoleRepository;
import com.example.quiz_app.repository.UserRepository;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User register(RegisterRequest request) throws QuizException {

        ValidationUtil.validateRegistration(request);

        log.info("Registration attempt for email: {}", request.email());

        if (userRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new QuizException(StatusCode.E4009, "Email already registered");
        }

        Role participantRole = roleRepository.findByRoleName(RoleName.PARTICIPANT)
                .orElseThrow(() -> new QuizException(StatusCode.E5000, "Participant role is not configured"));

        User user = new User();

        user.setRefId(UUID.randomUUID().toString());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email().trim().toLowerCase(java.util.Locale.ROOT));
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setStatus(Status.ACTIVE);
        user.setRoleId(participantRole.getId());

        log.info("User registered successfully: {}", user.getEmail());

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) throws QuizException {

        ValidationUtil.validateLogin(request);

        log.info("Login attempt for email: {}", request.email());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email().trim().toLowerCase(java.util.Locale.ROOT),
                                request.password()
                        )
                );
        } catch (AuthenticationException exception) {
            throw new QuizException(StatusCode.E4001, "Invalid email or password", exception);
        }

        log.info("Authentication successful for email: {}", request.email());

        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCaseAndStatus(email, Status.ACTIVE)
                .orElseThrow(() -> new QuizException(StatusCode.E4004, "User not found"));
        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new QuizException(StatusCode.E5000, "User role is not configured"));

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

    public void assignAdminRole(String userRefId) throws QuizException {
        User user = userRepository.findByRefId(userRefId)
                .orElseThrow(() -> new QuizException(StatusCode.E4004, "User not found"));
        if (user.getStatus() != Status.ACTIVE) {
            throw new QuizException(StatusCode.E4004, "Inactive users cannot be assigned the admin role");
        }
        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseThrow(() -> new QuizException(StatusCode.E5000, "Admin role is not configured"));
        user.setRoleId(adminRole.getId());
        userRepository.save(user);
    }
}
