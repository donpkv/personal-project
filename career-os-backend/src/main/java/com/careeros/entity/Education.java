package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Education entity for resume education history
 */
@Entity
@Table(name = "educations", indexes = {
    @Index(name = "idx_education_resume", columnList = "resume_id"),
    @Index(name = "idx_education_dates", columnList = "start_date, end_date")
})
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @NotBlank
    @Size(max = 100)
    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    @Size(max = 100)
    @Column(name = "degree")
    private String degree;

    @Size(max = 100)
    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(name = "gpa")
    private Double gpa;

    @Column(name = "max_gpa")
    private Double maxGpa;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;

    @Size(max = 100)
    @Column(name = "location")
    private String location;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "honors")
    private String honors;

    @Column(name = "relevant_coursework", length = 500)
    private String relevantCoursework;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Constructors
    public Education() {}

    public Education(Resume resume, String institutionName, String degree) {
        this.resume = resume;
        this.institutionName = institutionName;
        this.degree = degree;
    }

    // Getters and Setters
    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Double getMaxGpa() {
        return maxGpa;
    }

    public void setMaxGpa(Double maxGpa) {
        this.maxGpa = maxGpa;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHonors() {
        return honors;
    }

    public void setHonors(String honors) {
        this.honors = honors;
    }

    public String getRelevantCoursework() {
        return relevantCoursework;
    }

    public void setRelevantCoursework(String relevantCoursework) {
        this.relevantCoursework = relevantCoursework;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
