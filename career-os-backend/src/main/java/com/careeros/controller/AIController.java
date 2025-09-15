package com.careeros.controller;

import com.careeros.dto.ai.*;
import com.careeros.security.UserPrincipal;
import com.careeros.service.ai.OpenAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * AI-powered features REST Controller
 */
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Services", description = "AI-powered skill recommendations and resume analysis")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = {"http://localhost:3000", "https://career-os.com"})
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    @Autowired
    private OpenAIService openAIService;

    /**
     * Get personalized skill recommendations
     */
    @PostMapping("/recommendations")
    @Operation(summary = "Get skill recommendations", description = "Get AI-powered skill recommendations based on user profile and career goals")
    @PreAuthorize("hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<SkillRecommendationResponse> getSkillRecommendations(
            @Valid @RequestBody SkillRecommendationRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Skill recommendation request for user: {}", userPrincipal.getId());
        
        // Set user ID from authenticated user
        request.setUserId(userPrincipal.getId());
        
        SkillRecommendationResponse response = openAIService.getSkillRecommendations(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Analyze resume for ATS compliance
     */
    @PostMapping("/resume-analysis")
    @Operation(summary = "Analyze resume", description = "Get AI-powered resume analysis with ATS optimization suggestions")
    @PreAuthorize("hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @Valid @RequestBody ResumeAnalysisRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Resume analysis request for user: {}", userPrincipal.getId());
        
        // Set user ID from authenticated user
        request.setUserId(userPrincipal.getId());
        
        ResumeAnalysisResponse response = openAIService.analyzeResume(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get career path suggestions
     */
    @PostMapping("/career-path")
    @Operation(summary = "Get career path", description = "Get AI-suggested career progression paths")
    @PreAuthorize("hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<SkillRecommendationResponse> getCareerPath(
            @Valid @RequestBody SkillRecommendationRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Career path request for user: {}", userPrincipal.getId());
        
        // Set user ID from authenticated user
        request.setUserId(userPrincipal.getId());
        
        // Modify request to focus on career progression
        request.setAdditionalContext("Focus on career progression and advancement opportunities");
        
        SkillRecommendationResponse response = openAIService.getSkillRecommendations(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get skill gap analysis
     */
    @PostMapping("/skill-gap-analysis")
    @Operation(summary = "Skill gap analysis", description = "Analyze skill gaps for target role")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<SkillRecommendationResponse> analyzeSkillGap(
            @Valid @RequestBody SkillRecommendationRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Skill gap analysis request for user: {}", userPrincipal.getId());
        
        // Set user ID from authenticated user
        request.setUserId(userPrincipal.getId());
        
        // Modify request to focus on skill gaps
        request.setAdditionalContext("Identify skill gaps between current abilities and target role requirements");
        
        SkillRecommendationResponse response = openAIService.getSkillRecommendations(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get industry trends and insights
     */
    @GetMapping("/industry-trends/{industry}")
    @Operation(summary = "Industry trends", description = "Get AI insights on industry trends and in-demand skills")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<SkillRecommendationResponse> getIndustryTrends(
            @PathVariable String industry,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Industry trends request for industry: {} by user: {}", industry, userPrincipal.getId());
        
        // Create a request for industry trends
        SkillRecommendationRequest request = new SkillRecommendationRequest();
        request.setUserId(userPrincipal.getId());
        request.setCurrentRole("Industry Analyst");
        request.setCareerGoal("Stay updated with " + industry + " trends");
        request.setIndustry(industry);
        request.setExperienceLevel("MID_LEVEL");
        request.setAdditionalContext("Provide current industry trends, emerging technologies, and in-demand skills for " + industry);
        
        SkillRecommendationResponse response = openAIService.getSkillRecommendations(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate resume content quality
     */
    @PostMapping("/resume-quality-check")
    @Operation(summary = "Resume quality check", description = "Quick quality assessment of resume content")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<ResumeAnalysisResponse> checkResumeQuality(
            @Valid @RequestBody ResumeAnalysisRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Resume quality check request for user: {}", userPrincipal.getId());
        
        // Set user ID from authenticated user
        request.setUserId(userPrincipal.getId());
        request.setAnalysisType("BASIC");
        
        ResumeAnalysisResponse response = openAIService.analyzeResume(request);
        return ResponseEntity.ok(response);
    }
}
