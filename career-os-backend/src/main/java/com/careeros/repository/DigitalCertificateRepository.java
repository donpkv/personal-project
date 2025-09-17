package com.careeros.repository;

import com.careeros.entity.DigitalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for DigitalCertificate entity
 */
@Repository
public interface DigitalCertificateRepository extends JpaRepository<DigitalCertificate, UUID> {

    /**
     * Find certificate by certificate ID
     */
    Optional<DigitalCertificate> findByCertificateId(String certificateId);

    /**
     * Find certificates by recipient ID ordered by issued date
     */
    List<DigitalCertificate> findByRecipientIdOrderByIssuedAtDesc(UUID recipientId);

    /**
     * Find certificates by recipient ID and status
     */
    List<DigitalCertificate> findByRecipientIdAndStatus(UUID recipientId, DigitalCertificate.CertificateStatus status);

    /**
     * Find certificates by recipient ID and status ordered by issued date
     */
    List<DigitalCertificate> findByRecipientIdAndStatusOrderByIssuedAtDesc(UUID recipientId, DigitalCertificate.CertificateStatus status);

    /**
     * Find certificates by skill name
     */
    List<DigitalCertificate> findBySkillNameContainingIgnoreCase(String skillName);

    /**
     * Find certificates by issuer
     */
    List<DigitalCertificate> findByIssuer(String issuer);

    /**
     * Find certificates by certificate type
     */
    List<DigitalCertificate> findByCertificateType(DigitalCertificate.CertificateType certificateType);

    /**
     * Find certificates by status
     */
    List<DigitalCertificate> findByStatus(DigitalCertificate.CertificateStatus status);

    /**
     * Find active certificates
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.status = 'ACTIVE' ORDER BY dc.issuedAt DESC")
    List<DigitalCertificate> findActiveCertificates();

    /**
     * Find certificates by date range
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.issuedAt BETWEEN :startDate AND :endDate ORDER BY dc.issuedAt DESC")
    List<DigitalCertificate> findByIssuedDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find certificates expiring soon
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.expiresAt BETWEEN :now AND :soonDate AND dc.status = 'ACTIVE' ORDER BY dc.expiresAt ASC")
    List<DigitalCertificate> findCertificatesExpiringSoon(@Param("now") LocalDateTime now, @Param("soonDate") LocalDateTime soonDate);

    /**
     * Find expired certificates
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.expiresAt < :now AND dc.status = 'ACTIVE'")
    List<DigitalCertificate> findExpiredCertificates(@Param("now") LocalDateTime now);

    /**
     * Find certificates by minimum score
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.score >= :minScore ORDER BY dc.score DESC")
    List<DigitalCertificate> findCertificatesWithMinScore(@Param("minScore") Integer minScore);

    /**
     * Find recent certificates by recipient ID
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.recipient.id = :recipientId AND dc.issuedAt >= :since ORDER BY dc.issuedAt DESC")
    List<DigitalCertificate> findRecentCertificatesByRecipientId(@Param("recipientId") UUID recipientId, @Param("since") LocalDateTime since);

    /**
     * Find certificates by verification hash
     */
    Optional<DigitalCertificate> findByVerificationHash(String verificationHash);

    /**
     * Count certificates by recipient ID
     */
    Long countByRecipientId(UUID recipientId);

    /**
     * Count certificates by recipient ID and status
     */
    Long countByRecipientIdAndStatus(UUID recipientId, DigitalCertificate.CertificateStatus status);

    /**
     * Count certificates by skill name
     */
    @Query("SELECT COUNT(dc) FROM DigitalCertificate dc WHERE dc.skillName = :skillName AND dc.status = 'ACTIVE'")
    Long countCertificatesBySkillName(@Param("skillName") String skillName);

    /**
     * Get certificate statistics
     */
    @Query("SELECT COUNT(dc) as totalCertificates, " +
           "COUNT(CASE WHEN dc.status = 'ACTIVE' THEN 1 END) as activeCertificates, " +
           "COUNT(CASE WHEN dc.status = 'REVOKED' THEN 1 END) as revokedCertificates, " +
           "COUNT(CASE WHEN dc.expiresAt < CURRENT_TIMESTAMP THEN 1 END) as expiredCertificates, " +
           "AVG(dc.score) as avgScore " +
           "FROM DigitalCertificate dc")
    Object[] getCertificateStatistics();

    /**
     * Find top skills by certificate count
     */
    @Query("SELECT dc.skillName, COUNT(dc) as certificateCount FROM DigitalCertificate dc WHERE dc.status = 'ACTIVE' GROUP BY dc.skillName ORDER BY certificateCount DESC")
    List<Object[]> findTopSkillsByCertificateCount();

    /**
     * Find certificates by recipient and skill
     */
    List<DigitalCertificate> findByRecipientIdAndSkillNameContainingIgnoreCase(UUID recipientId, String skillName);

    /**
     * Find highest scoring certificate for recipient and skill
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.recipient.id = :recipientId AND dc.skillName = :skillName ORDER BY dc.score DESC")
    List<DigitalCertificate> findHighestScoringCertificateByRecipientAndSkill(@Param("recipientId") UUID recipientId, @Param("skillName") String skillName);

    /**
     * Find certificates needing renewal (expiring within certain period)
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.expiresAt BETWEEN :now AND :renewalDate AND dc.status = 'ACTIVE' ORDER BY dc.expiresAt ASC")
    List<DigitalCertificate> findCertificatesNeedingRenewal(@Param("now") LocalDateTime now, @Param("renewalDate") LocalDateTime renewalDate);

    /**
     * Find revoked certificates
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.status = 'REVOKED' ORDER BY dc.revokedAt DESC")
    List<DigitalCertificate> findRevokedCertificates();

    /**
     * Find certificates by revocation reason
     */
    List<DigitalCertificate> findByRevocationReason(String revocationReason);

    /**
     * Find certificates issued by date and issuer
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.issuer = :issuer AND dc.issuedAt >= :since ORDER BY dc.issuedAt DESC")
    List<DigitalCertificate> findCertificatesByIssuerAndDate(@Param("issuer") String issuer, @Param("since") LocalDateTime since);

    /**
     * Check if certificate ID exists
     */
    boolean existsByCertificateId(String certificateId);

    /**
     * Check if verification hash exists
     */
    boolean existsByVerificationHash(String verificationHash);

    /**
     * Find certificates by blockchain transaction hash
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.blockchainTxHash = :txHash")
    List<DigitalCertificate> findByBlockchainTxHash(@Param("txHash") String txHash);

    /**
     * Find certificates without blockchain record
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.blockchainTxHash IS NULL AND dc.status = 'ACTIVE'")
    List<DigitalCertificate> findCertificatesWithoutBlockchainRecord();

    /**
     * Find most recent certificate for recipient and skill
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.recipient.id = :recipientId AND dc.skillName = :skillName AND dc.status = 'ACTIVE' ORDER BY dc.issuedAt DESC")
    List<DigitalCertificate> findLatestCertificateByRecipientAndSkill(@Param("recipientId") UUID recipientId, @Param("skillName") String skillName);

    /**
     * Find certificates by score range
     */
    @Query("SELECT dc FROM DigitalCertificate dc WHERE dc.score BETWEEN :minScore AND :maxScore ORDER BY dc.score DESC")
    List<DigitalCertificate> findCertificatesByScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    /**
     * Get average score by skill
     */
    @Query("SELECT AVG(dc.score) FROM DigitalCertificate dc WHERE dc.skillName = :skillName AND dc.status = 'ACTIVE' AND dc.score IS NOT NULL")
    Double getAverageScoreBySkill(@Param("skillName") String skillName);

    /**
     * Find certificates by type and recipient
     */
    List<DigitalCertificate> findByRecipientIdAndCertificateType(UUID recipientId, DigitalCertificate.CertificateType certificateType);

    /**
     * Find all unique skill names from certificates
     */
    @Query("SELECT DISTINCT dc.skillName FROM DigitalCertificate dc WHERE dc.status = 'ACTIVE' ORDER BY dc.skillName")
    List<String> findAllUniqueSkillNames();

    /**
     * Find all unique issuers
     */
    @Query("SELECT DISTINCT dc.issuer FROM DigitalCertificate dc ORDER BY dc.issuer")
    List<String> findAllUniqueIssuers();

    /**
     * Delete certificates by recipient ID
     */
    void deleteByRecipientId(UUID recipientId);
}
