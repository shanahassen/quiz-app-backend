package com.example.quiz_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class CurrentUserController {

    @GetMapping("/permissions")
    public List<String> getCurrentUserPermissions(
            Authentication authentication
    ) {
        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new org.springframework.security.authentication
                    .AuthenticationCredentialsNotFoundException(
                    "User is not authenticated"
            );
        }

        return authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> !authority.startsWith("ROLE_"))
                .toList();
    }
}