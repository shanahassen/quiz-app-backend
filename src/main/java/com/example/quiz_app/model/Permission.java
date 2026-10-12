package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "permissions")
public class Permission extends BaseEntity{
    @Indexed(unique = true)
    private String refId;
    private String name;
    private String description;
}
