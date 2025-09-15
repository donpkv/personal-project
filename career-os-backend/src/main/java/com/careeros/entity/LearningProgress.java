package com.careeros.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Learning Progress entity tracking user's progress through learning resources
 */
@Entity
@Table(name = "learning_progress", indexes = {
    @Index(name = "idx_progress_user", columnList = "user_id"),
    @Index(name = "idx_progress_resource", columnList = "learning_resource_id"),
    @Index(name = "idx_progress_status", columnList = "status")
})
public class LearningProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_resource_id", nullable = false)
    private LearningResource learningResource;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "completion_percentage", nullable = false)
    private Integer completionPercentage = 0;

    @Column(name = "time_spent_minutes", nullable = false)
    private Integer timeSpentMinutes = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "current_chapter")
    private String currentChapter;

    @Column(name = "current_position")
    private String currentPosition; // For videos: timestamp, for books: page number, etc.

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "rating")
    private Integer rating; // 1-5 scale

    @Column(name = "certificate_earned", nullable = false)
    private Boolean certificateEarned = false;

    @Column(name = "certificate_url")
    private String certificateUrl;

    // Enum
    public enum ProgressStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        PAUSED,
        DROPPED
    }

    // Constructors
    public LearningProgress() {}

    public LearningProgress(User user, LearningResource learningResource) {
        this.user = user;
        this.learningResource = learningResource;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LearningResource getLearningResource() {
        return learningResource;
    }

    public void setLearningResource(LearningResource learningResource) {
        this.learningResource = learningResource;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
        
        // Auto-set timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        if (status == ProgressStatus.IN_PROGRESS && startedAt == null) {
            this.startedAt = now;
        } else if (status == ProgressStatus.COMPLETED && completedAt == null) {
            this.completedAt = now;
            this.completionPercentage = 100;
        }
        this.lastAccessedAt = now;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
        
        // Auto-update status based on completion
        if (completionPercentage == 0) {
            this.status = ProgressStatus.NOT_STARTED;
        } else if (completionPercentage == 100) {
            this.status = ProgressStatus.COMPLETED;
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now();
            }
        } else if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
        }
    }

    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }

    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
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

    public String getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(String currentChapter) {
        this.currentChapter = currentChapter;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getCertificateEarned() {
        return certificateEarned;
    }

    public void setCertificateEarned(Boolean certificateEarned) {
        this.certificateEarned = certificateEarned;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    // Helper methods
    public boolean isCompleted() {
        return status == ProgressStatus.COMPLETED || completionPercentage == 100;
    }

    public boolean isInProgress() {
        return status == ProgressStatus.IN_PROGRESS;
    }
}
