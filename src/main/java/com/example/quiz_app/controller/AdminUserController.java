//package com.example.quiz_app.controller;
//
//import com.example.quiz_app.common.ApiResponse;
//import com.example.quiz_app.common.QuizException;
//import com.example.quiz_app.common.StatusCode;
//import com.example.quiz_app.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/admin/users")
//@RequiredArgsConstructor
//public class AdminUserController {
//
//    private final AuthService authService;
//
//    @PutMapping("/{refId}/admin")
//    public ResponseEntity<ApiResponse> assignAdminRole(@PathVariable String refId) throws QuizException {
//        authService.assignAdminRole(refId);
//        return ResponseEntity.ok(ApiResponse.forStatus(StatusCode.S0000)
//                .withMessage("Admin role assigned successfully"));
//    }
//}