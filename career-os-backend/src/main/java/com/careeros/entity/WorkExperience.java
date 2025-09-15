package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Work Experience entity for resume work history
 */
@Entity
@Table(name = "work_experiences", indexes = {
    @Index(name = "idx_work_experience_resume", columnList = "resume_id"),
    @Index(name = "idx_work_experience_dates", columnList = "start_date, end_date")
})
public class WorkExperience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @NotBlank
    @Size(max = 100)
    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @NotBlank
    @Size(max = 100)
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_url")
    private String companyUrl;

    @Size(max = 100)
    @Column(name = "location")
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;

    @Column(name = "description", length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "work_experience_achievements", joinColumns = @JoinColumn(name = "work_experience_id"))
    @Column(name = "achievement", length = 500)
    private Set<String> achievements = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "work_experience_technologies", joinColumns = @JoinColumn(name = "work_experience_id"))
    @Column(name = "technology")
    private Set<String> technologies = new HashSet<>();

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Constructors
    public WorkExperience() {}

    public WorkExperience(Resume resume, String jobTitle, String companyName, LocalDate startDate) {
        this.resume = resume;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.startDate = startDate;
    }

    // Getters and Setters
    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.isCurrent = (endDate == null);
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
        if (isCurrent) {
            this.endDate = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getAchievements() {
        return achievements;
    }

    public void setAchievements(Set<String> achievements) {
        this.achievements = achievements;
    }

    public Set<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<String> technologies) {
        this.technologies = technologies;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
