package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.dto.CreatePermissionRequest;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.UpdatePermissionRequest;
import com.example.quiz_app.model.Permission;
import com.example.quiz_app.repository.PermissionRepository;
import com.example.quiz_app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermissionByRefId(
            String refId
    ) throws QuizException {

        return permissionRepository.findByRefId(refId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004, "Permission not found"));
    }

    public Permission updatePermission(
            String refId,
            UpdatePermissionRequest request
    ) throws QuizException {

        Permission permission = getPermissionByRefId(refId);

        if (permission.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4005,
                    "An inactive permission cannot be updated");
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Permission name is required");
        }

        String newName = request.name().trim();

        var existing = permissionRepository.findByName(newName);

        if (existing.isPresent()
                && !existing.get().getRefId().equals(refId)) {
            throw new QuizException(
                    StatusCode.E4009,
                    "A permission with this name already exists");
        }

        permission.setName(newName);
        permission.setDescription(request.description());

        return permissionRepository.save(permission);
    }

    public Permission deactivatePermission(
            String refId
    ) throws QuizException {

        Permission permission = getPermissionByRefId(refId);

        if (permission.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4009,
                    "Permission is already inactive");
        }

        // Protect essential system permissions
        if ("MANAGE_ROLE".equals(permission.getName())) {
            throw new QuizException(
                    StatusCode.E4005,
                    "The MANAGE_ROLE permission cannot be deactivated");
        }

        // Prevent deactivation while an active role uses this permission
        boolean assignedToActiveRole = roleRepository.findAll()
                .stream()
                .anyMatch(role ->
                        role.getStatus() == Status.ACTIVE
                                && role.getPermissionIds() != null
                                && role.getPermissionIds()
                                .contains(permission.getRefId()));

        if (assignedToActiveRole) {
            throw new QuizException(
                    StatusCode.E4005,
                    "Remove this permission from active roles before deactivating it");
        }

        permission.setStatus(Status.INACTIVE);

        return permissionRepository.save(permission);
    }

    public Permission reactivatePermission(
            String refId
    ) throws QuizException {

        Permission permission = getPermissionByRefId(refId);

        if (permission.getStatus() == Status.ACTIVE) {
            throw new QuizException(
                    StatusCode.E4009,
                    "Permission is already active");
        }

        permission.setStatus(Status.ACTIVE);

        return permissionRepository.save(permission);
    }

    public Permission createPermission(
            CreatePermissionRequest request
    ) throws QuizException {

        if (request.name() == null || request.name().isBlank()) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Permission name is required");
        }

        // Normalize the name, e.g. "Manage User" -> "MANAGE_USER"
        String name = request.name()
                .trim()
                .toUpperCase()
                .replaceAll("\\s+", "_");

        if (!name.matches("[A-Z][A-Z0-9_]*")) {
            throw new QuizException(
                    StatusCode.E4000,
                    "Permission name must contain only letters, numbers and underscores, and start with a letter");
        }

        if (permissionRepository.findByName(name).isPresent()) {
            throw new QuizException(
                    StatusCode.E4009,
                    "A permission with this name already exists");
        }

        Permission permission = new Permission();
        permission.setRefId(UUID.randomUUID().toString());
        permission.setName(name);
        permission.setDescription(request.description());
        permission.setStatus(Status.ACTIVE);

        return permissionRepository.save(permission);
    }
}