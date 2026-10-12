package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "categories")
public class Category extends BaseEntity {
    @Indexed(unique = true)
    private String refId;
    private String name;
    private String description;
    private Status status;
}
