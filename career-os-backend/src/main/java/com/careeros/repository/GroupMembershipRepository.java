package com.careeros.repository;

import com.careeros.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for GroupMembership entity
 */
@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

    /**
     * Find memberships by user ID
     */
    List<GroupMembership> findByUserId(UUID userId);

    /**
     * Find memberships by study group ID
     */
    List<GroupMembership> findByStudyGroupId(UUID studyGroupId);

    /**
     * Find membership by user ID and study group ID
     */
    Optional<GroupMembership> findByUserIdAndStudyGroupId(UUID userId, UUID studyGroupId);

    /**
     * Find active memberships by user ID
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.status = 'ACTIVE'")
    List<GroupMembership> findActiveMembershipsByUserId(@Param("userId") UUID userId);

    /**
     * Find active memberships by study group ID
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.status = 'ACTIVE'")
    List<GroupMembership> findActiveMembershipsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find memberships by role
     */
    List<GroupMembership> findByRole(GroupMembership.MemberRole role);

    /**
     * Find memberships by user ID and role
     */
    List<GroupMembership> findByUserIdAndRole(UUID userId, GroupMembership.MemberRole role);

    /**
     * Find memberships by study group ID and role
     */
    List<GroupMembership> findByStudyGroupIdAndRole(UUID studyGroupId, GroupMembership.MemberRole role);

    /**
     * Find memberships by status
     */
    List<GroupMembership> findByStatus(GroupMembership.MembershipStatus status);

    /**
     * Find pending memberships by study group ID
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.status = 'PENDING'")
    List<GroupMembership> findPendingMembershipsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find moderators of a study group
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.role = 'MODERATOR' AND gm.status = 'ACTIVE'")
    List<GroupMembership> findModeratorsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find admins of a study group
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.role = 'ADMIN' AND gm.status = 'ACTIVE'")
    List<GroupMembership> findAdminsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Count active members in a study group
     */
    @Query("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.status = 'ACTIVE'")
    Long countActiveMembersByGroupId(@Param("groupId") UUID groupId);

    /**
     * Count memberships by user ID
     */
    Long countByUserId(UUID userId);

    /**
     * Count active memberships by user ID
     */
    @Query("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.status = 'ACTIVE'")
    Long countActiveMembershipsByUserId(@Param("userId") UUID userId);

    /**
     * Find recent memberships by user ID
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.joinedAt >= :since ORDER BY gm.joinedAt DESC")
    List<GroupMembership> findRecentMembershipsByUserId(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    /**
     * Find recent memberships by study group ID
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.joinedAt >= :since ORDER BY gm.joinedAt DESC")
    List<GroupMembership> findRecentMembershipsByGroupId(@Param("groupId") UUID groupId, @Param("since") LocalDateTime since);

    /**
     * Find memberships by date range
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.joinedAt BETWEEN :startDate AND :endDate")
    List<GroupMembership> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Check if user is member of study group
     */
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.studyGroup.id = :groupId AND gm.status = 'ACTIVE'")
    boolean isUserMemberOfGroup(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

    /**
     * Check if user is admin or moderator of study group
     */
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMembership gm WHERE gm.user.id = :userId AND gm.studyGroup.id = :groupId AND gm.role IN ('ADMIN', 'MODERATOR') AND gm.status = 'ACTIVE'")
    boolean isUserAdminOrModeratorOfGroup(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

    /**
     * Find most active members (by last activity)
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.status = 'ACTIVE' ORDER BY gm.lastActivityAt DESC")
    List<GroupMembership> findMostActiveMembersByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find inactive members (not accessed recently)
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.status = 'ACTIVE' AND gm.lastActivityAt < :cutoffDate")
    List<GroupMembership> findInactiveMembersByGroupId(@Param("groupId") UUID groupId, @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Get membership statistics for a group
     */
    @Query("SELECT COUNT(gm) as totalMembers, " +
           "COUNT(CASE WHEN gm.status = 'ACTIVE' THEN 1 END) as activeMembers, " +
           "COUNT(CASE WHEN gm.status = 'PENDING' THEN 1 END) as pendingMembers, " +
           "COUNT(CASE WHEN gm.role = 'ADMIN' THEN 1 END) as adminCount, " +
           "COUNT(CASE WHEN gm.role = 'MODERATOR' THEN 1 END) as moderatorCount " +
           "FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId")
    Object[] getGroupMembershipStatistics(@Param("groupId") UUID groupId);

    /**
     * Find users who joined multiple groups
     */
    @Query("SELECT gm.user.id, COUNT(gm) as groupCount FROM GroupMembership gm WHERE gm.status = 'ACTIVE' GROUP BY gm.user.id HAVING COUNT(gm) > 1")
    List<Object[]> findUsersWithMultipleGroups();

    /**
     * Delete membership by user ID and study group ID
     */
    void deleteByUserIdAndStudyGroupId(UUID userId, UUID studyGroupId);

    /**
     * Find expired memberships
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.expiresAt IS NOT NULL AND gm.expiresAt < :now AND gm.status = 'ACTIVE'")
    List<GroupMembership> findExpiredMemberships(@Param("now") LocalDateTime now);

    /**
     * Find membership by study group and user
     */
    GroupMembership findByStudyGroupAndUser(com.careeros.entity.StudyGroup studyGroup, com.careeros.entity.User user);

    /**
     * Count active members in a time period
     */
    @Query("SELECT COUNT(gm) FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId AND gm.status = 'ACTIVE' AND gm.lastActivityAt >= :since")
    Long countActiveMembers(@Param("groupId") UUID groupId, @Param("since") LocalDateTime since);

    /**
     * Find memberships by user ID and status
     */
    List<GroupMembership> findByUserIdAndStatus(UUID userId, MembershipStatus status);

    /**
     * Find memberships by study group ID ordered by contribution score
     */
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.studyGroup.id = :groupId ORDER BY gm.contributionScore DESC")
    List<GroupMembership> findByStudyGroupIdOrderByContributionScoreDesc(@Param("groupId") UUID groupId);
}
