package com.careeros.repository;

import com.careeros.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for MentorProfile entity
 */
@Repository
public interface MentorProfileRepository extends JpaRepository<MentorProfile, UUID> {

    /**
     * Find mentor profile by user ID
     */
    Optional<MentorProfile> findByUserId(UUID userId);

    /**
     * Find available mentors
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.isAvailable = true AND mp.currentMentees < mp.maxMentees")
    List<MentorProfile> findAvailableMentors();

    /**
     * Find verified mentors
     */
    List<MentorProfile> findByIsVerifiedTrue();

    /**
     * Find mentors by expertise area
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE :expertise MEMBER OF mp.expertiseAreas")
    List<MentorProfile> findByExpertiseArea(@Param("expertise") String expertise);

    /**
     * Find mentors with expertise in multiple areas
     */
    @Query("SELECT DISTINCT mp FROM MentorProfile mp WHERE EXISTS (SELECT e FROM mp.expertiseAreas e WHERE e IN :expertiseAreas)")
    List<MentorProfile> findMentorsWithExpertiseIn(@Param("expertiseAreas") List<String> expertiseAreas);

    /**
     * Find mentors by industry
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE :industry MEMBER OF mp.industries")
    List<MentorProfile> findByIndustry(@Param("industry") String industry);

    /**
     * Find mentors by preferred mentee level
     */
    List<MentorProfile> findByPreferredMenteeLevel(MentorProfile.PreferredMenteeLevel preferredMenteeLevel);

    /**
     * Find mentors by mentorship style
     */
    List<MentorProfile> findByMentorshipStyle(MentorProfile.MentorshipStyle mentorshipStyle);

    /**
     * Find mentors with minimum rating
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.averageRating >= :minRating ORDER BY mp.averageRating DESC")
    List<MentorProfile> findMentorsWithMinRating(@Param("minRating") Double minRating);

    /**
     * Find top-rated mentors
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.averageRating IS NOT NULL ORDER BY mp.averageRating DESC, mp.totalReviews DESC")
    List<MentorProfile> findTopRatedMentors();

    /**
     * Find mentors by years of experience range
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.yearsOfExperience BETWEEN :minYears AND :maxYears")
    List<MentorProfile> findByExperienceRange(@Param("minYears") Integer minYears, @Param("maxYears") Integer maxYears);

    /**
     * Find mentors by hourly rate range
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.hourlyRate BETWEEN :minRate AND :maxRate")
    List<MentorProfile> findByHourlyRateRange(@Param("minRate") Double minRate, @Param("maxRate") Double maxRate);

    /**
     * Find mentors with availability
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.isAvailable = true AND mp.currentMentees < mp.maxMentees AND SIZE(mp.availableTimeSlots) > 0")
    List<MentorProfile> findMentorsWithAvailability();

    /**
     * Search mentors by bio or title
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE LOWER(mp.bio) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(mp.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MentorProfile> searchMentorsByKeyword(@Param("keyword") String keyword);

    /**
     * Find mentors by timezone
     */
    List<MentorProfile> findByTimezone(String timezone);

    /**
     * Find mentors with capacity for more mentees
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.currentMentees < mp.maxMentees AND mp.isAvailable = true")
    List<MentorProfile> findMentorsWithCapacity();

    /**
     * Count mentors by expertise area
     */
    @Query("SELECT COUNT(mp) FROM MentorProfile mp WHERE :expertise MEMBER OF mp.expertiseAreas")
    Long countMentorsByExpertiseArea(@Param("expertise") String expertise);

    /**
     * Count available mentors
     */
    @Query("SELECT COUNT(mp) FROM MentorProfile mp WHERE mp.isAvailable = true")
    Long countAvailableMentors();

    /**
     * Count verified mentors
     */
    Long countByIsVerifiedTrue();

    /**
     * Get mentor statistics
     */
    @Query("SELECT COUNT(mp) as totalMentors, " +
           "COUNT(CASE WHEN mp.isAvailable = true THEN 1 END) as availableMentors, " +
           "COUNT(CASE WHEN mp.isVerified = true THEN 1 END) as verifiedMentors, " +
           "AVG(mp.averageRating) as avgRating, " +
           "AVG(mp.yearsOfExperience) as avgExperience " +
           "FROM MentorProfile mp")
    Object[] getMentorStatistics();

    /**
     * Find mentors by availability time slot
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE :timeSlot MEMBER OF mp.availableTimeSlots")
    List<MentorProfile> findMentorsByTimeSlot(@Param("timeSlot") String timeSlot);

    /**
     * Find new mentors (recently created profiles)
     */
    @Query("SELECT mp FROM MentorProfile mp ORDER BY mp.createdAt DESC")
    List<MentorProfile> findNewMentors();

    /**
     * Find mentors with social profiles
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.linkedinUrl IS NOT NULL OR mp.githubUrl IS NOT NULL OR mp.portfolioUrl IS NOT NULL")
    List<MentorProfile> findMentorsWithSocialProfiles();

    /**
     * Find mentors needing verification
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.isVerified = false")
    List<MentorProfile> findMentorsNeedingVerification();

    /**
     * Find popular mentors (high review count)
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.totalReviews > 0 ORDER BY mp.totalReviews DESC, mp.averageRating DESC")
    List<MentorProfile> findPopularMentors();

    /**
     * Find mentors at capacity
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE mp.currentMentees >= mp.maxMentees")
    List<MentorProfile> findMentorsAtCapacity();

    /**
     * Check if user has mentor profile
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find mentors by multiple criteria
     */
    @Query("SELECT mp FROM MentorProfile mp WHERE " +
           "(:expertise IS NULL OR :expertise MEMBER OF mp.expertiseAreas) AND " +
           "(:industry IS NULL OR :industry MEMBER OF mp.industries) AND " +
           "(:minRating IS NULL OR mp.averageRating >= :minRating) AND " +
           "(:maxRate IS NULL OR mp.hourlyRate <= :maxRate) AND " +
           "(:availableOnly = false OR (mp.isAvailable = true AND mp.currentMentees < mp.maxMentees))")
    List<MentorProfile> findMentorsByCriteria(@Param("expertise") String expertise,
                                             @Param("industry") String industry,
                                             @Param("minRating") Double minRating,
                                             @Param("maxRate") Double maxRate,
                                             @Param("availableOnly") boolean availableOnly);

    /**
     * Find mentor profile by mentor ID (user ID)
     */
    Optional<MentorProfile> findByMentorId(UUID mentorId);
}
