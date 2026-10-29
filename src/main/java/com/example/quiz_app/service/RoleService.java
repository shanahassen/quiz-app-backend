package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.UpdateRolePermissionsRequest;
import com.example.quiz_app.dto.UpdateRoleRequest;
import com.example.quiz_app.model.Permission;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.PermissionRepository;
import com.example.quiz_app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    private static final Set<String> ADMIN_ALLOWED_PERMISSIONS = Set.of(
            "MANAGE_CATEGORY",
            "MANAGE_QUIZ"
    );

    public Role getRoleByRefId(String refId) throws QuizException {
        return roleRepository.findByRefId(refId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004, "Role not found"));
    }

    public Role updateRole(
            String refId,
            UpdateRoleRequest request
    ) throws QuizException {

        Role role = getRoleByRefId(refId);

        if (role.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4005,
                    "An inactive role cannot be updated");
        }

        role.setDescription(request.description());

        return roleRepository.save(role);
    }

    public Role updateRolePermissions(
            String refId,
            UpdateRolePermissionsRequest request
    ) throws QuizException {

        Role role = getRoleByRefId(refId);

        if (role.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4005,
                    "Permissions cannot be changed for an inactive role"
            );
        }

        // Only the Admin role can be configured through this endpoint.
        // Super Admin is protected, and Participant is not configurable here.
        if (role.getRoleName() !=
                com.example.quiz_app.model.RoleName.ADMIN) {
            throw new QuizException(
                    StatusCode.E4005,
                    "Permissions can only be configured for the Admin role"
            );
        }

        if (request == null || request.permissionRefIds() == null) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Permission reference IDs are required"
            );
        }

        List<String> requestedIds = request.permissionRefIds();

        if (new HashSet<>(requestedIds).size() != requestedIds.size()) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Duplicate permission IDs are not allowed"
            );
        }

        List<String> validatedIds = new ArrayList<>();

        for (String permissionRefId : requestedIds) {
            Permission permission = permissionRepository
                    .findByRefId(permissionRefId)
                    .orElseThrow(() -> new QuizException(
                            StatusCode.E4004,
                            "Permission not found: " + permissionRefId
                    ));

            if (permission.getStatus() != Status.ACTIVE) {
                throw new QuizException(
                        StatusCode.E4005,
                        "Inactive permissions cannot be assigned"
                );
            }

            // Enforce the allowlist on the backend, regardless of
            // which permission IDs the client sends.
            if (!ADMIN_ALLOWED_PERMISSIONS.contains(permission.getName())) {
                throw new QuizException(
                        StatusCode.E4005,
                        "This permission cannot be assigned to Admin: "
                                + permission.getName()
                );
            }

            validatedIds.add(permission.getRefId());
        }

        role.setPermissionIds(validatedIds);

        return roleRepository.save(role);
    }

    public Role deactivateRole(String refId) throws QuizException {
        Role role = getRoleByRefId(refId);

        if (role.getRoleName() ==
                com.example.quiz_app.model.RoleName.SUPER_ADMIN) {
            throw new QuizException(
                    StatusCode.E4005,
                    "The Super Admin role cannot be deactivated");
        }

        if (role.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4009,
                    "Role is already inactive");
        }

        role.setStatus(Status.INACTIVE);

        return roleRepository.save(role);
    }

    public Role reactivateRole(String refId) throws QuizException {
        Role role = getRoleByRefId(refId);

        if (role.getStatus() == Status.ACTIVE) {
            throw new QuizException(
                    StatusCode.E4009,
                    "Role is already active");
        }

        role.setStatus(Status.ACTIVE);

        return roleRepository.save(role);
    }
}