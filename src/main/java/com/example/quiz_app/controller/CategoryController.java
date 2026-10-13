package com.example.quiz_app.controller;

import com.example.quiz_app.dto.CategoryRequest;
import com.example.quiz_app.dto.CategoryResponse;
import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(
            @RequestBody(required = false) CategoryRequest request
    ) throws QuizException {

        CategoryResponse category =
                categoryService.createCategory(request);

        return ResponseEntity.ok(
                ApiResponse
                        .forStatus(StatusCode.S0000)
                        .withMessage("Category created successfully")
                        .withPayload(category)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCategories() {

        List<CategoryResponse> categories =
                categoryService.getAllCategories();

        return ResponseEntity.ok(
                ApiResponse
                        .forStatus(StatusCode.S0000)
                        .withMessage("Categories retrieved successfully")
                        .withPayload(categories, categories.size())
        );
    }

    @GetMapping("/{refId}")
    public ResponseEntity<ApiResponse> getCategory(
            @PathVariable String refId
    ) throws QuizException {

        CategoryResponse category =
                categoryService.getCategoryByRefId(refId);

        return ResponseEntity.ok(
                ApiResponse
                        .forStatus(StatusCode.S0000)
                        .withMessage("Category retrieved successfully")
                        .withPayload(category)
        );
    }

    @PutMapping("/{refId}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable String refId,
            @RequestBody(required = false) CategoryRequest request
    ) throws QuizException {

        CategoryResponse category =
                categoryService.updateCategory(refId, request);

        return ResponseEntity.ok(
                ApiResponse
                        .forStatus(StatusCode.S0000)
                        .withMessage("Category updated successfully")
                        .withPayload(category)
        );
    }

    @DeleteMapping("/{refId}")
    public ResponseEntity<ApiResponse> deactivateCategory(
            @PathVariable String refId
    ) throws QuizException {

        categoryService.deactivateCategory(refId);

        return ResponseEntity.ok(
                ApiResponse
                        .forStatus(StatusCode.S0000)
                        .withMessage("Category deactivated successfully")
        );
    }
    
    @PutMapping("/{refId}/reactivate")
    public ResponseEntity<ApiResponse> reactivateCategory(
            @PathVariable String refId
    ) throws QuizException {

        categoryService.reactivateCategory(refId);

        return ResponseEntity.ok(
                ApiResponse
                        .forStatus(StatusCode.S0000)
                        .withMessage("Category reactivated successfully")
        );
    }

}
