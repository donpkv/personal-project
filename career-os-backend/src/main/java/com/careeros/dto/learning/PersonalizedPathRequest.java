package com.careeros.dto.learning;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO for personalized learning path request
 */
@Data
public class PersonalizedPathRequest {
    private UUID userId;
    private List<String> targetSkills;
    private String careerGoal;
    private String currentLevel;
    private Integer availableHoursPerWeek;
    private String preferredLearningStyle;
    private List<String> preferredResourceTypes;
    private String deadline;
    private String targetRole;
    private java.time.LocalDateTime targetCompletionDate;
    private Integer dailyGoalHours;
    private Integer weeklyGoalHours;
}
