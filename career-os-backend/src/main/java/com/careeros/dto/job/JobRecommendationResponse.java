package com.careeros.dto.job;

import com.careeros.entity.JobPosting;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for job recommendation response
 */
@Data
public class JobRecommendationResponse {
    private List<JobPosting> recommendedJobs;
    private Integer totalJobs;
    private String aiInsights;
    private List<String> skillGaps;
    private JobMarketInsights marketInsights;
    private Map<String, Double> compatibilityScores;
    private List<String> careerSuggestions;
}
