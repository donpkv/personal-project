package com.careeros.dto.job;

import lombok.Data;

import java.util.List;

/**
 * DTO for job recommendation request
 */
@Data
public class JobRecommendationRequest {
    private String location;
    private String jobTitle;
    private List<String> skills;
    private String experienceLevel;
    private Double minSalary;
    private Double maxSalary;
    private String jobType;
    private Boolean remoteOnly;
    private String industry;
    private Integer limit;
}
