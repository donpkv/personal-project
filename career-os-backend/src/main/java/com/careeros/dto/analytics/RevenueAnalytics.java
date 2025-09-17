package com.careeros.dto.analytics;

import lombok.Data;

import java.util.Map;

/**
 * DTO for revenue analytics
 */
@Data
public class RevenueAnalytics {
    private Double totalRevenue;
    private Double monthlyRecurringRevenue;
    private Double averageRevenuePerUser;
    private Map<String, Double> revenueBySource;
    private Double growthRate;
    private Integer paidSubscriptions;
}
