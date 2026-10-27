package com.example.quiz_app.service;

import com.example.quiz_app.common.QuizException;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.CertificateResponse;
import com.example.quiz_app.model.Certificate;
import com.example.quiz_app.model.Quiz;
import com.example.quiz_app.model.QuizAttempt;
import com.example.quiz_app.model.QuizAttemptStatus;
import com.example.quiz_app.model.RatingLevel;
import com.example.quiz_app.model.Status;
import com.example.quiz_app.model.User;
import com.example.quiz_app.repository.CertificateRepository;
import com.example.quiz_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public Certificate createForSubmittedAttempt(
            QuizAttempt attempt,
            Quiz quiz,
            User participant
    ) throws QuizException {

        if (attempt.getStatus() != QuizAttemptStatus.SUBMITTED
                || attempt.getScore() == null
                || attempt.getTotalQuestions() == null
                || attempt.getTotalQuestions() <= 0) {
            throw new QuizException(
                    StatusCode.E7001,
                    "A completed quiz attempt is required for a certificate.");
        }

        // Prevent creating another certificate for the same attempt.
        var existing = certificateRepository.findByAttemptRefId(
                attempt.getRefId());

        if (existing.isPresent()) {
            return existing.get();
        }

        double percentage = Math.round(
                attempt.getScore() * 10000.0
                        / attempt.getTotalQuestions()) / 100.0;

        Certificate certificate = new Certificate();
        certificate.setRefId(UUID.randomUUID().toString());
        certificate.setAttemptRefId(attempt.getRefId());
        certificate.setQuizRefId(quiz.getRefId());
        certificate.setParticipantRefId(participant.getRefId());

        certificate.setParticipantName(
                (participant.getFirstName() == null ? "" :
                        participant.getFirstName())
                        + (participant.getLastName() == null ? "" :
                        " " + participant.getLastName())
        );

        certificate.setQuizTitle(quiz.getTitle());
        certificate.setScore(attempt.getScore());
        certificate.setTotalQuestions(attempt.getTotalQuestions());
        certificate.setPercentage(percentage);
        certificate.setRatingLevel(
                RatingLevel.fromPercentage(percentage));
        certificate.setIssuedAt(
                attempt.getSubmittedAt() != null
                        ? attempt.getSubmittedAt()
                        : LocalDateTime.now());

        return certificateRepository.save(certificate);
    }

    public Page<CertificateResponse> getMyCertificates(
            String authenticatedEmail,
            Pageable pageable
    ) throws QuizException {

        User participant = getActiveParticipant(authenticatedEmail);

        return certificateRepository
                .findByParticipantRefIdOrderByIssuedAtDesc(
                        participant.getRefId(), pageable)
                .map(this::toResponse);
    }

    public byte[] downloadCertificate(
            String authenticatedEmail,
            String certificateRefId
    ) throws QuizException {

        User participant = getActiveParticipant(authenticatedEmail);

        Certificate certificate = certificateRepository
                .findByRefIdAndParticipantRefId(
                        certificateRefId,
                        participant.getRefId())
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4004,
                        "Certificate not found."));

        try {
            return generatePdf(certificate);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to generate the certificate PDF.", exception);
        }
    }

    private User getActiveParticipant(String email)
            throws QuizException {

        if (email == null || email.isBlank()) {
            throw new QuizException(
                    StatusCode.E4001,
                    "Authenticated user is required.");
        }

        return userRepository.findByEmailIgnoreCaseAndStatus(
                        email.trim(), Status.ACTIVE)
                .orElseThrow(() -> new QuizException(
                        StatusCode.E4001,
                        "Active user not found."));
    }

    private CertificateResponse toResponse(
            Certificate certificate) {

        return new CertificateResponse(
                certificate.getRefId(),
                certificate.getAttemptRefId(),
                certificate.getQuizRefId(),
                certificate.getQuizTitle(),
                certificate.getParticipantName(),
                certificate.getScore(),
                certificate.getTotalQuestions(),
                certificate.getPercentage(),
                certificate.getRatingLevel(),
                certificate.getIssuedAt()
        );
    }

    private byte[] generatePdf(Certificate certificate) throws IOException {
        final float pageWidth = 842;
        final float pageHeight = 595;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(
                    new PDRectangle(pageWidth, pageHeight)
            );
            document.addPage(page);

            PDType1Font regularFont = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );
            PDType1Font boldFont = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );
            PDType1Font italicFont = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_OBLIQUE
            );

            Color navy = new Color(35, 49, 72);
            Color gold = new Color(157, 122, 48);
            Color lightGold = new Color(249, 245, 234);
            Color gray = new Color(100, 108, 120);
            Color borderColor = new Color(205, 184, 133);
            Color white = Color.WHITE;

            String rating = certificate.getRatingLevel().name();

            String ratingLabel =
                    certificate.getRatingLevel().getDisplayName();

            String issueDate = certificate.getIssuedAt() == null
                    ? "N/A"
                    : certificate.getIssuedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern(
                            "dd MMMM yyyy"
                    )
            );

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                // White background
                content.setNonStrokingColor(white);
                content.addRect(0, 0, pageWidth, pageHeight);
                content.fill();

                // Elegant double border
                content.setStrokingColor(borderColor);
                content.setLineWidth(2);
                content.addRect(22, 22, pageWidth - 44, pageHeight - 44);
                content.stroke();

                content.setStrokingColor(gold);
                content.setLineWidth(0.7f);
                content.addRect(29, 29, pageWidth - 58, pageHeight - 58);
                content.stroke();

                // QUBE branding
                drawCertificateCentered(
                        content, boldFont, 25, navy,
                        "QUBE", pageWidth, 535
                );

                drawCertificateCentered(
                        content, regularFont, 9, gray,
                        "QUIZ AND LEARNING PLATFORM", pageWidth, 519
                );

                // Main certificate title
                drawCertificateCentered(
                        content, boldFont, 25, navy,
                        "CERTIFICATE OF ASSESSMENT", pageWidth, 481
                );

                // Decorative line
                content.setStrokingColor(gold);
                content.setLineWidth(1.2f);
                content.moveTo(300, 465);
                content.lineTo(542, 465);
                content.stroke();

                // Participant details
                drawCertificateCentered(
                        content, italicFont, 12, gray,
                        "QUBE certifies that", pageWidth, 441
                );

                drawCertificateCentered(
                        content, boldFont, 23, navy,
                        pdfSafeText(certificate.getParticipantName()),
                        pageWidth, 407
                );

                drawCertificateCentered(
                        content, regularFont, 12, gray,
                        "has completed the assessment", pageWidth, 381
                );

                drawCertificateCentered(
                        content, boldFont, 17, navy,
                        pdfSafeText(certificate.getQuizTitle()),
                        pageWidth, 354
                );

                drawCertificateCentered(
                        content, regularFont, 10, gray,
                        "The assessment result is recorded under the QUBE Rating System.",
                        pageWidth, 332
                );

                // Highlight the participant's rating
                content.setNonStrokingColor(lightGold);
                content.addRect(275, 285, 292, 32);
                content.fill();

                content.setStrokingColor(borderColor);
                content.setLineWidth(0.8f);
                content.addRect(275, 285, 292, 32);
                content.stroke();

                drawCertificateCentered(
                        content, boldFont, 16, gold,
                        ratingLabel.toUpperCase(java.util.Locale.ROOT),
                        pageWidth, 296
                );

                // Rating scale heading
                drawCertificateCentered(
                        content, boldFont, 11, navy,
                        "QUBE RATING SYSTEM", pageWidth, 262
                );

                // Rating scale rows
                String[][] ratingLevels = {
                        {"FOUNDATION", "0 - 49%"},
                        {"DEVELOPING", "50 - 64%"},
                        {"PROFICIENT", "65 - 79%"},
                        {"ADVANCED", "80 - 89%"},
                        {"EXPERT", "90 - 100%"}
                };

                float rowX = 296;
                float rowWidth = 250;
                float rowHeight = 15;
                float firstRowY = 240;
                float rowGap = 17;

                for (int i = 0; i < ratingLevels.length; i++) {
                    float rowY = firstRowY - (i * rowGap);
                    boolean isCurrentRating =
                            rating.equals(ratingLevels[i][0]);

                    if (isCurrentRating) {
                        content.setNonStrokingColor(lightGold);
                        content.addRect(
                                rowX, rowY - 3, rowWidth, rowHeight
                        );
                        content.fill();
                    }

                    Color labelColor = isCurrentRating ? gold : gray;

                    drawCertificateText(
                            content,
                            isCurrentRating ? boldFont : regularFont,
                            9,
                            labelColor,
                            ratingLevels[i][0],
                            rowX + 12,
                            rowY
                    );

                    drawCertificateText(
                            content,
                            isCurrentRating ? boldFont : regularFont,
                            9,
                            labelColor,
                            ratingLevels[i][1],
                            rowX + 176,
                            rowY
                    );
                }

                // Actual assessment result
                String scoreText =
                        "Score: " + certificate.getScore()
                                + " / " + certificate.getTotalQuestions()
                                + "     |     "
                                + String.format(
                                java.util.Locale.US,
                                "%.2f%%",
                                certificate.getPercentage()
                        );

                drawCertificateCentered(
                        content, boldFont, 10, navy,
                        scoreText, pageWidth, 139
                );

                // Issue date and certificate reference
                drawCertificateCentered(
                        content, regularFont, 9, gray,
                        "Issued on " + issueDate, pageWidth, 116
                );

                drawCertificateCentered(
                        content, regularFont, 8, gray,
                        "Certificate Reference: "
                                + pdfSafeText(certificate.getRefId()),
                        pageWidth, 98
                );

                // Footer
                drawCertificateCentered(
                        content, boldFont, 9, navy,
                        "QUBE Assessment System", pageWidth, 70
                );
            }

            document.save(outputStream);
        }

        return outputStream.toByteArray();
    }

    private void drawCertificateCentered(
            PDPageContentStream content,
            PDType1Font font,
            float fontSize,
            Color color,
            String text,
            float pageWidth,
            float y
    ) throws IOException {

        String safeText = pdfSafeText(text);

        float textWidth = font.getStringWidth(safeText)
                / 1000f * fontSize;

        float x = (pageWidth - textWidth) / 2f;

        drawCertificateText(
                content, font, fontSize, color, safeText, x, y
        );
    }

    private void drawCertificateText(
            PDPageContentStream content,
            PDType1Font font,
            float fontSize,
            Color color,
            String text,
            float x,
            float y
    ) throws IOException {

        content.beginText();
        content.setFont(font, fontSize);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(pdfSafeText(text));
        content.endText();
    }

    private String pdfSafeText(String value) {
        if (value == null) {
            return "";
        }

        // Avoid unsupported characters in PDFBox's standard fonts.
        return value.replaceAll("[^\\x20-\\x7E]", "?");
    }

    private void drawCentered(
            PDPageContentStream stream,
            String text,
            float y,
            PDType1Font font,
            float fontSize,
            Color color
    ) throws IOException {

        text = safeText(text);

        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (842 - textWidth) / 2;

        stream.beginText();
        stream.setFont(font, fontSize);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    private String safeText(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }

        // Standard PDF fonts support a limited character set.
        // Replace unsupported characters rather than failing PDF generation.
        return value.replaceAll("[^\\x20-\\x7E]", "?");
    }
}