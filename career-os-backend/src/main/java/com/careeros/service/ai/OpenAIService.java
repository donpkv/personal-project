package com.careeros.service.ai;

import com.careeros.dto.ai.ResumeAnalysisRequest;
import com.careeros.dto.ai.ResumeAnalysisResponse;
import com.careeros.dto.ai.SkillRecommendationRequest;
import com.careeros.dto.ai.SkillRecommendationResponse;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OpenAI service for AI-powered features
 * Handles skill recommendations and resume analysis
 */
@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${ai.openai.model:gpt-4-turbo-preview}")
    private String model;

    @Value("${ai.openai.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${ai.openai.temperature:0.7}")
    private Double temperature;

    private OpenAiService openAiService;

    private OpenAiService getOpenAiService() {
        if (openAiService == null) {
            openAiService = new OpenAiService(openAiApiKey, Duration.ofSeconds(30));
        }
        return openAiService;
    }

    /**
     * Generate skill recommendations based on user profile and career goals
     */
    @Cacheable(value = "skillRecommendations", key = "#request.userId + '_' + #request.careerGoal")
    public SkillRecommendationResponse getSkillRecommendations(SkillRecommendationRequest request) {
        try {
            String prompt = buildSkillRecommendationPrompt(request);
            String response = callOpenAI(prompt);
            return parseSkillRecommendationResponse(response);
        } catch (Exception e) {
            logger.error("Error generating skill recommendations", e);
            return createFallbackSkillRecommendations(request);
        }
    }

    /**
     * Analyze resume for ATS compliance and provide improvement suggestions
     */
    public ResumeAnalysisResponse analyzeResume(ResumeAnalysisRequest request) {
        try {
            String prompt = buildResumeAnalysisPrompt(request);
            String response = callOpenAI(prompt);
            return parseResumeAnalysisResponse(response);
        } catch (Exception e) {
            logger.error("Error analyzing resume", e);
            return createFallbackResumeAnalysis(request);
        }
    }

    private String buildSkillRecommendationPrompt(SkillRecommendationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a career development AI assistant. Based on the following user profile, ");
        prompt.append("recommend 8-12 relevant skills they should learn to advance their career.\n\n");
        
        prompt.append("User Profile:\n");
        prompt.append("- Current Role: ").append(request.getCurrentRole()).append("\n");
        prompt.append("- Career Goal: ").append(request.getCareerGoal()).append("\n");
        prompt.append("- Experience Level: ").append(request.getExperienceLevel()).append("\n");
        prompt.append("- Industry: ").append(request.getIndustry()).append("\n");
        
        if (request.getCurrentSkills() != null && !request.getCurrentSkills().isEmpty()) {
            prompt.append("- Current Skills: ").append(String.join(", ", request.getCurrentSkills())).append("\n");
        }
        
        if (request.getInterests() != null && !request.getInterests().isEmpty()) {
            prompt.append("- Interests: ").append(String.join(", ", request.getInterests())).append("\n");
        }

        prompt.append("\nPlease provide recommendations in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"recommendations\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"skillName\": \"Skill Name\",\n");
        prompt.append("      \"category\": \"PROGRAMMING_LANGUAGES|WEB_DEVELOPMENT|DATA_SCIENCE|etc\",\n");
        prompt.append("      \"priority\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"reason\": \"Why this skill is important\",\n");
        prompt.append("      \"timeToLearn\": \"Estimated months to proficiency\",\n");
        prompt.append("      \"difficulty\": \"BEGINNER|INTERMEDIATE|ADVANCED\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"learningPath\": \"Suggested order and approach for learning these skills\",\n");
        prompt.append("  \"careerImpact\": \"How these skills will impact career progression\"\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    private String buildResumeAnalysisPrompt(ResumeAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an ATS (Applicant Tracking System) expert and resume optimization specialist. ");
        prompt.append("Analyze the following resume content and provide detailed feedback.\n\n");
        
        prompt.append("Resume Content:\n");
        prompt.append(request.getResumeContent()).append("\n\n");
        
        if (request.getJobDescription() != null && !request.getJobDescription().isEmpty()) {
            prompt.append("Target Job Description:\n");
            prompt.append(request.getJobDescription()).append("\n\n");
        }

        prompt.append("Please analyze and provide feedback in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"atsScore\": 85,\n");
        prompt.append("  \"overallFeedback\": \"General assessment of the resume\",\n");
        prompt.append("  \"strengths\": [\"List of resume strengths\"],\n");
        prompt.append("  \"weaknesses\": [\"List of areas for improvement\"],\n");
        prompt.append("  \"keywordAnalysis\": {\n");
        prompt.append("    \"foundKeywords\": [\"keywords found in resume\"],\n");
        prompt.append("    \"missingKeywords\": [\"important keywords missing\"],\n");
        prompt.append("    \"keywordDensity\": 12.5\n");
        prompt.append("  },\n");
        prompt.append("  \"sections\": {\n");
        prompt.append("    \"summary\": {\"score\": 8, \"feedback\": \"feedback\"},\n");
        prompt.append("    \"experience\": {\"score\": 7, \"feedback\": \"feedback\"},\n");
        prompt.append("    \"skills\": {\"score\": 9, \"feedback\": \"feedback\"},\n");
        prompt.append("    \"education\": {\"score\": 8, \"feedback\": \"feedback\"}\n");
        prompt.append("  },\n");
        prompt.append("  \"recommendations\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"category\": \"FORMATTING|CONTENT|KEYWORDS|STRUCTURE\",\n");
        prompt.append("      \"priority\": \"HIGH|MEDIUM|LOW\",\n");
        prompt.append("      \"suggestion\": \"Specific improvement suggestion\",\n");
        prompt.append("      \"impact\": \"Expected impact on ATS score\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    private String callOpenAI(String prompt) {
        ChatMessage systemMessage = new ChatMessage("system", 
            "You are a professional career development AI assistant with expertise in skill development and resume optimization.");
        ChatMessage userMessage = new ChatMessage("user", prompt);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(systemMessage, userMessage))
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        ChatCompletionResult result = getOpenAiService().createChatCompletion(chatCompletionRequest);
        return result.getChoices().get(0).getMessage().getContent();
    }

    private SkillRecommendationResponse parseSkillRecommendationResponse(String response) {
        try {
            // Extract JSON from response
            String jsonStr = extractJsonFromResponse(response);
            
            // Parse the response (simplified parsing - in production, use Jackson ObjectMapper)
            SkillRecommendationResponse skillResponse = new SkillRecommendationResponse();
            
            // This is a simplified parser - implement proper JSON parsing
            skillResponse.setRecommendations(parseSkillRecommendations(jsonStr));
            skillResponse.setLearningPath(extractLearningPath(jsonStr));
            skillResponse.setCareerImpact(extractCareerImpact(jsonStr));
            
            return skillResponse;
        } catch (Exception e) {
            logger.error("Error parsing skill recommendation response", e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private ResumeAnalysisResponse parseResumeAnalysisResponse(String response) {
        try {
            // Extract JSON from response
            String jsonStr = extractJsonFromResponse(response);
            
            // Parse the response (simplified parsing - in production, use Jackson ObjectMapper)
            ResumeAnalysisResponse analysisResponse = new ResumeAnalysisResponse();
            
            // Extract ATS score
            Pattern scorePattern = Pattern.compile("\"atsScore\"\\s*:\\s*(\\d+)");
            Matcher scoreMatcher = scorePattern.matcher(jsonStr);
            if (scoreMatcher.find()) {
                analysisResponse.setAtsScore(Integer.parseInt(scoreMatcher.group(1)));
            }
            
            // Extract overall feedback
            analysisResponse.setOverallFeedback(extractStringValue(jsonStr, "overallFeedback"));
            
            // Extract strengths and weaknesses
            analysisResponse.setStrengths(extractArrayValues(jsonStr, "strengths"));
            analysisResponse.setWeaknesses(extractArrayValues(jsonStr, "weaknesses"));
            
            // Extract recommendations
            analysisResponse.setRecommendations(parseRecommendations(jsonStr));
            
            return analysisResponse;
        } catch (Exception e) {
            logger.error("Error parsing resume analysis response", e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private String extractJsonFromResponse(String response) {
        // Find JSON content between { and }
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }

    private List<SkillRecommendationResponse.SkillRecommendation> parseSkillRecommendations(String jsonStr) {
        List<SkillRecommendationResponse.SkillRecommendation> recommendations = new ArrayList<>();
        
        // Simplified parsing - implement proper JSON parsing in production
        Pattern pattern = Pattern.compile("\"skillName\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonStr);
        
        while (matcher.find()) {
            SkillRecommendationResponse.SkillRecommendation recommendation = 
                new SkillRecommendationResponse.SkillRecommendation();
            recommendation.setSkillName(matcher.group(1));
            recommendation.setPriority("HIGH"); // Default values
            recommendation.setCategory("PROGRAMMING_LANGUAGES");
            recommendation.setReason("AI-recommended skill for career advancement");
            recommendations.add(recommendation);
        }
        
        return recommendations;
    }

    private String extractStringValue(String jsonStr, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonStr);
        return matcher.find() ? matcher.group(1) : "";
    }

    private List<String> extractArrayValues(String jsonStr, String key) {
        List<String> values = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(jsonStr);
        
        if (matcher.find()) {
            String arrayContent = matcher.group(1);
            Pattern valuePattern = Pattern.compile("\"([^\"]+)\"");
            Matcher valueMatcher = valuePattern.matcher(arrayContent);
            
            while (valueMatcher.find()) {
                values.add(valueMatcher.group(1));
            }
        }
        
        return values;
    }

    private String extractLearningPath(String jsonStr) {
        return extractStringValue(jsonStr, "learningPath");
    }

    private String extractCareerImpact(String jsonStr) {
        return extractStringValue(jsonStr, "careerImpact");
    }

    private List<ResumeAnalysisResponse.Recommendation> parseRecommendations(String jsonStr) {
        List<ResumeAnalysisResponse.Recommendation> recommendations = new ArrayList<>();
        
        // Simplified parsing - implement proper JSON parsing in production
        Pattern pattern = Pattern.compile("\"suggestion\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonStr);
        
        while (matcher.find()) {
            ResumeAnalysisResponse.Recommendation recommendation = 
                new ResumeAnalysisResponse.Recommendation();
            recommendation.setSuggestion(matcher.group(1));
            recommendation.setPriority("MEDIUM"); // Default values
            recommendation.setCategory("CONTENT");
            recommendation.setImpact("Moderate improvement expected");
            recommendations.add(recommendation);
        }
        
        return recommendations;
    }

    private SkillRecommendationResponse createFallbackSkillRecommendations(SkillRecommendationRequest request) {
        SkillRecommendationResponse response = new SkillRecommendationResponse();
        
        List<SkillRecommendationResponse.SkillRecommendation> recommendations = new ArrayList<>();
        
        // Add some default recommendations based on role
        if (request.getCurrentRole().toLowerCase().contains("developer")) {
            addFallbackSkill(recommendations, "React", "WEB_DEVELOPMENT", "HIGH", 
                "Essential for modern web development");
            addFallbackSkill(recommendations, "Docker", "DEVOPS", "MEDIUM", 
                "Important for containerization and deployment");
        }
        
        response.setRecommendations(recommendations);
        response.setLearningPath("AI service temporarily unavailable. These are general recommendations.");
        response.setCareerImpact("Learning these skills will enhance your career prospects.");
        
        return response;
    }

    private void addFallbackSkill(List<SkillRecommendationResponse.SkillRecommendation> recommendations,
                                 String skillName, String category, String priority, String reason) {
        SkillRecommendationResponse.SkillRecommendation skill = 
            new SkillRecommendationResponse.SkillRecommendation();
        skill.setSkillName(skillName);
        skill.setCategory(category);
        skill.setPriority(priority);
        skill.setReason(reason);
        skill.setTimeToLearn("3-6 months");
        skill.setDifficulty("INTERMEDIATE");
        recommendations.add(skill);
    }

    private ResumeAnalysisResponse createFallbackResumeAnalysis(ResumeAnalysisRequest request) {
        ResumeAnalysisResponse response = new ResumeAnalysisResponse();
        response.setAtsScore(75); // Default score
        response.setOverallFeedback("AI analysis temporarily unavailable. Please try again later.");
        response.setStrengths(Arrays.asList("Resume structure appears professional"));
        response.setWeaknesses(Arrays.asList("Unable to perform detailed analysis"));
        response.setRecommendations(new ArrayList<>());
        
        return response;
    }

    /**
     * Generate job market insights using AI
     */
    public String generateJobMarketInsights(com.careeros.entity.User user, java.util.List<com.careeros.entity.JobPosting> jobs, java.util.List<com.careeros.entity.UserSkill> userSkills) {
        logger.info("Generating job market insights for user {}", user.getId());
        
        // In production, this would make API calls to OpenAI/Claude
        return "Based on your skills and current market trends, here are key insights about job opportunities...";
    }
}
