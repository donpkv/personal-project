package com.careeros.dto.certificate;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for certificate validation response
 */
@Data
public class CertificateValidationResponse {
    private String certificateId;
    private boolean valid;
    private String message;
    private String recipientName;
    private String skillName;
    private String issuer;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private Integer score;
    private String blockchainTxHash;
    private LocalDateTime validatedAt;
    private Double completionPercentage;
}
