package com.careeros.repository;

import com.careeros.entity.UserPathStepProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for UserPathStepProgress entity
 */
@Repository
public interface UserPathStepProgressRepository extends JpaRepository<UserPathStepProgress, UUID> {

    /**
     * Find step progress by user ID
     */
    List<UserPathStepProgress> findByUserId(UUID userId);

    /**
     * Find step progress by user ID and path step ID
     */
    Optional<UserPathStepProgress> findByUserIdAndPathStepId(UUID userId, UUID pathStepId);

    /**
     * Find step progress by user learning path ID
     */
    List<UserPathStepProgress> findByUserLearningPathId(UUID userLearningPathId);

    /**
     * Find completed steps by user ID
     */
    List<UserPathStepProgress> findByUserIdAndCompleted(UUID userId, boolean completed);

    /**
     * Find step progress by path step ID
     */
    List<UserPathStepProgress> findByPathStepId(UUID pathStepId);

    /**
     * Find step progress for a user's learning path
     */
    @Query("SELECT usps FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.userLearningPath.learningPath.id = :pathId")
    List<UserPathStepProgress> findByUserIdAndPathId(@Param("userId") UUID userId, @Param("pathId") UUID pathId);

    /**
     * Find next uncompleted step for user in a path
     */
    @Query("SELECT usps FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.userLearningPath.learningPath.id = :pathId AND usps.completed = false ORDER BY usps.pathStep.orderIndex ASC")
    Optional<UserPathStepProgress> findNextUncompletedStep(@Param("userId") UUID userId, @Param("pathId") UUID pathId);

    /**
     * Count completed steps for user in a path
     */
    @Query("SELECT COUNT(usps) FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.userLearningPath.learningPath.id = :pathId AND usps.completed = true")
    Long countCompletedStepsByUserAndPath(@Param("userId") UUID userId, @Param("pathId") UUID pathId);

    /**
     * Count total steps for user in a path
     */
    @Query("SELECT COUNT(usps) FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.userLearningPath.learningPath.id = :pathId")
    Long countTotalStepsByUserAndPath(@Param("userId") UUID userId, @Param("pathId") UUID pathId);

    /**
     * Find step progress with minimum progress percentage
     */
    @Query("SELECT usps FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.progressPercentage >= :minProgress")
    List<UserPathStepProgress> findByUserIdAndMinProgress(@Param("userId") UUID userId, @Param("minProgress") Double minProgress);

    /**
     * Find recent step progress by user ID
     */
    @Query("SELECT usps FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.lastAccessedAt >= :since ORDER BY usps.lastAccessedAt DESC")
    List<UserPathStepProgress> findRecentProgressByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Get total time spent on steps for user
     */
    @Query("SELECT SUM(usps.timeSpentMinutes) FROM UserPathStepProgress usps WHERE usps.user.id = :userId")
    Long getTotalTimeSpentByUserId(@Param("userId") UUID userId);

    /**
     * Get total time spent on steps for user in a specific path
     */
    @Query("SELECT SUM(usps.timeSpentMinutes) FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.userLearningPath.learningPath.id = :pathId")
    Long getTotalTimeSpentByUserAndPath(@Param("userId") UUID userId, @Param("pathId") UUID pathId);

    /**
     * Find step progress by date range
     */
    @Query("SELECT usps FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.startedAt BETWEEN :startDate AND :endDate")
    List<UserPathStepProgress> findByUserIdAndDateRange(@Param("userId") UUID userId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find steps that need attention (started but not accessed recently)
     */
    @Query("SELECT usps FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.completed = false AND usps.lastAccessedAt < :cutoffDate")
    List<UserPathStepProgress> findStaleProgressByUserId(@Param("userId") UUID userId, @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Get step statistics
     */
    @Query("SELECT COUNT(usps) as totalUsers, " +
           "COUNT(CASE WHEN usps.completed = true THEN 1 END) as completedUsers, " +
           "AVG(usps.progressPercentage) as avgProgress, " +
           "AVG(usps.timeSpentMinutes) as avgTimeSpent " +
           "FROM UserPathStepProgress usps WHERE usps.pathStep.id = :stepId")
    Object[] getStepStatistics(@Param("stepId") UUID stepId);

    /**
     * Find users who completed a specific step
     */
    @Query("SELECT usps.user FROM UserPathStepProgress usps WHERE usps.pathStep.id = :stepId AND usps.completed = true")
    List<com.careeros.entity.User> findUsersWhoCompletedStep(@Param("stepId") UUID stepId);

    /**
     * Check if user completed a specific step
     */
    @Query("SELECT CASE WHEN COUNT(usps) > 0 THEN true ELSE false END FROM UserPathStepProgress usps WHERE usps.user.id = :userId AND usps.pathStep.id = :stepId AND usps.completed = true")
    boolean isStepCompletedByUser(@Param("userId") UUID userId, @Param("stepId") UUID stepId);

    /**
     * Delete step progress by user ID and path step ID
     */
    void deleteByUserIdAndPathStepId(UUID userId, UUID pathStepId);

    /**
     * Find step progress by user and path step
     */
    Optional<UserPathStepProgress> findByUserAndPathStep(com.careeros.entity.User user, com.careeros.entity.PathStep pathStep);
}
