package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Digital Certificate entity for skill and learning path certifications
 */
@Entity
@Table(name = "digital_certificates", indexes = {
    @Index(name = "idx_cert_id", columnList = "certificate_id"),
    @Index(name = "idx_cert_recipient", columnList = "recipient_id"),
    @Index(name = "idx_cert_status", columnList = "status"),
    @Index(name = "idx_cert_skill", columnList = "skill_name")
})
public class DigitalCertificate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @NotBlank
    @Size(max = 50)
    @Column(name = "certificate_id", unique = true, nullable = false)
    private String certificateId;

    @NotBlank
    @Size(max = 200)
    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @NotBlank
    @Size(max = 100)
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false)
    private CertificateType certificateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revocation_reason")
    private String revocationReason;

    @Column(name = "blockchain_tx_hash")
    private String blockchainTxHash;

    @Column(name = "score")
    private Integer score; // For skill assessments (0-100)

    @Column(name = "completion_percentage")
    private Double completionPercentage; // For learning paths

    @Column(name = "total_hours")
    private Integer totalHours; // Total learning hours

    @Column(name = "verification_hash", nullable = false)
    private String verificationHash;

    @Column(name = "verification_url")
    private String verificationUrl;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "credential_id")
    private String credentialId; // For external integrations

    @Column(name = "blockchain_hash")
    private String blockchainHash; // Hash on blockchain for immutable verification

    @Column(name = "metadata", length = 2000)
    private String metadata; // JSON metadata for additional information

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true; // Whether certificate can be publicly verified

    @Column(name = "sharing_enabled", nullable = false)
    private Boolean sharingEnabled = true; // Whether recipient can share certificate

    // Enums
    public enum CertificateType {
        SKILL_PROFICIENCY,
        LEARNING_PATH_COMPLETION,
        ASSESSMENT_COMPLETION,
        COURSE_COMPLETION,
        PROJECT_COMPLETION,
        MENTORSHIP_COMPLETION,
        CUSTOM_ACHIEVEMENT
    }

    public enum CertificateStatus {
        ACTIVE,
        EXPIRED,
        REVOKED,
        SUSPENDED
    }

    // Constructors
    public DigitalCertificate() {
        this.issuedAt = LocalDateTime.now();
    }

    public DigitalCertificate(User recipient, String skillName, String issuer, CertificateType type) {
        this();
        this.recipient = recipient;
        this.skillName = skillName;
        this.issuer = issuer;
        this.certificateType = type;
    }

    // Getters and Setters
    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public String getVerificationHash() {
        return verificationHash;
    }

    public void setVerificationHash(String verificationHash) {
        this.verificationHash = verificationHash;
    }

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getBlockchainHash() {
        return blockchainHash;
    }

    public void setBlockchainHash(String blockchainHash) {
        this.blockchainHash = blockchainHash;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(Boolean sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    // Helper methods
    public boolean isValid() {
        return status == CertificateStatus.ACTIVE && 
               (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isRevoked() {
        return status == CertificateStatus.REVOKED;
    }

    public long getDaysUntilExpiry() {
        if (expiresAt == null) return Long.MAX_VALUE;
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).toDays();
    }

    public long getDaysSinceIssued() {
        return java.time.Duration.between(issuedAt, LocalDateTime.now()).toDays();
    }

    public String getDisplayTitle() {
        return skillName + " " + certificateType.name().replace("_", " ").toLowerCase();
    }
}
