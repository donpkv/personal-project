package com.careeros.dto.analytics;

import lombok.Data;

import java.util.Map;

/**
 * DTO for time analytics
 */
@Data
public class TimeAnalytics {
    private Integer totalMinutesLearned;
    private Integer averageDailyMinutes;
    private Map<String, Integer> learningByDay;
    private Map<String, Integer> learningByHour;
    private String mostActiveDay;
    private String mostActiveHour;
    private Map<Integer, Integer> dailyLearningPattern;
    private Map<Integer, Integer> hourlyLearningPattern;
}
