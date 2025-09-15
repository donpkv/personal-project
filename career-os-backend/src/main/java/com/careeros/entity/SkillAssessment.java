package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Skill Assessment entity for comprehensive skill evaluation
 */
@Entity
@Table(name = "skill_assessments", indexes = {
    @Index(name = "idx_assessment_user", columnList = "user_id"),
    @Index(name = "idx_assessment_skill", columnList = "skill_id"),
    @Index(name = "idx_assessment_status", columnList = "status")
})
public class SkillAssessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false)
    private AssessmentType assessmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssessmentStatus status = AssessmentStatus.NOT_STARTED;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "answered_questions")
    private Integer answeredQuestions = 0;

    @Column(name = "correct_answers")
    private Integer correctAnswers = 0;

    @Min(0)
    @Max(100)
    @Column(name = "score_percentage")
    private Integer scorePercentage;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "ai_evaluation", length = 2000)
    private String aiEvaluation;

    @Column(name = "strengths", length = 1000)
    private String strengths;

    @Column(name = "weaknesses", length = 1000)
    private String weaknesses;

    @Column(name = "recommendations", length = 2000)
    private String recommendations;

    @Column(name = "certificate_issued", nullable = false)
    private Boolean certificateIssued = false;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AssessmentQuestion> questions = new HashSet<>();

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AssessmentResponse> responses = new HashSet<>();

    // Enums
    public enum AssessmentType {
        MULTIPLE_CHOICE,
        CODING_CHALLENGE,
        PROJECT_BASED,
        PRACTICAL_EXERCISE,
        MIXED
    }

    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }

    public enum AssessmentStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        EXPIRED,
        ABANDONED
    }

    // Constructors
    public SkillAssessment() {}

    public SkillAssessment(User user, Skill skill, String title, AssessmentType assessmentType) {
        this.user = user;
        this.skill = skill;
        this.title = title;
        this.assessmentType = assessmentType;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
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

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
        
        LocalDateTime now = LocalDateTime.now();
        if (status == AssessmentStatus.IN_PROGRESS && startedAt == null) {
            this.startedAt = now;
        } else if (status == AssessmentStatus.COMPLETED && completedAt == null) {
            this.completedAt = now;
        }
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(Integer answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(Integer scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getAiEvaluation() {
        return aiEvaluation;
    }

    public void setAiEvaluation(String aiEvaluation) {
        this.aiEvaluation = aiEvaluation;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
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

    public Set<AssessmentQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<AssessmentQuestion> questions) {
        this.questions = questions;
    }

    public Set<AssessmentResponse> getResponses() {
        return responses;
    }

    public void setResponses(Set<AssessmentResponse> responses) {
        this.responses = responses;
    }

    // Helper methods
    public boolean isCompleted() {
        return status == AssessmentStatus.COMPLETED;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public double getCompletionPercentage() {
        if (totalQuestions == null || totalQuestions == 0) return 0.0;
        return (double) answeredQuestions / totalQuestions * 100;
    }

    public double getAccuracyPercentage() {
        if (answeredQuestions == null || answeredQuestions == 0) return 0.0;
        return (double) correctAnswers / answeredQuestions * 100;
    }

    public long getRemainingTimeMinutes() {
        if (timeLimitMinutes == null || startedAt == null) return 0;
        LocalDateTime deadline = startedAt.plusMinutes(timeLimitMinutes);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(deadline)) return 0;
        return java.time.Duration.between(now, deadline).toMinutes();
    }
}
