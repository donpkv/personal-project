package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Learning Path entity for structured skill development journeys
 */
@Entity
@Table(name = "learning_paths", indexes = {
    @Index(name = "idx_path_title", columnList = "title"),
    @Index(name = "idx_path_difficulty", columnList = "difficulty_level"),
    @Index(name = "idx_path_category", columnList = "category")
})
public class LearningPath extends BaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 2000)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PathCategory category;

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;

    @Column(name = "estimated_duration_weeks")
    private Integer estimatedDurationWeeks;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "enrollment_count", nullable = false)
    private Long enrollmentCount = 0L;

    @Column(name = "completion_rate")
    private Double completionRate = 0.0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @ElementCollection
    @CollectionTable(name = "learning_path_tags", joinColumns = @JoinColumn(name = "path_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "learning_path_outcomes", joinColumns = @JoinColumn(name = "path_id"))
    @Column(name = "outcome", length = 500)
    private Set<String> learningOutcomes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "learning_path_skills",
        joinColumns = @JoinColumn(name = "path_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> targetSkills = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "learning_path_prerequisites",
        joinColumns = @JoinColumn(name = "path_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_skill_id")
    )
    private Set<Skill> prerequisiteSkills = new HashSet<>();

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PathStep> steps = new HashSet<>();

    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL)
    private Set<UserLearningPath> userEnrollments = new HashSet<>();

    // Enums
    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }

    public enum PathCategory {
        PROGRAMMING,
        DATA_SCIENCE,
        WEB_DEVELOPMENT,
        MOBILE_DEVELOPMENT,
        CLOUD_COMPUTING,
        CYBERSECURITY,
        AI_MACHINE_LEARNING,
        DEVOPS,
        UI_UX_DESIGN,
        PROJECT_MANAGEMENT,
        BUSINESS_ANALYSIS,
        SOFT_SKILLS,
        CERTIFICATION_PREP,
        CAREER_TRANSITION
    }

    // Constructors
    public LearningPath() {}

    public LearningPath(String title, DifficultyLevel difficultyLevel, PathCategory category) {
        this.title = title;
        this.difficultyLevel = difficultyLevel;
        this.category = category;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public PathCategory getCategory() {
        return category;
    }

    public void setCategory(PathCategory category) {
        this.category = category;
    }

    public Integer getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Integer estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public Integer getEstimatedDurationWeeks() {
        return estimatedDurationWeeks;
    }

    public void setEstimatedDurationWeeks(Integer estimatedDurationWeeks) {
        this.estimatedDurationWeeks = estimatedDurationWeeks;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(Long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public Double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getLearningOutcomes() {
        return learningOutcomes;
    }

    public void setLearningOutcomes(Set<String> learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }

    public Set<Skill> getTargetSkills() {
        return targetSkills;
    }

    public void setTargetSkills(Set<Skill> targetSkills) {
        this.targetSkills = targetSkills;
    }

    public Set<Skill> getPrerequisiteSkills() {
        return prerequisiteSkills;
    }

    public void setPrerequisiteSkills(Set<Skill> prerequisiteSkills) {
        this.prerequisiteSkills = prerequisiteSkills;
    }

    public Set<PathStep> getSteps() {
        return steps;
    }

    public void setSteps(Set<PathStep> steps) {
        this.steps = steps;
    }

    public Set<UserLearningPath> getUserEnrollments() {
        return userEnrollments;
    }

    public void setUserEnrollments(Set<UserLearningPath> userEnrollments) {
        this.userEnrollments = userEnrollments;
    }

    // Helper methods
    public void incrementEnrollmentCount() {
        this.enrollmentCount++;
    }

    public void updateAverageRating(Double newRating) {
        if (this.averageRating == null || this.averageRating == 0.0) {
            this.averageRating = newRating;
            this.ratingCount = 1;
        } else {
            double totalRating = this.averageRating * this.ratingCount;
            this.ratingCount++;
            this.averageRating = (totalRating + newRating) / this.ratingCount;
        }
    }

    public int getTotalSteps() {
        return steps.size();
    }

    public boolean hasPrerequisites() {
        return !prerequisiteSkills.isEmpty();
    }
}
