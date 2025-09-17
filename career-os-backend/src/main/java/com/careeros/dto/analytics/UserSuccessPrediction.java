package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO for user success prediction
 */
@Data
public class UserSuccessPrediction {
    private UUID userId;
    private UUID pathId;
    private Double successProbability;
    private String confidenceLevel;
    private List<String> riskFactors;
    private List<String> recommendations;
    private Integer estimatedCompletionDays;
    private String predictionModel;
    private java.time.LocalDateTime predictionDate;
    private Double engagementScore;
    private Double learningVelocity;
    private Double consistencyScore;
}
