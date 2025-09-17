package com.careeros.dto.analytics;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO for skill gap analysis report
 */
@Data
public class SkillGapAnalysisReport {
    private UUID userId;
    private String targetRole;
    private List<String> currentSkills;
    private List<String> missingSkills;
    private List<SkillGapMetrics> skillGaps;
    private List<String> recommendations;
    private Integer estimatedTimeToFill;
    private java.time.LocalDateTime analysisDate;
    private Integer userCount;
    private List<String> targetSkills;
}
