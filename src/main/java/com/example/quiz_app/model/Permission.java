package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "permissions")
public class Permission {

    @Id
    private String refId;

    private String name;
    private String description;
}
