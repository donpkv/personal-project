package com.careeros.dto.mentorship;

import com.careeros.entity.MentorProfile;
import lombok.Data;

import java.util.List;

/**
 * DTO for mentorship match response
 */
@Data
public class MentorshipMatchResponse {
    private MentorProfile mentorProfile;
    private Double compatibilityScore;
    private String matchReason;
    private List<String> sharedSkills;
    private List<String> mentorStrengths;
    private String availabilityMatch;
    private String recommendationText;
    private Double averageRating;
    private Integer totalReviews;
    private java.util.UUID mentorId;
    private String mentorName;
    private String mentorBio;
    private Integer yearsOfExperience;
    private Double hourlyRate;
    private List<String> matchReasons;
    private String aiExplanation;
}
