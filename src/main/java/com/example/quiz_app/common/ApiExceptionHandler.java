package com.example.quiz_app.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(QuizException.class)
    public ResponseEntity<ApiResponse> handleQuizException(QuizException exception) {
        StatusCode statusCode = exception.getStatusCode();
        return ResponseEntity.status(toHttpStatus(statusCode))
                .body(ApiResponse.forStatus(statusCode).withMessage(exception.getResponseDescription()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleUnreadableRequest() {
        StatusCode statusCode = StatusCode.E7001;
        return ResponseEntity.badRequest()
                .body(ApiResponse.forStatus(statusCode).withMessage("Request body is missing or invalid"));
    }

    private HttpStatus toHttpStatus(StatusCode statusCode) {
        return switch (statusCode) {
            case E4001 -> HttpStatus.UNAUTHORIZED;
            case E4003 -> HttpStatus.FORBIDDEN;
            case E4004 -> HttpStatus.NOT_FOUND;
            case E4009 -> HttpStatus.CONFLICT;
            case E7000, E7001, E4000 -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
