package com.careeros.dto.analytics;

import lombok.Data;

/**
 * DTO for career analytics
 */
@Data
public class CareerAnalytics {
    private Integer certificatesEarned;
    private Double jobReadinessScore;
    private Integer skillsInDemand;
    private Integer portfolioProjects;
    private String careerLevel;
    private Double marketValue;
}
