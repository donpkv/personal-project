package com.careeros.dto.analytics;

import lombok.Data;

import java.util.Map;

/**
 * DTO for user engagement analytics
 */
@Data
public class UserEngagementAnalytics {
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsers;
    private Double engagementRate;
    private Double retentionRate;
    private Map<String, Long> usersByRegion;
    private Integer averageSessionDuration;
}
