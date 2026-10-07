package com.example.quiz_app.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApiResponse {
    private StatusCode statusCode;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime timestamp;
    private Object payload;
    private long totalElements;


    protected ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static ApiResponse forStatus(@NonNull StatusCode status) {
        ApiResponse response = new ApiResponse();
        response.statusCode = status;
        return response;
    }

    public ApiResponse withMessage(String message) {
        this.message = String.format(message);
        return this;
    }

    public ApiResponse withPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public ApiResponse withPayload(Object payload, long totalElements) {
        this.payload = payload;
        this.totalElements = totalElements;
        return this;
    }

}