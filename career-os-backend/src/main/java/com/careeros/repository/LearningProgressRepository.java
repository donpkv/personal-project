package com.careeros.repository;

import com.careeros.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for LearningProgress entity
 */
@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {

    /**
     * Find learning progress by user ID
     */
    List<LearningProgress> findByUserId(UUID userId);

    /**
     * Find learning progress by user ID and resource ID
     */
    Optional<LearningProgress> findByUserIdAndResourceId(UUID userId, UUID resourceId);

    /**
     * Find completed learning progress by user ID
     */
    List<LearningProgress> findByUserIdAndCompleted(UUID userId, boolean completed);

    /**
     * Find learning progress by resource ID
     */
    List<LearningProgress> findByResourceId(UUID resourceId);

    /**
     * Find recent learning progress by user ID
     */
    @Query("SELECT lp FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.lastAccessedAt >= :since ORDER BY lp.lastAccessedAt DESC")
    List<LearningProgress> findRecentProgressByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Find learning progress with minimum progress percentage
     */
    @Query("SELECT lp FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.progressPercentage >= :minProgress")
    List<LearningProgress> findByUserIdAndMinProgress(@Param("userId") UUID userId, @Param("minProgress") Double minProgress);

    /**
     * Get total learning time for user
     */
    @Query("SELECT SUM(lp.timeSpentMinutes) FROM LearningProgress lp WHERE lp.user.id = :userId")
    Long getTotalLearningTimeByUserId(@Param("userId") UUID userId);

    /**
     * Get average progress percentage for user
     */
    @Query("SELECT AVG(lp.progressPercentage) FROM LearningProgress lp WHERE lp.user.id = :userId")
    Double getAverageProgressByUserId(@Param("userId") UUID userId);

    /**
     * Count completed resources by user
     */
    @Query("SELECT COUNT(lp) FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.completed = true")
    Long countCompletedResourcesByUserId(@Param("userId") UUID userId);

    /**
     * Find learning progress by date range
     */
    @Query("SELECT lp FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.startedAt BETWEEN :startDate AND :endDate")
    List<LearningProgress> findByUserIdAndDateRange(@Param("userId") UUID userId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find learning progress that needs attention (started but not accessed recently)
     */
    @Query("SELECT lp FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.completed = false AND lp.lastAccessedAt < :cutoffDate")
    List<LearningProgress> findStaleProgressByUserId(@Param("userId") UUID userId, @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Get learning statistics for a resource
     */
    @Query("SELECT COUNT(lp) as totalUsers, " +
           "COUNT(CASE WHEN lp.completed = true THEN 1 END) as completedUsers, " +
           "AVG(lp.progressPercentage) as avgProgress, " +
           "AVG(lp.timeSpentMinutes) as avgTimeSpent " +
           "FROM LearningProgress lp WHERE lp.resource.id = :resourceId")
    Object[] getResourceStatistics(@Param("resourceId") UUID resourceId);

    /**
     * Delete learning progress by user ID and resource ID
     */
    void deleteByUserIdAndResourceId(UUID userId, UUID resourceId);
}
