package com.careeros.dto.certificate;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for certificate response
 */
@Data
public class CertificateResponse {
    private String certificateId;
    private String certificateUrl;
    private String verificationUrl;
    private String verificationHash;
    private String recipientName;
    private String skillName;
    private Integer score;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private String issuer;
    private String sharingCredentials;
    private String linkedinShareUrl;
    private String twitterShareUrl;
    private String title;
    private java.time.LocalDate issuedDate;
    private String pdfUrl;
    private String credentialId;
    private String linkedInSharingUrl;
}
