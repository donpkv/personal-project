package com.careeros.dto.analytics;

import lombok.Data;

/**
 * DTO for mentorship analytics
 */
@Data
public class MentorshipAnalytics {
    private Long totalMentors;
    private Long activeMentors;
    private Long totalSessions;
    private Long completedSessions;
    private Double averageRating;
    private Double successRate;
    private Integer averageSessionDuration;
}
