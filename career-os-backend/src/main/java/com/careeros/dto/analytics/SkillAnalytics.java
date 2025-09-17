package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for skill analytics
 */
@Data
public class SkillAnalytics {
    private Integer totalSkills;
    private Map<String, Integer> skillsByProficiency;
    private List<String> topSkills;
    private List<String> improvingSkills;
    private Integer skillsGainedThisMonth;
    private Double averageProficiency;
    private java.util.Map<com.careeros.entity.UserSkill.ProficiencyLevel, Long> skillLevelDistribution;
    private java.util.Map<String, Long> skillCategoryDistribution;
    private Integer recentlyImprovedSkills;
}
