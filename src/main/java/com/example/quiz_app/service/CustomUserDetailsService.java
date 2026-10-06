package com.example.quiz_app.service;

import com.example.quiz_app.model.Permission;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.User;
import com.example.quiz_app.repository.PermissionRepository;
import com.example.quiz_app.repository.RoleRepository;
import com.example.quiz_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + email));

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Role not found for user: " + email));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Add role
        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.getRoleName().name()
                )
        );

        // Add permissions
        if (role.getPermissionIds() != null) {

            for (String permissionId : role.getPermissionIds()) {

                permissionRepository.findById(permissionId)
                        .ifPresent(permission ->
                                authorities.add(
                                        new SimpleGrantedAuthority(
                                                permission.getName()
                                        )
                                )
                        );
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(user.getUserStatus().name().equals("INACTIVE"))
                .build();
    }
}
