package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Question Option entity for multiple choice questions
 */
@Entity
@Table(name = "question_options", indexes = {
    @Index(name = "idx_option_question", columnList = "question_id"),
    @Index(name = "idx_option_order", columnList = "option_order")
})
public class QuestionOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private AssessmentQuestion question;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "option_text", nullable = false, length = 1000)
    private String optionText;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "explanation", length = 500)
    private String explanation;

    @Column(name = "code_snippet", length = 2000)
    private String codeSnippet;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public QuestionOption() {}

    public QuestionOption(AssessmentQuestion question, String optionText, Integer optionOrder, Boolean isCorrect) {
        this.question = question;
        this.optionText = optionText;
        this.optionOrder = optionOrder;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public AssessmentQuestion getQuestion() {
        return question;
    }

    public void setQuestion(AssessmentQuestion question) {
        this.question = question;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
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
}
