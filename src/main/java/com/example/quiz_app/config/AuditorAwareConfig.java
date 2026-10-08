package com.example.quiz_app.config;

import com.example.quiz_app.model.User;
import com.example.quiz_app.model.UserStatus;
import com.example.quiz_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareConfig implements AuditorAware<String> {

    private final UserRepository userRepository;

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String email = authentication.getName();

        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .map(User::getRefId);
    }
}
