package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.CategoryRequest;
import com.example.quiz_app.dto.CategoryResponse;
import com.example.quiz_app.model.Category;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.CategoryRepository;
import com.example.quiz_app.util.ValidationUtil;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.QuizAttemptStatus;
import com.example.quiz_app.model.QuizStatus;
import com.example.quiz_app.repository.QuizAttemptRepository;
import com.example.quiz_app.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AuditLogService auditLogService;

    public CategoryResponse createCategory(CategoryRequest request)
            throws QuizException {

        ValidationUtil.validateCategory(request);

        if (categoryRepository.existsByNameIgnoreCaseAndStatus(
                request.name().trim(), Status.ACTIVE)) {

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

        auditLogService.logAction(
                getActorEmail(),
                "CATEGORY_CREATED",
                "Category",
                savedCategory.getRefId(),
                "Created category: " + savedCategory.getName()
        );

        return toResponse(savedCategory);
    }

    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public CategoryResponse getCategoryByRefId(String refId)
            throws QuizException {

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Category not found"
                ));

        return toResponse(category);
    }

    public CategoryResponse updateCategory(
            String refId,
            CategoryRequest request
    ) throws QuizException {

        ValidationUtil.validateCategory(request);

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Category not found"
                ));

        if (category.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4004,
                    "Category is inactive"
            );
        }

        if (categoryRepository.existsByNameIgnoreCaseAndStatusAndRefIdNot(
                request.name().trim(), Status.ACTIVE, refId)) {

            throw new QuizException(
                    StatusCode.E4009,
                    "A category with this name already exists"
            );
        }

        category.setName(request.name().trim());
        category.setDescription(request.description());

        Category updatedCategory = categoryRepository.save(category);

        auditLogService.logAction(
                getActorEmail(),
                "CATEGORY_UPDATED",
                "Category",
                updatedCategory.getRefId(),
                "Updated category: " + updatedCategory.getName()
        );

        return toResponse(updatedCategory);
    }

    public void deactivateCategory(String refId)
            throws QuizException {

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Category not found"
                ));

        if (category.getStatus() == Status.INACTIVE) {
            throw new QuizException(
                    StatusCode.E4004,
                    "Category is already inactive"
            );
        }

        List<Quiz> categoryQuizzes =
                quizRepository.findByCategoryRefIdAndStatusNot(
                        category.getRefId(),
                        QuizStatus.ARCHIVED
                );

        long activeAttemptCount = categoryQuizzes.stream()
                .mapToLong(quiz ->
                        quizAttemptRepository.countByQuizRefIdAndStatus(
                                quiz.getRefId(),
                                QuizAttemptStatus.IN_PROGRESS
                        ))
                .sum();

        if (activeAttemptCount > 0) {
            throw new QuizException(
                    StatusCode.E7001,
                    "Cannot deactivate this category because "
                            + activeAttemptCount
                            + " active quiz attempt(s) are still in progress. "
                            + "Please wait until they are completed."
            );
        }

        category.setStatus(Status.INACTIVE);
        Category savedCategory = categoryRepository.save(category);

        auditLogService.logAction(
                getActorEmail(),
                "CATEGORY_DEACTIVATED",
                "Category",
                savedCategory.getRefId(),
                "Deactivated category: " + savedCategory.getName()
        );
    }

    public void reactivateCategory(String refId)
            throws QuizException {

        Category category = categoryRepository.findByRefId(refId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Category not found"
                ));

        if (category.getStatus() == Status.ACTIVE) {
            throw new QuizException(
                    StatusCode.E4004,
                    "Category is already active"
            );
        }

        category.setStatus(Status.ACTIVE);
        Category savedCategory = categoryRepository.save(category);

        auditLogService.logAction(
                getActorEmail(),
                "CATEGORY_REACTIVATED",
                "Category",
                savedCategory.getRefId(),
                "Reactivated category: " + savedCategory.getName()
        );
    }

    private String getActorEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            return "UNKNOWN";
        }

        return authentication.getName();
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
