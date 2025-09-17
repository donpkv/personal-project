package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO for learning path effectiveness report
 */
@Data
public class LearningPathEffectivenessReport {
    private UUID pathId;
    private String pathName;
    private Double completionRate;
    private Double averageRating;
    private Integer totalEnrollments;
    private Integer completedEnrollments;
    private Double averageCompletionTime;
    private Double averageCompletionDays;
    private List<PathStepAnalytics> stepAnalytics;
    private String recommendations;
    private java.time.LocalDateTime analysisDate;
    private java.util.Map<String, Integer> dropoutReasons;
    private java.util.Map<String, Object> feedbackAnalysis;
}
