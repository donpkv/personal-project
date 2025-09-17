package com.careeros.dto.analytics;

import lombok.Data;

/**
 * DTO for learning analytics
 */
@Data
public class LearningAnalytics {
    private Integer totalLearningPaths;
    private Integer completedPaths;
    private Integer inProgressPaths;
    private Integer totalLearningHours;
    private Double averageProgress;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalResources;
    private Integer completedResources;
}
