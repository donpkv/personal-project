package com.careeros.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Assessment Response entity for user answers to assessment questions
 */
@Entity
@Table(name = "assessment_responses", indexes = {
    @Index(name = "idx_response_assessment", columnList = "assessment_id"),
    @Index(name = "idx_response_question", columnList = "question_id"),
    @Index(name = "idx_response_user", columnList = "user_id")
})
public class AssessmentResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private SkillAssessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private AssessmentQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "answer_text", length = 5000)
    private String answerText;

    @Column(name = "code_answer", length = 10000)
    private String codeAnswer;

    @ManyToMany
    @JoinTable(
        name = "response_selected_options",
        joinColumns = @JoinColumn(name = "response_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<QuestionOption> selectedOptions = new HashSet<>();

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "points_earned")
    private Integer pointsEarned = 0;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds = 0;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "ai_feedback", length = 1000)
    private String aiFeedback;

    @Column(name = "confidence_level")
    private Integer confidenceLevel; // 1-5 scale

    // Constructors
    public AssessmentResponse() {}

    public AssessmentResponse(SkillAssessment assessment, AssessmentQuestion question, User user) {
        this.assessment = assessment;
        this.question = question;
        this.user = user;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public SkillAssessment getAssessment() {
        return assessment;
    }

    public void setAssessment(SkillAssessment assessment) {
        this.assessment = assessment;
    }

    public AssessmentQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AssessmentQuestion question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getCodeAnswer() {
        return codeAnswer;
    }

    public void setCodeAnswer(String codeAnswer) {
        this.codeAnswer = codeAnswer;
    }

    public Set<QuestionOption> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Set<QuestionOption> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public Integer getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(Integer confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    // Helper methods
    public void addSelectedOption(QuestionOption option) {
        this.selectedOptions.add(option);
    }

    public void removeSelectedOption(QuestionOption option) {
        this.selectedOptions.remove(option);
    }

    public boolean hasSelectedOption(QuestionOption option) {
        return this.selectedOptions.contains(option);
    }
}
