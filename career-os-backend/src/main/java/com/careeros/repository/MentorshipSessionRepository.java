package com.careeros.repository;

import com.careeros.entity.MentorshipSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for MentorshipSession entity
 */
@Repository
public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, UUID> {

    /**
     * Find sessions by mentor ID ordered by scheduled date
     */
    List<MentorshipSession> findByMentorIdOrderByScheduledAtDesc(UUID mentorId);

    /**
     * Find sessions by mentee ID ordered by scheduled date
     */
    List<MentorshipSession> findByMenteeIdOrderByScheduledAtDesc(UUID menteeId);

    /**
     * Find sessions by mentor ID or mentee ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentor.id = :userId OR ms.mentee.id = :userId ORDER BY ms.scheduledAt DESC")
    List<MentorshipSession> findByMentorIdOrMenteeId(@Param("userId") UUID mentorId, @Param("userId") UUID menteeId);

    /**
     * Find sessions by mentor ID and mentee ID
     */
    List<MentorshipSession> findByMentorIdAndMenteeId(UUID mentorId, UUID menteeId);

    /**
     * Find sessions by mentorship request ID
     */
    List<MentorshipSession> findByMentorshipRequestId(UUID mentorshipRequestId);

    /**
     * Find sessions by status
     */
    List<MentorshipSession> findByStatus(MentorshipSession.SessionStatus status);

    /**
     * Find sessions by mentor ID and status
     */
    List<MentorshipSession> findByMentorIdAndStatus(UUID mentorId, MentorshipSession.SessionStatus status);

    /**
     * Find sessions by mentee ID and status
     */
    List<MentorshipSession> findByMenteeIdAndStatus(UUID menteeId, MentorshipSession.SessionStatus status);

    /**
     * Find scheduled sessions by mentor ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.status = 'SCHEDULED' ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findScheduledSessionsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Find scheduled sessions by mentee ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.status = 'SCHEDULED' ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findScheduledSessionsByMenteeId(@Param("menteeId") UUID menteeId);

    /**
     * Find completed sessions by mentor ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.status = 'COMPLETED' ORDER BY ms.endedAt DESC")
    List<MentorshipSession> findCompletedSessionsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Find completed sessions by mentee ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.status = 'COMPLETED' ORDER BY ms.endedAt DESC")
    List<MentorshipSession> findCompletedSessionsByMenteeId(@Param("menteeId") UUID menteeId);

    /**
     * Find upcoming sessions (scheduled in the future)
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.scheduledAt > :now AND ms.status = 'SCHEDULED' ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findUpcomingSessions(@Param("now") LocalDateTime now);

    /**
     * Find upcoming sessions by mentor ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.scheduledAt > :now AND ms.status = 'SCHEDULED' ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findUpcomingSessionsByMentorId(@Param("mentorId") UUID mentorId, @Param("now") LocalDateTime now);

    /**
     * Find upcoming sessions by mentee ID
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.scheduledAt > :now AND ms.status = 'SCHEDULED' ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findUpcomingSessionsByMenteeId(@Param("menteeId") UUID menteeId, @Param("now") LocalDateTime now);

    /**
     * Find sessions by date range
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.scheduledAt BETWEEN :startDate AND :endDate ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find sessions by mentor ID and date range
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.scheduledAt BETWEEN :startDate AND :endDate ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findByMentorIdAndDateRange(@Param("mentorId") UUID mentorId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find sessions by mentee ID and date range
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.scheduledAt BETWEEN :startDate AND :endDate ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findByMenteeIdAndDateRange(@Param("menteeId") UUID menteeId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find overdue sessions (should have started but didn't)
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.scheduledAt < :now AND ms.status = 'SCHEDULED'")
    List<MentorshipSession> findOverdueSessions(@Param("now") LocalDateTime now);

    /**
     * Find recurring sessions
     */
    List<MentorshipSession> findByIsRecurringTrue();

    /**
     * Find sessions with ratings
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.mentorRating IS NOT NULL OR ms.menteeRating IS NOT NULL")
    List<MentorshipSession> findSessionsWithRatings();

    /**
     * Find sessions without ratings (completed but not rated)
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.status = 'COMPLETED' AND ms.mentorRating IS NULL AND ms.menteeRating IS NULL")
    List<MentorshipSession> findUnratedCompletedSessions();

    /**
     * Count sessions by mentor ID and status
     */
    Long countByMentorIdAndStatus(UUID mentorId, MentorshipSession.SessionStatus status);

    /**
     * Count sessions by mentee ID and status
     */
    Long countByMenteeIdAndStatus(UUID menteeId, MentorshipSession.SessionStatus status);

    /**
     * Count completed sessions by mentor ID
     */
    @Query("SELECT COUNT(ms) FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.status = 'COMPLETED'")
    Long countCompletedSessionsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Count completed sessions by mentee ID
     */
    @Query("SELECT COUNT(ms) FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.status = 'COMPLETED'")
    Long countCompletedSessionsByMenteeId(@Param("menteeId") UUID menteeId);

    /**
     * Get total mentoring time by mentor ID (in minutes)
     */
    @Query("SELECT SUM(ms.durationMinutes) FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.status = 'COMPLETED' AND ms.durationMinutes IS NOT NULL")
    Long getTotalMentoringTimeByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Get total mentoring time by mentee ID (in minutes)
     */
    @Query("SELECT SUM(ms.durationMinutes) FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.status = 'COMPLETED' AND ms.durationMinutes IS NOT NULL")
    Long getTotalMentoringTimeByMenteeId(@Param("menteeId") UUID menteeId);

    /**
     * Get average rating for mentor
     */
    @Query("SELECT AVG(ms.menteeRating) FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId AND ms.menteeRating IS NOT NULL")
    Double getAverageRatingForMentor(@Param("mentorId") UUID mentorId);

    /**
     * Get average rating for mentee
     */
    @Query("SELECT AVG(ms.mentorRating) FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId AND ms.mentorRating IS NOT NULL")
    Double getAverageRatingForMentee(@Param("menteeId") UUID menteeId);

    /**
     * Get session statistics for mentor
     */
    @Query("SELECT COUNT(ms) as totalSessions, " +
           "COUNT(CASE WHEN ms.status = 'COMPLETED' THEN 1 END) as completedSessions, " +
           "COUNT(CASE WHEN ms.status = 'CANCELLED' THEN 1 END) as cancelledSessions, " +
           "COUNT(CASE WHEN ms.status = 'NO_SHOW' THEN 1 END) as noShowSessions, " +
           "AVG(ms.menteeRating) as avgRating, " +
           "SUM(ms.durationMinutes) as totalMinutes " +
           "FROM MentorshipSession ms WHERE ms.mentor.id = :mentorId")
    Object[] getMentorSessionStatistics(@Param("mentorId") UUID mentorId);

    /**
     * Get session statistics for mentee
     */
    @Query("SELECT COUNT(ms) as totalSessions, " +
           "COUNT(CASE WHEN ms.status = 'COMPLETED' THEN 1 END) as completedSessions, " +
           "COUNT(CASE WHEN ms.status = 'CANCELLED' THEN 1 END) as cancelledSessions, " +
           "COUNT(CASE WHEN ms.status = 'NO_SHOW' THEN 1 END) as noShowSessions, " +
           "AVG(ms.mentorRating) as avgRating, " +
           "SUM(ms.durationMinutes) as totalMinutes " +
           "FROM MentorshipSession ms WHERE ms.mentee.id = :menteeId")
    Object[] getMenteeSessionStatistics(@Param("menteeId") UUID menteeId);

    /**
     * Find sessions starting soon (within next hour)
     */
    @Query("SELECT ms FROM MentorshipSession ms WHERE ms.scheduledAt BETWEEN :now AND :oneHourLater AND ms.status = 'SCHEDULED' ORDER BY ms.scheduledAt ASC")
    List<MentorshipSession> findSessionsStartingSoon(@Param("now") LocalDateTime now, @Param("oneHourLater") LocalDateTime oneHourLater);

    /**
     * Find most active mentor-mentee pairs
     */
    @Query("SELECT ms.mentor.id, ms.mentee.id, COUNT(ms) as sessionCount FROM MentorshipSession ms WHERE ms.status = 'COMPLETED' GROUP BY ms.mentor.id, ms.mentee.id ORDER BY sessionCount DESC")
    List<Object[]> findMostActiveMentorMenteePairs();

    /**
     * Delete sessions by mentor ID
     */
    void deleteByMentorId(UUID mentorId);

    /**
     * Delete sessions by mentee ID
     */
    void deleteByMenteeId(UUID menteeId);

    /**
     * Find sessions by mentor ID ordered by scheduled time
     */
    List<MentorshipSession> findByMentorIdOrderByScheduledTimeDesc(UUID mentorId);
}
