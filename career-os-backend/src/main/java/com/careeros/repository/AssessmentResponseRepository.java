package com.careeros.repository;

import com.careeros.entity.AssessmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for AssessmentResponse entity
 */
@Repository
public interface AssessmentResponseRepository extends JpaRepository<AssessmentResponse, UUID> {

    /**
     * Find responses by user ID ordered by submitted date
     */
    List<AssessmentResponse> findByUserIdOrderBySubmittedAtDesc(UUID userId);

    /**
     * Find responses by assessment ID
     */
    List<AssessmentResponse> findByAssessmentId(UUID assessmentId);

    /**
     * Find responses by user ID and assessment ID
     */
    List<AssessmentResponse> findByUserIdAndAssessmentId(UUID userId, UUID assessmentId);

    /**
     * Find responses by question ID
     */
    List<AssessmentResponse> findByQuestionId(UUID questionId);

    /**
     * Find correct responses by user ID
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.user.id = :userId AND ar.isCorrect = true")
    List<AssessmentResponse> findCorrectResponsesByUserId(@Param("userId") UUID userId);

    /**
     * Find incorrect responses by user ID
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.user.id = :userId AND ar.isCorrect = false")
    List<AssessmentResponse> findIncorrectResponsesByUserId(@Param("userId") UUID userId);

    /**
     * Get accuracy rate for user
     */
    @Query("SELECT (COUNT(CASE WHEN ar.isCorrect = true THEN 1 END) * 100.0 / COUNT(ar)) FROM AssessmentResponse ar WHERE ar.user.id = :userId")
    Double getAccuracyRateByUserId(@Param("userId") UUID userId);

    /**
     * Get accuracy rate for user in specific assessment
     */
    @Query("SELECT (COUNT(CASE WHEN ar.isCorrect = true THEN 1 END) * 100.0 / COUNT(ar)) FROM AssessmentResponse ar WHERE ar.user.id = :userId AND ar.assessment.id = :assessmentId")
    Double getAccuracyRateByUserIdAndAssessmentId(@Param("userId") UUID userId, @Param("assessmentId") UUID assessmentId);

    /**
     * Get average score for user responses
     */
    @Query("SELECT AVG(ar.score) FROM AssessmentResponse ar WHERE ar.user.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") UUID userId);

    /**
     * Find responses by date range
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.submittedAt BETWEEN :startDate AND :endDate")
    List<AssessmentResponse> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find responses by user ID and date range
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.user.id = :userId AND ar.submittedAt BETWEEN :startDate AND :endDate")
    List<AssessmentResponse> findByUserIdAndDateRange(@Param("userId") UUID userId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Count responses by user ID
     */
    Long countByUserId(UUID userId);

    /**
     * Count correct responses by user ID
     */
    @Query("SELECT COUNT(ar) FROM AssessmentResponse ar WHERE ar.user.id = :userId AND ar.isCorrect = true")
    Long countCorrectResponsesByUserId(@Param("userId") UUID userId);

    /**
     * Count responses by assessment ID
     */
    Long countByAssessmentId(UUID assessmentId);

    /**
     * Find recent responses by user ID
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.user.id = :userId AND ar.submittedAt >= :since ORDER BY ar.submittedAt DESC")
    List<AssessmentResponse> findRecentResponsesByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Get question statistics
     */
    @Query("SELECT COUNT(ar) as totalResponses, " +
           "COUNT(CASE WHEN ar.isCorrect = true THEN 1 END) as correctResponses, " +
           "(COUNT(CASE WHEN ar.isCorrect = true THEN 1 END) * 100.0 / COUNT(ar)) as accuracyRate, " +
           "AVG(ar.score) as avgScore " +
           "FROM AssessmentResponse ar WHERE ar.question.id = :questionId")
    Object[] getQuestionStatistics(@Param("questionId") UUID questionId);

    /**
     * Find responses with minimum score
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.score >= :minScore ORDER BY ar.score DESC")
    List<AssessmentResponse> findResponsesWithMinScore(@Param("minScore") Double minScore);

    /**
     * Get user performance trend (last N responses)
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.user.id = :userId ORDER BY ar.submittedAt DESC")
    List<AssessmentResponse> findUserPerformanceTrend(@Param("userId") UUID userId);

    /**
     * Find responses by selected option
     */
    @Query("SELECT ar FROM AssessmentResponse ar WHERE ar.selectedOption.id = :optionId")
    List<AssessmentResponse> findBySelectedOptionId(@Param("optionId") UUID optionId);

    /**
     * Get assessment completion statistics
     */
    @Query("SELECT COUNT(DISTINCT ar.user.id) as uniqueUsers, " +
           "COUNT(ar) as totalResponses, " +
           "AVG(ar.score) as avgScore, " +
           "(COUNT(CASE WHEN ar.isCorrect = true THEN 1 END) * 100.0 / COUNT(ar)) as overallAccuracy " +
           "FROM AssessmentResponse ar WHERE ar.assessment.id = :assessmentId")
    Object[] getAssessmentCompletionStatistics(@Param("assessmentId") UUID assessmentId);

    /**
     * Delete responses by user ID
     */
    void deleteByUserId(UUID userId);

    /**
     * Delete responses by assessment ID
     */
    void deleteByAssessmentId(UUID assessmentId);
}
