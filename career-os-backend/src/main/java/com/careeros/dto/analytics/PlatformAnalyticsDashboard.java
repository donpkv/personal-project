package com.careeros.dto.analytics;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for platform analytics dashboard
 */
@Data
public class PlatformAnalyticsDashboard {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime generatedAt;
    private UserEngagementAnalytics userEngagement;
    private ContentAnalytics contentAnalytics;
    private SkillTrendsAnalytics skillTrends;
    private MentorshipAnalytics mentorshipAnalytics;
    private JobMarketAnalytics jobMarketAnalytics;
    private RevenueAnalytics revenueAnalytics;
}
