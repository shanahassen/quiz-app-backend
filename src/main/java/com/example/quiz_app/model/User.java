package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User extends BaseEntity {
    @Indexed(unique = true)
    private String refId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Status status;
    private String roleId;
}
