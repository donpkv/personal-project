package com.careeros.dto.job;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for skill demand analysis
 */
@Data
public class SkillDemandAnalysis {
    private String skillName;
    private String location;
    private String industry;
    private LocalDateTime analysisDate;
    private Integer totalJobsRequiringSkill;
    private Double averageSalaryForSkill;
    private Integer jobGrowthLastMonth;
    private Integer jobGrowthLastQuarter;
    private Double demandScore;
    private String demandTrend;
    private List<String> relatedSkills;
    private Map<String, Integer> topHiringCompanies;
    private List<String> recommendedCertifications;
}
