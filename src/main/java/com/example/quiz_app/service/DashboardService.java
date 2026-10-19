package com.example.quiz_app.service;

import com.example.quiz_app.dto.DashboardStatsResponse;
import com.example.quiz_app.dto.NewestUserResponse;
import com.example.quiz_app.model.QuizStatus;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.RoleName;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.CategoryRepository;
import com.example.quiz_app.repository.QuizRepository;
import com.example.quiz_app.repository.RoleRepository;
import com.example.quiz_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final RoleRepository roleRepository;

    public DashboardStatsResponse getDashboardStats() {

        long activeUsers =
                userRepository.countByStatus(Status.ACTIVE);

        long activeCategories =
                categoryRepository.countByStatus(Status.ACTIVE);

        long draftQuizzes =
                quizRepository.countByStatus(QuizStatus.DRAFT);

        long publishedQuizzes =
                quizRepository.countByStatus(QuizStatus.PUBLISHED);

        long archivedQuizzes =
                quizRepository.countByStatus(QuizStatus.ARCHIVED);

        return new DashboardStatsResponse(
                activeUsers,
                activeCategories,
                draftQuizzes,
                publishedQuizzes,
                archivedQuizzes
        );
    }

    public List<NewestUserResponse> getNewestUsers() {

        Role participantRole = roleRepository
                .findByRoleName(RoleName.PARTICIPANT)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Participant role was not found."
                        )
                );

        return userRepository
                .findTop3ByRoleIdOrderByCreatedAtDesc(
                        participantRole.getId()
                )
                .stream()
                .map(user -> new NewestUserResponse(
                        user.getRefId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getCreatedAt()
                ))
                .toList();
    }
}