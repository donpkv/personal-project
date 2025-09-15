package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Path Step entity representing individual steps in a learning path
 */
@Entity
@Table(name = "path_steps", indexes = {
    @Index(name = "idx_step_path", columnList = "learning_path_id"),
    @Index(name = "idx_step_order", columnList = "step_order")
})
public class PathStep extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private StepType stepType;

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
        name = "path_step_resources",
        joinColumns = @JoinColumn(name = "step_id"),
        inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<LearningResource> resources = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "path_step_assessments",
        joinColumns = @JoinColumn(name = "step_id"),
        inverseJoinColumns = @JoinColumn(name = "assessment_id")
    )
    private Set<SkillAssessment> assessments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "path_step_prerequisites",
        joinColumns = @JoinColumn(name = "step_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_step_id")
    )
    private Set<PathStep> prerequisiteSteps = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "path_step_objectives", joinColumns = @JoinColumn(name = "step_id"))
    @Column(name = "objective", length = 500)
    private Set<String> objectives = new HashSet<>();

    @OneToMany(mappedBy = "pathStep", cascade = CascadeType.ALL)
    private Set<UserPathStepProgress> userProgress = new HashSet<>();

    // Enum
    public enum StepType {
        LEARNING,
        PRACTICE,
        ASSESSMENT,
        PROJECT,
        READING,
        VIDEO,
        INTERACTIVE,
        MILESTONE
    }

    // Constructors
    public PathStep() {}

    public PathStep(LearningPath learningPath, String title, Integer stepOrder, StepType stepType) {
        this.learningPath = learningPath;
        this.title = title;
        this.stepOrder = stepOrder;
        this.stepType = stepType;
    }

    // Getters and Setters
    public LearningPath getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

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

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public StepType getStepType() {
        return stepType;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public Integer getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Integer estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<LearningResource> getResources() {
        return resources;
    }

    public void setResources(Set<LearningResource> resources) {
        this.resources = resources;
    }

    public Set<SkillAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(Set<SkillAssessment> assessments) {
        this.assessments = assessments;
    }

    public Set<PathStep> getPrerequisiteSteps() {
        return prerequisiteSteps;
    }

    public void setPrerequisiteSteps(Set<PathStep> prerequisiteSteps) {
        this.prerequisiteSteps = prerequisiteSteps;
    }

    public Set<String> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<String> objectives) {
        this.objectives = objectives;
    }

    public Set<UserPathStepProgress> getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(Set<UserPathStepProgress> userProgress) {
        this.userProgress = userProgress;
    }

    // Helper methods
    public void addResource(LearningResource resource) {
        this.resources.add(resource);
    }

    public void removeResource(LearningResource resource) {
        this.resources.remove(resource);
    }

    public void addAssessment(SkillAssessment assessment) {
        this.assessments.add(assessment);
    }

    public void removeAssessment(SkillAssessment assessment) {
        this.assessments.remove(assessment);
    }

    public void addPrerequisiteStep(PathStep step) {
        this.prerequisiteSteps.add(step);
    }

    public void removePrerequisiteStep(PathStep step) {
        this.prerequisiteSteps.remove(step);
    }

    public boolean hasPrerequisites() {
        return !prerequisiteSteps.isEmpty();
    }

    public int getTotalResources() {
        return resources.size();
    }

    public int getTotalAssessments() {
        return assessments.size();
    }
}
