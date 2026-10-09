package com.example.quiz_app.model;

import lombok.Data;
<<<<<<< Updated upstream
=======
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
>>>>>>> Stashed changes
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User extends BaseEntity {

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String password;
    private UserStatus status;
    private String roleId;
<<<<<<< Updated upstream
}
=======
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
>>>>>>> Stashed changes
