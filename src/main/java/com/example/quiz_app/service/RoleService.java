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

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

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
                    "Permissions cannot be changed for an inactive role");
        }

        if (role.getRoleName() ==
                com.example.quiz_app.model.RoleName.SUPER_ADMIN) {
            throw new QuizException(
                    StatusCode.E4005,
                    "The Super Admin permissions cannot be modified here");
        }

        List<String> requestedIds = request.permissionRefIds();

        if (requestedIds == null) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Permission reference IDs are required");
        }

        if (new HashSet<>(requestedIds).size() != requestedIds.size()) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Duplicate permission IDs are not allowed");
        }

        List<String> validatedIds = new ArrayList<>();

        for (String permissionRefId : requestedIds) {
            Permission permission = permissionRepository
                    .findByRefId(permissionRefId)
                    .orElseThrow(() -> new QuizException(
                            StatusCode.E4004,
                            "Permission not found: " + permissionRefId));

            if (permission.getStatus() != Status.ACTIVE) {
                throw new QuizException(
                        StatusCode.E4005,
                        "Inactive permissions cannot be assigned");
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