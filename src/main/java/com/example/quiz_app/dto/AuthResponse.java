package com.example.quiz_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
