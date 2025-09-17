package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for skill trends analytics
 */
@Data
public class SkillTrendsAnalytics {
    private List<String> trendingSkills;
    private List<String> emergingSkills;
    private Map<String, Double> skillGrowthRates;
    private List<String> mostCertifiedSkills;
    private Map<String, Long> skillsByIndustry;
}
