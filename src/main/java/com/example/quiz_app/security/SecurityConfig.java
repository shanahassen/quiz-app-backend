package com.example.quiz_app.security;

import com.example.quiz_app.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        // Super Admin
                        .requestMatchers("/api/admin/users/**")
                        .hasAuthority("MANAGE_USER")

                        .requestMatchers("/api/admin/roles/**")
                        .hasAuthority("MANAGE_ROLE")

                        // Quiz management
                        .requestMatchers("/api/categories/**")
                        .hasAuthority("MANAGE_CATEGORY")

                        .requestMatchers("/api/quizzes/**")
                        .hasAuthority("MANAGE_QUIZ")

                        .requestMatchers("/api/questions/**")
                        .hasAuthority("MANAGE_QUESTION")

                        .requestMatchers("/api/answer-options/**")
                        .hasAuthority("MANAGE_ANSWER_OPTION")

                        // Participant
                        .requestMatchers("/api/quiz-attempts/**")
                        .hasAuthority("TAKE_QUIZ")

                        .requestMatchers("/api/profile/**")
                        .hasAuthority("MANAGE_PROFILE")

                        .requestMatchers("/api/test/quiz-management")
                        .hasAuthority("MANAGE_QUIZ")

                        .requestMatchers("/api/test/profile")
                        .hasAuthority("MANAGE_PROFILE")

                        // Everything else requires login
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}