package com.careeros.repository;

import com.careeros.entity.MentorshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for MentorshipRequest entity
 */
@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, UUID> {

    /**
     * Find requests by mentee ID ordered by requested date
     */
    List<MentorshipRequest> findByMenteeIdOrderByRequestedAtDesc(UUID menteeId);

    /**
     * Find requests by mentor ID ordered by requested date
     */
    List<MentorshipRequest> findByMentorIdOrderByRequestedAtDesc(UUID mentorId);

    /**
     * Find requests by mentor profile ID
     */
    List<MentorshipRequest> findByMentorProfileId(UUID mentorProfileId);

    /**
     * Find requests by status
     */
    List<MentorshipRequest> findByStatus(MentorshipRequest.RequestStatus status);

    /**
     * Find requests by mentee ID and status
     */
    List<MentorshipRequest> findByMenteeIdAndStatus(UUID menteeId, MentorshipRequest.RequestStatus status);

    /**
     * Find requests by mentor ID and status
     */
    List<MentorshipRequest> findByMentorIdAndStatus(UUID mentorId, MentorshipRequest.RequestStatus status);

    /**
     * Find pending requests by mentor ID
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.mentor.id = :mentorId AND mr.status = 'PENDING' ORDER BY mr.requestedAt ASC")
    List<MentorshipRequest> findPendingRequestsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Find pending requests by mentee ID
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.mentee.id = :menteeId AND mr.status = 'PENDING' ORDER BY mr.requestedAt DESC")
    List<MentorshipRequest> findPendingRequestsByMenteeId(@Param("menteeId") UUID menteeId);

    /**
     * Find accepted requests by mentor ID
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.mentor.id = :mentorId AND mr.status = 'ACCEPTED' ORDER BY mr.respondedAt DESC")
    List<MentorshipRequest> findAcceptedRequestsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Find accepted requests by mentee ID
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.mentee.id = :menteeId AND mr.status = 'ACCEPTED' ORDER BY mr.respondedAt DESC")
    List<MentorshipRequest> findAcceptedRequestsByMenteeId(@Param("menteeId") UUID menteeId);

    /**
     * Find requests by date range
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.requestedAt BETWEEN :startDate AND :endDate ORDER BY mr.requestedAt DESC")
    List<MentorshipRequest> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find expired requests (pending too long)
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.status = 'PENDING' AND mr.requestedAt < :cutoffDate")
    List<MentorshipRequest> findExpiredRequests(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find recent requests by mentor ID
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.mentor.id = :mentorId AND mr.requestedAt >= :since ORDER BY mr.requestedAt DESC")
    List<MentorshipRequest> findRecentRequestsByMentorId(@Param("mentorId") UUID mentorId, @Param("since") LocalDateTime since);

    /**
     * Find recent requests by mentee ID
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.mentee.id = :menteeId AND mr.requestedAt >= :since ORDER BY mr.requestedAt DESC")
    List<MentorshipRequest> findRecentRequestsByMenteeId(@Param("menteeId") UUID menteeId, @Param("since") LocalDateTime since);

    /**
     * Count requests by mentee ID and status
     */
    Long countByMenteeIdAndStatus(UUID menteeId, MentorshipRequest.RequestStatus status);

    /**
     * Count requests by mentor ID and status
     */
    Long countByMentorIdAndStatus(UUID mentorId, MentorshipRequest.RequestStatus status);

    /**
     * Count pending requests by mentor ID
     */
    @Query("SELECT COUNT(mr) FROM MentorshipRequest mr WHERE mr.mentor.id = :mentorId AND mr.status = 'PENDING'")
    Long countPendingRequestsByMentorId(@Param("mentorId") UUID mentorId);

    /**
     * Count total requests by mentor ID
     */
    Long countByMentorId(UUID mentorId);

    /**
     * Count total requests by mentee ID
     */
    Long countByMenteeId(UUID menteeId);

    /**
     * Find requests with high compatibility scores
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.compatibilityScore >= :minScore ORDER BY mr.compatibilityScore DESC")
    List<MentorshipRequest> findHighCompatibilityRequests(@Param("minScore") Double minScore);

    /**
     * Get request statistics for a mentor
     */
    @Query("SELECT COUNT(mr) as totalRequests, " +
           "COUNT(CASE WHEN mr.status = 'PENDING' THEN 1 END) as pendingRequests, " +
           "COUNT(CASE WHEN mr.status = 'ACCEPTED' THEN 1 END) as acceptedRequests, " +
           "COUNT(CASE WHEN mr.status = 'DECLINED' THEN 1 END) as declinedRequests, " +
           "AVG(mr.compatibilityScore) as avgCompatibilityScore " +
           "FROM MentorshipRequest mr WHERE mr.mentor.id = :mentorId")
    Object[] getMentorRequestStatistics(@Param("mentorId") UUID mentorId);

    /**
     * Get request statistics for a mentee
     */
    @Query("SELECT COUNT(mr) as totalRequests, " +
           "COUNT(CASE WHEN mr.status = 'PENDING' THEN 1 END) as pendingRequests, " +
           "COUNT(CASE WHEN mr.status = 'ACCEPTED' THEN 1 END) as acceptedRequests, " +
           "COUNT(CASE WHEN mr.status = 'DECLINED' THEN 1 END) as declinedRequests, " +
           "AVG(mr.compatibilityScore) as avgCompatibilityScore " +
           "FROM MentorshipRequest mr WHERE mr.mentee.id = :menteeId")
    Object[] getMenteeRequestStatistics(@Param("menteeId") UUID menteeId);

    /**
     * Find requests between specific mentor and mentee
     */
    List<MentorshipRequest> findByMentorIdAndMenteeId(UUID mentorId, UUID menteeId);

    /**
     * Find requests that need response (pending for certain time)
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.status = 'PENDING' AND mr.requestedAt <= :responseDeadline ORDER BY mr.requestedAt ASC")
    List<MentorshipRequest> findRequestsNeedingResponse(@Param("responseDeadline") LocalDateTime responseDeadline);

    /**
     * Find most requested mentors
     */
    @Query("SELECT mr.mentor.id, COUNT(mr) as requestCount FROM MentorshipRequest mr GROUP BY mr.mentor.id ORDER BY requestCount DESC")
    List<Object[]> findMostRequestedMentors();

    /**
     * Find most active mentees (sending most requests)
     */
    @Query("SELECT mr.mentee.id, COUNT(mr) as requestCount FROM MentorshipRequest mr GROUP BY mr.mentee.id ORDER BY requestCount DESC")
    List<Object[]> findMostActiveMentees();

    /**
     * Find successful mentor-mentee pairs (accepted requests)
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.status = 'ACCEPTED' ORDER BY mr.respondedAt DESC")
    List<MentorshipRequest> findSuccessfulMatches();

    /**
     * Check if mentorship request exists between mentor and mentee
     */
    @Query("SELECT CASE WHEN COUNT(mr) > 0 THEN true ELSE false END FROM MentorshipRequest mr WHERE mr.mentor.id = :mentorId AND mr.mentee.id = :menteeId AND mr.status IN ('PENDING', 'ACCEPTED')")
    boolean existsActiveMentorshipRequest(@Param("mentorId") UUID mentorId, @Param("menteeId") UUID menteeId);

    /**
     * Find requests by duration weeks
     */
    List<MentorshipRequest> findByDurationWeeks(Integer durationWeeks);

    /**
     * Find requests by duration range
     */
    @Query("SELECT mr FROM MentorshipRequest mr WHERE mr.durationWeeks BETWEEN :minWeeks AND :maxWeeks")
    List<MentorshipRequest> findByDurationRange(@Param("minWeeks") Integer minWeeks, @Param("maxWeeks") Integer maxWeeks);

    /**
     * Delete requests by mentee ID
     */
    void deleteByMenteeId(UUID menteeId);

    /**
     * Delete requests by mentor ID
     */
    void deleteByMentorId(UUID mentorId);
}
