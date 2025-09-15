package com.careeros.dto.ai;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for resume analysis
 */
public class ResumeAnalysisResponse {

    private Integer atsScore; // 0-100
    private String overallFeedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private KeywordAnalysis keywordAnalysis;
    private Map<String, SectionAnalysis> sections;
    private List<Recommendation> recommendations;
    private LocalDateTime analyzedAt;

    // Constructors
    public ResumeAnalysisResponse() {
        this.analyzedAt = LocalDateTime.now();
    }

    // Inner class for keyword analysis
    public static class KeywordAnalysis {
        private List<String> foundKeywords;
        private List<String> missingKeywords;
        private Double keywordDensity;
        private Integer keywordMatches;
        private Integer totalKeywords;

        // Constructors
        public KeywordAnalysis() {}

        // Getters and Setters
        public List<String> getFoundKeywords() {
            return foundKeywords;
        }

        public void setFoundKeywords(List<String> foundKeywords) {
            this.foundKeywords = foundKeywords;
        }

        public List<String> getMissingKeywords() {
            return missingKeywords;
        }

        public void setMissingKeywords(List<String> missingKeywords) {
            this.missingKeywords = missingKeywords;
        }

        public Double getKeywordDensity() {
            return keywordDensity;
        }

        public void setKeywordDensity(Double keywordDensity) {
            this.keywordDensity = keywordDensity;
        }

        public Integer getKeywordMatches() {
            return keywordMatches;
        }

        public void setKeywordMatches(Integer keywordMatches) {
            this.keywordMatches = keywordMatches;
        }

        public Integer getTotalKeywords() {
            return totalKeywords;
        }

        public void setTotalKeywords(Integer totalKeywords) {
            this.totalKeywords = totalKeywords;
        }
    }

    // Inner class for section analysis
    public static class SectionAnalysis {
        private Integer score; // 0-10
        private String feedback;
        private List<String> suggestions;
        private Boolean present;

        // Constructors
        public SectionAnalysis() {}

        public SectionAnalysis(Integer score, String feedback, Boolean present) {
            this.score = score;
            this.feedback = feedback;
            this.present = present;
        }

        // Getters and Setters
        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }

        public Boolean getPresent() {
            return present;
        }

        public void setPresent(Boolean present) {
            this.present = present;
        }
    }

    // Inner class for recommendations
    public static class Recommendation {
        private String category; // FORMATTING, CONTENT, KEYWORDS, STRUCTURE
        private String priority; // HIGH, MEDIUM, LOW
        private String suggestion;
        private String impact;
        private Integer expectedScoreIncrease;

        // Constructors
        public Recommendation() {}

        // Getters and Setters
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

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public String getImpact() {
            return impact;
        }

        public void setImpact(String impact) {
            this.impact = impact;
        }

        public Integer getExpectedScoreIncrease() {
            return expectedScoreIncrease;
        }

        public void setExpectedScoreIncrease(Integer expectedScoreIncrease) {
            this.expectedScoreIncrease = expectedScoreIncrease;
        }
    }

    // Getters and Setters
    public Integer getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public String getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public KeywordAnalysis getKeywordAnalysis() {
        return keywordAnalysis;
    }

    public void setKeywordAnalysis(KeywordAnalysis keywordAnalysis) {
        this.keywordAnalysis = keywordAnalysis;
    }

    public Map<String, SectionAnalysis> getSections() {
        return sections;
    }

    public void setSections(Map<String, SectionAnalysis> sections) {
        this.sections = sections;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
