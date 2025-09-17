package com.careeros.service;

import com.careeros.dto.mentorship.MentorshipMatchRequest;
import com.careeros.dto.mentorship.MentorshipMatchResponse;
import com.careeros.dto.mentorship.MentorProfileRequest;
import com.careeros.entity.*;
import com.careeros.repository.*;
import com.careeros.service.ai.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Mentorship Matching Service
 */
@Service
@Transactional
public class MentorshipMatchingService {

    private static final Logger logger = LoggerFactory.getLogger(MentorshipMatchingService.class);

    @Autowired
    private MentorProfileRepository mentorProfileRepository;

    @Autowired
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Autowired
    private MentorshipSessionRepository mentorshipSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Create or update mentor profile
     */
    public MentorProfile createMentorProfile(UUID userId, MentorProfileRequest request) {
        logger.info("Creating mentor profile for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MentorProfile profile = mentorProfileRepository.findByMentorId(userId)
                .orElse(new MentorProfile());

        profile.setMentor(user);
        profile.setBio(request.getBio());
        profile.setExpertiseAreas(request.getExpertiseAreas());
        profile.setIndustries(request.getIndustries());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setHourlyRate(request.getHourlyRate());
        profile.setAvailableHoursPerWeek(request.getAvailableHoursPerWeek());
        // Note: TimeZone field not implemented in MentorProfile entity yet
        // Note: Languages field not implemented in MentorProfile entity yet
        profile.setMentorshipStyle(MentorProfile.MentorshipStyle.valueOf(request.getMentorshipStyle().toUpperCase()));
        profile.setPreferredMenteeLevel(MentorProfile.PreferredMenteeLevel.valueOf(request.getPreferredMenteeLevel().toUpperCase()));
        profile.setMaxMentees(request.getMaxMentees());
        profile.setIsAvailable(true);
        // Note: Status field not implemented in MentorProfile entity yet

        return mentorProfileRepository.save(profile);
    }

    /**
     * Find mentor matches using AI-powered algorithm
     */
    public List<MentorshipMatchResponse> findMentorMatches(UUID menteeId, MentorshipMatchRequest request) {
        logger.info("Finding mentor matches for user {}", menteeId);

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get mentee's profile and skills
        List<UserSkill> menteeSkills = userSkillRepository.findByUserId(menteeId);
        
        // Get available mentors
        List<MentorProfile> availableMentors = mentorProfileRepository.findAvailableMentors();

        // Calculate compatibility scores
        List<MentorshipMatchResponse> matches = new ArrayList<>();
        
        for (MentorProfile mentor : availableMentors) {
            double compatibilityScore = calculateCompatibilityScore(mentee, mentor, menteeSkills, request);
            
            if (compatibilityScore >= request.getMinCompatibilityScore()) {
                MentorshipMatchResponse match = createMatchResponse(mentor, compatibilityScore, mentee, request);
                matches.add(match);
            }
        }

        // Sort by compatibility score (highest first)
        matches.sort((a, b) -> Double.compare(b.getCompatibilityScore(), a.getCompatibilityScore()));

        // Use AI to refine and explain matches
        enhanceMatchesWithAI(matches, mentee, request);

        return matches.stream().limit(10).collect(Collectors.toList()); // Return top 10 matches
    }

    /**
     * Request mentorship from a specific mentor
     */
    public MentorshipRequest requestMentorship(UUID menteeId, UUID mentorId, String message, 
                                             List<String> goals, String preferredSchedule) {
        logger.info("Creating mentorship request from mentee {} to mentor {}", menteeId, mentorId);

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("Mentee not found"));

        MentorProfile mentor = mentorProfileRepository.findByMentorId(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        // Check if mentor is available
        if (!mentor.getIsAvailable() || mentor.getCurrentMentees() >= mentor.getMaxMentees()) {
            throw new RuntimeException("Mentor is not available for new mentees");
        }

        MentorshipRequest request = new MentorshipRequest();
        request.setMentee(mentee);
        request.setMentor(mentor.getMentor());
        request.setMessage(message);
        request.setGoals(String.join(",", goals));
        request.setPreferredSchedule(preferredSchedule);
        request.setStatus(MentorshipRequest.RequestStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());

        MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);

        // Send notification to mentor
        notificationService.sendMentorshipRequestNotification(mentor.getMentor(), savedRequest);

        return savedRequest;
    }

    /**
     * Accept mentorship request
     */
    public void acceptMentorshipRequest(UUID requestId, UUID mentorId) {
        logger.info("Accepting mentorship request {} by mentor {}", requestId, mentorId);

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Mentorship request not found"));

        if (!request.getMentor().getId().equals(mentorId)) {
            throw new RuntimeException("Unauthorized to accept this request");
        }

        request.setStatus(MentorshipRequest.RequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());
        mentorshipRequestRepository.save(request);

        // Update mentor's current mentee count
        MentorProfile mentorProfile = mentorProfileRepository.findByMentorId(request.getMentor().getId())
                .orElse(null);
        if (mentorProfile != null) {
            mentorProfile.setCurrentMentees(mentorProfile.getCurrentMentees() + 1);
            mentorProfileRepository.save(mentorProfile);
        }

        // Send notification to mentee
        notificationService.sendMentorshipAcceptedNotification(request.getMentee(), request.getMentor());

        // Create initial mentorship session
        createInitialMentorshipSession(request);
    }

    /**
     * Decline mentorship request
     */
    public void declineMentorshipRequest(UUID requestId, UUID mentorId, String reason) {
        logger.info("Declining mentorship request {} by mentor {}", requestId, mentorId);

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Mentorship request not found"));

        if (!request.getMentor().getId().equals(mentorId)) {
            throw new RuntimeException("Unauthorized to decline this request");
        }

        request.setStatus(MentorshipRequest.RequestStatus.DECLINED);
        request.setDeclineReason(reason);
        request.setRespondedAt(LocalDateTime.now());
        mentorshipRequestRepository.save(request);

        // Send notification to mentee
        notificationService.sendMentorshipDeclinedNotification(request.getMentee(), 
                request.getMentor(), reason);
    }

    /**
     * Schedule mentorship session
     */
    public MentorshipSession scheduleMentorshipSession(UUID mentorId, UUID menteeId, 
                                                      LocalDateTime scheduledTime, String topic, 
                                                      Integer durationMinutes) {
        logger.info("Scheduling mentorship session between mentor {} and mentee {}", mentorId, menteeId);

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new RuntimeException("Mentee not found"));

        MentorshipSession session = new MentorshipSession();
        session.setMentor(mentor);
        session.setMentee(mentee);
        session.setScheduledTime(scheduledTime);
        session.setTopic(topic);
        session.setDurationMinutes(durationMinutes);
        session.setStatus(MentorshipSession.SessionStatus.SCHEDULED);

        MentorshipSession savedSession = mentorshipSessionRepository.save(session);

        // Send calendar invitations
        notificationService.sendMentorshipSessionScheduledNotification(mentor, mentee, savedSession);

        return savedSession;
    }

    /**
     * Get mentorship analytics for mentor
     */
    public Map<String, Object> getMentorAnalytics(UUID mentorId) {
        MentorProfile mentor = mentorProfileRepository.findByMentorId(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor profile not found"));

        List<MentorshipSession> sessions = mentorshipSessionRepository.findByMentorIdOrderByScheduledTimeDesc(mentorId);
        List<MentorshipRequest> requests = mentorshipRequestRepository.findByMentorIdOrderByRequestedAtDesc(mentorId);

        Map<String, Object> analytics = new HashMap<>();
        
        // Basic stats
        analytics.put("totalMentees", mentor.getCurrentMentees());
        analytics.put("totalSessions", sessions.size());
        analytics.put("totalRequests", requests.size());
        
        // Session analytics
        long completedSessions = sessions.stream().filter(s -> s.getStatus() == MentorshipSession.SessionStatus.COMPLETED).count();
        analytics.put("completedSessions", completedSessions);
        analytics.put("sessionCompletionRate", sessions.isEmpty() ? 0 : (double) completedSessions / sessions.size() * 100);
        
        // Request analytics
        long acceptedRequests = requests.stream().filter(r -> r.getStatus() == MentorshipRequest.RequestStatus.ACCEPTED).count();
        analytics.put("acceptedRequests", acceptedRequests);
        analytics.put("requestAcceptanceRate", requests.isEmpty() ? 0 : (double) acceptedRequests / requests.size() * 100);
        
        // Time analytics
        int totalMinutes = sessions.stream()
                .filter(s -> s.getStatus() == MentorshipSession.SessionStatus.COMPLETED)
                .mapToInt(MentorshipSession::getDurationMinutes)
                .sum();
        analytics.put("totalMentoringHours", totalMinutes / 60.0);
        
        // Rating analytics
        double averageRating = sessions.stream()
                .filter(s -> s.getMenteeRating() != null)
                .mapToDouble(MentorshipSession::getMenteeRating)
                .average()
                .orElse(0.0);
        analytics.put("averageRating", averageRating);
        
        return analytics;
    }

    /**
     * Get recommended mentors for a user based on their learning goals
     */
    public List<MentorProfile> getRecommendedMentors(UUID userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        
        // Get skill areas where user needs improvement
        List<String> improvementAreas = userSkills.stream()
                .filter(skill -> skill.getProficiencyLevel() == UserSkill.ProficiencyLevel.BEGINNER ||
                               skill.getProficiencyLevel() == UserSkill.ProficiencyLevel.INTERMEDIATE)
                .map(skill -> skill.getSkill().getName())
                .collect(Collectors.toList());

        // Find mentors with expertise in these areas
        List<MentorProfile> mentors = mentorProfileRepository.findMentorsWithExpertiseIn(improvementAreas);
        
        // Score and sort mentors
        return mentors.stream()
                .filter(MentorProfile::getIsAvailable)
                .sorted((a, b) -> Double.compare(calculateMentorScore(b, userSkills), calculateMentorScore(a, userSkills)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateCompatibilityScore(User mentee, MentorProfile mentor, 
                                             List<UserSkill> menteeSkills, MentorshipMatchRequest request) {
        double score = 0.0;
        
        // Skill compatibility (40% weight)
        double skillScore = calculateSkillCompatibility(menteeSkills, mentor.getExpertiseAreas());
        score += skillScore * 0.4;
        
        // Experience level match (20% weight)
        double experienceScore = calculateExperienceMatch(menteeSkills, mentor.getPreferredMenteeLevel());
        score += experienceScore * 0.2;
        
        // Availability match (15% weight)
        double availabilityScore = calculateAvailabilityMatch(request.getPreferredSchedule(), mentor);
        score += availabilityScore * 0.15;
        
        // Industry match (10% weight)
        double industryScore = calculateIndustryMatch(request.getIndustryPreference(), mentor.getIndustries());
        score += industryScore * 0.1;
        
        // Communication style match (10% weight)
        double styleScore = calculateStyleMatch(request.getPreferredMentorshipStyle(), mentor.getMentorshipStyle());
        score += styleScore * 0.1;
        
        // Mentor rating and reviews (5% weight)
        double reputationScore = mentor.getAverageRating() != null ? mentor.getAverageRating() / 5.0 : 0.5;
        score += reputationScore * 0.05;
        
        return Math.min(score, 1.0); // Cap at 1.0
    }

    private double calculateSkillCompatibility(List<UserSkill> menteeSkills, List<String> mentorExpertise) {
        if (mentorExpertise == null || mentorExpertise.isEmpty()) return 0.0;
        
        List<String> expertiseAreas = mentorExpertise;
        
        long matchingSkills = menteeSkills.stream()
                .map(skill -> skill.getSkill().getName().toLowerCase())
                .filter(skillName -> expertiseAreas.stream()
                        .anyMatch(expertise -> expertise.toLowerCase().contains(skillName) || 
                                             skillName.contains(expertise.toLowerCase())))
                .count();
        
        return menteeSkills.isEmpty() ? 0.0 : (double) matchingSkills / menteeSkills.size();
    }

    private double calculateExperienceMatch(List<UserSkill> menteeSkills, 
                                          MentorProfile.PreferredMenteeLevel preferredLevel) {
        if (preferredLevel == null) return 0.5;
        
        // Calculate average skill level of mentee
        double averageLevel = menteeSkills.stream()
                .mapToDouble(skill -> switch (skill.getProficiencyLevel()) {
                    case BEGINNER -> 1.0;
                    case INTERMEDIATE -> 2.0;
                    case ADVANCED -> 3.0;
                    case EXPERT -> 4.0;
                })
                .average()
                .orElse(1.0);
        
        // Match with mentor's preferred mentee level
        return switch (preferredLevel) {
            case BEGINNER -> averageLevel <= 1.5 ? 1.0 : Math.max(0.0, 1.0 - (averageLevel - 1.5) / 2.5);
            case INTERMEDIATE -> averageLevel >= 1.5 && averageLevel <= 2.5 ? 1.0 : 
                                Math.max(0.0, 1.0 - Math.abs(averageLevel - 2.0) / 2.0);
            case ADVANCED -> averageLevel >= 2.5 ? 1.0 : Math.max(0.0, (averageLevel - 1.0) / 1.5);
            case ALL_LEVELS -> 0.8; // Good match for flexible mentors
        };
    }

    private double calculateAvailabilityMatch(String preferredSchedule, MentorProfile mentor) {
        // Simplified availability matching - in real implementation would parse schedules
        return mentor.getAvailableHoursPerWeek() > 0 ? 0.8 : 0.2;
    }

    private double calculateIndustryMatch(String industryPreference, List<String> mentorIndustries) {
        if (industryPreference == null || mentorIndustries == null) return 0.5;
        
        return mentorIndustries.stream()
                .anyMatch(industry -> industry.toLowerCase().contains(industryPreference.toLowerCase())) ? 1.0 : 0.0;
    }

    private double calculateStyleMatch(String preferredStyle, MentorProfile.MentorshipStyle mentorStyle) {
        if (preferredStyle == null || mentorStyle == null) return 0.5;
        return preferredStyle.equalsIgnoreCase(mentorStyle.name()) ? 1.0 : 0.3;
    }

    // Duplicate method removed - keeping the switch expression version

    private MentorshipMatchResponse createMatchResponse(MentorProfile mentor, double compatibilityScore, 
                                                       User mentee, MentorshipMatchRequest request) {
        MentorshipMatchResponse response = new MentorshipMatchResponse();
        response.setMentorId(mentor.getMentor().getId());
        response.setMentorName(mentor.getMentor().getFullName());
        response.setMentorBio(mentor.getBio());
        response.setMentorStrengths(mentor.getExpertiseAreas());
        response.setYearsOfExperience(mentor.getYearsOfExperience());
        response.setHourlyRate(mentor.getHourlyRate());
        response.setAverageRating(mentor.getAverageRating());
        response.setTotalReviews(mentor.getTotalReviews());
        response.setCompatibilityScore(compatibilityScore);
        response.setMatchReasons(generateMatchReasons(mentor, mentee, compatibilityScore));
        
        return response;
    }

    private List<String> generateMatchReasons(MentorProfile mentor, User mentee, double compatibilityScore) {
        List<String> reasons = new ArrayList<>();
        
        if (compatibilityScore >= 0.8) {
            reasons.add("Excellent skill alignment with your learning goals");
        }
        if (mentor.getYearsOfExperience() >= 5) {
            reasons.add("Extensive industry experience (" + mentor.getYearsOfExperience() + " years)");
        }
        if (mentor.getAverageRating() != null && mentor.getAverageRating() >= 4.5) {
            reasons.add("Highly rated by previous mentees (" + mentor.getAverageRating() + "/5.0)");
        }
        if (mentor.getAvailableHoursPerWeek() >= 5) {
            reasons.add("Good availability for regular mentoring sessions");
        }
        
        return reasons;
    }

    private void enhanceMatchesWithAI(List<MentorshipMatchResponse> matches, User mentee, 
                                     MentorshipMatchRequest request) {
        // Use AI to provide personalized match explanations and suggestions
        for (MentorshipMatchResponse match : matches) {
            try {
                String aiExplanation = openAIService.generateMentorshipMatchExplanation(mentee, match, request);
                match.setAiExplanation(aiExplanation);
            } catch (Exception e) {
                logger.warn("Failed to generate AI explanation for mentor match", e);
            }
        }
    }

    private void createInitialMentorshipSession(MentorshipRequest request) {
        MentorshipSession initialSession = new MentorshipSession();
        initialSession.setMentor(request.getMentor());
        initialSession.setMentee(request.getMentee());
        initialSession.setTopic("Initial Mentorship Meeting - Goal Setting");
        initialSession.setDurationMinutes(60);
        initialSession.setStatus(MentorshipSession.SessionStatus.SCHEDULED);
        initialSession.setScheduledTime(LocalDateTime.now().plusDays(3)); // Schedule 3 days from now
        
        mentorshipSessionRepository.save(initialSession);
    }

    private double calculateMentorScore(MentorProfile mentor, List<UserSkill> userSkills) {
        double score = 0.0;
        
        // Base score from rating
        if (mentor.getAverageRating() != null) {
            score += mentor.getAverageRating() * 0.3;
        }
        
        // Experience factor
        score += Math.min(mentor.getYearsOfExperience() / 10.0, 1.0) * 0.3;
        
        // Availability factor
        score += (mentor.getAvailableHoursPerWeek() / 20.0) * 0.2;
        
        // Skill relevance
        double skillRelevance = calculateSkillCompatibility(userSkills, mentor.getExpertiseAreas());
        score += skillRelevance * 0.2;
        
        return score;
    }
}
