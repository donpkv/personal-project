package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;

/**
 * DTO for performance analytics
 */
@Data
public class PerformanceAnalytics {
    private Double averageAssessmentScore;
    private String performanceTrend;
    private List<Double> recentScores;
    private Integer totalAssessments;
    private Integer passedAssessments;
    private Double improvementRate;
}
