package com.careeros.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * User Path Step Progress entity tracking individual step completion
 */
@Entity
@Table(name = "user_path_step_progress", indexes = {
    @Index(name = "idx_step_progress_user", columnList = "user_id"),
    @Index(name = "idx_step_progress_step", columnList = "path_step_id"),
    @Index(name = "idx_step_progress_status", columnList = "status")
})
public class UserPathStepProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "path_step_id", nullable = false)
    private PathStep pathStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StepStatus status = StepStatus.NOT_STARTED;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage = 0.0;

    @Column(name = "time_spent_minutes", nullable = false)
    private Integer timeSpentMinutes = 0;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "difficulty_rating")
    private Integer difficultyRating; // 1-5 scale

    @Column(name = "quality_rating")
    private Integer qualityRating; // 1-5 scale

    @Column(name = "feedback", length = 500)
    private String feedback;

    // Enum
    public enum StepStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        SKIPPED,
        FAILED
    }

    // Constructors
    public UserPathStepProgress() {}

    public UserPathStepProgress(User user, PathStep pathStep) {
        this.user = user;
        this.pathStep = pathStep;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PathStep getPathStep() {
        return pathStep;
    }

    public void setPathStep(PathStep pathStep) {
        this.pathStep = pathStep;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
        
        LocalDateTime now = LocalDateTime.now();
        if (status == StepStatus.IN_PROGRESS && startedAt == null) {
            this.startedAt = now;
        } else if (status == StepStatus.COMPLETED && completedAt == null) {
            this.completedAt = now;
            this.progressPercentage = 100.0;
        }
        this.lastAccessedAt = now;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
        
        if (progressPercentage >= 100.0 && status != StepStatus.COMPLETED) {
            setStatus(StepStatus.COMPLETED);
        } else if (progressPercentage > 0.0 && status == StepStatus.NOT_STARTED) {
            setStatus(StepStatus.IN_PROGRESS);
        }
    }

    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }

    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getDifficultyRating() {
        return difficultyRating;
    }

    public void setDifficultyRating(Integer difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public Integer getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    // Helper methods
    public void addTimeSpent(int minutes) {
        this.timeSpentMinutes += minutes;
        this.lastAccessedAt = LocalDateTime.now();
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public boolean isCompleted() {
        return status == StepStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == StepStatus.IN_PROGRESS;
    }

    public boolean isStarted() {
        return status != StepStatus.NOT_STARTED;
    }

    public long getDaysInProgress() {
        if (startedAt == null) return 0;
        LocalDateTime endTime = completedAt != null ? completedAt : LocalDateTime.now();
        return java.time.Duration.between(startedAt, endTime).toDays();
    }

    public double getTimeSpentHours() {
        return timeSpentMinutes / 60.0;
    }
}
