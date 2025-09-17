package com.careeros.service;

import com.careeros.dto.job.JobRecommendationRequest;
import com.careeros.dto.job.JobRecommendationResponse;
import com.careeros.dto.job.JobMarketInsights;
import com.careeros.dto.job.SkillDemandAnalysis;
import com.careeros.entity.*;
import com.careeros.repository.*;
import com.careeros.service.ai.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Job Market Integration Service for job recommendations and market insights
 */
@Service
public class JobMarketIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(JobMarketIntegrationService.class);

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.job-apis.indeed.api-key:}")
    private String indeedApiKey;

    @Value("${app.job-apis.linkedin.api-key:}")
    private String linkedInApiKey;

    @Value("${app.job-apis.glassdoor.api-key:}")
    private String glassdoorApiKey;

    @Value("${app.job-apis.github.api-key:}")
    private String githubApiKey;

    /**
     * Get personalized job recommendations for a user
     */
    public JobRecommendationResponse getJobRecommendations(UUID userId, JobRecommendationRequest request) {
        logger.info("Getting job recommendations for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);

        // Get jobs from multiple sources
        List<JobPosting> recommendedJobs = new ArrayList<>();
        
        // Get jobs from database (cached/scraped jobs)
        recommendedJobs.addAll(getJobsFromDatabase(userSkills, request));
        
        // Get fresh jobs from external APIs
        recommendedJobs.addAll(getJobsFromExternalAPIs(userSkills, request));

        // Score and rank jobs
        List<JobPosting> rankedJobs = rankJobsByCompatibility(recommendedJobs, userSkills, user);

        // Generate AI insights
        String aiInsights = generateAIJobInsights(user, rankedJobs, userSkills);

        // Build response
        JobRecommendationResponse response = new JobRecommendationResponse();
        response.setRecommendedJobs(rankedJobs.stream().limit(20).collect(Collectors.toList()));
        response.setTotalJobs(rankedJobs.size());
        response.setAiInsights(aiInsights);
        response.setSkillGaps(identifySkillGaps(rankedJobs, userSkills));
        response.setMarketInsights(getMarketInsights(request.getLocation(), request.getJobTitle()));

        return response;
    }

    /**
     * Analyze skill demand in the job market
     */
    public SkillDemandAnalysis analyzeSkillDemand(String location, String industry, List<String> skills) {
        logger.info("Analyzing skill demand for location: {}, industry: {}", location, industry);

        SkillDemandAnalysis analysis = new SkillDemandAnalysis();
        analysis.setLocation(location);
        analysis.setIndustry(industry);
        analysis.setAnalysisDate(LocalDateTime.now());

        Map<String, Integer> skillDemandCounts = new HashMap<>();
        Map<String, Double> averageSalaries = new HashMap<>();
        Map<String, Double> skillGrowthRates = new HashMap<>();

        // Analyze each skill
        for (String skill : skills) {
            // Count job postings requiring this skill
            int demandCount = jobPostingRepository.countJobsRequiringSkill(skill, location, industry);
            skillDemandCounts.put(skill, demandCount);

            // Calculate average salary for this skill
            Double avgSalary = jobPostingRepository.getAverageSalaryForSkill(skill, location, industry);
            averageSalaries.put(skill, avgSalary != null ? avgSalary : 0.0);

            // Calculate growth rate (compare with 6 months ago)
            int pastDemand = jobPostingRepository.countJobsRequiringSkillInPeriod(
                    skill, location, industry, LocalDateTime.now().minusMonths(6), LocalDateTime.now().minusMonths(3));
            int currentDemand = jobPostingRepository.countJobsRequiringSkillInPeriod(
                    skill, location, industry, LocalDateTime.now().minusMonths(3), LocalDateTime.now());
            
            double growthRate = pastDemand > 0 ? ((double) (currentDemand - pastDemand) / pastDemand) * 100 : 0;
            skillGrowthRates.put(skill, growthRate);
        }

        analysis.setSkillDemandCounts(skillDemandCounts);
        analysis.setAverageSalaries(averageSalaries);
        analysis.setSkillGrowthRates(skillGrowthRates);

        // Identify trending skills
        List<String> trendingSkills = skillGrowthRates.entrySet().stream()
                .filter(entry -> entry.getValue() > 10.0) // Growth > 10%
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(10)
                .collect(Collectors.toList());
        analysis.setTrendingSkills(trendingSkills);

        // Identify high-demand skills
        List<String> highDemandSkills = skillDemandCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(10)
                .collect(Collectors.toList());
        analysis.setHighDemandSkills(highDemandSkills);

        return analysis;
    }

    /**
     * Get job market insights for a specific location and role
     */
    public JobMarketInsights getMarketInsights(String location, String jobTitle) {
        logger.info("Getting market insights for {} in {}", jobTitle, location);

        JobMarketInsights insights = new JobMarketInsights();
        insights.setLocation(location);
        insights.setJobTitle(jobTitle);
        insights.setAnalysisDate(LocalDateTime.now());

        // Job availability metrics
        int totalJobs = jobPostingRepository.countJobsByLocationAndTitle(location, jobTitle);
        int recentJobs = jobPostingRepository.countRecentJobsByLocationAndTitle(
                location, jobTitle, LocalDateTime.now().minusDays(30));
        
        insights.setTotalJobPostings(totalJobs);
        insights.setRecentJobPostings(recentJobs);

        // Salary insights
        Double averageSalary = jobPostingRepository.getAverageSalaryByLocationAndTitle(location, jobTitle);
        Double medianSalary = jobPostingRepository.getMedianSalaryByLocationAndTitle(location, jobTitle);
        insights.setAverageSalary(averageSalary != null ? averageSalary : 0.0);
        insights.setMedianSalary(medianSalary != null ? medianSalary : 0.0);

        // Top companies hiring
        List<String> topCompanies = jobPostingRepository.getTopHiringCompanies(location, jobTitle, 10);
        insights.setTopHiringCompanies(topCompanies);

        // Required skills analysis
        Map<String, Integer> skillFrequency = jobPostingRepository.getSkillFrequencyForRole(jobTitle, location);
        List<String> topRequiredSkills = skillFrequency.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(15)
                .collect(Collectors.toList());
        insights.setTopRequiredSkills(topRequiredSkills);

        // Experience level distribution
        List<Object[]> experienceData = jobPostingRepository.getExperienceLevelDistribution(jobTitle, location);
        Map<String, Integer> experienceLevels = experienceData.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> ((Number) row[1]).intValue()
                ));
        insights.setExperienceLevelDistribution(experienceLevels);

        // Job type distribution (full-time, contract, etc.)
        List<Object[]> jobTypeData = jobPostingRepository.getJobTypeDistribution(jobTitle, location);
        Map<String, Integer> jobTypes = jobTypeData.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0],
                    row -> ((Number) row[1]).intValue()
                ));
        insights.setJobTypeDistribution(jobTypes);

        // Remote work availability
        int remoteJobs = jobPostingRepository.countRemoteJobsByTitle(jobTitle);
        double remotePercentage = totalJobs > 0 ? (double) remoteJobs / totalJobs * 100 : 0;
        insights.setRemoteWorkPercentage(remotePercentage);

        return insights;
    }

    /**
     * Sync jobs from external APIs
     */
    public void syncJobsFromExternalAPIs() {
        logger.info("Syncing jobs from external APIs");

        try {
            // Sync from Indeed
            syncJobsFromIndeed();
            
            // Sync from LinkedIn (if API key available)
            if (!linkedInApiKey.isEmpty()) {
                syncJobsFromLinkedIn();
            }
            
            // Sync from GitHub Jobs
            syncJobsFromGitHub();
            
        } catch (Exception e) {
            logger.error("Error syncing jobs from external APIs", e);
        }
    }

    /**
     * Track job application for analytics
     */
    public void trackJobApplication(UUID userId, UUID jobId, String applicationStatus) {
        logger.info("Tracking job application for user {} and job {}", userId, jobId);

        // Create job application tracking record
        // This would be implemented with a JobApplication entity
        // For now, just log the event
        logger.info("User {} applied to job {} with status {}", userId, jobId, applicationStatus);
    }

    /**
     * Get career path suggestions based on current role and market trends
     */
    public List<String> getCareerPathSuggestions(UUID userId, String currentRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);

        // Analyze market trends and suggest career progressions
        List<String> suggestions = new ArrayList<>();

        // Get similar roles with higher salaries
        List<String> higherPayingRoles = jobPostingRepository.getHigherPayingRoles(currentRole);
        suggestions.addAll(higherPayingRoles.stream()
                .limit(3)
                .map(role -> "Consider transitioning to " + role + " for better compensation")
                .collect(Collectors.toList()));

        // Get trending roles that match user's skills
        List<Object[]> trendingRoleData = jobPostingRepository.getTrendingRolesForSkills(
                userSkills.stream().map(skill -> skill.getSkill().getName()).collect(Collectors.toList()),
                LocalDateTime.now().minusMonths(3));
        List<String> trendingRoles = trendingRoleData.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
        suggestions.addAll(trendingRoles.stream()
                .limit(3)
                .map(role -> "Explore opportunities in " + role + " - it's trending in your skill area")
                .collect(Collectors.toList()));

        // Suggest skill development based on job market
        List<String> inDemandSkills = getInDemandSkillsForRole(currentRole);
        suggestions.addAll(inDemandSkills.stream()
                .limit(2)
                .map(skill -> "Consider learning " + skill + " to increase job opportunities")
                .collect(Collectors.toList()));

        return suggestions;
    }

    private List<JobPosting> getJobsFromDatabase(List<UserSkill> userSkills, JobRecommendationRequest request) {
        List<String> skillNames = userSkills.stream()
                .map(skill -> skill.getSkill().getName())
                .collect(Collectors.toList());

        return jobPostingRepository.findJobsBySkillsAndLocation(
                skillNames, request.getLocation(), request.getRemoteOnly());
    }

    private List<JobPosting> getJobsFromExternalAPIs(List<UserSkill> userSkills, JobRecommendationRequest request) {
        List<JobPosting> jobs = new ArrayList<>();
        
        try {
            // Get jobs from Indeed API
            jobs.addAll(fetchJobsFromIndeed(request));
            
            // Get jobs from other APIs
            // Implementation would depend on specific API integrations
            
        } catch (Exception e) {
            logger.warn("Error fetching jobs from external APIs", e);
        }
        
        return jobs;
    }

    private List<JobPosting> fetchJobsFromIndeed(JobRecommendationRequest request) {
        List<JobPosting> jobs = new ArrayList<>();
        
        try {
            // Build Indeed API request
            String url = "https://api.indeed.com/ads/apisearch?" +
                    "publisher=" + indeedApiKey +
                    "&q=" + request.getJobTitle() +
                    "&l=" + request.getLocation() +
                    "&format=json&limit=25";

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                
                for (Map<String, Object> result : results) {
                    JobPosting job = mapIndeedResultToJobPosting(result);
                    jobs.add(job);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error fetching jobs from Indeed API", e);
        }
        
        return jobs;
    }

    private JobPosting mapIndeedResultToJobPosting(Map<String, Object> result) {
        JobPosting job = new JobPosting();
        job.setTitle((String) result.get("jobtitle"));
        job.setCompany((String) result.get("company"));
        job.setLocation((String) result.get("formattedLocation"));
        job.setDescription((String) result.get("snippet"));
        job.setUrl((String) result.get("url"));
        job.setSource("Indeed");
        job.setPostedDate(LocalDateTime.now()); // Indeed doesn't provide exact date
        job.setIsActive(true);
        
        return job;
    }

    private void syncJobsFromIndeed() {
        // Implementation for syncing jobs from Indeed
        logger.info("Syncing jobs from Indeed API");
    }

    private void syncJobsFromLinkedIn() {
        // Implementation for syncing jobs from LinkedIn
        logger.info("Syncing jobs from LinkedIn API");
    }

    private void syncJobsFromGitHub() {
        // Implementation for syncing jobs from GitHub Jobs
        logger.info("Syncing jobs from GitHub Jobs API");
    }

    private List<JobPosting> rankJobsByCompatibility(List<JobPosting> jobs, List<UserSkill> userSkills, User user) {
        Map<String, Double> jobScores = new HashMap<>();
        
        for (JobPosting job : jobs) {
            double score = calculateJobCompatibilityScore(job, userSkills, user);
            jobScores.put(job.getId().toString(), score);
        }
        
        return jobs.stream()
                .sorted((a, b) -> Double.compare(
                        jobScores.getOrDefault(b.getId().toString(), 0.0),
                        jobScores.getOrDefault(a.getId().toString(), 0.0)))
                .collect(Collectors.toList());
    }

    private double calculateJobCompatibilityScore(JobPosting job, List<UserSkill> userSkills, User user) {
        double score = 0.0;
        
        // Skill match score (60% weight)
        double skillScore = calculateSkillMatchScore(job, userSkills);
        score += skillScore * 0.6;
        
        // Location preference (15% weight)
        double locationScore = calculateLocationScore(job, user);
        score += locationScore * 0.15;
        
        // Salary expectation (15% weight)
        double salaryScore = calculateSalaryScore(job, user);
        score += salaryScore * 0.15;
        
        // Company reputation (10% weight)
        double companyScore = calculateCompanyScore(job);
        score += companyScore * 0.1;
        
        return score;
    }

    private double calculateSkillMatchScore(JobPosting job, List<UserSkill> userSkills) {
        if (job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty()) {
            return 0.5; // Default score if no skills specified
        }
        
        List<String> requiredSkills = job.getRequiredSkills();
        List<String> userSkillNames = userSkills.stream()
                .map(skill -> skill.getSkill().getName().toLowerCase())
                .collect(Collectors.toList());
        
        long matchingSkills = requiredSkills.stream()
                .filter(skill -> userSkillNames.contains(skill.toLowerCase().trim()))
                .count();
        
        return (double) matchingSkills / requiredSkills.size();
    }

    private double calculateLocationScore(JobPosting job, User user) {
        // Simplified location scoring - in real implementation would use geolocation
        if (job.getLocation() != null && user.getLocation() != null) {
            return job.getLocation().toLowerCase().contains(user.getLocation().toLowerCase()) ? 1.0 : 0.5;
        }
        return 0.5;
    }

    private double calculateSalaryScore(JobPosting job, User user) {
        // Simplified salary scoring - would use user's salary expectations
        return job.getSalaryMin() != null && job.getSalaryMin() > 0 ? 0.8 : 0.5;
    }

    private double calculateCompanyScore(JobPosting job) {
        // Simplified company scoring - would integrate with company rating APIs
        return 0.7; // Default score
    }

    private String generateAIJobInsights(User user, List<JobPosting> jobs, List<UserSkill> userSkills) {
        try {
            return openAIService.generateJobMarketInsights(user, jobs, userSkills);
        } catch (Exception e) {
            logger.warn("Failed to generate AI job insights", e);
            return "Based on your skills and preferences, we found " + jobs.size() + 
                   " relevant job opportunities. Focus on roles that match your strongest skills.";
        }
    }

    private List<String> identifySkillGaps(List<JobPosting> jobs, List<UserSkill> userSkills) {
        Map<String, Integer> requiredSkillFrequency = new HashMap<>();
        Set<String> userSkillNames = userSkills.stream()
                .map(skill -> skill.getSkill().getName().toLowerCase())
                .collect(Collectors.toSet());
        
        // Count frequency of required skills in job postings
        for (JobPosting job : jobs) {
            if (job.getRequiredSkills() != null) {
                job.getRequiredSkills().stream()
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .forEach(skill -> requiredSkillFrequency.merge(skill, 1, Integer::sum));
            }
        }
        
        // Find skills that are frequently required but user doesn't have
        return requiredSkillFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() >= 3) // Required in at least 3 jobs
                .filter(entry -> !userSkillNames.contains(entry.getKey()))
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<String> getInDemandSkillsForRole(String role) {
        List<Object[]> skillData = jobPostingRepository.getTopSkillsForRole(role, 10);
        return skillData.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }
}
