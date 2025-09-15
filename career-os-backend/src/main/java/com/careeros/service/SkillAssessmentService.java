package com.careeros.service;

import com.careeros.dto.assessment.AssessmentSubmissionRequest;
import com.careeros.dto.assessment.AssessmentResultResponse;
import com.careeros.entity.*;
import com.careeros.repository.SkillAssessmentRepository;
import com.careeros.repository.AssessmentResponseRepository;
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
 * Skill Assessment Service for comprehensive skill evaluation
 */
@Service
@Transactional
public class SkillAssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(SkillAssessmentService.class);

    @Autowired
    private SkillAssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentResponseRepository responseRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private CertificateService certificateService;

    /**
     * Create a new skill assessment
     */
    public SkillAssessment createAssessment(User user, Skill skill, SkillAssessment.DifficultyLevel difficulty) {
        logger.info("Creating assessment for user {} and skill {}", user.getId(), skill.getName());

        SkillAssessment assessment = new SkillAssessment();
        assessment.setUser(user);
        assessment.setSkill(skill);
        assessment.setTitle(skill.getName() + " Assessment - " + difficulty.name());
        assessment.setDescription("Comprehensive assessment to evaluate your " + skill.getName() + " skills");
        assessment.setAssessmentType(SkillAssessment.AssessmentType.MIXED);
        assessment.setDifficultyLevel(difficulty);
        assessment.setTimeLimitMinutes(getTimeLimitForDifficulty(difficulty));
        assessment.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days to complete

        // Generate questions based on skill and difficulty
        generateQuestionsForAssessment(assessment);

        return assessmentRepository.save(assessment);
    }

    /**
     * Start an assessment
     */
    public SkillAssessment startAssessment(UUID assessmentId, UUID userId) {
        SkillAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (!assessment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to assessment");
        }

        if (assessment.isExpired()) {
            throw new RuntimeException("Assessment has expired");
        }

        assessment.setStatus(SkillAssessment.AssessmentStatus.IN_PROGRESS);
        assessment.setStartedAt(LocalDateTime.now());

        return assessmentRepository.save(assessment);
    }

    /**
     * Submit assessment responses
     */
    public AssessmentResultResponse submitAssessment(AssessmentSubmissionRequest request) {
        logger.info("Processing assessment submission for assessment {}", request.getAssessmentId());

        SkillAssessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (!assessment.getUser().getId().equals(request.getUserId())) {
            throw new RuntimeException("Unauthorized access to assessment");
        }

        if (assessment.getStatus() != SkillAssessment.AssessmentStatus.IN_PROGRESS) {
            throw new RuntimeException("Assessment is not in progress");
        }

        // Process responses
        processAssessmentResponses(assessment, request.getResponses());

        // Calculate score and generate evaluation
        AssessmentResultResponse result = evaluateAssessment(assessment);

        // Update assessment status
        assessment.setStatus(SkillAssessment.AssessmentStatus.COMPLETED);
        assessment.setCompletedAt(LocalDateTime.now());
        assessment.setTimeSpentMinutes(request.getTimeSpentMinutes());
        
        assessmentRepository.save(assessment);

        // Issue certificate if score is high enough
        if (result.getScorePercentage() >= 80) {
            issueCertificate(assessment);
        }

        return result;
    }

    /**
     * Get assessment results with detailed feedback
     */
    public AssessmentResultResponse getAssessmentResults(UUID assessmentId, UUID userId) {
        SkillAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (!assessment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to assessment");
        }

        if (!assessment.isCompleted()) {
            throw new RuntimeException("Assessment is not completed yet");
        }

        return buildAssessmentResult(assessment);
    }

    /**
     * Get user's assessment history
     */
    public List<SkillAssessment> getUserAssessments(UUID userId) {
        return assessmentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get skill-specific assessments for user
     */
    public List<SkillAssessment> getUserSkillAssessments(UUID userId, UUID skillId) {
        return assessmentRepository.findByUserIdAndSkillIdOrderByCreatedAtDesc(userId, skillId);
    }

    /**
     * Generate personalized assessment based on user's current skill level
     */
    public SkillAssessment generatePersonalizedAssessment(User user, Skill skill) {
        logger.info("Generating personalized assessment for user {} and skill {}", user.getId(), skill.getName());

        // Analyze user's current skill level based on previous assessments and progress
        SkillAssessment.DifficultyLevel suggestedLevel = determineSuggestedDifficulty(user, skill);

        SkillAssessment assessment = createAssessment(user, skill, suggestedLevel);
        
        // Customize questions based on user's learning history
        customizeAssessmentForUser(assessment, user);

        return assessment;
    }

    private void generateQuestionsForAssessment(SkillAssessment assessment) {
        Skill skill = assessment.getSkill();
        SkillAssessment.DifficultyLevel difficulty = assessment.getDifficultyLevel();

        // Generate questions based on skill category and difficulty
        List<AssessmentQuestion> questions = new ArrayList<>();

        switch (skill.getCategory()) {
            case PROGRAMMING_LANGUAGES:
                questions.addAll(generateProgrammingQuestions(assessment, difficulty));
                break;
            case WEB_DEVELOPMENT:
                questions.addAll(generateWebDevQuestions(assessment, difficulty));
                break;
            case DATA_SCIENCE:
                questions.addAll(generateDataScienceQuestions(assessment, difficulty));
                break;
            default:
                questions.addAll(generateGenericQuestions(assessment, difficulty));
        }

        assessment.setTotalQuestions(questions.size());
        assessment.getQuestions().addAll(questions);
    }

    private List<AssessmentQuestion> generateProgrammingQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();
        String skillName = assessment.getSkill().getName().toLowerCase();

        if (skillName.contains("java")) {
            questions.addAll(generateJavaQuestions(assessment, difficulty));
        } else if (skillName.contains("python")) {
            questions.addAll(generatePythonQuestions(assessment, difficulty));
        } else if (skillName.contains("javascript")) {
            questions.addAll(generateJavaScriptQuestions(assessment, difficulty));
        }

        return questions;
    }

    private List<AssessmentQuestion> generateJavaQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();

        // Basic Java question
        AssessmentQuestion q1 = new AssessmentQuestion(assessment, 
            "Which of the following is the correct way to declare a variable in Java?", 
            AssessmentQuestion.QuestionType.SINGLE_CHOICE, 1);
        
        q1.getOptions().add(new QuestionOption(q1, "var name = \"John\";", 1, false));
        q1.getOptions().add(new QuestionOption(q1, "String name = \"John\";", 2, true));
        q1.getOptions().add(new QuestionOption(q1, "name = \"John\";", 3, false));
        q1.getOptions().add(new QuestionOption(q1, "let name = \"John\";", 4, false));
        
        questions.add(q1);

        if (difficulty != SkillAssessment.DifficultyLevel.BEGINNER) {
            // Intermediate/Advanced questions
            AssessmentQuestion q2 = new AssessmentQuestion(assessment,
                "What will be the output of the following code?\n\n" +
                "```java\n" +
                "public class Test {\n" +
                "    public static void main(String[] args) {\n" +
                "        String s1 = \"Hello\";\n" +
                "        String s2 = \"Hello\";\n" +
                "        System.out.println(s1 == s2);\n" +
                "    }\n" +
                "}\n" +
                "```",
                AssessmentQuestion.QuestionType.SINGLE_CHOICE, 2);
            
            q2.getOptions().add(new QuestionOption(q2, "true", 1, true));
            q2.getOptions().add(new QuestionOption(q2, "false", 2, false));
            q2.getOptions().add(new QuestionOption(q2, "Compilation error", 3, false));
            q2.getOptions().add(new QuestionOption(q2, "Runtime error", 4, false));
            
            questions.add(q2);
        }

        return questions;
    }

    private List<AssessmentQuestion> generatePythonQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();

        AssessmentQuestion q1 = new AssessmentQuestion(assessment,
            "Which of the following is used to define a function in Python?",
            AssessmentQuestion.QuestionType.SINGLE_CHOICE, 1);
        
        q1.getOptions().add(new QuestionOption(q1, "function", 1, false));
        q1.getOptions().add(new QuestionOption(q1, "def", 2, true));
        q1.getOptions().add(new QuestionOption(q1, "func", 3, false));
        q1.getOptions().add(new QuestionOption(q1, "define", 4, false));
        
        questions.add(q1);

        return questions;
    }

    private List<AssessmentQuestion> generateJavaScriptQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();

        AssessmentQuestion q1 = new AssessmentQuestion(assessment,
            "Which method is used to add an element to the end of an array in JavaScript?",
            AssessmentQuestion.QuestionType.SINGLE_CHOICE, 1);
        
        q1.getOptions().add(new QuestionOption(q1, "append()", 1, false));
        q1.getOptions().add(new QuestionOption(q1, "add()", 2, false));
        q1.getOptions().add(new QuestionOption(q1, "push()", 3, true));
        q1.getOptions().add(new QuestionOption(q1, "insert()", 4, false));
        
        questions.add(q1);

        return questions;
    }

    private List<AssessmentQuestion> generateWebDevQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();

        AssessmentQuestion q1 = new AssessmentQuestion(assessment,
            "Which HTML tag is used to define the largest heading?",
            AssessmentQuestion.QuestionType.SINGLE_CHOICE, 1);
        
        q1.getOptions().add(new QuestionOption(q1, "<h6>", 1, false));
        q1.getOptions().add(new QuestionOption(q1, "<h1>", 2, true));
        q1.getOptions().add(new QuestionOption(q1, "<heading>", 3, false));
        q1.getOptions().add(new QuestionOption(q1, "<header>", 4, false));
        
        questions.add(q1);

        return questions;
    }

    private List<AssessmentQuestion> generateDataScienceQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();

        AssessmentQuestion q1 = new AssessmentQuestion(assessment,
            "Which Python library is most commonly used for data manipulation and analysis?",
            AssessmentQuestion.QuestionType.SINGLE_CHOICE, 1);
        
        q1.getOptions().add(new QuestionOption(q1, "NumPy", 1, false));
        q1.getOptions().add(new QuestionOption(q1, "Pandas", 2, true));
        q1.getOptions().add(new QuestionOption(q1, "Matplotlib", 3, false));
        q1.getOptions().add(new QuestionOption(q1, "Seaborn", 4, false));
        
        questions.add(q1);

        return questions;
    }

    private List<AssessmentQuestion> generateGenericQuestions(SkillAssessment assessment, SkillAssessment.DifficultyLevel difficulty) {
        List<AssessmentQuestion> questions = new ArrayList<>();

        AssessmentQuestion q1 = new AssessmentQuestion(assessment,
            "How would you rate your confidence in " + assessment.getSkill().getName() + "?",
            AssessmentQuestion.QuestionType.SINGLE_CHOICE, 1);
        
        q1.getOptions().add(new QuestionOption(q1, "Very Low", 1, false));
        q1.getOptions().add(new QuestionOption(q1, "Low", 2, false));
        q1.getOptions().add(new QuestionOption(q1, "Medium", 3, false));
        q1.getOptions().add(new QuestionOption(q1, "High", 4, false));
        q1.getOptions().add(new QuestionOption(q1, "Very High", 5, false));
        
        questions.add(q1);

        return questions;
    }

    private void processAssessmentResponses(SkillAssessment assessment, List<AssessmentSubmissionRequest.QuestionResponse> responses) {
        int correctAnswers = 0;
        int totalAnswered = 0;

        for (AssessmentSubmissionRequest.QuestionResponse response : responses) {
            AssessmentQuestion question = assessment.getQuestions().stream()
                    .filter(q -> q.getId().equals(response.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            AssessmentResponse assessmentResponse = new AssessmentResponse(assessment, question, assessment.getUser());
            
            if (response.getSelectedOptionIds() != null && !response.getSelectedOptionIds().isEmpty()) {
                // Handle multiple choice questions
                Set<QuestionOption> selectedOptions = question.getOptions().stream()
                        .filter(option -> response.getSelectedOptionIds().contains(option.getId()))
                        .collect(Collectors.toSet());
                
                assessmentResponse.setSelectedOptions(selectedOptions);
                
                // Check if answer is correct
                Set<QuestionOption> correctOptions = question.getCorrectOptions();
                boolean isCorrect = selectedOptions.equals(correctOptions);
                assessmentResponse.setIsCorrect(isCorrect);
                
                if (isCorrect) {
                    correctAnswers++;
                    assessmentResponse.setPointsEarned(question.getPoints());
                }
            } else if (response.getAnswerText() != null) {
                // Handle text-based questions
                assessmentResponse.setAnswerText(response.getAnswerText());
                // For now, assume text answers are correct (would need AI evaluation)
                assessmentResponse.setIsCorrect(true);
                assessmentResponse.setPointsEarned(question.getPoints());
                correctAnswers++;
            }

            assessmentResponse.setTimeSpentSeconds(response.getTimeSpentSeconds());
            assessmentResponse.setConfidenceLevel(response.getConfidenceLevel());
            
            responseRepository.save(assessmentResponse);
            totalAnswered++;
        }

        assessment.setAnsweredQuestions(totalAnswered);
        assessment.setCorrectAnswers(correctAnswers);
        
        if (assessment.getTotalQuestions() > 0) {
            assessment.setScorePercentage((int) ((double) correctAnswers / assessment.getTotalQuestions() * 100));
        }
    }

    private AssessmentResultResponse evaluateAssessment(SkillAssessment assessment) {
        AssessmentResultResponse result = buildAssessmentResult(assessment);

        // Generate AI-powered evaluation and feedback
        try {
            String aiEvaluation = generateAIEvaluation(assessment);
            assessment.setAiEvaluation(aiEvaluation);
            
            // Extract strengths, weaknesses, and recommendations from AI evaluation
            extractFeedbackFromAI(assessment, aiEvaluation);
            
        } catch (Exception e) {
            logger.error("Error generating AI evaluation", e);
            // Fallback to basic evaluation
            generateBasicEvaluation(assessment);
        }

        return result;
    }

    private AssessmentResultResponse buildAssessmentResult(SkillAssessment assessment) {
        AssessmentResultResponse result = new AssessmentResultResponse();
        result.setAssessmentId(assessment.getId());
        result.setSkillName(assessment.getSkill().getName());
        result.setScorePercentage(assessment.getScorePercentage());
        result.setTotalQuestions(assessment.getTotalQuestions());
        result.setCorrectAnswers(assessment.getCorrectAnswers());
        result.setTimeSpentMinutes(assessment.getTimeSpentMinutes());
        result.setCompletedAt(assessment.getCompletedAt());
        result.setStrengths(parseStrengths(assessment.getStrengths()));
        result.setWeaknesses(parseWeaknesses(assessment.getWeaknesses()));
        result.setRecommendations(parseRecommendations(assessment.getRecommendations()));
        result.setCertificateIssued(assessment.getCertificateIssued());
        result.setCertificateUrl(assessment.getCertificateUrl());
        
        return result;
    }

    private String generateAIEvaluation(SkillAssessment assessment) {
        // Create a prompt for AI evaluation
        StringBuilder prompt = new StringBuilder();
        prompt.append("Evaluate this skill assessment result:\n");
        prompt.append("Skill: ").append(assessment.getSkill().getName()).append("\n");
        prompt.append("Score: ").append(assessment.getScorePercentage()).append("%\n");
        prompt.append("Questions answered: ").append(assessment.getAnsweredQuestions()).append("/").append(assessment.getTotalQuestions()).append("\n");
        prompt.append("Difficulty: ").append(assessment.getDifficultyLevel()).append("\n");
        
        // Add question responses for context
        List<AssessmentResponse> responses = responseRepository.findByAssessmentId(assessment.getId());
        for (AssessmentResponse response : responses) {
            prompt.append("Q: ").append(response.getQuestion().getQuestionText()).append("\n");
            prompt.append("Correct: ").append(response.getIsCorrect()).append("\n");
        }
        
        prompt.append("\nProvide detailed feedback including strengths, areas for improvement, and specific recommendations for skill development.");

        // Use OpenAI service to generate evaluation (simplified)
        return "AI-generated evaluation based on assessment performance"; // Placeholder
    }

    private void extractFeedbackFromAI(SkillAssessment assessment, String aiEvaluation) {
        // Parse AI evaluation to extract structured feedback
        // This would involve more sophisticated text processing
        
        assessment.setStrengths("Strong foundational knowledge, Good problem-solving approach");
        assessment.setWeaknesses("Need more practice with advanced concepts, Time management");
        assessment.setRecommendations("Focus on advanced topics, Practice coding challenges, Review fundamentals");
    }

    private void generateBasicEvaluation(SkillAssessment assessment) {
        int score = assessment.getScorePercentage();
        
        if (score >= 90) {
            assessment.setStrengths("Excellent mastery of " + assessment.getSkill().getName());
            assessment.setRecommendations("Consider advanced topics or teaching others");
        } else if (score >= 75) {
            assessment.setStrengths("Good understanding of core concepts");
            assessment.setWeaknesses("Some gaps in advanced topics");
            assessment.setRecommendations("Focus on areas where you scored lower");
        } else if (score >= 60) {
            assessment.setStrengths("Basic understanding established");
            assessment.setWeaknesses("Need more practice with fundamentals");
            assessment.setRecommendations("Review basic concepts and practice more");
        } else {
            assessment.setWeaknesses("Significant gaps in understanding");
            assessment.setRecommendations("Start with beginner resources and build foundation");
        }
    }

    private void issueCertificate(SkillAssessment assessment) {
        try {
            String certificateUrl = certificateService.generateCertificate(
                assessment.getUser(),
                assessment.getSkill().getName() + " Proficiency",
                assessment.getScorePercentage()
            );
            
            assessment.setCertificateIssued(true);
            assessment.setCertificateUrl(certificateUrl);
            
            logger.info("Certificate issued for user {} and skill {}", 
                assessment.getUser().getId(), assessment.getSkill().getName());
                
        } catch (Exception e) {
            logger.error("Error issuing certificate", e);
        }
    }

    private SkillAssessment.DifficultyLevel determineSuggestedDifficulty(User user, Skill skill) {
        // Analyze user's previous assessments and learning progress
        List<SkillAssessment> previousAssessments = getUserSkillAssessments(user.getId(), skill.getId());
        
        if (previousAssessments.isEmpty()) {
            return SkillAssessment.DifficultyLevel.BEGINNER;
        }
        
        // Get the latest assessment score
        SkillAssessment latest = previousAssessments.get(0);
        int latestScore = latest.getScorePercentage();
        
        if (latestScore >= 90) {
            return SkillAssessment.DifficultyLevel.EXPERT;
        } else if (latestScore >= 75) {
            return SkillAssessment.DifficultyLevel.ADVANCED;
        } else if (latestScore >= 60) {
            return SkillAssessment.DifficultyLevel.INTERMEDIATE;
        } else {
            return SkillAssessment.DifficultyLevel.BEGINNER;
        }
    }

    private void customizeAssessmentForUser(SkillAssessment assessment, User user) {
        // Customize questions based on user's learning history and preferences
        // This could involve adjusting question types, difficulty distribution, etc.
        logger.info("Customizing assessment for user {}", user.getId());
    }

    private int getTimeLimitForDifficulty(SkillAssessment.DifficultyLevel difficulty) {
        return switch (difficulty) {
            case BEGINNER -> 30;
            case INTERMEDIATE -> 45;
            case ADVANCED -> 60;
            case EXPERT -> 90;
        };
    }

    private List<String> parseStrengths(String strengths) {
        if (strengths == null) return new ArrayList<>();
        return Arrays.asList(strengths.split(",\\s*"));
    }

    private List<String> parseWeaknesses(String weaknesses) {
        if (weaknesses == null) return new ArrayList<>();
        return Arrays.asList(weaknesses.split(",\\s*"));
    }

    private List<String> parseRecommendations(String recommendations) {
        if (recommendations == null) return new ArrayList<>();
        return Arrays.asList(recommendations.split(",\\s*"));
    }
}
