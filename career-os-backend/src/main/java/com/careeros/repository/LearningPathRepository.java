package com.careeros.repository;

import com.careeros.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for LearningPath entity
 */
@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, UUID> {

    /**
     * Find learning paths by category
     */
    List<LearningPath> findByCategory(LearningPath.PathCategory category);

    /**
     * Find learning paths by difficulty level
     */
    List<LearningPath> findByDifficultyLevel(LearningPath.DifficultyLevel difficultyLevel);

    /**
     * Find active learning paths
     */
    List<LearningPath> findByIsActiveTrue();

    /**
     * Find learning paths by title containing keyword
     */
    List<LearningPath> findByTitleContainingIgnoreCase(String keyword);

    /**
     * Find popular learning paths (most enrolled)
     */
    @Query("SELECT lp FROM LearningPath lp LEFT JOIN UserLearningPath ulp ON lp.id = ulp.learningPath.id " +
           "GROUP BY lp.id ORDER BY COUNT(ulp.id) DESC")
    List<LearningPath> findPopularPaths();

    /**
     * Find recommended paths based on user skills
     */
    @Query("SELECT DISTINCT lp FROM LearningPath lp " +
           "JOIN PathStep ps ON ps.learningPath.id = lp.id " +
           "WHERE ps.requiredSkills LIKE %:skillName% AND lp.isActive = true")
    List<LearningPath> findPathsForSkill(@Param("skillName") String skillName);

    /**
     * Find paths by estimated duration range
     */
    List<LearningPath> findByEstimatedDurationWeeksBetween(Integer minWeeks, Integer maxWeeks);
}
