package com.careeros.dto.learning;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO for learning path recommendation request
 */
@Data
public class LearningPathRecommendationRequest {
    private UUID userId;
    private List<String> skills;
    private String difficulty;
    private String category;
    private Integer maxDuration;
    private String learningStyle;
    private Integer limit;
}
