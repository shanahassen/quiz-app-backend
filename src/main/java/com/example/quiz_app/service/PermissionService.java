package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.UpdatePermissionRequest;
import com.example.quiz_app.model.Permission;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.PermissionRepository;
import com.example.quiz_app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;

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

        if (request == null
                || request.name() == null
                || request.name().isBlank()) {
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

        Permission savedPermission =
                permissionRepository.save(permission);

        auditLogService.logAction(
                getCurrentActorEmail(),
                "PERMISSION_UPDATED",
                "Permission",
                savedPermission.getRefId(),
                "Updated permission: " + savedPermission.getName()
        );

        return savedPermission;
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

        // Protect essential system permissions.
        if ("MANAGE_ROLE".equals(permission.getName())) {
            throw new QuizException(
                    StatusCode.E4005,
                    "The MANAGE_ROLE permission cannot be deactivated");
        }

        // Prevent deactivation while an active role uses this permission.
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

        Permission savedPermission =
                permissionRepository.save(permission);

        auditLogService.logAction(
                getCurrentActorEmail(),
                "PERMISSION_DEACTIVATED",
                "Permission",
                savedPermission.getRefId(),
                "Deactivated permission: " + savedPermission.getName()
        );

        return savedPermission;
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

        Permission savedPermission =
                permissionRepository.save(permission);

        auditLogService.logAction(
                getCurrentActorEmail(),
                "PERMISSION_REACTIVATED",
                "Permission",
                savedPermission.getRefId(),
                "Reactivated permission: " + savedPermission.getName()
        );

        return savedPermission;
    }

    private String getCurrentActorEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {
            return "UNKNOWN";
        }

        return authentication.getName();
    }
}