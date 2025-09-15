package com.careeros.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for resume analysis
 */
public class ResumeAnalysisRequest {

    private UUID userId;
    private UUID resumeId;

    @NotBlank(message = "Resume content is required")
    @Size(min = 100, message = "Resume content must be at least 100 characters")
    private String resumeContent;

    @Size(max = 5000, message = "Job description must be less than 5000 characters")
    private String jobDescription;

    @Size(max = 100, message = "Target role must be less than 100 characters")
    private String targetRole;

    @Size(max = 100, message = "Target industry must be less than 100 characters")
    private String targetIndustry;

    private List<String> targetKeywords;

    private String analysisType; // BASIC, COMPREHENSIVE, ATS_FOCUSED

    // Constructors
    public ResumeAnalysisRequest() {}

    public ResumeAnalysisRequest(UUID userId, String resumeContent) {
        this.userId = userId;
        this.resumeContent = resumeContent;
        this.analysisType = "COMPREHENSIVE";
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getResumeId() {
        return resumeId;
    }

    public void setResumeId(UUID resumeId) {
        this.resumeId = resumeId;
    }

    public String getResumeContent() {
        return resumeContent;
    }

    public void setResumeContent(String resumeContent) {
        this.resumeContent = resumeContent;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTargetIndustry() {
        return targetIndustry;
    }

    public void setTargetIndustry(String targetIndustry) {
        this.targetIndustry = targetIndustry;
    }

    public List<String> getTargetKeywords() {
        return targetKeywords;
    }

    public void setTargetKeywords(List<String> targetKeywords) {
        this.targetKeywords = targetKeywords;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
}
