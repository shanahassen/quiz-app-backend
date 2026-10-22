package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.ParticipantQuizResponse;
import com.example.quiz_app.dto.QuizRequest;
import com.example.quiz_app.dto.ParticipantQuizResponse;
import com.example.quiz_app.model.Category;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.QuizStatus;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.repository.CategoryRepository;
import com.example.quiz_app.repository.QuizAttemptRepository;
import com.example.quiz_app.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.quiz_app.model.QuizAttemptStatus;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;

    public Quiz createQuiz(QuizRequest request) throws QuizException {
        validateRequest(request);

        String categoryRefId = request.categoryRefId().trim();
        String title = request.title().trim();

        Category category = categoryRepository.findByRefId(categoryRefId)
                .orElseThrow(() ->
                        new QuizException(StatusCode.E4004,
                                "Category not found."));

        if (category.getStatus() != Status.ACTIVE) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz must belong to an active category.");
        }

        if (quizRepository.existsByTitleIgnoreCaseAndCategoryRefIdAndStatusNot(
                title, categoryRefId, QuizStatus.ARCHIVED)) {
            throw new QuizException(StatusCode.E4009,
                    "A quiz with this title already exists in this category.");
        }

        Quiz quiz = new Quiz();
        quiz.setRefId(UUID.randomUUID().toString());
        quiz.setCategoryRefId(categoryRefId);
        quiz.setTitle(title);
        quiz.setDescription(
                request.description() == null
                        ? ""
                        : request.description().trim()
        );
        quiz.setStatus(QuizStatus.DRAFT);

        List<Quiz.Question> questions = new ArrayList<>();

        for (QuizRequest.QuestionRequest questionRequest : request.questions()) {
            Quiz.Question question = new Quiz.Question();
            question.setRefId(UUID.randomUUID().toString());
            question.setText(questionRequest.text().trim());
            question.setHint(
                    questionRequest.hint() == null
                            ? ""
                            : questionRequest.hint().trim()
            );

            List<Quiz.AnswerOption> options = new ArrayList<>();

            for (QuizRequest.AnswerOptionRequest optionRequest
                    : questionRequest.options()) {
                Quiz.AnswerOption option = new Quiz.AnswerOption();
                option.setRefId(UUID.randomUUID().toString());
                option.setText(optionRequest.text().trim());
                option.setCorrect(optionRequest.correct());
                options.add(option);
            }

            question.setOptions(options);
            questions.add(question);
        }

        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(String refId, QuizRequest request)
            throws QuizException {
        validateRequest(request);

        Quiz quiz = getQuizByRefId(refId);

        if (quiz.getStatus() != QuizStatus.DRAFT
                && quiz.getStatus() != QuizStatus.ARCHIVED) {
            throw new QuizException(StatusCode.E7001,
                    "Only draft or archived quizzes can be updated.");
        }

        if (quizAttemptRepository.existsByQuizRefId(refId)) {
            throw new QuizException(StatusCode.E7001,
                    "A quiz cannot be updated after an attempt has been started.");
        }

        String categoryRefId = request.categoryRefId().trim();
        String title = request.title().trim();

        Category category = categoryRepository.findByRefId(categoryRefId)
                .orElseThrow(() ->
                        new QuizException(StatusCode.E4004,
                                "Category not found."));

        if (category.getStatus() != Status.ACTIVE) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz must belong to an active category.");
        }

        if (quizRepository
                .existsByTitleIgnoreCaseAndCategoryRefIdAndStatusNotAndRefIdNot(
                        title, categoryRefId, QuizStatus.ARCHIVED, refId)) {
            throw new QuizException(StatusCode.E4009,
                    "A quiz with this title already exists in this category.");
        }

        quiz.setCategoryRefId(categoryRefId);
        quiz.setTitle(title);
        quiz.setDescription(
                request.description() == null
                        ? ""
                        : request.description().trim()
        );

        List<Quiz.Question> questions = new ArrayList<>();

        for (QuizRequest.QuestionRequest questionRequest : request.questions()) {
            Quiz.Question question = new Quiz.Question();
            question.setRefId(
                    questionRequest.refId() == null
                            || questionRequest.refId().isBlank()
                            ? UUID.randomUUID().toString()
                            : questionRequest.refId()
            );
            question.setText(questionRequest.text().trim());
            question.setHint(
                    questionRequest.hint() == null
                            ? ""
                            : questionRequest.hint().trim()
            );

            List<Quiz.AnswerOption> options = new ArrayList<>();

            for (QuizRequest.AnswerOptionRequest optionRequest
                    : questionRequest.options()) {
                Quiz.AnswerOption option = new Quiz.AnswerOption();
                option.setRefId(
                        optionRequest.refId() == null
                                || optionRequest.refId().isBlank()
                                ? UUID.randomUUID().toString()
                                : optionRequest.refId()
                );
                option.setText(optionRequest.text().trim());
                option.setCorrect(optionRequest.correct());
                options.add(option);
            }

            question.setOptions(options);
            questions.add(question);
        }

        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();

        for (Quiz quiz : quizzes) {
            long activeCount =
                    quizAttemptRepository.countByQuizRefIdAndStatus(
                            quiz.getRefId(),
                            QuizAttemptStatus.IN_PROGRESS
                    );

            quiz.setActiveAttemptCount(activeCount);
        }

        return quizzes;
    }

    public Quiz getQuizByRefId(String refId) throws QuizException {
        return quizRepository.findByRefId(refId)
                .orElseThrow(() ->
                        new QuizException(StatusCode.E4004,
                                "Quiz not found."));
    }

    public Quiz publishQuiz(String refId) throws QuizException {
        Quiz quiz = getQuizByRefId(refId);

        if (quiz.getStatus() != QuizStatus.DRAFT
                && quiz.getStatus() != QuizStatus.ARCHIVED) {
            throw new QuizException(StatusCode.E7001,
                    "Only draft or archived quizzes can be published.");
        }

        Category category = categoryRepository.findByRefId(
                quiz.getCategoryRefId()
        ).orElseThrow(() ->
                new QuizException(StatusCode.E4004,
                        "Quiz category not found."));

        if (category.getStatus() != Status.ACTIVE) {
            throw new QuizException(StatusCode.E7001,
                    "Cannot publish a quiz under an inactive category.");
        }

        quiz.setStatus(QuizStatus.PUBLISHED);
        return quizRepository.save(quiz);
    }

    public Quiz moveQuizToDraft(String refId) throws QuizException {
        Quiz quiz = getQuizByRefId(refId);

        if (quiz.getStatus() != QuizStatus.PUBLISHED
                && quiz.getStatus() != QuizStatus.ARCHIVED) {
            throw new QuizException(StatusCode.E7001,
                    "Only published or archived quizzes can be moved to draft.");
        }

        quiz.setStatus(QuizStatus.DRAFT);
        return quizRepository.save(quiz);
    }

    public Quiz archiveQuiz(String refId) throws QuizException {
        Quiz quiz = getQuizByRefId(refId);

        if (quiz.getStatus() == QuizStatus.ARCHIVED) {
            throw new QuizException(
                    StatusCode.E7001,
                    "Quiz is already archived."
            );
        }

        long activeCount =
                quizAttemptRepository.countByQuizRefIdAndStatus(
                        quiz.getRefId(),
                        QuizAttemptStatus.IN_PROGRESS
                );

        if (activeCount > 0) {
            String participantText =
                    activeCount == 1 ? "participant is" : "participants are";

            throw new QuizException(
                    StatusCode.E7001,
                    "Cannot archive this quiz because "
                            + activeCount + " " + participantText
                            + " currently taking it. Please wait until "
                            + "all active attempts are completed."
            );
        }

        quiz.setStatus(QuizStatus.ARCHIVED);
        return quizRepository.save(quiz);
    }

    public List<ParticipantQuizResponse> getPublishedQuizzes() {
        return quizRepository.findByStatus(QuizStatus.PUBLISHED)
                .stream()
                .map(this::toParticipantResponse)
                .toList();
    }

    // Participants may open published quizzes only.
    public ParticipantQuizResponse getPublishedQuizByRefId(String refId)
            throws QuizException {
        Quiz quiz = getQuizByRefId(refId);

        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new QuizException(StatusCode.E4004,
                    "Published quiz not found.");
        }

        return toParticipantResponse(quiz);
    }

    // Return a hint only when explicitly requested.
    public String getQuestionHint(String quizRefId, String questionRefId)
            throws QuizException {
        Quiz quiz = getQuizByRefId(quizRefId);

        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new QuizException(StatusCode.E4004,
                    "Published quiz not found.");
        }

        Quiz.Question question = quiz.getQuestions().stream()
                .filter(q -> q.getRefId().equals(questionRefId))
                .findFirst()
                .orElseThrow(() ->
                        new QuizException(StatusCode.E4004,
                                "Question not found."));

        if (question.getHint() == null || question.getHint().isBlank()) {
            return "No hint is available for this question.";
        }

        return question.getHint();
    }

    private ParticipantQuizResponse toParticipantResponse(Quiz quiz) {
        List<ParticipantQuizResponse.QuestionResponse> questions =
                quiz.getQuestions().stream()
                        .map(question ->
                                new ParticipantQuizResponse.QuestionResponse(
                                        question.getRefId(),
                                        question.getText(),
                                        question.getHint() != null
                                                && !question.getHint().isBlank(),
                                        question.getOptions().stream()
                                                .map(option ->
                                                        new ParticipantQuizResponse.OptionResponse(
                                                                option.getRefId(),
                                                                option.getText()
                                                        ))
                                                .toList()
                                ))
                        .toList();

        return new ParticipantQuizResponse(
                quiz.getRefId(),
                quiz.getCategoryRefId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getStatus(),
                questions
        );
    }

    private void validateRequest(QuizRequest request) throws QuizException {
        if (request == null) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz request is required.");
        }

        if (request.categoryRefId() == null
                || request.categoryRefId().isBlank()) {
            throw new QuizException(StatusCode.E7001,
                    "Category is required.");
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz title is required.");
        }

        if (request.title().trim().length() > 150) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz title must not exceed 150 characters.");
        }

        if (request.description() != null
                && request.description().length() > 1000) {
            throw new QuizException(StatusCode.E7001,
                    "Quiz description must not exceed 1000 characters.");
        }

        if (request.questions() == null
                || request.questions().size() < 5
                || request.questions().size() > 50) {
            throw new QuizException(StatusCode.E7001,
                    "A quiz must contain between 5 and 50 questions.");
        }

        for (int i = 0; i < request.questions().size(); i++) {
            QuizRequest.QuestionRequest question = request.questions().get(i);

            if (question == null
                    || question.text() == null
                    || question.text().isBlank()) {
                throw new QuizException(StatusCode.E7001,
                        "Question " + (i + 1) + " must have text.");
            }

            if (question.options() == null
                    || question.options().size() < 2
                    || question.options().size() > 6) {
                throw new QuizException(StatusCode.E7001,
                        "Question " + (i + 1)
                                + " must have between 2 and 6 options.");
            }

            int correctCount = 0;
            HashSet<String> optionTexts = new HashSet<>();

            for (QuizRequest.AnswerOptionRequest option : question.options()) {
                if (option == null
                        || option.text() == null
                        || option.text().isBlank()) {
                    throw new QuizException(StatusCode.E7001,
                            "Every answer option must have text.");
                }

                String normalizedText = option.text()
                        .trim()
                        .toLowerCase(Locale.ROOT);

                if (!optionTexts.add(normalizedText)) {
                    throw new QuizException(StatusCode.E7001,
                            "Answer options must not be duplicated within a question.");
                }

                if (option.correct()) {
                    correctCount++;
                }
            }

            if (correctCount != 1) {
                throw new QuizException(StatusCode.E7001,
                        "Each question must have exactly one correct answer.");
            }
        }
    }
}
