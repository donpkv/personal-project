package com.careeros.dto.analytics;

import lombok.Data;

import java.util.UUID;

/**
 * DTO for path step analytics
 */
@Data
public class PathStepAnalytics {
    private UUID stepId;
    private String stepName;
    private Double completionRate;
    private Integer averageTimeToComplete;
    private Double dropOffRate;
    private String difficulty;
    private String recommendations;
}
