package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.DashboardStatsResponse;
import com.example.quiz_app.dto.NewestUserResponse;
import com.example.quiz_app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {

        DashboardStatsResponse stats =
                dashboardService.getDashboardStats();

        ApiResponse response = ApiResponse
                .forStatus(StatusCode.S0000)
                .withMessage("Dashboard statistics retrieved successfully.")
                .withPayload(stats);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/newest-users")
    public ResponseEntity<ApiResponse> getNewestUsers() {

        List<NewestUserResponse> newestUsers =
                dashboardService.getNewestUsers();

        ApiResponse response = ApiResponse
                .forStatus(StatusCode.S0000)
                .withMessage("Newest users retrieved successfully.")
                .withPayload(newestUsers, newestUsers.size());

        return ResponseEntity.ok(response);
    }
}