package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.CreatePermissionRequest;
import com.example.quiz_app.dto.UpdatePermissionRequest;
import com.example.quiz_app.model.Permission;
import com.example.quiz_app.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_ROLE')")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPermissions() {
        var permissions = permissionService.getAllPermissions();

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Permissions retrieved successfully")
                        .withPayload(permissions, permissions.size())
        );
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse> getPermission(
            @PathVariable String refId
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Permission retrieved successfully")
                        .withPayload(
                                permissionService.getPermissionByRefId(refId))
        );
    }

    @PutMapping("/{refId}")
    public ResponseEntity<ApiResponse> updatePermission(
            @PathVariable String refId,
            @RequestBody UpdatePermissionRequest request
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Permission updated successfully")
                        .withPayload(
                                permissionService.updatePermission(
                                        refId, request))
        );
    }

    @PutMapping("/{refId}/deactivate")
    public ApiResponse deactivatePermission(
            @PathVariable String refId) throws QuizException {

        Permission permission = permissionService.deactivatePermission(refId);

        return ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Permission deactivated successfully")
                .withPayload(permission);
    }

    @PutMapping("/{refId}/reactivate")
    public ResponseEntity<ApiResponse> reactivatePermission(
            @PathVariable String refId
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Permission reactivated successfully")
                        .withPayload(
                                permissionService.reactivatePermission(refId))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPermission(
            @RequestBody CreatePermissionRequest request
    ) throws QuizException {

        Permission permission = permissionService.createPermission(request);

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Permission created successfully")
                        .withPayload(permission)
        );
    }
}