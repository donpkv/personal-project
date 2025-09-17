package com.careeros.dto.analytics;

import lombok.Data;

/**
 * DTO for social analytics
 */
@Data
public class SocialAnalytics {
    private Integer studyGroupsJoined;
    private Integer mentoringSessions;
    private Double peerInteractionScore;
    private Integer postsCreated;
    private Integer commentsPosted;
    private Integer helpfulVotes;
}
