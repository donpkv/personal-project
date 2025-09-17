package com.careeros.repository;

import com.careeros.entity.SkillAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for SkillAssessment entity
 */
@Repository
public interface SkillAssessmentRepository extends JpaRepository<SkillAssessment, UUID> {

    /**
     * Find assessments by user ID ordered by created date
     */
    List<SkillAssessment> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find assessments by user ID and skill ID ordered by created date
     */
    List<SkillAssessment> findByUserIdAndSkillIdOrderByCreatedAtDesc(UUID userId, UUID skillId);

    /**
     * Find assessments by skill ID
     */
    List<SkillAssessment> findBySkillId(UUID skillId);

    /**
     * Find assessments by status
     */
    List<SkillAssessment> findByStatus(SkillAssessment.AssessmentStatus status);

    /**
     * Find assessments by user ID and status
     */
    List<SkillAssessment> findByUserIdAndStatus(UUID userId, SkillAssessment.AssessmentStatus status);

    /**
     * Find completed assessments by user ID
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.status = 'COMPLETED' ORDER BY sa.completedAt DESC")
    List<SkillAssessment> findCompletedAssessmentsByUserId(@Param("userId") UUID userId);

    /**
     * Find in-progress assessments by user ID
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.status = 'IN_PROGRESS'")
    List<SkillAssessment> findInProgressAssessmentsByUserId(@Param("userId") UUID userId);

    /**
     * Find assessments by difficulty level
     */
    List<SkillAssessment> findByDifficultyLevel(SkillAssessment.DifficultyLevel difficultyLevel);

    /**
     * Find assessments by assessment type
     */
    List<SkillAssessment> findByAssessmentType(SkillAssessment.AssessmentType assessmentType);

    /**
     * Find expired assessments
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.expiresAt < :now AND sa.status != 'COMPLETED'")
    List<SkillAssessment> findExpiredAssessments(@Param("now") LocalDateTime now);

    /**
     * Find assessments expiring soon
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.expiresAt BETWEEN :now AND :soonDate AND sa.status = 'IN_PROGRESS'")
    List<SkillAssessment> findAssessmentsExpiringSoon(@Param("now") LocalDateTime now, @Param("soonDate") LocalDateTime soonDate);

    /**
     * Find recent assessments by user ID
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.createdAt >= :since ORDER BY sa.createdAt DESC")
    List<SkillAssessment> findRecentAssessmentsByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Get average score for user assessments
     */
    @Query("SELECT AVG(sa.score) FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.status = 'COMPLETED' AND sa.score IS NOT NULL")
    Double getAverageScoreByUserId(@Param("userId") UUID userId);

    /**
     * Get average score for skill assessments
     */
    @Query("SELECT AVG(sa.score) FROM SkillAssessment sa WHERE sa.skill.id = :skillId AND sa.status = 'COMPLETED' AND sa.score IS NOT NULL")
    Double getAverageScoreBySkillId(@Param("skillId") UUID skillId);

    /**
     * Count assessments by user ID and status
     */
    Long countByUserIdAndStatus(UUID userId, SkillAssessment.AssessmentStatus status);

    /**
     * Count completed assessments by user ID
     */
    @Query("SELECT COUNT(sa) FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.status = 'COMPLETED'")
    Long countCompletedAssessmentsByUserId(@Param("userId") UUID userId);

    /**
     * Find best assessment score for user and skill
     */
    @Query("SELECT MAX(sa.score) FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.skill.id = :skillId AND sa.status = 'COMPLETED'")
    Double getBestScoreByUserIdAndSkillId(@Param("userId") UUID userId, @Param("skillId") UUID skillId);

    /**
     * Find latest assessment for user and skill
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.user.id = :userId AND sa.skill.id = :skillId ORDER BY sa.createdAt DESC")
    List<SkillAssessment> findLatestAssessmentByUserIdAndSkillId(@Param("userId") UUID userId, @Param("skillId") UUID skillId);

    /**
     * Find assessments with high scores (above threshold)
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.score >= :minScore AND sa.status = 'COMPLETED' ORDER BY sa.score DESC")
    List<SkillAssessment> findHighScoringAssessments(@Param("minScore") Double minScore);

    /**
     * Get assessment statistics for a skill
     */
    @Query("SELECT COUNT(sa) as totalAssessments, " +
           "COUNT(CASE WHEN sa.status = 'COMPLETED' THEN 1 END) as completedAssessments, " +
           "AVG(sa.score) as avgScore, " +
           "MAX(sa.score) as maxScore, " +
           "MIN(sa.score) as minScore " +
           "FROM SkillAssessment sa WHERE sa.skill.id = :skillId")
    Object[] getSkillAssessmentStatistics(@Param("skillId") UUID skillId);

    /**
     * Find assessments by date range
     */
    @Query("SELECT sa FROM SkillAssessment sa WHERE sa.createdAt BETWEEN :startDate AND :endDate")
    List<SkillAssessment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Delete assessments by user ID
     */
    void deleteByUserId(UUID userId);
}
