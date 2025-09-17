package com.careeros.dto.analytics;

import lombok.Data;

/**
 * DTO for goal analytics
 */
@Data
public class GoalAnalytics {
    private Integer totalGoals;
    private Integer achievedGoals;
    private Integer activeGoals;
    private Double completionRate;
    private Integer goalsThisMonth;
    private Integer goalsThisYear;
    private Double goalAchievementRate;
}
