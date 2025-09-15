package com.careeros.repository;

import com.careeros.entity.PathStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for PathStep entity
 */
@Repository
public interface PathStepRepository extends JpaRepository<PathStep, UUID> {

    /**
     * Find steps by learning path ID ordered by step order
     */
    List<PathStep> findByLearningPathIdOrderByStepOrder(UUID learningPathId);

    /**
     * Find required steps for a learning path
     */
    List<PathStep> findByLearningPathIdAndIsRequiredTrueOrderByStepOrder(UUID learningPathId);

    /**
     * Find steps by type
     */
    List<PathStep> findByStepType(PathStep.StepType stepType);

    /**
     * Find prerequisite steps for a given step
     */
    @Query("SELECT ps FROM PathStep ps JOIN ps.prerequisiteSteps prereq WHERE prereq.id = :stepId")
    List<PathStep> findPrerequisiteSteps(@Param("stepId") UUID stepId);

    /**
     * Count total steps in a learning path
     */
    @Query("SELECT COUNT(ps) FROM PathStep ps WHERE ps.learningPath.id = :pathId")
    Long countStepsInPath(@Param("pathId") UUID pathId);

    /**
     * Find steps that have no prerequisites (starting points)
     */
    @Query("SELECT ps FROM PathStep ps WHERE ps.learningPath.id = :pathId AND ps.prerequisiteSteps IS EMPTY ORDER BY ps.stepOrder")
    List<PathStep> findStartingSteps(@Param("pathId") UUID pathId);
}
