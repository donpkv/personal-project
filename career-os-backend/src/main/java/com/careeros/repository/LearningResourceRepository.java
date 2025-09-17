package com.careeros.repository;

import com.careeros.entity.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for LearningResource entity
 */
@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, UUID> {

    /**
     * Find resources by type
     */
    List<LearningResource> findByType(LearningResource.ResourceType type);

    /**
     * Find resources by skill ID
     */
    List<LearningResource> findBySkillId(UUID skillId);

    /**
     * Find resources by difficulty level
     */
    List<LearningResource> findByDifficultyLevel(LearningResource.DifficultyLevel difficultyLevel);

    /**
     * Find resources by category
     */
    List<LearningResource> findByCategory(String category);

    /**
     * Find resources by title containing (case insensitive)
     */
    List<LearningResource> findByTitleContainingIgnoreCase(String title);

    /**
     * Search resources by keywords
     */
    @Query("SELECT lr FROM LearningResource lr WHERE LOWER(lr.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(lr.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(lr.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<LearningResource> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find popular resources (by average rating)
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.averageRating >= :minRating ORDER BY lr.averageRating DESC, lr.reviewCount DESC")
    List<LearningResource> findPopularResources(@Param("minRating") Double minRating);

    /**
     * Find resources by duration range (in minutes)
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.estimatedDurationMinutes BETWEEN :minDuration AND :maxDuration")
    List<LearningResource> findByDurationRange(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);

    /**
     * Find free resources
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.isFree = true")
    List<LearningResource> findFreeResources();

    /**
     * Find resources by provider
     */
    List<LearningResource> findByProvider(String provider);

    /**
     * Find recently added resources
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.createdAt >= :since ORDER BY lr.createdAt DESC")
    List<LearningResource> findRecentResources(@Param("since") LocalDateTime since);

    /**
     * Find resources with high completion rate
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.completionRate >= :minRate ORDER BY lr.completionRate DESC")
    List<LearningResource> findResourcesWithHighCompletionRate(@Param("minRate") Double minRate);

    /**
     * Find resources by skill and difficulty
     */
    List<LearningResource> findBySkillIdAndDifficultyLevel(UUID skillId, LearningResource.DifficultyLevel difficultyLevel);

    /**
     * Find resources by type and skill
     */
    List<LearningResource> findByTypeAndSkillId(LearningResource.ResourceType type, UUID skillId);

    /**
     * Count resources by skill
     */
    Long countBySkillId(UUID skillId);

    /**
     * Count resources by type
     */
    Long countByType(LearningResource.ResourceType type);

    /**
     * Count resources by provider
     */
    Long countByProvider(String provider);

    /**
     * Get all unique providers
     */
    @Query("SELECT DISTINCT lr.provider FROM LearningResource lr WHERE lr.provider IS NOT NULL ORDER BY lr.provider")
    List<String> findAllProviders();

    /**
     * Get all unique categories
     */
    @Query("SELECT DISTINCT lr.category FROM LearningResource lr WHERE lr.category IS NOT NULL ORDER BY lr.category")
    List<String> findAllCategories();

    /**
     * Find resources with minimum rating
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.averageRating >= :minRating AND lr.reviewCount >= :minReviews ORDER BY lr.averageRating DESC")
    List<LearningResource> findResourcesWithMinRating(@Param("minRating") Double minRating, @Param("minReviews") Integer minReviews);

    /**
     * Find trending resources (high recent activity)
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.viewCount > 0 ORDER BY (lr.viewCount * 0.3 + lr.reviewCount * 0.7) DESC")
    List<LearningResource> findTrendingResources();

    /**
     * Find resources by tags
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.tags LIKE CONCAT('%', :tag, '%')")
    List<LearningResource> findByTag(@Param("tag") String tag);

    /**
     * Get resource statistics
     */
    @Query("SELECT COUNT(lr) as totalResources, " +
           "COUNT(CASE WHEN lr.isFree = true THEN 1 END) as freeResources, " +
           "AVG(lr.averageRating) as avgRating, " +
           "AVG(lr.estimatedDurationMinutes) as avgDuration, " +
           "AVG(lr.completionRate) as avgCompletionRate " +
           "FROM LearningResource lr")
    Object[] getResourceStatistics();

    /**
     * Find resources for learning path recommendations
     */
    @Query("SELECT lr FROM LearningResource lr WHERE lr.skillId = :skillId AND lr.difficultyLevel <= :maxDifficulty ORDER BY lr.averageRating DESC, lr.completionRate DESC")
    List<LearningResource> findResourcesForSkillAndMaxDifficulty(@Param("skillId") UUID skillId, @Param("maxDifficulty") LearningResource.DifficultyLevel maxDifficulty);

    /**
     * Check if resource URL exists
     */
    boolean existsByUrl(String url);

    /**
     * Find resources by external ID
     */
    List<LearningResource> findByExternalId(String externalId);
}
