package com.careeros.repository;

import com.careeros.entity.UserLearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for UserLearningPath entity
 */
@Repository
public interface UserLearningPathRepository extends JpaRepository<UserLearningPath, UUID> {

    /**
     * Find learning paths by user ID
     */
    List<UserLearningPath> findByUserId(UUID userId);

    /**
     * Find learning path by user ID and learning path ID
     */
    Optional<UserLearningPath> findByUserIdAndLearningPathId(UUID userId, UUID learningPathId);

    /**
     * Find learning paths by status for a user
     */
    List<UserLearningPath> findByUserIdAndStatus(UUID userId, UserLearningPath.EnrollmentStatus status);

    /**
     * Find active learning paths for a user
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.status = 'IN_PROGRESS'")
    List<UserLearningPath> findActivePathsByUserId(@Param("userId") UUID userId);

    /**
     * Find completed learning paths for a user
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.status = 'COMPLETED'")
    List<UserLearningPath> findCompletedPathsByUserId(@Param("userId") UUID userId);

    /**
     * Find learning paths by learning path ID
     */
    List<UserLearningPath> findByLearningPathId(UUID learningPathId);

    /**
     * Find learning paths with minimum progress percentage
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.progressPercentage >= :minProgress")
    List<UserLearningPath> findByUserIdAndMinProgress(@Param("userId") UUID userId, @Param("minProgress") Double minProgress);

    /**
     * Find recently enrolled learning paths
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.enrolledAt >= :since ORDER BY ulp.enrolledAt DESC")
    List<UserLearningPath> findRecentEnrollmentsByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Get total learning time for user across all paths
     */
    @Query("SELECT SUM(ulp.timeSpentHours) FROM UserLearningPath ulp WHERE ulp.user.id = :userId")
    Integer getTotalLearningHoursByUserId(@Param("userId") UUID userId);

    /**
     * Get average progress across all paths for user
     */
    @Query("SELECT AVG(ulp.progressPercentage) FROM UserLearningPath ulp WHERE ulp.user.id = :userId")
    Double getAverageProgressByUserId(@Param("userId") UUID userId);

    /**
     * Count paths by status for user
     */
    @Query("SELECT COUNT(ulp) FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.status = :status")
    Long countByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") UserLearningPath.EnrollmentStatus status);

    /**
     * Find paths that need attention (enrolled but not accessed recently)
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.status = 'IN_PROGRESS' AND ulp.lastAccessedAt < :cutoffDate")
    List<UserLearningPath> findStalePathsByUserId(@Param("userId") UUID userId, @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find paths by target completion date
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.user.id = :userId AND ulp.targetCompletionDate BETWEEN :startDate AND :endDate")
    List<UserLearningPath> findByUserIdAndTargetDateRange(@Param("userId") UUID userId, 
                                                          @Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Get learning path statistics
     */
    @Query("SELECT COUNT(ulp) as totalEnrollments, " +
           "COUNT(CASE WHEN ulp.status = 'COMPLETED' THEN 1 END) as completedEnrollments, " +
           "AVG(ulp.progressPercentage) as avgProgress, " +
           "AVG(ulp.timeSpentHours) as avgTimeSpent " +
           "FROM UserLearningPath ulp WHERE ulp.learningPath.id = :pathId")
    Object[] getPathStatistics(@Param("pathId") UUID pathId);

    /**
     * Find top performing users in a learning path
     */
    @Query("SELECT ulp FROM UserLearningPath ulp WHERE ulp.learningPath.id = :pathId ORDER BY ulp.progressPercentage DESC, ulp.timeSpentHours ASC")
    List<UserLearningPath> findTopPerformersByPathId(@Param("pathId") UUID pathId);

    /**
     * Check if user is enrolled in learning path
     */
    boolean existsByUserIdAndLearningPathId(UUID userId, UUID learningPathId);

    /**
     * Delete user learning path by user ID and learning path ID
     */
    void deleteByUserIdAndLearningPathId(UUID userId, UUID learningPathId);
}
