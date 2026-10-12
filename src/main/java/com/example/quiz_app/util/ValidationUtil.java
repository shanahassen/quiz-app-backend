package com.example.quiz_app.util;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.CategoryRequest;
import com.example.quiz_app.dto.LoginRequest;
import com.example.quiz_app.dto.RegisterRequest;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private ValidationUtil() {
    }

    public static void validateRegistration(RegisterRequest request) throws QuizException {
        if (request == null) {
            throw invalid("Registration details are required");
        }
        requireText(request.firstName(), "First name");
        requireText(request.lastName(), "Last name");
        validateEmail(request.email());
        validatePassword(request.password());
    }

    public static void validateLogin(LoginRequest request) throws QuizException {
        if (request == null) {
            throw invalid("Login details are required");
        }
        validateEmail(request.email());
        requireText(request.password(), "Password");
    }

    public static void validateCategory(CategoryRequest request) throws QuizException {
        if (request == null) {
            throw invalid("Category details are required");
        }
        requireText(request.name(), "Category name");
        if (request.name().trim().length() > 100) {
            throw invalid("Category name must be 100 characters or fewer");
        }
        if (request.description() != null && request.description().length() > 500) {
            throw invalid("Category description must be 500 characters or fewer");
        }
    }

    private static void validateEmail(String email) throws QuizException {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw invalid("A valid email address is required");
        }
    }

    private static void validatePassword(String password) throws QuizException {
        if (password == null || password.length() < 8 || password.length() > 72) {
            throw invalid("Password must be between 8 and 72 characters");
        }
    }

    private static void requireText(String value, String field) throws QuizException {
        if (value == null || value.isBlank()) {
            throw invalid(field + " is required");
        }
        if (value.trim().length() > 100) {
            throw invalid(field + " must be 100 characters or fewer");
        }
    }

    private static QuizException invalid(String message) {
        return new QuizException(StatusCode.E7001, message);
    }
}
