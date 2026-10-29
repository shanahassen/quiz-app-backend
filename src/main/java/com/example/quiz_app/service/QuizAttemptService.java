package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.*;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.QuizAttempt;
import com.example.quiz_app.model.QuizAttemptStatus;
import com.example.quiz_app.model.QuizStatus;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.model.User;
import com.example.quiz_app.repository.QuizAttemptRepository;
import com.example.quiz_app.repository.QuizRepository;
import com.example.quiz_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.quiz_app.model.Category;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final CertificateService certificateService;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;
    private final MongoTemplate mongoTemplate;

    public QuizAttemptResponse startAttempt(
            String authenticatedEmail,
            StartQuizAttemptRequest request
    ) throws QuizException {

        if (request == null || request.quizRefId() == null
                || request.quizRefId().isBlank()) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz reference ID is required.");
        }

        User participant = getActiveParticipant(authenticatedEmail);

        Quiz quiz = quizRepository.findByRefId(request.quizRefId().trim())
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004, "Quiz not found."));

        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new QuizException(StatusCode.E4004,
                    "Published quiz not found.");
        }

        Category category = categoryRepository
                .findByRefId(quiz.getCategoryRefId())
                .filter(item -> item.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "This quiz is unavailable because its category is inactive."
                ));

        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            throw new QuizException(StatusCode.E7001,
                    "This quiz has no questions.");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setRefId(UUID.randomUUID().toString());
        attempt.setQuizRefId(quiz.getRefId());
        attempt.setParticipantRefId(participant.getRefId());
        attempt.setStatus(QuizAttemptStatus.IN_PROGRESS);
        attempt.setAnswers(new ArrayList<>());
        attempt.setTotalQuestions(quiz.getQuestions().size());
        attempt.setScore(null);
        LocalDateTime startedAt = LocalDateTime.now();

        int durationMinutes = quiz.getDurationMinutes() == null
                ? 30
                : quiz.getDurationMinutes();

        attempt.setStartedAt(startedAt);
        attempt.setDeadlineAt(startedAt.plusMinutes(durationMinutes));

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        auditLogService.logAction(
                authenticatedEmail,
                "QUIZ_STARTED",
                "QuizAttempt",
                savedAttempt.getRefId(),
                "Started quiz: " + quiz.getTitle()
        );

        return toResponse(savedAttempt);
    }

    public AnswerFeedbackResponse submitAnswer(
            String authenticatedEmail,
            String attemptRefId,
            SubmitAnswerRequest request
    ) throws QuizException {

        if (request == null
                || request.questionRefId() == null
                || request.questionRefId().isBlank()
                || request.selectedOptionRefId() == null
                || request.selectedOptionRefId().isBlank()) {
            throw new QuizException(StatusCode.E7001,
                    "Question and selected option are required.");
        }

        User participant = getActiveParticipant(authenticatedEmail);
        QuizAttempt attempt = getOwnedAttempt(
                attemptRefId, participant.getRefId());

        ensureInProgress(attempt);

        if (isExpired(attempt)) {
            finalizeAttempt(attempt);
            throw new QuizException(StatusCode.E7001,
                    "The quiz deadline has passed; the attempt was submitted.");
        }

        Quiz quiz = getQuizForAttempt(attempt);

        Quiz.Question question = quiz.getQuestions().stream()
                .filter(q -> q.getRefId().equals(request.questionRefId()))
                .findFirst()
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Question not found in this quiz."));

        Quiz.AnswerOption option = question.getOptions().stream()
                .filter(o -> o.getRefId().equals(
                        request.selectedOptionRefId()))
                .findFirst()
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Selected option not found for this question."));

        if (attempt.getAnswers().stream().anyMatch(
                a -> a.getQuestionRefId().equals(question.getRefId()))) {
            throw new QuizException(StatusCode.E4009,
                    "An answer has already been submitted for this question.");
        }

        QuizAttempt.Answer answer = new QuizAttempt.Answer();
        answer.setQuestionRefId(question.getRefId());
        answer.setSelectedOptionRefId(option.getRefId());
        answer.setCorrect(option.isCorrect());
        answer.setAnsweredAt(LocalDateTime.now());

        Query answerQuery = Query.query(Criteria.where("_id").is(attempt.getId())
                .and("status").is(QuizAttemptStatus.IN_PROGRESS)
                .and("deadlineAt").gt(LocalDateTime.now()));
        var answerUpdate = mongoTemplate.updateFirst(answerQuery,
                new Update().push("answers", answer), QuizAttempt.class);
        if (answerUpdate.getModifiedCount() == 0) {
            if (isExpired(attempt)) {
                finalizeAttempt(attempt);
                throw new QuizException(StatusCode.E7001,
                        "The quiz deadline has passed; the attempt was submitted.");
            }
            throw new QuizException(StatusCode.E7001,
                    "This attempt has already been submitted.");
        }
        QuizAttempt savedAttempt = attemptRepository.findByRefId(attemptRefId)
                .orElseThrow(() -> new QuizException(StatusCode.E4004, "Attempt not found."));

        return new AnswerFeedbackResponse(
                savedAttempt.getRefId(),
                question.getRefId(),
                option.getRefId(),
                option.isCorrect(),
                savedAttempt.getAnswers().size(),
                savedAttempt.getTotalQuestions()
        );
    }

    public QuizAttemptResponse submitAttempt(
            String authenticatedEmail,
            String attemptRefId
    ) throws QuizException {

        User participant = getActiveParticipant(authenticatedEmail);
        QuizAttempt attempt = getOwnedAttempt(
                attemptRefId, participant.getRefId());

        if (attempt.getStatus() == QuizAttemptStatus.IN_PROGRESS && isExpired(attempt)) {
            return toResponse(finalizeAttempt(attempt));
        }

        ensureInProgress(attempt);
        return toResponse(finalizeAttempt(attempt));
    }

    private QuizAttempt finalizeAttempt(QuizAttempt attempt) throws QuizException {
        Query claim = Query.query(Criteria.where("_id").is(attempt.getId())
                .and("status").is(QuizAttemptStatus.IN_PROGRESS));
        var claimed = mongoTemplate.updateFirst(claim,
                Update.update("status", QuizAttemptStatus.FINALIZING), QuizAttempt.class);
        if (claimed.getModifiedCount() == 0) {
            return attemptRepository.findByRefId(attempt.getRefId()).orElse(attempt);
        }

        attempt.setStatus(QuizAttemptStatus.FINALIZING);
        Quiz quiz = getQuizForAttempt(attempt);
        int score = 0;

        for (QuizAttempt.Answer answer : attempt.getAnswers()) {
            Quiz.Question question = quiz.getQuestions().stream()
                    .filter(q -> q.getRefId().equals(
                            answer.getQuestionRefId()))
                    .findFirst()
                    .orElseThrow(() -> new QuizException(
                            StatusCode.E4004,
                            "An answered question no longer exists."));

            boolean correct = question.getOptions().stream()
                    .anyMatch(option ->
                            option.getRefId().equals(
                                    answer.getSelectedOptionRefId())
                                    && option.isCorrect());

            answer.setCorrect(correct);

            if (correct) {
                score++;
            }
        }

        attempt.setScore(score);
        attempt.setStatus(QuizAttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        User participant = userRepository.findByRefId(attempt.getParticipantRefId())
                .orElseThrow(() -> new QuizException(StatusCode.E4001, "Active user not found."));

        auditLogService.logAction(
                participant.getEmail(),
                "QUIZ_SUBMITTED",
                "QuizAttempt",
                savedAttempt.getRefId(),
                "Submitted quiz: " + quiz.getTitle()
        );

        // Generate a certificate for every submitted attempt.
        certificateService.createForSubmittedAttempt(savedAttempt, quiz, participant);
        return savedAttempt;
    }

    public QuizAttemptResponse getAttempt(
            String authenticatedEmail,
            String attemptRefId
    ) throws QuizException {

        User participant = getActiveParticipant(authenticatedEmail);
        QuizAttempt attempt = getOwnedAttempt(
                attemptRefId, participant.getRefId());

        if (attempt.getStatus() == QuizAttemptStatus.IN_PROGRESS && isExpired(attempt)) {
            return toResponse(finalizeAttempt(attempt));
        }

        return toResponse(attempt);
    }

    @Scheduled(fixedDelay = 1000)
    public void finalizeExpiredAttempts() {
        attemptRepository.findByStatusAndDeadlineAtLessThanEqual(
                        QuizAttemptStatus.IN_PROGRESS, LocalDateTime.now())
                .forEach(attempt -> {
                    try {
                        finalizeAttempt(attempt);
                    } catch (QuizException exception) {
                        mongoTemplate.updateFirst(
                                Query.query(Criteria.where("_id").is(attempt.getId())
                                        .and("status").is(QuizAttemptStatus.FINALIZING)),
                                Update.update("status", QuizAttemptStatus.IN_PROGRESS),
                                QuizAttempt.class);
                    }
                });
    }

    public Page<QuizAttemptHistoryResponse> getAttemptHistory(
            String authenticatedEmail,
            Pageable pageable
    ) throws QuizException {

        User participant = getActiveParticipant(authenticatedEmail);

        return attemptRepository.findByParticipantRefId(
                        participant.getRefId(),
                        PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                Sort.by(Sort.Direction.DESC, "startedAt")
                        )
                )
                .map(attempt -> {
                    String quizTitle = quizRepository
                            .findByRefId(attempt.getQuizRefId())
                            .map(Quiz::getTitle)
                            .orElse("Quiz no longer available");

                    int answeredQuestions = attempt.getAnswers() == null
                            ? 0
                            : attempt.getAnswers().size();

                    return new QuizAttemptHistoryResponse(
                            attempt.getRefId(),
                            attempt.getQuizRefId(),
                            quizTitle,
                            attempt.getStatus(),
                            answeredQuestions,
                            attempt.getTotalQuestions() == null
                                    ? 0 : attempt.getTotalQuestions(),
                            attempt.getScore(),
                            attempt.getStartedAt(),
                            attempt.getSubmittedAt(),
                            attempt.isArchived()
                    );
                });
    }

    private User getActiveParticipant(String email) throws QuizException {
        if (email == null || email.isBlank()) {
            throw new QuizException(StatusCode.E4001,
                    "Authenticated user is required.");
        }

        return userRepository.findByEmailIgnoreCaseAndStatus(
                        email.trim(), Status.ACTIVE)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4001,
                        "Active user not found."));
    }

    private QuizAttempt getOwnedAttempt(
            String attemptRefId,
            String participantRefId
    ) throws QuizException {
        return attemptRepository.findByRefIdAndParticipantRefId(
                        attemptRefId, participantRefId)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004, "Attempt not found."));
    }

    private Quiz getQuizForAttempt(QuizAttempt attempt)
            throws QuizException {
        return quizRepository.findByRefId(attempt.getQuizRefId())
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004, "Quiz not found."));
    }

    private void ensureInProgress(QuizAttempt attempt)
            throws QuizException {
        if (attempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new QuizException(StatusCode.E7001,
                    "This attempt has already been submitted.");
        }
    }

    private boolean isExpired(QuizAttempt attempt) {
        return attempt.getDeadlineAt() != null
                && !LocalDateTime.now().isBefore(attempt.getDeadlineAt());
    }

    private QuizAttemptResponse toResponse(QuizAttempt attempt) {
        List<QuizAttempt.Answer> answers = attempt.getAnswers() == null
                ? List.of()
                : attempt.getAnswers();

        return new QuizAttemptResponse(
                attempt.getRefId(),
                attempt.getQuizRefId(),
                attempt.getStatus(),
                answers.size(),
                attempt.getTotalQuestions() == null
                        ? 0 : attempt.getTotalQuestions(),
                attempt.getScore(),
                attempt.getStartedAt(),
                attempt.getDeadlineAt(),
                attempt.getSubmittedAt()
        );
    }
}