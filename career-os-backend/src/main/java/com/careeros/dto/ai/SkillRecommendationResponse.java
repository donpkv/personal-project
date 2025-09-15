package com.careeros.dto.ai;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for skill recommendations
 */
public class SkillRecommendationResponse {

    private List<SkillRecommendation> recommendations;
    private String learningPath;
    private String careerImpact;
    private LocalDateTime generatedAt;

    // Constructors
    public SkillRecommendationResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    // Inner class for individual skill recommendations
    public static class SkillRecommendation {
        private String skillName;
        private String category;
        private String priority; // HIGH, MEDIUM, LOW
        private String reason;
        private String timeToLearn;
        private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
        private Double marketDemand; // 0.0 to 1.0
        private Double salaryImpact; // Percentage increase

        // Constructors
        public SkillRecommendation() {}

        // Getters and Setters
        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getTimeToLearn() {
            return timeToLearn;
        }

        public void setTimeToLearn(String timeToLearn) {
            this.timeToLearn = timeToLearn;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public Double getMarketDemand() {
            return marketDemand;
        }

        public void setMarketDemand(Double marketDemand) {
            this.marketDemand = marketDemand;
        }

        public Double getSalaryImpact() {
            return salaryImpact;
        }

        public void setSalaryImpact(Double salaryImpact) {
            this.salaryImpact = salaryImpact;
        }
    }

    // Getters and Setters
    public List<SkillRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<SkillRecommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public String getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(String learningPath) {
        this.learningPath = learningPath;
    }

    public String getCareerImpact() {
        return careerImpact;
    }

    public void setCareerImpact(String careerImpact) {
        this.careerImpact = careerImpact;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
