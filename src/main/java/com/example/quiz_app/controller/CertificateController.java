package com.example.quiz_app.controller;

import com.example.quiz_app.common.ApiResponse;
import com.example.quiz_app.common.StatusCode;
import com.example.quiz_app.dto.CertificateResponse;
import com.example.quiz_app.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<ApiResponse> getMyCertificates(
            Authentication authentication) throws Exception {

        List<CertificateResponse> certificates =
                certificateService.getMyCertificates(
                        authentication.getName());

        ApiResponse response = ApiResponse.forStatus(StatusCode.S0000)
                .withMessage("Certificates retrieved successfully.")
                .withPayload(certificates);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{certificateRefId}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable String certificateRefId,
            Authentication authentication) throws Exception {

        byte[] pdf = certificateService.downloadCertificate(
                authentication.getName(), certificateRefId);

        String filename = "QUBE-Certificate-"
                + certificateRefId + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename)
                        .build());
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}