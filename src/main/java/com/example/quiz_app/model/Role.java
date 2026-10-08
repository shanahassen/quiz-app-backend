package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "roles")
public class Role extends BaseEntity{

    private RoleName roleName;
    private String description;
    private List<String> permissionIds;
}