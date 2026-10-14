package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.UpdateRolePermissionsRequest;
import com.example.quiz_app.dto.UpdateRoleRequest;
import com.example.quiz_app.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MANAGE_ROLE')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllRoles() {
        var roles = roleService.getAllRoles();

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Roles retrieved successfully")
                        .withPayload(roles, roles.size())
        );
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse> getRole(
            @PathVariable String refId
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Role retrieved successfully")
                        .withPayload(roleService.getRoleByRefId(refId))
        );
    }

    @PutMapping("/{refId}")
    public ResponseEntity<ApiResponse> updateRole(
            @PathVariable String refId,
            @RequestBody UpdateRoleRequest request
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Role updated successfully")
                        .withPayload(roleService.updateRole(refId, request))
        );
    }

    @PutMapping("/{refId}/permissions")
    public ResponseEntity<ApiResponse> updatePermissions(
            @PathVariable String refId,
            @RequestBody UpdateRolePermissionsRequest request
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Role permissions updated successfully")
                        .withPayload(
                                roleService.updateRolePermissions(refId, request))
        );
    }

    @PutMapping("/{refId}/deactivate")
    public ResponseEntity<ApiResponse> deactivateRole(
            @PathVariable String refId
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Role deactivated successfully")
                        .withPayload(roleService.deactivateRole(refId))
        );
    }

    @PutMapping("/{refId}/reactivate")
    public ResponseEntity<ApiResponse> reactivateRole(
            @PathVariable String refId
    ) throws QuizException {

        return ResponseEntity.ok(
                ApiResponse.forStatus(StatusCode.S0000)
                        .withMessage("Role reactivated successfully")
                        .withPayload(roleService.reactivateRole(refId))
        );
    }
}