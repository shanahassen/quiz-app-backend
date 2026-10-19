package com.example.quiz_app.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "roles")
public class Role extends BaseEntity{
    @Indexed(unique = true)
    private String refId;

    @Indexed(unique = true)
    private RoleName roleName;

    private String description;

    private List<String> permissionIds = new ArrayList<>();

    private Status status = Status.ACTIVE;
}