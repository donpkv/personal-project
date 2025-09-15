package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Assessment Question entity for skill evaluation questions
 */
@Entity
@Table(name = "assessment_questions", indexes = {
    @Index(name = "idx_question_assessment", columnList = "assessment_id"),
    @Index(name = "idx_question_order", columnList = "question_order")
})
public class AssessmentQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private SkillAssessment assessment;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "question_text", nullable = false, length = 2000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "points", nullable = false)
    private Integer points = 1;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "explanation", length = 1000)
    private String explanation;

    @Column(name = "code_snippet", length = 5000)
    private String codeSnippet;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionOption> options = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<AssessmentResponse> responses = new HashSet<>();

    // Enum
    public enum QuestionType {
        MULTIPLE_CHOICE,
        SINGLE_CHOICE,
        TRUE_FALSE,
        SHORT_ANSWER,
        LONG_ANSWER,
        CODE_COMPLETION,
        CODE_DEBUG,
        DRAG_DROP,
        MATCHING
    }

    // Constructors
    public AssessmentQuestion() {}

    public AssessmentQuestion(SkillAssessment assessment, String questionText, QuestionType questionType, Integer questionOrder) {
        this.assessment = assessment;
        this.questionText = questionText;
        this.questionType = questionType;
        this.questionOrder = questionOrder;
    }

    // Getters and Setters
    public SkillAssessment getAssessment() {
        return assessment;
    }

    public void setAssessment(SkillAssessment assessment) {
        this.assessment = assessment;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(Integer timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Set<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(Set<QuestionOption> options) {
        this.options = options;
    }

    public Set<AssessmentResponse> getResponses() {
        return responses;
    }

    public void setResponses(Set<AssessmentResponse> responses) {
        this.responses = responses;
    }

    // Helper methods
    public Set<QuestionOption> getCorrectOptions() {
        Set<QuestionOption> correctOptions = new HashSet<>();
        for (QuestionOption option : options) {
            if (option.getIsCorrect()) {
                correctOptions.add(option);
            }
        }
        return correctOptions;
    }

    public boolean hasMultipleCorrectAnswers() {
        return getCorrectOptions().size() > 1;
    }
}
