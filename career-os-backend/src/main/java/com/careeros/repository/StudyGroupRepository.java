package com.careeros.repository;

import com.careeros.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for StudyGroup entity
 */
@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, UUID> {

    /**
     * Find study groups by creator ID
     */
    List<StudyGroup> findByCreatorId(UUID creatorId);

    /**
     * Find public study groups
     */
    List<StudyGroup> findByIsPrivateFalse();

    /**
     * Find active study groups
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.status = 'ACTIVE'")
    List<StudyGroup> findActiveStudyGroups();

    /**
     * Find study groups by skill focus
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE :skill MEMBER OF sg.skillsFocus")
    List<StudyGroup> findBySkillsFocus(@Param("skill") String skill);

    /**
     * Find study groups by multiple skills
     */
    @Query("SELECT DISTINCT sg FROM StudyGroup sg WHERE EXISTS (SELECT s FROM sg.skillsFocus s WHERE s IN :skills)")
    List<StudyGroup> findBySkillsFocusIn(@Param("skills") List<String> skills);

    /**
     * Search study groups by name or description
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE LOWER(sg.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(sg.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StudyGroup> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find study groups with available spots
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.currentMembers < sg.maxMembers")
    List<StudyGroup> findGroupsWithAvailableSpots();

    /**
     * Find popular study groups (by member count)
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.status = 'ACTIVE' ORDER BY sg.currentMembers DESC")
    List<StudyGroup> findPopularStudyGroups();

    /**
     * Find recently created study groups
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.createdAt >= :since ORDER BY sg.createdAt DESC")
    List<StudyGroup> findRecentStudyGroups(@Param("since") LocalDateTime since);

    /**
     * Find study groups by privacy setting
     */
    List<StudyGroup> findByIsPrivate(boolean isPrivate);

    /**
     * Find study groups by status
     */
    List<StudyGroup> findByStatus(StudyGroup.GroupStatus status);

    /**
     * Count study groups by creator
     */
    Long countByCreatorId(UUID creatorId);

    /**
     * Find study groups with minimum member count
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.currentMembers >= :minMembers")
    List<StudyGroup> findGroupsWithMinMembers(@Param("minMembers") Integer minMembers);

    /**
     * Find study groups by date range
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.createdAt BETWEEN :startDate AND :endDate")
    List<StudyGroup> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get study group statistics
     */
    @Query("SELECT COUNT(sg) as totalGroups, " +
           "AVG(sg.currentMembers) as avgMembers, " +
           "MAX(sg.currentMembers) as maxMembers, " +
           "COUNT(CASE WHEN sg.status = 'ACTIVE' THEN 1 END) as activeGroups " +
           "FROM StudyGroup sg")
    Object[] getStudyGroupStatistics();

    /**
     * Find groups that need attention (low activity)
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.lastActivityAt < :cutoffDate AND sg.status = 'ACTIVE'")
    List<StudyGroup> findInactiveGroups(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find study groups by name containing (case insensitive)
     */
    List<StudyGroup> findByNameContainingIgnoreCase(String name);

    /**
     * Check if study group name exists
     */
    boolean existsByName(String name);

    /**
     * Find featured study groups
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.isFeatured = true AND sg.status = 'ACTIVE' ORDER BY sg.currentMembers DESC")
    List<StudyGroup> findFeaturedStudyGroups();

    /**
     * Find study groups by creator and status
     */
    List<StudyGroup> findByCreatorIdAndStatus(UUID creatorId, StudyGroup.GroupStatus status);

    /**
     * Count active study groups
     */
    @Query("SELECT COUNT(sg) FROM StudyGroup sg WHERE sg.status = 'ACTIVE'")
    Long countActiveStudyGroups();

    /**
     * Find study groups by name, status and privacy type
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE LOWER(sg.name) LIKE LOWER(CONCAT('%', :name, '%')) AND sg.status = :status AND sg.isPrivate = :isPrivate")
    List<StudyGroup> findByNameContainingIgnoreCaseAndStatusAndPrivacyType(@Param("name") String name, @Param("status") GroupStatus status, @Param("isPrivate") boolean isPrivate, org.springframework.data.domain.Pageable pageable);

    /**
     * Find study groups by category, status and privacy type
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.category = :category AND sg.status = :status AND sg.isPrivate = :isPrivate")
    List<StudyGroup> findByCategoryAndStatusAndPrivacyType(@Param("category") String category, @Param("status") GroupStatus status, @Param("isPrivate") boolean isPrivate, org.springframework.data.domain.Pageable pageable);

    /**
     * Find study groups by status and privacy type
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.status = :status AND sg.isPrivate = :isPrivate")
    List<StudyGroup> findByStatusAndPrivacyType(@Param("status") GroupStatus status, @Param("isPrivate") boolean isPrivate, org.springframework.data.domain.Pageable pageable);

    /**
     * Find featured study groups by status ordered by member count
     */
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.isFeatured = true AND sg.status = :status ORDER BY sg.currentMembers DESC")
    List<StudyGroup> findByIsFeaturedTrueAndStatusOrderByMemberCountDesc(@Param("status") GroupStatus status);
}
