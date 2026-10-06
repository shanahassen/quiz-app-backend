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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role participantRole = roleRepository.findByRoleName(RoleName.PARTICIPANT)
                .orElseThrow(() -> new RuntimeException("Participant role not found"));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setUserStatus(UserStatus.ACTIVE);
        user.setRoleId(participantRole.getRoleId());

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() ->
                        new RuntimeException("Role not found"));

        String token = jwtService.generateToken(email);

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                role.getRoleName().name()
        );
    }
}
