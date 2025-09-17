package com.careeros.dto.analytics;

import lombok.Data;

/**
 * DTO for skill gap metrics
 */
@Data
public class SkillGapMetrics {
    private String skillName;
    private String currentLevel;
    private String requiredLevel;
    private Integer gapSize;
    private Integer estimatedLearningTime;
    private String priority;
    private String difficulty;
    private java.util.Map<com.careeros.entity.UserSkill.ProficiencyLevel, Long> levelDistribution;
    private Integer usersWithSkill;
    private Integer usersWithoutSkill;
    private Double skillCoverage;
    private Double averageProficiency;
}
