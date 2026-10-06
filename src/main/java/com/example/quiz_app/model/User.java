package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String userId;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserStatus userStatus;
    private String roleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
