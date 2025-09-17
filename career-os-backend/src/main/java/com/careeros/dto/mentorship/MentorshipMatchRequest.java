package com.careeros.dto.mentorship;

import lombok.Data;

import java.util.List;

/**
 * DTO for mentorship matching request
 */
@Data
public class MentorshipMatchRequest {
    private List<String> skillsToLearn;
    private String experienceLevel;
    private String industryPreference;
    private String preferredMentorshipStyle;
    private String preferredSchedule;
    private String timezone;
    private Double minCompatibilityScore = 0.6;
    private Integer maxResults = 10;
    private String learningGoals;
    private String communicationPreference;
    private List<String> goals;
}
