package com.careeros.service;

import com.careeros.dto.certificate.CertificateRequest;
import com.careeros.dto.certificate.CertificateResponse;
import com.careeros.dto.certificate.CertificateValidationResponse;
import com.careeros.entity.User;
import com.careeros.entity.DigitalCertificate;
import com.careeros.repository.DigitalCertificateRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Certificate Service for generating and managing digital certificates
 */
@Service
@Transactional
public class CertificateService {

    private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);

    @Autowired
    private DigitalCertificateRepository certificateRepository;

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private EmailService emailService;

    @Value("${app.certificate.base-url:https://career-os.com/certificates}")
    private String certificateBaseUrl;

    @Value("${app.certificate.issuer:Career OS}")
    private String certificateIssuer;

    /**
     * Generate a digital certificate for skill completion
     */
    public String generateCertificate(User user, String skillName, Integer score) {
        logger.info("Generating certificate for user {} for skill {}", user.getId(), skillName);

        try {
            // Create certificate record
            DigitalCertificate certificate = new DigitalCertificate();
            certificate.setRecipient(user);
            certificate.setSkillName(skillName);
            certificate.setIssuer(certificateIssuer);
            certificate.setScore(score);
            certificate.setIssuedAt(LocalDateTime.now());
            certificate.setExpiresAt(LocalDateTime.now().plusYears(3)); // 3 years validity
            certificate.setCertificateType(DigitalCertificate.CertificateType.SKILL_PROFICIENCY);
            certificate.setStatus(DigitalCertificate.CertificateStatus.ACTIVE);

            // Generate unique certificate ID
            String certificateId = generateCertificateId();
            certificate.setCertificateId(certificateId);

            // Generate verification hash
            String verificationHash = generateVerificationHash(certificate);
            certificate.setVerificationHash(verificationHash);

            // Generate PDF certificate
            byte[] pdfBytes = generatePdfCertificate(certificate);
            String pdfUrl = saveCertificatePdf(pdfBytes, certificateId);
            certificate.setPdfUrl(pdfUrl);

            // Generate verification URL
            String verificationUrl = generateVerificationUrl(certificateId);
            certificate.setVerificationUrl(verificationUrl);

            // Save to database
            DigitalCertificate savedCertificate = certificateRepository.save(certificate);

            // Record on blockchain for immutable verification
            recordOnBlockchain(savedCertificate);

            // Send certificate email
            emailService.sendCertificateEmail(user, savedCertificate);

            logger.info("Certificate generated successfully with ID {}", certificateId);
            return pdfUrl;

        } catch (Exception e) {
            logger.error("Error generating certificate", e);
            throw new RuntimeException("Failed to generate certificate", e);
        }
    }

    /**
     * Generate certificate for learning path completion
     */
    public String generatePathCompletionCertificate(User user, String pathName, Double completionPercentage, 
                                                   Integer totalHours) {
        logger.info("Generating path completion certificate for user {} for path {}", user.getId(), pathName);

        DigitalCertificate certificate = new DigitalCertificate();
        certificate.setRecipient(user);
        certificate.setSkillName(pathName);
        certificate.setIssuer(certificateIssuer);
        certificate.setCompletionPercentage(completionPercentage);
        certificate.setTotalHours(totalHours);
        certificate.setIssuedAt(LocalDateTime.now());
        certificate.setExpiresAt(LocalDateTime.now().plusYears(5)); // 5 years for path completion
        certificate.setCertificateType(DigitalCertificate.CertificateType.LEARNING_PATH_COMPLETION);
        certificate.setStatus(DigitalCertificate.CertificateStatus.ACTIVE);

        String certificateId = generateCertificateId();
        certificate.setCertificateId(certificateId);

        String verificationHash = generateVerificationHash(certificate);
        certificate.setVerificationHash(verificationHash);

        try {
            byte[] pdfBytes = generatePathCompletionPdf(certificate);
            String pdfUrl = saveCertificatePdf(pdfBytes, certificateId);
            certificate.setPdfUrl(pdfUrl);

            String verificationUrl = generateVerificationUrl(certificateId);
            certificate.setVerificationUrl(verificationUrl);

            DigitalCertificate savedCertificate = certificateRepository.save(certificate);
            recordOnBlockchain(savedCertificate);
            emailService.sendCertificateEmail(user, savedCertificate);

            return pdfUrl;

        } catch (Exception e) {
            logger.error("Error generating path completion certificate", e);
            throw new RuntimeException("Failed to generate certificate", e);
        }
    }

    /**
     * Validate a certificate by ID and verification hash
     */
    public CertificateValidationResponse validateCertificate(String certificateId, String verificationHash) {
        logger.info("Validating certificate with ID {}", certificateId);

        DigitalCertificate certificate = certificateRepository.findByCertificateId(certificateId)
                .orElse(null);

        CertificateValidationResponse response = new CertificateValidationResponse();
        response.setCertificateId(certificateId);

        if (certificate == null) {
            response.setValid(false);
            response.setMessage("Certificate not found");
            return response;
        }

        if (!certificate.getVerificationHash().equals(verificationHash)) {
            response.setValid(false);
            response.setMessage("Invalid verification hash");
            return response;
        }

        if (certificate.getStatus() != DigitalCertificate.CertificateStatus.ACTIVE) {
            response.setValid(false);
            response.setMessage("Certificate is not active");
            return response;
        }

        if (certificate.getExpiresAt() != null && certificate.getExpiresAt().isBefore(LocalDateTime.now())) {
            response.setValid(false);
            response.setMessage("Certificate has expired");
            return response;
        }

        // Certificate is valid
        response.setValid(true);
        response.setMessage("Certificate is valid");
        response.setRecipientName(certificate.getRecipient().getFullName());
        response.setSkillName(certificate.getSkillName());
        response.setIssuer(certificate.getIssuer());
        response.setIssuedAt(certificate.getIssuedAt());
        response.setExpiresAt(certificate.getExpiresAt());
        response.setScore(certificate.getScore());
        response.setCompletionPercentage(certificate.getCompletionPercentage());

        return response;
    }

    /**
     * Get user's certificates
     */
    public List<DigitalCertificate> getUserCertificates(UUID userId) {
        return certificateRepository.findByRecipientIdAndStatusOrderByIssuedAtDesc(
                userId, DigitalCertificate.CertificateStatus.ACTIVE);
    }

    /**
     * Revoke a certificate
     */
    public void revokeCertificate(String certificateId, String reason) {
        logger.info("Revoking certificate {}", certificateId);

        DigitalCertificate certificate = certificateRepository.findByCertificateId(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificate.setStatus(DigitalCertificate.CertificateStatus.REVOKED);
        certificate.setRevokedAt(LocalDateTime.now());
        certificate.setRevocationReason(reason);

        certificateRepository.save(certificate);

        // Record revocation on blockchain
        recordRevocationOnBlockchain(certificate);
    }

    /**
     * Generate certificate sharing credentials for LinkedIn, etc.
     */
    public CertificateResponse generateSharingCredentials(String certificateId) {
        DigitalCertificate certificate = certificateRepository.findByCertificateId(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        CertificateResponse response = new CertificateResponse();
        response.setCertificateId(certificateId);
        response.setTitle(certificate.getSkillName() + " Proficiency Certificate");
        response.setIssuer(certificate.getIssuer());
        response.setIssuedDate(certificate.getIssuedAt().toLocalDate());
        response.setVerificationUrl(certificate.getVerificationUrl());
        response.setPdfUrl(certificate.getPdfUrl());
        response.setCredentialId(certificate.getCredentialId());

        // Generate LinkedIn sharing URL
        String linkedInUrl = generateLinkedInSharingUrl(certificate);
        response.setLinkedInSharingUrl(linkedInUrl);

        return response;
    }

    private byte[] generatePdfCertificate(DigitalCertificate certificate) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add certificate content
        document.add(new Paragraph("CERTIFICATE OF ACHIEVEMENT")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold());

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("This certifies that")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));

        document.add(new Paragraph(certificate.getRecipient().getFullName())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold());

        document.add(new Paragraph("has successfully demonstrated proficiency in")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));

        document.add(new Paragraph(certificate.getSkillName())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());

        if (certificate.getScore() != null) {
            document.add(new Paragraph("with a score of " + certificate.getScore() + "%")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14));
        }

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Issued by: " + certificate.getIssuer())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));

        document.add(new Paragraph("Date: " + certificate.getIssuedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));

        document.add(new Paragraph("Certificate ID: " + certificate.getCertificateId())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.add(new Paragraph("Verify at: " + certificate.getVerificationUrl())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.close();
        return baos.toByteArray();
    }

    private byte[] generatePathCompletionPdf(DigitalCertificate certificate) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add certificate content for learning path completion
        document.add(new Paragraph("CERTIFICATE OF COMPLETION")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold());

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("This certifies that")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));

        document.add(new Paragraph(certificate.getRecipient().getFullName())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold());

        document.add(new Paragraph("has successfully completed the learning path")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));

        document.add(new Paragraph(certificate.getSkillName())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());

        if (certificate.getCompletionPercentage() != null) {
            document.add(new Paragraph("with " + String.format("%.1f", certificate.getCompletionPercentage()) + "% completion")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14));
        }

        if (certificate.getTotalHours() != null) {
            document.add(new Paragraph("Total learning hours: " + certificate.getTotalHours())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));
        }

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("Issued by: " + certificate.getIssuer())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));

        document.add(new Paragraph("Date: " + certificate.getIssuedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));

        document.add(new Paragraph("Certificate ID: " + certificate.getCertificateId())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.add(new Paragraph("Verify at: " + certificate.getVerificationUrl())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.close();
        return baos.toByteArray();
    }

    private String generateCertificateId() {
        return "CERT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private String generateVerificationHash(DigitalCertificate certificate) {
        try {
            String data = certificate.getCertificateId() + 
                         certificate.getRecipient().getId().toString() + 
                         certificate.getSkillName() + 
                         certificate.getIssuedAt().toString();
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating verification hash", e);
        }
    }

    private String generateVerificationUrl(String certificateId) {
        return certificateBaseUrl + "/verify/" + certificateId;
    }

    private String saveCertificatePdf(byte[] pdfBytes, String certificateId) {
        // In a real implementation, this would save to cloud storage (S3, etc.)
        // For now, return a placeholder URL
        return certificateBaseUrl + "/pdf/" + certificateId + ".pdf";
    }

    private void recordOnBlockchain(DigitalCertificate certificate) {
        try {
            blockchainService.recordCertificate(certificate);
        } catch (Exception e) {
            logger.warn("Failed to record certificate on blockchain", e);
            // Don't fail the entire operation if blockchain recording fails
        }
    }

    private void recordRevocationOnBlockchain(DigitalCertificate certificate) {
        try {
            blockchainService.recordRevocation(certificate);
        } catch (Exception e) {
            logger.warn("Failed to record certificate revocation on blockchain", e);
        }
    }

    private String generateLinkedInSharingUrl(DigitalCertificate certificate) {
        String baseUrl = "https://www.linkedin.com/profile/add";
        String certificationId = certificate.getCertificateId();
        String name = certificate.getSkillName() + " Proficiency";
        String organization = certificate.getIssuer();
        String issueYear = String.valueOf(certificate.getIssuedAt().getYear());
        String issueMonth = String.valueOf(certificate.getIssuedAt().getMonthValue());
        String certUrl = certificate.getVerificationUrl();

        return baseUrl + "?startTask=CERTIFICATION_NAME" +
               "&name=" + java.net.URLEncoder.encode(name, StandardCharsets.UTF_8) +
               "&organizationName=" + java.net.URLEncoder.encode(organization, StandardCharsets.UTF_8) +
               "&issueYear=" + issueYear +
               "&issueMonth=" + issueMonth +
               "&certificationUrl=" + java.net.URLEncoder.encode(certUrl, StandardCharsets.UTF_8) +
               "&certId=" + certificationId;
    }
}
