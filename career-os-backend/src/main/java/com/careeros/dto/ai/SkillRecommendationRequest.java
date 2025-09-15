package com.careeros.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for skill recommendations
 */
public class SkillRecommendationRequest {

    private UUID userId;

    @NotBlank(message = "Current role is required")
    @Size(max = 100, message = "Current role must be less than 100 characters")
    private String currentRole;

    @NotBlank(message = "Career goal is required")
    @Size(max = 200, message = "Career goal must be less than 200 characters")
    private String careerGoal;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel; // ENTRY_LEVEL, MID_LEVEL, SENIOR_LEVEL, EXECUTIVE

    @Size(max = 100, message = "Industry must be less than 100 characters")
    private String industry;

    private List<String> currentSkills;

    private List<String> interests;

    @Size(max = 500, message = "Additional context must be less than 500 characters")
    private String additionalContext;

    // Constructors
    public SkillRecommendationRequest() {}

    public SkillRecommendationRequest(UUID userId, String currentRole, String careerGoal, String experienceLevel) {
        this.userId = userId;
        this.currentRole = currentRole;
        this.careerGoal = careerGoal;
        this.experienceLevel = experienceLevel;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    public String getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(String careerGoal) {
        this.careerGoal = careerGoal;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public List<String> getCurrentSkills() {
        return currentSkills;
    }

    public void setCurrentSkills(List<String> currentSkills) {
        this.currentSkills = currentSkills;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getAdditionalContext() {
        return additionalContext;
    }

    public void setAdditionalContext(String additionalContext) {
        this.additionalContext = additionalContext;
    }
}
