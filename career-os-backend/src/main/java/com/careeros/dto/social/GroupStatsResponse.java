package com.careeros.dto.social;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for group statistics response
 */
@Data
public class GroupStatsResponse {
    private Long totalMembers;
    private Long activeMembers;
    private Long totalPosts;
    private Long totalComments;
    private Long weeklyPosts;
    private Long weeklyComments;
    private Double engagementRate;
    private LocalDateTime lastActivity;
    private String mostActiveDay;
    private String popularTags;
    private java.util.UUID groupId;
    private Integer memberCount;
    private Long postsCount;
    private Integer userContributionScore;
    private Integer userRank;
}
