package com.careeros.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a challenge in a study group
 */
@Entity
@Table(name = "group_challenges")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupChallenge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "challenge_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;
    
    @Column(name = "difficulty_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "points_reward")
    private Integer pointsReward = 0;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status = ChallengeStatus.ACTIVE;
    
    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;
    
    @Column(name = "success_criteria", columnDefinition = "TEXT")
    private String successCriteria;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChallengeParticipant> participants = new ArrayList<>();
    
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChallengeSubmission> submissions = new ArrayList<>();
    
    public enum ChallengeType {
        CODING,
        LEARNING_STREAK,
        PROJECT_COMPLETION,
        SKILL_ASSESSMENT,
        PEER_REVIEW,
        STUDY_HOURS,
        QUIZ_COMPETITION,
        PRESENTATION,
        RESEARCH,
        COLLABORATION
    }
    
    public enum DifficultyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }
    
    public enum ChallengeStatus {
        DRAFT,
        ACTIVE,
        COMPLETED,
        CANCELLED,
        PAUSED
    }
    
    // Entity for challenge participants
    @Entity
    @Table(name = "challenge_participants")
    @Data
    public static class ChallengeParticipant {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "challenge_id", nullable = false)
        private GroupChallenge challenge;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        
        @Column(name = "participation_status")
        @Enumerated(EnumType.STRING)
        private ParticipationStatus participationStatus = ParticipationStatus.JOINED;
        
        @Column(name = "progress_percentage")
        private Double progressPercentage = 0.0;
        
        @Column(name = "points_earned")
        private Integer pointsEarned = 0;
        
        @CreationTimestamp
        @Column(name = "joined_at")
        private LocalDateTime joinedAt;
        
        @Column(name = "completed_at")
        private LocalDateTime completedAt;
        
        public enum ParticipationStatus {
            JOINED,
            IN_PROGRESS,
            COMPLETED,
            DROPPED_OUT,
            DISQUALIFIED
        }
    }
    
    // Entity for challenge submissions
    @Entity
    @Table(name = "challenge_submissions")
    @Data
    public static class ChallengeSubmission {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "challenge_id", nullable = false)
        private GroupChallenge challenge;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "participant_id", nullable = false)
        private User participant;
        
        @Column(name = "submission_content", columnDefinition = "TEXT")
        private String submissionContent;
        
        @Column(name = "submission_url")
        private String submissionUrl;
        
        @Column(name = "score")
        private Double score;
        
        @Column(name = "feedback", columnDefinition = "TEXT")
        private String feedback;
        
        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        private SubmissionStatus status = SubmissionStatus.SUBMITTED;
        
        @CreationTimestamp
        @Column(name = "submitted_at")
        private LocalDateTime submittedAt;
        
        @Column(name = "reviewed_at")
        private LocalDateTime reviewedAt;
        
        public enum SubmissionStatus {
            SUBMITTED,
            UNDER_REVIEW,
            APPROVED,
            REJECTED,
            NEEDS_REVISION
        }
    }
}
