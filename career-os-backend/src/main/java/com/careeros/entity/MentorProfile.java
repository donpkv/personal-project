package com.careeros.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mentor Profile Entity for mentorship matching system
 */
@Entity
@Table(name = "mentor_profiles")
@Data
@EqualsAndHashCode(callSuper = true)
public class MentorProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @ElementCollection
    @CollectionTable(name = "mentor_expertise_areas", joinColumns = @JoinColumn(name = "mentor_profile_id"))
    @Column(name = "expertise_area")
    private List<String> expertiseAreas;

    @ElementCollection
    @CollectionTable(name = "mentor_industries", joinColumns = @JoinColumn(name = "mentor_profile_id"))
    @Column(name = "industry")
    private List<String> industries;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_mentee_level")
    private PreferredMenteeLevel preferredMenteeLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "mentorship_style")
    private MentorshipStyle mentorshipStyle;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "max_mentees")
    private Integer maxMentees = 5;

    @Column(name = "available_hours_per_week")
    private Integer availableHoursPerWeek;

    @Column(name = "current_mentees")
    private Integer currentMentees = 0;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @ElementCollection
    @CollectionTable(name = "mentor_availability", joinColumns = @JoinColumn(name = "mentor_profile_id"))
    @Column(name = "time_slot")
    private List<String> availableTimeSlots;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    public enum PreferredMenteeLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        ALL_LEVELS
    }

    public enum MentorshipStyle {
        STRUCTURED,
        FLEXIBLE,
        PROJECT_BASED,
        CAREER_FOCUSED,
        TECHNICAL_DEEP_DIVE
    }
}
