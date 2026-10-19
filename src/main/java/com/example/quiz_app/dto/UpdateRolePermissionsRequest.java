package com.example.quiz_app.dto;

import java.util.List;

public record UpdateRolePermissionsRequest(
        List<String> permissionRefIds
) {}
