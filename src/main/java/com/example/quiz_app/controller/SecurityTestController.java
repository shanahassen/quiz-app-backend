package com.example.quiz_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SecurityTestController {

    @GetMapping("/authenticated")
    public String authenticated() {
        return "You are authenticated";
    }

    @GetMapping("/quiz-management")
    public String quizManagement() {
        return "You have  MANAGE_QUIZ permission";
    }

    @GetMapping("/profile")
    public String profile() {
        return "You have  MANAGE_PROFILE permission";
    }
}