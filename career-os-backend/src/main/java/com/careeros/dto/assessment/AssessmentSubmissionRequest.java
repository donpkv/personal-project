package com.careeros.dto.assessment;

import java.util.List;
import java.util.UUID;

/**
 * DTO for assessment submission request
 */
public class AssessmentSubmissionRequest {
    private UUID assessmentId;
    private List<QuestionResponse> responses;
    
    // Getters and setters
    public UUID getAssessmentId() { return assessmentId; }
    public void setAssessmentId(UUID assessmentId) { this.assessmentId = assessmentId; }
    
    public List<QuestionResponse> getResponses() { return responses; }
    public void setResponses(List<QuestionResponse> responses) { this.responses = responses; }
    
    public static class QuestionResponse {
        private UUID questionId;
        private UUID selectedOptionId;
        private String textAnswer;
        private Double score;
        
        // Getters and setters
        public UUID getQuestionId() { return questionId; }
        public void setQuestionId(UUID questionId) { this.questionId = questionId; }
        
        public UUID getSelectedOptionId() { return selectedOptionId; }
        public void setSelectedOptionId(UUID selectedOptionId) { this.selectedOptionId = selectedOptionId; }
        
        public String getTextAnswer() { return textAnswer; }
        public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
        
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
    }
}
