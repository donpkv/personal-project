package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for job market analytics
 */
@Data
public class JobMarketAnalytics {
    private Long totalJobPostings;
    private Long activeJobPostings;
    private List<String> topHiringCompanies;
    private List<String> inDemandSkills;
    private Map<String, Double> salaryTrends;
    private Double jobGrowthRate;
}
