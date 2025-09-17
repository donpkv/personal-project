package com.careeros.repository;

import com.careeros.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repository interface for JobPosting entity
 */
@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> {

    /**
     * Find jobs by status
     */
    List<JobPosting> findByStatus(JobPosting.JobStatus status);

    /**
     * Find active jobs
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.status = 'ACTIVE' ORDER BY jp.postedDate DESC")
    List<JobPosting> findActiveJobs();

    /**
     * Find jobs by location
     */
    List<JobPosting> findByLocationContainingIgnoreCase(String location);

    /**
     * Find remote jobs
     */
    List<JobPosting> findByIsRemoteTrue();

    /**
     * Find jobs by company
     */
    List<JobPosting> findByCompanyContainingIgnoreCase(String company);

    /**
     * Find jobs by title
     */
    List<JobPosting> findByTitleContainingIgnoreCase(String title);

    /**
     * Find jobs by experience level
     */
    List<JobPosting> findByExperienceLevel(String experienceLevel);

    /**
     * Find jobs by industry
     */
    List<JobPosting> findByIndustry(String industry);

    /**
     * Find jobs by job type
     */
    List<JobPosting> findByJobType(String jobType);

    /**
     * Find jobs by required skill
     */
    @Query("SELECT jp FROM JobPosting jp WHERE :skill MEMBER OF jp.requiredSkills")
    List<JobPosting> findByRequiredSkill(@Param("skill") String skill);

    /**
     * Find jobs by multiple skills
     */
    @Query("SELECT DISTINCT jp FROM JobPosting jp WHERE EXISTS (SELECT s FROM jp.requiredSkills s WHERE s IN :skills)")
    List<JobPosting> findJobsBySkillsAndLocation(@Param("skills") List<String> skills, 
                                                @Param("location") String location, 
                                                @Param("remoteOnly") Boolean remoteOnly);

    /**
     * Find jobs by salary range
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.minSalary >= :minSalary AND jp.maxSalary <= :maxSalary")
    List<JobPosting> findBySalaryRange(@Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);

    /**
     * Find recent jobs (posted within last N days)
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.postedDate >= :since AND jp.status = 'ACTIVE' ORDER BY jp.postedDate DESC")
    List<JobPosting> findRecentJobs(@Param("since") LocalDateTime since);

    /**
     * Find featured jobs
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.isFeatured = true AND jp.status = 'ACTIVE' ORDER BY jp.postedDate DESC")
    List<JobPosting> findFeaturedJobs();

    /**
     * Search jobs by keywords
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.status = 'ACTIVE' AND (" +
           "LOWER(jp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.requirements) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobPosting> searchJobsByKeyword(@Param("keyword") String keyword);

    /**
     * Find jobs by source
     */
    List<JobPosting> findBySource(String source);

    /**
     * Find jobs posted by date range
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.postedDate BETWEEN :startDate AND :endDate AND jp.status = 'ACTIVE' ORDER BY jp.postedDate DESC")
    List<JobPosting> findByPostedDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find jobs expiring soon
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.applicationDeadline BETWEEN :now AND :soonDate AND jp.status = 'ACTIVE' ORDER BY jp.applicationDeadline ASC")
    List<JobPosting> findJobsExpiringSoon(@Param("now") LocalDateTime now, @Param("soonDate") LocalDateTime soonDate);

    /**
     * Find expired jobs
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.applicationDeadline < :now AND jp.status = 'ACTIVE'")
    List<JobPosting> findExpiredJobs(@Param("now") LocalDateTime now);

    /**
     * Count jobs by location and title
     */
    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.status = 'ACTIVE'")
    Integer countJobsByLocationAndTitle(@Param("location") String location, @Param("title") String title);

    /**
     * Count recent jobs by location and title
     */
    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.status = 'ACTIVE' AND jp.postedDate >= :since")
    Integer countRecentJobsByLocationAndTitle(@Param("location") String location, @Param("title") String title, @Param("since") LocalDateTime since);

    /**
     * Get average salary by location and title
     */
    @Query("SELECT AVG((jp.minSalary + jp.maxSalary) / 2) FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.minSalary IS NOT NULL AND jp.maxSalary IS NOT NULL")
    Double getAverageSalaryByLocationAndTitle(@Param("location") String location, @Param("title") String title);

    /**
     * Get median salary by location and title (approximation)
     */
    @Query("SELECT AVG(salary) FROM (SELECT (jp.minSalary + jp.maxSalary) / 2 as salary FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.minSalary IS NOT NULL AND jp.maxSalary IS NOT NULL ORDER BY salary) AS sorted_salaries")
    Double getMedianSalaryByLocationAndTitle(@Param("location") String location, @Param("title") String title);

    /**
     * Get top hiring companies by location and title
     */
    @Query("SELECT jp.company FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.status = 'ACTIVE' GROUP BY jp.company ORDER BY COUNT(jp) DESC")
    List<String> getTopHiringCompanies(@Param("location") String location, @Param("title") String title, @Param("limit") Integer limit);

    /**
     * Get skill frequency for a role and location
     */
    @Query("SELECT skill as skillName, COUNT(*) as frequency FROM JobPosting jp JOIN jp.requiredSkills skill WHERE jp.title LIKE %:title% AND jp.location LIKE %:location% AND jp.status = 'ACTIVE' GROUP BY skill ORDER BY frequency DESC")
    List<Object[]> getSkillFrequencyForRoleRaw(@Param("title") String title, @Param("location") String location);

    // Helper method to convert List<Object[]> to Map<String, Integer>
    default Map<String, Integer> getSkillFrequencyForRole(String title, String location) {
        List<Object[]> results = getSkillFrequencyForRoleRaw(title, location);
        return results.stream().collect(
            java.util.stream.Collectors.toMap(
                result -> (String) result[0],
                result -> ((Number) result[1]).intValue()
            )
        );
    }

    /**
     * Get experience level distribution
     */
    @Query("SELECT jp.experienceLevel, COUNT(jp) FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.status = 'ACTIVE' GROUP BY jp.experienceLevel")
    List<Object[]> getExperienceLevelDistribution(@Param("location") String location, @Param("title") String title);

    /**
     * Find high-paying jobs (above threshold)
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.minSalary >= :minSalary AND jp.status = 'ACTIVE' ORDER BY jp.minSalary DESC")
    List<JobPosting> findHighPayingJobs(@Param("minSalary") Double minSalary);

    /**
     * Find jobs with high view count
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.viewCount >= :minViews AND jp.status = 'ACTIVE' ORDER BY jp.viewCount DESC")
    List<JobPosting> findPopularJobs(@Param("minViews") Integer minViews);

    /**
     * Get job statistics
     */
    @Query("SELECT COUNT(jp) as totalJobs, " +
           "COUNT(CASE WHEN jp.status = 'ACTIVE' THEN 1 END) as activeJobs, " +
           "COUNT(CASE WHEN jp.isRemote = true THEN 1 END) as remoteJobs, " +
           "COUNT(CASE WHEN jp.isFeatured = true THEN 1 END) as featuredJobs, " +
           "AVG((jp.minSalary + jp.maxSalary) / 2) as avgSalary, " +
           "AVG(jp.viewCount) as avgViews " +
           "FROM JobPosting jp")
    Object[] getJobStatistics();

    /**
     * Find jobs by external ID and source
     */
    JobPosting findByExternalIdAndSource(String externalId, String source);

    /**
     * Find jobs needing update (not scraped recently)
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.lastScrapedAt < :cutoffDate AND jp.status = 'ACTIVE'")
    List<JobPosting> findJobsNeedingUpdate(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find duplicate jobs (same title and company)
     */
    @Query("SELECT jp FROM JobPosting jp WHERE EXISTS (SELECT jp2 FROM JobPosting jp2 WHERE jp2.title = jp.title AND jp2.company = jp.company AND jp2.id != jp.id)")
    List<JobPosting> findDuplicateJobs();

    /**
     * Count jobs by company
     */
    Long countByCompany(String company);

    /**
     * Find top companies by job count
     */
    @Query("SELECT jp.company, COUNT(jp) as jobCount FROM JobPosting jp WHERE jp.status = 'ACTIVE' GROUP BY jp.company ORDER BY jobCount DESC")
    List<Object[]> findTopCompaniesByJobCount();

    /**
     * Find jobs with minimum compatibility score
     */
    @Query("SELECT jp FROM JobPosting jp WHERE jp.compatibilityScore >= :minScore ORDER BY jp.compatibilityScore DESC")
    List<JobPosting> findJobsWithMinCompatibilityScore(@Param("minScore") Double minScore);

    /**
     * Update view count
     */
    @Query("UPDATE JobPosting jp SET jp.viewCount = jp.viewCount + 1 WHERE jp.id = :jobId")
    void incrementViewCount(@Param("jobId") UUID jobId);

    /**
     * Find jobs by company size
     */
    List<JobPosting> findByCompanySize(String companySize);

    /**
     * Check if job exists by external ID and source
     */
    boolean existsByExternalIdAndSource(String externalId, String source);

    /**
     * Count jobs requiring specific skill in location and industry
     */
    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE :skill MEMBER OF jp.requiredSkills AND jp.location LIKE %:location% AND jp.industry = :industry AND jp.status = 'ACTIVE'")
    Integer countJobsRequiringSkill(@Param("skill") String skill, @Param("location") String location, @Param("industry") String industry);

    /**
     * Get average salary for skill in location and industry
     */
    @Query("SELECT AVG((jp.minSalary + jp.maxSalary) / 2) FROM JobPosting jp WHERE :skill MEMBER OF jp.requiredSkills AND jp.location LIKE %:location% AND jp.industry = :industry AND jp.minSalary IS NOT NULL AND jp.maxSalary IS NOT NULL")
    Double getAverageSalaryForSkill(@Param("skill") String skill, @Param("location") String location, @Param("industry") String industry);

    /**
     * Count jobs requiring skill in time period
     */
    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE :skill MEMBER OF jp.requiredSkills AND jp.location LIKE %:location% AND jp.industry = :industry AND jp.postedDate BETWEEN :startDate AND :endDate AND jp.status = 'ACTIVE'")
    Integer countJobsRequiringSkillInPeriod(@Param("skill") String skill, @Param("location") String location, @Param("industry") String industry, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get job type distribution for location and title
     */
    @Query("SELECT jp.jobType, COUNT(jp) FROM JobPosting jp WHERE jp.location LIKE %:location% AND jp.title LIKE %:title% AND jp.status = 'ACTIVE' GROUP BY jp.jobType")
    List<Object[]> getJobTypeDistribution(@Param("location") String location, @Param("title") String title);

    /**
     * Count remote jobs by title
     */
    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE jp.title LIKE %:title% AND jp.isRemote = true AND jp.status = 'ACTIVE'")
    Integer countRemoteJobsByTitle(@Param("title") String title);

    /**
     * Get higher paying roles for skill
     */
    @Query("SELECT jp.title FROM JobPosting jp WHERE :skill MEMBER OF jp.requiredSkills AND jp.minSalary IS NOT NULL ORDER BY jp.minSalary DESC")
    List<String> getHigherPayingRoles(@Param("skill") String skill);

    /**
     * Get trending roles for skills
     */
    @Query("SELECT jp.title, COUNT(jp) as jobCount FROM JobPosting jp WHERE EXISTS (SELECT s FROM jp.requiredSkills s WHERE s IN :skills) AND jp.postedDate >= :since GROUP BY jp.title ORDER BY jobCount DESC")
    List<Object[]> getTrendingRolesForSkills(@Param("skills") List<String> skills, @Param("since") LocalDateTime since);
    
    /**
     * Get top skills for a role
     */
    @Query(value = "SELECT skill, COUNT(*) as count FROM job_posting_required_skills jprs JOIN job_posting jp ON jprs.job_posting_id = jp.id WHERE jp.title ILIKE CONCAT('%', :role, '%') AND jp.is_active = true GROUP BY skill ORDER BY count DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> getTopSkillsForRole(@Param("role") String role, @Param("limit") int limit);
}
