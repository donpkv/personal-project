package com.careeros.service;

import com.careeros.dto.learning.LearningPathRecommendationRequest;
import com.careeros.dto.learning.LearningPathRecommendationResponse;
import com.careeros.dto.learning.PersonalizedPathRequest;
import com.careeros.entity.*;
import com.careeros.repository.*;
import com.careeros.service.ai.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Learning Path Engine Service for dynamic path generation and management
 */
@Service
@Transactional
public class LearningPathEngineService {

    private static final Logger logger = LoggerFactory.getLogger(LearningPathEngineService.class);

    @Autowired
    private LearningPathRepository pathRepository;

    @Autowired
    private UserLearningPathRepository userPathRepository;

    @Autowired
    private PathStepRepository stepRepository;

    @Autowired
    private UserPathStepProgressRepository stepProgressRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private LearningResourceRepository resourceRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private com.careeros.repository.UserRepository userRepository;

    /**
     * Generate personalized learning path based on user profile and goals
     */
    public LearningPath generatePersonalizedPath(PersonalizedPathRequest request, UUID userId) {
        logger.info("Generating personalized learning path for user {}", userId);

        User user = getUserById(userId);
        
        // Analyze user's current skills and experience
        List<UserSkill> currentSkills = userSkillRepository.findByUserId(userId);
        Map<String, UserSkill.ProficiencyLevel> skillLevels = currentSkills.stream()
                .collect(Collectors.toMap(
                    skill -> skill.getSkill().getName(),
                    UserSkill::getProficiencyLevel
                ));

        // Create personalized learning path
        LearningPath path = new LearningPath();
        path.setTitle(generatePathTitle(request.getTargetRole(), request.getTargetSkills()));
        path.setDescription(generatePathDescription(request));
        path.setCategory(determinePathCategory(request.getTargetSkills()));
        path.setDifficultyLevel(calculateOptimalDifficulty(skillLevels, request.getTargetSkills()));
        path.setEstimatedDurationWeeks(calculateEstimatedDuration(request.getTargetSkills(), skillLevels));
        
        LearningPath savedPath = pathRepository.save(path);

        // Generate learning steps
        generateLearningSteps(savedPath, request, skillLevels);

        // Enroll user in the path
        enrollUserInPath(user, savedPath, request);

        logger.info("Personalized learning path created with ID {}", savedPath.getId());
        return savedPath;
    }

    /**
     * Get learning path recommendations based on user profile
     */
    public LearningPathRecommendationResponse getPathRecommendations(LearningPathRecommendationRequest request, UUID userId) {
        logger.info("Getting path recommendations for user {}", userId);

        User user = getUserById(userId);
        
        // Get user's current skills and progress
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        List<UserLearningPath> completedPaths = userPathRepository.findCompletedPathsByUserId(userId);

        // Find suitable existing paths
        List<LearningPath> recommendedPaths = findRecommendedPaths(request, userSkills, completedPaths);

        // Generate AI-powered recommendations
        List<LearningPath> aiRecommendations = generateAIPathRecommendations(user, request);

        LearningPathRecommendationResponse response = new LearningPathRecommendationResponse();
        response.setRecommendedPaths(recommendedPaths);
        response.setAiGeneratedPaths(aiRecommendations);
        response.setPersonalizedSuggestions(generatePersonalizedSuggestions(user, userSkills));

        return response;
    }

    /**
     * Update user progress in learning path
     */
    public UserPathStepProgress updateStepProgress(UUID userId, UUID stepId, double progressPercentage, 
                                                  int timeSpentMinutes, String notes) {
        logger.info("Updating step progress for user {} and step {}", userId, stepId);

        PathStep step = stepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Path step not found"));

        User user = getUserById(userId);

        // Find or create progress record
        UserPathStepProgress progress = stepProgressRepository
                .findByUserAndPathStep(user, step)
                .orElse(new UserPathStepProgress(user, step));

        progress.setProgressPercentage(progressPercentage);
        progress.addTimeSpent(timeSpentMinutes);
        progress.setNotes(notes);

        if (progressPercentage >= 100.0) {
            progress.setStatus(UserPathStepProgress.StepStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
        } else if (progressPercentage > 0.0 && progress.getStatus() == UserPathStepProgress.StepStatus.NOT_STARTED) {
            progress.setStatus(UserPathStepProgress.StepStatus.IN_PROGRESS);
        }

        UserPathStepProgress savedProgress = stepProgressRepository.save(progress);

        // Update overall learning path progress
        updateLearningPathProgress(userId, step.getLearningPath().getId());

        return savedProgress;
    }

    /**
     * Get next recommended step for user in a learning path
     */
    public PathStep getNextRecommendedStep(UUID userId, UUID pathId) {
        UserLearningPath userPath = userPathRepository.findByUserIdAndLearningPathId(userId, pathId)
                .orElseThrow(() -> new RuntimeException("User not enrolled in this learning path"));

        // Get all steps in the path
        List<PathStep> allSteps = stepRepository.findByLearningPathIdOrderByStepOrder(pathId);

        // Get user's progress on all steps
        List<UserPathStepProgress> userProgress = stepProgressRepository.findByUserIdAndPathId(userId, pathId);
        Set<UUID> completedStepIds = userProgress.stream()
                .filter(progress -> progress.getStatus() == UserPathStepProgress.StepStatus.COMPLETED)
                .map(progress -> progress.getPathStep().getId())
                .collect(Collectors.toSet());

        // Find next step considering prerequisites
        for (PathStep step : allSteps) {
            if (!completedStepIds.contains(step.getId())) {
                // Check if all prerequisites are completed
                boolean prerequisitesMet = step.getPrerequisiteSteps().stream()
                        .allMatch(prereq -> completedStepIds.contains(prereq.getId()));

                if (prerequisitesMet) {
                    return step;
                }
            }
        }

        return null; // All steps completed
    }

    /**
     * Generate adaptive learning path based on user performance
     */
    public LearningPath generateAdaptivePath(UUID userId, UUID currentPathId) {
        logger.info("Generating adaptive path for user {} based on current path {}", userId, currentPathId);

        User user = getUserById(userId);
        UserLearningPath currentUserPath = userPathRepository.findByUserIdAndLearningPathId(userId, currentPathId)
                .orElseThrow(() -> new RuntimeException("User not enrolled in current path"));

        // Analyze user's performance and learning patterns
        List<UserPathStepProgress> progressHistory = stepProgressRepository.findByUserIdAndPathId(userId, currentPathId);
        
        // Calculate performance metrics
        double averageCompletionTime = calculateAverageCompletionTime(progressHistory);
        double difficultyPreference = analyzeDifficultyPreference(progressHistory);
        Set<String> strugglingAreas = identifyStrugglingAreas(progressHistory);
        Set<String> strongAreas = identifyStrongAreas(progressHistory);

        // Generate adaptive path
        LearningPath adaptivePath = new LearningPath();
        adaptivePath.setTitle("Adaptive Path - " + currentUserPath.getLearningPath().getTitle());
        adaptivePath.setDescription("Personalized path based on your learning performance");
        adaptivePath.setCategory(currentUserPath.getLearningPath().getCategory());
        adaptivePath.setDifficultyLevel(adaptDifficultyLevel(difficultyPreference));
        
        LearningPath savedPath = pathRepository.save(adaptivePath);

        // Generate adaptive steps focusing on struggling areas
        generateAdaptiveSteps(savedPath, strugglingAreas, strongAreas, user);

        return savedPath;
    }

    /**
     * Get learning path analytics for user
     */
    public Map<String, Object> getPathAnalytics(UUID userId, UUID pathId) {
        UserLearningPath userPath = userPathRepository.findByUserIdAndLearningPathId(userId, pathId)
                .orElseThrow(() -> new RuntimeException("User not enrolled in this learning path"));

        List<UserPathStepProgress> stepProgress = stepProgressRepository.findByUserIdAndPathId(userId, pathId);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("overallProgress", userPath.getProgressPercentage());
        analytics.put("completedSteps", userPath.getCompletedSteps());
        analytics.put("totalSteps", userPath.getTotalSteps());
        analytics.put("timeSpent", userPath.getTimeSpentHours());
        analytics.put("averageStepTime", calculateAverageStepTime(stepProgress));
        analytics.put("strugglingAreas", identifyStrugglingAreas(stepProgress));
        analytics.put("strongAreas", identifyStrongAreas(stepProgress));
        analytics.put("learningVelocity", calculateLearningVelocity(stepProgress));
        analytics.put("estimatedCompletion", estimateCompletionDate(userPath, stepProgress));

        return analytics;
    }

    private void generateLearningSteps(LearningPath path, PersonalizedPathRequest request, 
                                     Map<String, UserSkill.ProficiencyLevel> currentSkills) {
        List<String> targetSkills = request.getTargetSkills();
        int stepOrder = 1;

        for (String skillName : targetSkills) {
            UserSkill.ProficiencyLevel currentLevel = currentSkills.getOrDefault(skillName, UserSkill.ProficiencyLevel.BEGINNER);
            
            // Generate steps for this skill based on current level
            List<PathStep> skillSteps = generateSkillSteps(path, skillName, currentLevel, stepOrder);
            stepOrder += skillSteps.size();
        }
    }

    private List<PathStep> generateSkillSteps(LearningPath path, String skillName, 
                                            UserSkill.ProficiencyLevel currentLevel, int startOrder) {
        List<PathStep> steps = new ArrayList<>();
        int stepOrder = startOrder;

        // Generate progressive steps based on current level
        switch (currentLevel) {
            case BEGINNER:
                steps.add(createStep(path, "Introduction to " + skillName, PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, "Basic " + skillName + " Concepts", PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, "Hands-on " + skillName + " Practice", PathStep.StepType.PRACTICE, stepOrder++));
                steps.add(createStep(path, skillName + " Fundamentals Assessment", PathStep.StepType.ASSESSMENT, stepOrder++));
                break;
            case INTERMEDIATE:
                steps.add(createStep(path, "Advanced " + skillName + " Techniques", PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, skillName + " Best Practices", PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, "Complex " + skillName + " Project", PathStep.StepType.PROJECT, stepOrder++));
                break;
            case ADVANCED:
                steps.add(createStep(path, "Expert " + skillName + " Patterns", PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, skillName + " Architecture & Design", PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, "Master " + skillName + " Capstone", PathStep.StepType.PROJECT, stepOrder++));
                break;
            case EXPERT:
                steps.add(createStep(path, "Advanced " + skillName + " Research", PathStep.StepType.LEARNING, stepOrder++));
                steps.add(createStep(path, skillName + " Innovation Project", PathStep.StepType.PROJECT, stepOrder++));
                break;
        }

        return stepRepository.saveAll(steps);
    }

    private PathStep createStep(LearningPath path, String title, PathStep.StepType type, int order) {
        PathStep step = new PathStep();
        step.setLearningPath(path);
        step.setTitle(title);
        step.setStepType(type);
        step.setStepOrder(order);
        step.setEstimatedDurationHours(getEstimatedHoursForStepType(type));
        step.setIsRequired(true);
        return step;
    }

    private int getEstimatedHoursForStepType(PathStep.StepType type) {
        return switch (type) {
            case LEARNING -> 4;
            case PRACTICE -> 6;
            case ASSESSMENT -> 2;
            case PROJECT -> 12;
            case READING -> 2;
            case VIDEO -> 3;
            case INTERACTIVE -> 5;
            case MILESTONE -> 1;
        };
    }

    private void enrollUserInPath(User user, LearningPath path, PersonalizedPathRequest request) {
        UserLearningPath userPath = new UserLearningPath(user, path);
        userPath.setTargetCompletionDate(request.getTargetCompletionDate());
        userPath.setDailyGoalHours(request.getDailyGoalHours());
        userPath.setWeeklyGoalHours(request.getWeeklyGoalHours());
        userPath.setReminderEnabled(true);
        userPath.setReminderTime("09:00"); // Default reminder time
        
        userPathRepository.save(userPath);
    }

    private void updateLearningPathProgress(UUID userId, UUID pathId) {
        UserLearningPath userPath = userPathRepository.findByUserIdAndLearningPathId(userId, pathId)
                .orElseThrow(() -> new RuntimeException("User path not found"));

        List<UserPathStepProgress> stepProgress = stepProgressRepository.findByUserIdAndPathId(userId, pathId);
        
        long completedSteps = stepProgress.stream()
                .filter(progress -> progress.getStatus() == UserPathStepProgress.StepStatus.COMPLETED)
                .count();

        userPath.setCompletedSteps((int) completedSteps);
        
        // Update overall progress percentage
        if (userPath.getTotalSteps() > 0) {
            double progressPercentage = (double) completedSteps / userPath.getTotalSteps() * 100;
            userPath.setProgressPercentage(progressPercentage);
            
            if (progressPercentage >= 100.0) {
                userPath.setStatus(UserLearningPath.EnrollmentStatus.COMPLETED);
            } else if (progressPercentage > 0.0 && userPath.getStatus() == UserLearningPath.EnrollmentStatus.ENROLLED) {
                userPath.setStatus(UserLearningPath.EnrollmentStatus.IN_PROGRESS);
            }
        }

        // Update time spent
        int totalTimeSpent = stepProgress.stream()
                .mapToInt(UserPathStepProgress::getTimeSpentMinutes)
                .sum();
        userPath.setTimeSpentHours(totalTimeSpent / 60);

        userPathRepository.save(userPath);
    }

    private List<LearningPath> findRecommendedPaths(LearningPathRecommendationRequest request, 
                                                   List<UserSkill> userSkills, 
                                                   List<UserLearningPath> completedPaths) {
        // Implementation would find suitable paths based on user profile
        // For now, return popular paths in the requested category
        return pathRepository.findByCategory(LearningPath.PathCategory.PROGRAMMING)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<LearningPath> generateAIPathRecommendations(User user, LearningPathRecommendationRequest request) {
        // Use AI service to generate custom path recommendations
        // This would integrate with OpenAI to create personalized paths
        return new ArrayList<>(); // Placeholder
    }

    private List<String> generatePersonalizedSuggestions(User user, List<UserSkill> userSkills) {
        List<String> suggestions = new ArrayList<>();
        
        // Analyze user's skill gaps and provide suggestions
        if (userSkills.stream().noneMatch(skill -> skill.getSkill().getName().toLowerCase().contains("javascript"))) {
            suggestions.add("Consider learning JavaScript for web development opportunities");
        }
        
        if (userSkills.stream().noneMatch(skill -> skill.getSkill().getName().toLowerCase().contains("python"))) {
            suggestions.add("Python is highly recommended for data science and automation");
        }
        
        suggestions.add("Focus on building a portfolio project to demonstrate your skills");
        suggestions.add("Consider obtaining industry certifications to validate your expertise");
        
        return suggestions;
    }

    private String generatePathTitle(String targetRole, List<String> targetSkills) {
        if (targetRole != null && !targetRole.isEmpty()) {
            return "Path to " + targetRole;
        } else if (!targetSkills.isEmpty()) {
            return "Master " + String.join(" & ", targetSkills.subList(0, Math.min(2, targetSkills.size())));
        }
        return "Personalized Learning Journey";
    }

    private String generatePathDescription(PersonalizedPathRequest request) {
        StringBuilder description = new StringBuilder();
        description.append("A personalized learning path designed to help you achieve your career goals. ");
        
        if (request.getTargetRole() != null) {
            description.append("This path will prepare you for a role as ").append(request.getTargetRole()).append(". ");
        }
        
        if (!request.getTargetSkills().isEmpty()) {
            description.append("You'll master key skills including ")
                    .append(String.join(", ", request.getTargetSkills())).append(". ");
        }
        
        description.append("The path is tailored to your current skill level and learning preferences.");
        
        return description.toString();
    }

    private LearningPath.PathCategory determinePathCategory(List<String> targetSkills) {
        // Analyze target skills to determine the most appropriate category
        for (String skill : targetSkills) {
            String skillLower = skill.toLowerCase();
            if (skillLower.contains("javascript") || skillLower.contains("html") || skillLower.contains("css")) {
                return LearningPath.PathCategory.WEB_DEVELOPMENT;
            } else if (skillLower.contains("python") || skillLower.contains("data") || skillLower.contains("analytics")) {
                return LearningPath.PathCategory.DATA_SCIENCE;
            } else if (skillLower.contains("java") || skillLower.contains("programming")) {
                return LearningPath.PathCategory.PROGRAMMING;
            }
        }
        return LearningPath.PathCategory.PROGRAMMING; // Default
    }

    private LearningPath.DifficultyLevel calculateOptimalDifficulty(Map<String, UserSkill.ProficiencyLevel> skillLevels, 
                                                                   List<String> targetSkills) {
        // Calculate average skill level for target skills
        double averageLevel = targetSkills.stream()
                .mapToDouble(skill -> {
                    UserSkill.ProficiencyLevel level = skillLevels.getOrDefault(skill, UserSkill.ProficiencyLevel.BEGINNER);
                    return switch (level) {
                        case BEGINNER -> 1.0;
                        case INTERMEDIATE -> 2.0;
                        case ADVANCED -> 3.0;
                        case EXPERT -> 4.0;
                    };
                })
                .average()
                .orElse(1.0);

        if (averageLevel >= 3.5) return LearningPath.DifficultyLevel.EXPERT;
        if (averageLevel >= 2.5) return LearningPath.DifficultyLevel.ADVANCED;
        if (averageLevel >= 1.5) return LearningPath.DifficultyLevel.INTERMEDIATE;
        return LearningPath.DifficultyLevel.BEGINNER;
    }

    private Integer calculateEstimatedDuration(List<String> targetSkills, Map<String, UserSkill.ProficiencyLevel> skillLevels) {
        // Estimate duration based on number of skills and current proficiency
        int baseWeeksPerSkill = 4;
        int totalWeeks = 0;
        
        for (String skill : targetSkills) {
            UserSkill.ProficiencyLevel currentLevel = skillLevels.getOrDefault(skill, UserSkill.ProficiencyLevel.BEGINNER);
            int weeksForSkill = switch (currentLevel) {
                case BEGINNER -> baseWeeksPerSkill * 2;
                case INTERMEDIATE -> baseWeeksPerSkill;
                case ADVANCED -> baseWeeksPerSkill / 2;
                case EXPERT -> baseWeeksPerSkill / 4;
            };
            totalWeeks += weeksForSkill;
        }
        
        return Math.max(2, totalWeeks); // Minimum 2 weeks
    }

    // Helper methods for analytics
    private double calculateAverageCompletionTime(List<UserPathStepProgress> progressHistory) {
        return progressHistory.stream()
                .filter(p -> p.isCompleted())
                .mapToDouble(UserPathStepProgress::getTimeSpentMinutes)
                .average()
                .orElse(0.0);
    }

    private double analyzeDifficultyPreference(List<UserPathStepProgress> progressHistory) {
        // Analyze which difficulty levels user performs best at
        return 2.0; // Placeholder - would analyze actual performance data
    }

    private Set<String> identifyStrugglingAreas(List<UserPathStepProgress> progressHistory) {
        Set<String> strugglingAreas = new HashSet<>();
        
        for (UserPathStepProgress progress : progressHistory) {
            if (progress.getAttempts() > 3 || progress.getTimeSpentMinutes() > 480) { // More than 8 hours
                strugglingAreas.add(progress.getPathStep().getTitle());
            }
        }
        
        return strugglingAreas;
    }

    private Set<String> identifyStrongAreas(List<UserPathStepProgress> progressHistory) {
        Set<String> strongAreas = new HashSet<>();
        
        for (UserPathStepProgress progress : progressHistory) {
            if (progress.isCompleted() && progress.getTimeSpentMinutes() < 120) { // Less than 2 hours
                strongAreas.add(progress.getPathStep().getTitle());
            }
        }
        
        return strongAreas;
    }

    private LearningPath.DifficultyLevel adaptDifficultyLevel(double difficultyPreference) {
        if (difficultyPreference >= 3.5) return LearningPath.DifficultyLevel.EXPERT;
        if (difficultyPreference >= 2.5) return LearningPath.DifficultyLevel.ADVANCED;
        if (difficultyPreference >= 1.5) return LearningPath.DifficultyLevel.INTERMEDIATE;
        return LearningPath.DifficultyLevel.BEGINNER;
    }

    private void generateAdaptiveSteps(LearningPath path, Set<String> strugglingAreas, 
                                     Set<String> strongAreas, User user) {
        // Generate steps that focus more on struggling areas
        int stepOrder = 1;
        
        for (String area : strugglingAreas) {
            PathStep reviewStep = createStep(path, "Review: " + area, PathStep.StepType.LEARNING, stepOrder++);
            PathStep practiceStep = createStep(path, "Extra Practice: " + area, PathStep.StepType.PRACTICE, stepOrder++);
            stepRepository.save(reviewStep);
            stepRepository.save(practiceStep);
        }
    }

    private double calculateAverageStepTime(List<UserPathStepProgress> stepProgress) {
        return stepProgress.stream()
                .filter(p -> p.isCompleted())
                .mapToDouble(UserPathStepProgress::getTimeSpentMinutes)
                .average()
                .orElse(0.0);
    }

    private double calculateLearningVelocity(List<UserPathStepProgress> stepProgress) {
        // Calculate steps completed per week
        long completedSteps = stepProgress.stream().filter(UserPathStepProgress::isCompleted).count();
        if (completedSteps == 0) return 0.0;
        
        // Find the time span of learning
        Optional<UserPathStepProgress> earliest = stepProgress.stream()
                .filter(p -> p.getStartedAt() != null)
                .min((a, b) -> a.getStartedAt().compareTo(b.getStartedAt()));
        
        if (earliest.isEmpty()) return 0.0;
        
        long weeksSinceStart = java.time.Duration.between(earliest.get().getStartedAt(), LocalDateTime.now()).toDays() / 7;
        return weeksSinceStart > 0 ? (double) completedSteps / weeksSinceStart : completedSteps;
    }

    private LocalDateTime estimateCompletionDate(UserLearningPath userPath, List<UserPathStepProgress> stepProgress) {
        if (userPath.getProgressPercentage() >= 100.0) {
            return userPath.getCompletedAt();
        }
        
        double velocity = calculateLearningVelocity(stepProgress);
        if (velocity <= 0) {
            return userPath.getTargetCompletionDate();
        }
        
        int remainingSteps = userPath.getTotalSteps() - userPath.getCompletedSteps();
        long weeksToComplete = (long) (remainingSteps / velocity);
        
        return LocalDateTime.now().plusWeeks(weeksToComplete);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
