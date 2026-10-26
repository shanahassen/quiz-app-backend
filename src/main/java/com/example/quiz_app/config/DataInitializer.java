package com.example.quiz_app.config;

import com.example.quiz_app.model.Permission;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.RoleName;
import com.example.quiz_app.repository.PermissionRepository;
import com.example.quiz_app.repository.RoleRepository;
import com.example.quiz_app.repository.UserRepository;
import com.example.quiz_app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        createPermissions();
        createRoles();
        backfillUserReferenceIds();
    }

    private void backfillUserReferenceIds() {
        for (User user : userRepository.findAll()) {
            if (user.getRefId() == null || user.getRefId().isBlank()) {
                user.setRefId(UUID.randomUUID().toString());
                userRepository.save(user);
            }
        }
    }

    private void createPermissions() {

        createPermission("MANAGE_USER", "Manage user accounts");
        createPermission("MANAGE_ROLE", "Manage roles and permissions");
        createPermission("MANAGE_CATEGORY", "Manage categories");
        createPermission("MANAGE_QUIZ", "Manage quizzes");
        createPermission("MANAGE_QUESTION", "Manage questions");
        createPermission("MANAGE_ANSWER_OPTION", "Manage answer options");
        createPermission("MANAGE_REPORT", "Manage system reports");
        createPermission("VIEW_AUDIT_LOG", "View audit logs");
        createPermission("TAKE_QUIZ", "Take quizzes");
        createPermission("MANAGE_PROFILE", "Manage profile");
    }

    private void createPermission(String name, String description) {

        Permission permission = permissionRepository
                .findAll()
                .stream()
                .filter(existing -> existing.getName().equals(name))
                .findFirst()
                .orElseGet(Permission::new);

        if (permission.getRefId() == null) {
            permission.setRefId(UUID.randomUUID().toString());
        }

        permission.setName(name);
        permission.setDescription(description);

        permissionRepository.save(permission);
    }

    private void createRoles() {

        createRole(
                RoleName.SUPER_ADMIN,
                "Super Admin role"
        );
        createRole(
                RoleName.ADMIN,
                "Admin role"
        );
        createRole(
                RoleName.PARTICIPANT,
                "Participant role"
        );
    }

    private void createRole(RoleName roleName, String description) {

        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(Role::new);

        if (role.getRefId() == null) {
            role.setRefId(UUID.randomUUID().toString());
        }

        role.setRoleName(roleName);
        role.setDescription(description);
        role.setPermissionIds(getPermissionIds(roleName));

        roleRepository.save(role);
    }

    private List<String> getPermissionIds(RoleName roleName) {

        List<String> permissionNames;

        if (roleName == RoleName.SUPER_ADMIN) {

            permissionNames = Arrays.asList(
                    "MANAGE_USER",
                    "MANAGE_ROLE",
                    "MANAGE_CATEGORY",
                    "MANAGE_QUIZ",
                    "MANAGE_QUESTION",
                    "MANAGE_ANSWER_OPTION",
                    "MANAGE_REPORT",
                    "VIEW_AUDIT_LOG"
            );
        } else if (roleName == RoleName.ADMIN) {

            permissionNames = Arrays.asList(
                    "MANAGE_CATEGORY",
                    "MANAGE_QUIZ",
                    "MANAGE_QUESTION",
                    "MANAGE_ANSWER_OPTION"
            );
        } else {

            permissionNames = Arrays.asList(
                    "TAKE_QUIZ",
                    "MANAGE_PROFILE"
            );
        }
        return permissionRepository.findAll()
                .stream()
                .filter(permission ->
                        permissionNames.contains(permission.getName()))
                .map(Permission::getRefId)
                .toList();
    }
}