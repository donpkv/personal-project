package com.careeros.service;

import com.careeros.dto.analytics.*;
import com.careeros.entity.*;
import com.careeros.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced Analytics Service for comprehensive user and platform analytics
 */
@Service
public class AdvancedAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AdvancedAnalyticsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private LearningProgressRepository learningProgressRepository;

    @Autowired
    private UserLearningPathRepository userLearningPathRepository;

    @Autowired
    private UserPathStepProgressRepository stepProgressRepository;

    @Autowired
    private AssessmentResponseRepository assessmentResponseRepository;

    @Autowired
    private MentorshipSessionRepository mentorshipSessionRepository;

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private DigitalCertificateRepository certificateRepository;

    /**
     * Get comprehensive user analytics dashboard
     */
    public UserAnalyticsDashboard getUserAnalyticsDashboard(UUID userId) {
        logger.info("Generating user analytics dashboard for user {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAnalyticsDashboard dashboard = new UserAnalyticsDashboard();
        dashboard.setUserId(userId);
        dashboard.setGeneratedAt(LocalDateTime.now());

        // Learning Progress Analytics
        dashboard.setLearningAnalytics(generateLearningAnalytics(userId));

        // Skill Development Analytics
        dashboard.setSkillAnalytics(generateSkillAnalytics(userId));

        // Performance Analytics
        dashboard.setPerformanceAnalytics(generatePerformanceAnalytics(userId));

        // Goal Achievement Analytics
        dashboard.setGoalAnalytics(generateGoalAnalytics(userId));

        // Social Learning Analytics
        dashboard.setSocialAnalytics(generateSocialAnalytics(userId));

        // Career Readiness Analytics
        dashboard.setCareerAnalytics(generateCareerAnalytics(userId));

        // Time Management Analytics
        dashboard.setTimeAnalytics(generateTimeAnalytics(userId));

        return dashboard;
    }

    /**
     * Get platform-wide analytics for admins
     */
    public PlatformAnalyticsDashboard getPlatformAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Generating platform analytics from {} to {}", startDate, endDate);

        PlatformAnalyticsDashboard dashboard = new PlatformAnalyticsDashboard();
        dashboard.setStartDate(startDate);
        dashboard.setEndDate(endDate);
        dashboard.setGeneratedAt(LocalDateTime.now());

        // User Engagement Analytics
        dashboard.setUserEngagement(generateUserEngagementAnalytics(startDate, endDate));

        // Learning Content Analytics
        dashboard.setContentAnalytics(generateContentAnalytics(startDate, endDate));

        // Skill Trends Analytics
        dashboard.setSkillTrends(generateSkillTrendsAnalytics(startDate, endDate));

        // Mentorship Analytics
        dashboard.setMentorshipAnalytics(generateMentorshipAnalytics(startDate, endDate));

        // Job Market Analytics
        dashboard.setJobMarketAnalytics(generateJobMarketAnalytics(startDate, endDate));

        // Revenue Analytics (if applicable)
        dashboard.setRevenueAnalytics(generateRevenueAnalytics(startDate, endDate));

        return dashboard;
    }

    /**
     * Generate learning path effectiveness report
     */
    public LearningPathEffectivenessReport analyzeLearningPathEffectiveness(UUID pathId) {
        logger.info("Analyzing learning path effectiveness for path {}", pathId);

        LearningPathEffectivenessReport report = new LearningPathEffectivenessReport();
        report.setPathId(pathId);
        report.setAnalysisDate(LocalDateTime.now());

        List<UserLearningPath> enrollments = userLearningPathRepository.findByLearningPathId(pathId);
        
        // Completion Analytics
        long totalEnrollments = enrollments.size();
        long completedEnrollments = enrollments.stream()
                .filter(path -> path.getStatus() == UserLearningPath.EnrollmentStatus.COMPLETED)
                .count();
        
        report.setTotalEnrollments((int) totalEnrollments);
        report.setCompletionRate(totalEnrollments > 0 ? (double) completedEnrollments / totalEnrollments * 100 : 0);

        // Time Analytics
        List<UserLearningPath> completedPaths = enrollments.stream()
                .filter(path -> path.getCompletedAt() != null)
                .collect(Collectors.toList());

        if (!completedPaths.isEmpty()) {
            double averageCompletionDays = completedPaths.stream()
                    .mapToLong(path -> ChronoUnit.DAYS.between(path.getEnrolledAt(), path.getCompletedAt()))
                    .average()
                    .orElse(0.0);
            report.setAverageCompletionDays(averageCompletionDays);
        }

        // Dropout Analysis
        Map<String, Integer> dropoutReasons = analyzeDropoutReasons(enrollments);
        report.setDropoutReasons(dropoutReasons);

        // Step-wise Analysis
        List<PathStepAnalytics> stepAnalytics = analyzePathSteps(pathId);
        report.setStepAnalytics(stepAnalytics);

        // User Feedback Analysis
        Map<String, Object> feedbackAnalysis = analyzeLearningPathFeedback(pathId);
        report.setFeedbackAnalysis(feedbackAnalysis);

        return report;
    }

    /**
     * Generate skill gap analysis for organization/cohort
     */
    public SkillGapAnalysisReport generateSkillGapAnalysis(List<UUID> userIds, List<String> targetSkills) {
        logger.info("Generating skill gap analysis for {} users and {} skills", userIds.size(), targetSkills.size());

        SkillGapAnalysisReport report = new SkillGapAnalysisReport();
        report.setAnalysisDate(LocalDateTime.now());
        report.setUserCount(userIds.size());
        report.setTargetSkills(targetSkills);

        Map<String, SkillGapMetrics> skillGaps = new HashMap<>();

        for (String skillName : targetSkills) {
            SkillGapMetrics metrics = new SkillGapMetrics();
            metrics.setSkillName(skillName);

            // Count users with this skill at different levels
            Map<UserSkill.ProficiencyLevel, Long> levelDistribution = userIds.stream()
                    .map(userId -> userSkillRepository.findByUserIdAndSkillName(userId, skillName))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            UserSkill::getProficiencyLevel,
                            Collectors.counting()));

            metrics.setLevelDistribution(levelDistribution);

            // Calculate gap metrics
            long usersWithSkill = levelDistribution.values().stream().mapToLong(Long::longValue).sum();
            long usersWithoutSkill = userIds.size() - usersWithSkill;
            
            metrics.setUsersWithSkill((int) usersWithSkill);
            metrics.setUsersWithoutSkill((int) usersWithoutSkill);
            metrics.setSkillCoverage((double) usersWithSkill / userIds.size() * 100);

            // Calculate average proficiency
            double averageProficiency = levelDistribution.entrySet().stream()
                    .mapToDouble(entry -> getProficiencyValue(entry.getKey()) * entry.getValue())
                    .sum() / Math.max(usersWithSkill, 1);
            metrics.setAverageProficiency(averageProficiency);

            skillGaps.put(skillName, metrics);
        }

        report.setSkillGaps(new ArrayList<>(skillGaps.values()));

        // Generate recommendations
        report.setRecommendations(generateSkillGapRecommendations(skillGaps));

        return report;
    }

    /**
     * Generate predictive analytics for user success
     */
    public UserSuccessPrediction predictUserSuccess(UUID userId) {
        logger.info("Generating success prediction for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSuccessPrediction prediction = new UserSuccessPrediction();
        prediction.setUserId(userId);
        prediction.setPredictionDate(LocalDateTime.now());

        // Gather user data for prediction
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        userLearningPathRepository.findByUserId(userId);
        stepProgressRepository.findByUserId(userId);

        // Calculate engagement score
        double engagementScore = calculateEngagementScore(userId);
        prediction.setEngagementScore(engagementScore);

        // Calculate learning velocity
        double learningVelocity = calculateLearningVelocity(userId);
        prediction.setLearningVelocity(learningVelocity);

        // Calculate consistency score
        double consistencyScore = calculateConsistencyScore(userId);
        prediction.setConsistencyScore(consistencyScore);

        // Calculate overall success probability
        double successProbability = calculateSuccessProbability(engagementScore, learningVelocity, consistencyScore, userSkills);
        prediction.setSuccessProbability(successProbability);

        // Generate risk factors and recommendations
        prediction.setRiskFactors(identifyRiskFactors(userSkills));
        prediction.setRecommendations(generateSuccessRecommendations());

        return prediction;
    }

    private LearningAnalytics generateLearningAnalytics(UUID userId) {
        LearningAnalytics analytics = new LearningAnalytics();
        
        List<UserLearningPath> paths = userLearningPathRepository.findByUserId(userId);
        List<UserPathStepProgress> stepProgress = stepProgressRepository.findByUserId(userId);

        // Basic metrics
        analytics.setTotalLearningPaths(paths.size());
        analytics.setCompletedPaths((int) paths.stream().filter(p -> p.getStatus() == UserLearningPath.EnrollmentStatus.COMPLETED).count());
        analytics.setInProgressPaths((int) paths.stream().filter(p -> p.getStatus() == UserLearningPath.EnrollmentStatus.IN_PROGRESS).count());

        // Time metrics
        int totalHours = paths.stream().mapToInt(UserLearningPath::getTimeSpentHours).sum();
        analytics.setTotalLearningHours(totalHours);

        // Progress metrics
        double averageProgress = paths.stream().mapToDouble(UserLearningPath::getProgressPercentage).average().orElse(0.0);
        analytics.setAverageProgress(averageProgress);

        // Learning streak
        analytics.setCurrentStreak(calculateLearningStreak(userId));
        analytics.setLongestStreak(calculateLongestStreak(userId));

        return analytics;
    }

    private SkillAnalytics generateSkillAnalytics(UUID userId) {
        SkillAnalytics analytics = new SkillAnalytics();
        
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        
        analytics.setTotalSkills(userSkills.size());
        
        // Skill level distribution
        Map<UserSkill.ProficiencyLevel, Long> levelDistribution = userSkills.stream()
                .collect(Collectors.groupingBy(UserSkill::getProficiencyLevel, Collectors.counting()));
        analytics.setSkillLevelDistribution(levelDistribution);

        // Skill categories
        Map<String, Long> categoryDistribution = userSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill.getSkill().getCategory(), Collectors.counting()));
        analytics.setSkillCategoryDistribution(categoryDistribution);

        // Recent skill improvements
        List<UserSkill> recentlyImproved = userSkills.stream()
                .filter(skill -> skill.getUpdatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .collect(Collectors.toList());
        analytics.setRecentlyImprovedSkills(recentlyImproved.size());

        return analytics;
    }

    private PerformanceAnalytics generatePerformanceAnalytics(UUID userId) {
        PerformanceAnalytics analytics = new PerformanceAnalytics();
        
        List<AssessmentResponse> assessments = assessmentResponseRepository.findByUserIdOrderBySubmittedAtDesc(userId);
        
        if (!assessments.isEmpty()) {
            double averageScore = assessments.stream().mapToDouble(response -> response.getScore() != null ? response.getScore() : 0.0).average().orElse(0.0);
            analytics.setAverageAssessmentScore(averageScore);
            
            // Performance trend (last 10 assessments)
            List<Double> recentScores = assessments.stream()
                    .limit(10)
                    .map(response -> response.getScore() != null ? response.getScore() : 0.0)
                    .collect(Collectors.toList());
            Collections.reverse(recentScores); // Chronological order
            analytics.setPerformanceTrend(calculateTrendString(recentScores));
        }

        return analytics;
    }

    private GoalAnalytics generateGoalAnalytics(UUID userId) {
        GoalAnalytics analytics = new GoalAnalytics();
        
        // This would integrate with a goals/objectives system
        // For now, use learning path completion as goals
        List<UserLearningPath> paths = userLearningPathRepository.findByUserId(userId);
        
        int totalGoals = paths.size();
        int achievedGoals = (int) paths.stream().filter(p -> p.getStatus() == UserLearningPath.EnrollmentStatus.COMPLETED).count();
        
        analytics.setTotalGoals(totalGoals);
        analytics.setAchievedGoals(achievedGoals);
        analytics.setGoalAchievementRate(totalGoals > 0 ? (double) achievedGoals / totalGoals * 100 : 0);

        return analytics;
    }

    private SocialAnalytics generateSocialAnalytics(UUID userId) {
        SocialAnalytics analytics = new SocialAnalytics();
        
        List<GroupMembership> memberships = groupMembershipRepository.findByUserId(userId);
        List<MentorshipSession> mentoringSessions = mentorshipSessionRepository.findByMentorIdOrMenteeId(userId, userId);
        
        analytics.setStudyGroupsJoined(memberships.size());
        analytics.setMentoringSessions(mentoringSessions.size());
        
        // Peer interaction score would be calculated based on posts, comments, etc.
        analytics.setPeerInteractionScore(calculatePeerInteractionScore(userId));

        return analytics;
    }

    private CareerAnalytics generateCareerAnalytics(UUID userId) {
        CareerAnalytics analytics = new CareerAnalytics();
        
        List<DigitalCertificate> certificates = certificateRepository.findByRecipientIdAndStatus(
                userId, DigitalCertificate.CertificateStatus.ACTIVE);
        
        analytics.setCertificatesEarned(certificates.size());
        
        // Job readiness score based on skills, certifications, and market demand
        analytics.setJobReadinessScore(calculateJobReadinessScore(userId));

        return analytics;
    }

    private TimeAnalytics generateTimeAnalytics(UUID userId) {
        TimeAnalytics analytics = new TimeAnalytics();
        
        List<UserPathStepProgress> stepProgress = stepProgressRepository.findByUserId(userId);
        
        // Daily/weekly learning patterns
        Map<Integer, Integer> dailyPattern = stepProgress.stream()
                .filter(p -> p.getStartedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getStartedAt().getDayOfWeek().getValue(),
                        Collectors.summingInt(UserPathStepProgress::getTimeSpentMinutes)));
        
        analytics.setDailyLearningPattern(dailyPattern);
        
        // Peak learning hours
        Map<Integer, Integer> hourlyPattern = stepProgress.stream()
                .filter(p -> p.getStartedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getStartedAt().getHour(),
                        Collectors.summingInt(UserPathStepProgress::getTimeSpentMinutes)));
        
        analytics.setHourlyLearningPattern(hourlyPattern);

        return analytics;
    }

    // Helper methods for calculations
    private double calculateEngagementScore(UUID userId, List<UserPathStepProgress> stepProgress) {
        // Calculate based on frequency of learning activities, session duration, etc.
        long recentActivities = stepProgress.stream()
                .filter(p -> p.getUpdatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .count();
        
        return Math.min(recentActivities / 30.0, 1.0) * 100; // Max 100%
    }

    private double calculateLearningVelocity(List<UserPathStepProgress> stepProgress) {
        // Calculate steps completed per week
        long completedSteps = stepProgress.stream().filter(UserPathStepProgress::isCompleted).count();
        if (completedSteps == 0) return 0.0;
        
        Optional<UserPathStepProgress> earliest = stepProgress.stream()
                .filter(p -> p.getStartedAt() != null)
                .min((a, b) -> a.getStartedAt().compareTo(b.getStartedAt()));
        
        if (earliest.isEmpty()) return 0.0;
        
        long weeksSinceStart = ChronoUnit.WEEKS.between(earliest.get().getStartedAt(), LocalDateTime.now());
        return weeksSinceStart > 0 ? (double) completedSteps / weeksSinceStart : completedSteps;
    }

    private double calculateConsistencyScore(List<UserPathStepProgress> stepProgress) {
        // Measure how consistently user engages in learning
        Map<LocalDateTime, Long> dailyActivities = stepProgress.stream()
                .filter(p -> p.getUpdatedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getUpdatedAt().toLocalDate().atStartOfDay(),
                        Collectors.counting()));
        
        if (dailyActivities.size() < 7) return dailyActivities.size() * 10.0; // Early stage bonus
        
        // Calculate coefficient of variation (lower is more consistent)
        double mean = dailyActivities.values().stream().mapToDouble(Long::doubleValue).average().orElse(0);
        double variance = dailyActivities.values().stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        
        double cv = mean > 0 ? Math.sqrt(variance) / mean : 1.0;
        return Math.max(0, 100 - cv * 50); // Convert to 0-100 scale
    }

    private double calculateSuccessProbability(double engagement, double velocity, double consistency, List<UserSkill> skills) {
        // Weighted combination of factors
        double skillFactor = Math.min(skills.size() / 10.0, 1.0) * 100; // More skills = higher probability
        
        return (engagement * 0.3 + velocity * 10 * 0.3 + consistency * 0.2 + skillFactor * 0.2);
    }

    private int calculateLearningStreak(UUID userId) {
        // Calculate current consecutive days of learning activity
        List<UserPathStepProgress> recentProgress = stepProgressRepository.findRecentProgressByUserId(userId, LocalDateTime.now().minusDays(30));
        
        Set<LocalDateTime> activeDays = recentProgress.stream()
                .map(p -> p.getUpdatedAt().toLocalDate().atStartOfDay())
                .collect(Collectors.toSet());
        
        int streak = 0;
        LocalDateTime currentDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        
        while (activeDays.contains(currentDay)) {
            streak++;
            currentDay = currentDay.minusDays(1);
        }
        
        return streak;
    }

    private int calculateLongestStreak(UUID userId) {
        // Would require more complex calculation over user's entire history
        return calculateLearningStreak(userId); // Simplified for now
    }

    private double calculateTrend(List<Double> scores) {
        if (scores.size() < 2) return 0.0;
        
        // Simple linear regression slope
        int n = scores.size();
        double sumX = n * (n - 1) / 2.0;
        double sumY = scores.stream().mapToDouble(Double::doubleValue).sum();
        double sumXY = 0;
        double sumXX = 0;
        
        for (int i = 0; i < n; i++) {
            sumXY += i * scores.get(i);
            sumXX += i * i;
        }
        
        return (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
    }

    private double calculatePeerInteractionScore(UUID userId) {
        // Would calculate based on posts, comments, group participation
        return 75.0; // Placeholder
    }

    private double calculateJobReadinessScore(UUID userId) {
        // Complex calculation based on skills, certifications, market demand
        return 80.0; // Placeholder
    }

    private double getProficiencyValue(UserSkill.ProficiencyLevel level) {
        return switch (level) {
            case BEGINNER -> 1.0;
            case INTERMEDIATE -> 2.0;
            case ADVANCED -> 3.0;
            case EXPERT -> 4.0;
        };
    }

    // Additional helper methods would be implemented for other analytics functions
    private UserEngagementAnalytics generateUserEngagementAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for user engagement analytics
        return new UserEngagementAnalytics();
    }

    private ContentAnalytics generateContentAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for content analytics
        return new ContentAnalytics();
    }

    private SkillTrendsAnalytics generateSkillTrendsAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for skill trends analytics
        return new SkillTrendsAnalytics();
    }

    private MentorshipAnalytics generateMentorshipAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for mentorship analytics
        return new MentorshipAnalytics();
    }

    private JobMarketAnalytics generateJobMarketAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for job market analytics
        return new JobMarketAnalytics();
    }

    private RevenueAnalytics generateRevenueAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for revenue analytics
        return new RevenueAnalytics();
    }

    private Map<String, Integer> analyzeDropoutReasons(List<UserLearningPath> enrollments) {
        // Analyze dropout patterns and reasons
        return new HashMap<>();
    }

    private List<PathStepAnalytics> analyzePathSteps(UUID pathId) {
        // Analyze individual step performance
        return new ArrayList<>();
    }

    private Map<String, Object> analyzeLearningPathFeedback(UUID pathId) {
        // Analyze user feedback for the learning path
        return new HashMap<>();
    }

    private List<String> generateSkillGapRecommendations(Map<String, SkillGapMetrics> skillGaps) {
        // Generate recommendations based on skill gaps
        return new ArrayList<>();
    }

    private List<String> identifyRiskFactors(List<UserSkill> userSkills) {
        // Identify factors that might affect user success
        return new ArrayList<>();
    }

    private List<String> generateSuccessRecommendations() {
        // Generate recommendations to improve success probability
        return new ArrayList<>();
    }

    private String calculateTrendString(List<Double> scores) {
        double trend = calculateTrend(scores);
        if (trend > 0.1) return "Improving";
        else if (trend < -0.1) return "Declining";
        else return "Stable";
    }

    private double calculateEngagementScore(UUID userId) {
        // Calculate user engagement score based on activity
        return 75.0; // Placeholder
    }

    private double calculateLearningVelocity(UUID userId) {
        // Calculate learning velocity based on progress
        return 8.5; // Placeholder
    }

    private double calculateConsistencyScore(UUID userId) {
        // Calculate consistency score based on regular activity
        return 85.0; // Placeholder
    }
}
