package com.example.quiz_app.service;

import com.example.quiz_app.dto.CategoryRequest;
import com.example.quiz_app.dto.CategoryResponse;
import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.model.Category;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import com.example.quiz_app.util.ValidationUtil;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request)
            throws QuizException {

        ValidationUtil.validateCategory(request);

        if (categoryRepository.existsByNameIgnoreCaseAndStatus(
                request.name().trim(),
                Status.ACTIVE)) {

            throw new QuizException(
                    StatusCode.E4009,
                    "A category with this name already exists"
            );
        }

        Category category = new Category();

        category.setRefId(UUID.randomUUID().toString());
        category.setName(request.name().trim());
        category.setDescription(request.description());
        category.setStatus(Status.ACTIVE);

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryByRefId(String refId)
            throws QuizException {

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() ->
                        new QuizException(
                                StatusCode.E4004,
                                "Category not found"
                        )
                );

        return toResponse(category);
    }

    public CategoryResponse updateCategory(
            String refId,
            CategoryRequest request
    ) throws QuizException {

        ValidationUtil.validateCategory(request);

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() ->
                        new QuizException(
                                StatusCode.E4004,
                                "Category not found"
                        )
                );

        if (category.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4004,
                    "Category is inactive"
            );
        }

        if (categoryRepository.existsByNameIgnoreCaseAndStatusAndRefIdNot(
                request.name().trim(), Status.ACTIVE, refId)) {
            throw new QuizException(StatusCode.E4009, "A category with this name already exists");
        }

        category.setName(request.name().trim());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);

        return toResponse(updatedCategory);
    }

    public void deactivateCategory(String refId)
            throws QuizException {

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() ->
                        new QuizException(
                                StatusCode.E4004,
                                "Category not found"
                        )
                );

        if (category.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4004,
                    "Category is already inactive"
            );
        }

        category.setStatus(Status.INACTIVE);

        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getRefId(),
                category.getName(),
                category.getDescription(),
                category.getStatus(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
