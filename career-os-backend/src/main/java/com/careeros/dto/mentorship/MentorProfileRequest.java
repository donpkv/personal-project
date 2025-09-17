package com.careeros.dto.mentorship;

import lombok.Data;

import java.util.List;

/**
 * DTO for mentor profile request
 */
@Data
public class MentorProfileRequest {
    private String title;
    private String bio;
    private Integer yearsOfExperience;
    private List<String> expertiseAreas;
    private List<String> industries;
    private String preferredMenteeLevel;
    private String mentorshipStyle;
    private Double hourlyRate;
    private Integer maxMentees;
    private List<String> availableTimeSlots;
    private String timezone;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
}
