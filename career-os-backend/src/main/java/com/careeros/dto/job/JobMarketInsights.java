package com.careeros.dto.job;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for job market insights
 */
@Data
public class JobMarketInsights {
    private String location;
    private String jobTitle;
    private LocalDateTime analysisDate;
    private Integer totalJobPostings;
    private Integer recentJobPostings;
    private Double averageSalary;
    private Double medianSalary;
    private List<String> topHiringCompanies;
    private List<String> topRequiredSkills;
    private Map<String, Integer> experienceLevelDistribution;
    private Map<String, Integer> jobTypeDistribution;
    private Integer remoteJobCount;
    private Double growthRate;
    private String marketTrend;
    private double remoteWorkPercentage;
}
