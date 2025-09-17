package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for content analytics
 */
@Data
public class ContentAnalytics {
    private Long totalResources;
    private Long totalLearningPaths;
    private Long totalAssessments;
    private List<String> popularResources;
    private List<String> popularPaths;
    private Map<String, Long> resourcesByCategory;
    private Double averageCompletionRate;
}
