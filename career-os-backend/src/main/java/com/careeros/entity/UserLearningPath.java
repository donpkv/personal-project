package com.careeros.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * User Learning Path entity tracking user enrollment and progress in learning paths
 */
@Entity
@Table(name = "user_learning_paths", indexes = {
    @Index(name = "idx_user_path_user", columnList = "user_id"),
    @Index(name = "idx_user_path_path", columnList = "learning_path_id"),
    @Index(name = "idx_user_path_status", columnList = "status")
})
public class UserLearningPath extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage = 0.0;

    @Column(name = "completed_steps", nullable = false)
    private Integer completedSteps = 0;

    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps = 0;

    @Column(name = "time_spent_hours", nullable = false)
    private Integer timeSpentHours = 0;

    @Column(name = "current_step_id")
    private String currentStepId;

    @Column(name = "rating")
    private Integer rating; // 1-5 scale

    @Column(name = "review", length = 1000)
    private String review;

    @Column(name = "certificate_issued", nullable = false)
    private Boolean certificateIssued = false;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "target_completion_date")
    private LocalDateTime targetCompletionDate;

    @Column(name = "daily_goal_hours")
    private Integer dailyGoalHours;

    @Column(name = "weekly_goal_hours")
    private Integer weeklyGoalHours;

    @Column(name = "reminder_enabled", nullable = false)
    private Boolean reminderEnabled = true;

    @Column(name = "reminder_time")
    private String reminderTime; // Format: "HH:MM"

    // Enum
    public enum EnrollmentStatus {
        ENROLLED,
        IN_PROGRESS,
        COMPLETED,
        PAUSED,
        DROPPED,
        EXPIRED
    }

    // Constructors
    public UserLearningPath() {
        this.enrolledAt = LocalDateTime.now();
    }

    public UserLearningPath(User user, LearningPath learningPath) {
        this();
        this.user = user;
        this.learningPath = learningPath;
        this.totalSteps = learningPath.getTotalSteps();
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LearningPath getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(LearningPath learningPath) {
        this.learningPath = learningPath;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
        
        LocalDateTime now = LocalDateTime.now();
        if (status == EnrollmentStatus.IN_PROGRESS && startedAt == null) {
            this.startedAt = now;
        } else if (status == EnrollmentStatus.COMPLETED && completedAt == null) {
            this.completedAt = now;
            this.progressPercentage = 100.0;
        }
        this.lastAccessedAt = now;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
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
    }

    public Integer getCompletedSteps() {
        return completedSteps;
    }

    public void setCompletedSteps(Integer completedSteps) {
        this.completedSteps = completedSteps;
        updateProgressPercentage();
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
        updateProgressPercentage();
    }

    public Integer getTimeSpentHours() {
        return timeSpentHours;
    }

    public void setTimeSpentHours(Integer timeSpentHours) {
        this.timeSpentHours = timeSpentHours;
    }

    public String getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(String currentStepId) {
        this.currentStepId = currentStepId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Boolean getCertificateIssued() {
        return certificateIssued;
    }

    public void setCertificateIssued(Boolean certificateIssued) {
        this.certificateIssued = certificateIssued;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public LocalDateTime getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(LocalDateTime targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Integer getDailyGoalHours() {
        return dailyGoalHours;
    }

    public void setDailyGoalHours(Integer dailyGoalHours) {
        this.dailyGoalHours = dailyGoalHours;
    }

    public Integer getWeeklyGoalHours() {
        return weeklyGoalHours;
    }

    public void setWeeklyGoalHours(Integer weeklyGoalHours) {
        this.weeklyGoalHours = weeklyGoalHours;
    }

    public Boolean getReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(Boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    // Helper methods
    private void updateProgressPercentage() {
        if (totalSteps != null && totalSteps > 0) {
            this.progressPercentage = (double) completedSteps / totalSteps * 100;
        }
    }

    public void incrementCompletedSteps() {
        this.completedSteps++;
        updateProgressPercentage();
        
        if (this.progressPercentage >= 100.0) {
            setStatus(EnrollmentStatus.COMPLETED);
        } else if (this.status == EnrollmentStatus.ENROLLED) {
            setStatus(EnrollmentStatus.IN_PROGRESS);
        }
    }

    public void addTimeSpent(int hours) {
        this.timeSpentHours += hours;
        this.lastAccessedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == EnrollmentStatus.IN_PROGRESS;
    }

    public boolean isOverdue() {
        return targetCompletionDate != null && 
               LocalDateTime.now().isAfter(targetCompletionDate) && 
               !isCompleted();
    }

    public long getDaysEnrolled() {
        return java.time.Duration.between(enrolledAt, LocalDateTime.now()).toDays();
    }

    public long getDaysToTarget() {
        if (targetCompletionDate == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), targetCompletionDate).toDays();
    }
}
