package com.careeros.dto.assessment;

import java.util.List;
import java.util.UUID;

/**
 * DTO for assessment submission request
 */
public class AssessmentSubmissionRequest {
    private UUID assessmentId;
    private UUID userId;
    private Integer timeSpentMinutes;
    private List<QuestionResponse> responses;
    
    // Getters and setters
    public UUID getAssessmentId() { return assessmentId; }
    public void setAssessmentId(UUID assessmentId) { this.assessmentId = assessmentId; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public Integer getTimeSpentMinutes() { return timeSpentMinutes; }
    public void setTimeSpentMinutes(Integer timeSpentMinutes) { this.timeSpentMinutes = timeSpentMinutes; }
    
    public List<QuestionResponse> getResponses() { return responses; }
    public void setResponses(List<QuestionResponse> responses) { this.responses = responses; }
    
    public static class QuestionResponse {
        private UUID questionId;
        private UUID selectedOptionId;
        private List<UUID> selectedOptionIds;
        private String textAnswer;
        private String answerText;
        private Double score;
        private Integer timeSpentSeconds;
        private String confidenceLevel;
        
        // Getters and setters
        public UUID getQuestionId() { return questionId; }
        public void setQuestionId(UUID questionId) { this.questionId = questionId; }
        
        public UUID getSelectedOptionId() { return selectedOptionId; }
        public void setSelectedOptionId(UUID selectedOptionId) { this.selectedOptionId = selectedOptionId; }
        
        public List<UUID> getSelectedOptionIds() { return selectedOptionIds; }
        public void setSelectedOptionIds(List<UUID> selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }
        
        public String getTextAnswer() { return textAnswer; }
        public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
        
        public String getAnswerText() { return answerText; }
        public void setAnswerText(String answerText) { this.answerText = answerText; }
        
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        
        public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
        public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
        
        public String getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    }
}
