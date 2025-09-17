package com.careeros.dto.analytics;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for user analytics dashboard
 */
@Data
public class UserAnalyticsDashboard {
    private java.util.UUID userId;
    private LocalDateTime generatedAt;
    private LearningAnalytics learningAnalytics;
    private SkillAnalytics skillAnalytics;
    private PerformanceAnalytics performanceAnalytics;
    private GoalAnalytics goalAnalytics;
    private SocialAnalytics socialAnalytics;
    private CareerAnalytics careerAnalytics;
    private TimeAnalytics timeAnalytics;
}
