package com.careeros.dto.learning;

import com.careeros.entity.LearningPath;
import lombok.Data;

import java.util.List;

/**
 * DTO for learning path recommendation response
 */
@Data
public class LearningPathRecommendationResponse {
    private List<LearningPath> recommendedPaths;
    private List<String> reasons;
    private Double matchScore;
    private String aiInsights;
    private Integer totalPaths;
    private List<LearningPath> aiGeneratedPaths;
    private List<String> personalizedSuggestions;
}
