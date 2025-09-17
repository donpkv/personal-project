package com.careeros.dto.certificate;

import lombok.Data;

import java.util.UUID;

/**
 * DTO for certificate generation requests
 */
@Data
public class CertificateRequest {
    private UUID userId;
    private String skillName;
    private Integer score;
    private String certificateType;
    private String additionalInfo;
}
