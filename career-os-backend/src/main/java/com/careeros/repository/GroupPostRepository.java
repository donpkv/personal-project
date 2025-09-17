package com.careeros.repository;

import com.careeros.entity.GroupPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for GroupPost entity
 */
@Repository
public interface GroupPostRepository extends JpaRepository<GroupPost, UUID> {

    /**
     * Find posts by study group ID ordered by created date
     */
    List<GroupPost> findByStudyGroupIdOrderByCreatedAtDesc(UUID studyGroupId);

    /**
     * Find posts by author ID ordered by created date
     */
    List<GroupPost> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);

    /**
     * Find posts by study group ID and author ID
     */
    List<GroupPost> findByStudyGroupIdAndAuthorId(UUID studyGroupId, UUID authorId);

    /**
     * Find posts by post type
     */
    List<GroupPost> findByPostType(GroupPost.PostType postType);

    /**
     * Find posts by study group ID and post type
     */
    List<GroupPost> findByStudyGroupIdAndPostType(UUID studyGroupId, GroupPost.PostType postType);

    /**
     * Find pinned posts by study group ID
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.isPinned = true ORDER BY gp.createdAt DESC")
    List<GroupPost> findPinnedPostsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find recent posts by study group ID
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.createdAt >= :since ORDER BY gp.createdAt DESC")
    List<GroupPost> findRecentPostsByGroupId(@Param("groupId") UUID groupId, @Param("since") LocalDateTime since);

    /**
     * Find popular posts (by likes count)
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId ORDER BY gp.likesCount DESC, gp.createdAt DESC")
    List<GroupPost> findPopularPostsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find posts with most comments
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId ORDER BY gp.commentsCount DESC, gp.createdAt DESC")
    List<GroupPost> findMostCommentedPostsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Search posts by content
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND (LOWER(gp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(gp.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<GroupPost> searchPostsInGroup(@Param("groupId") UUID groupId, @Param("keyword") String keyword);

    /**
     * Find posts by date range
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.createdAt BETWEEN :startDate AND :endDate ORDER BY gp.createdAt DESC")
    List<GroupPost> findPostsByGroupIdAndDateRange(@Param("groupId") UUID groupId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Count posts by study group ID
     */
    Long countByStudyGroupId(UUID studyGroupId);

    /**
     * Count posts by author ID
     */
    Long countByAuthorId(UUID authorId);

    /**
     * Count posts by study group ID and post type
     */
    Long countByStudyGroupIdAndPostType(UUID studyGroupId, GroupPost.PostType postType);

    /**
     * Find posts with attachments
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.attachmentUrl IS NOT NULL")
    List<GroupPost> findPostsWithAttachmentsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find posts without comments
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.commentsCount = 0")
    List<GroupPost> findPostsWithoutCommentsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find posts with minimum likes
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.likesCount >= :minLikes ORDER BY gp.likesCount DESC")
    List<GroupPost> findPostsWithMinLikesByGroupId(@Param("groupId") UUID groupId, @Param("minLikes") Integer minLikes);

    /**
     * Get post statistics for a group
     */
    @Query("SELECT COUNT(gp) as totalPosts, " +
           "COUNT(CASE WHEN gp.postType = 'QUESTION' THEN 1 END) as questionPosts, " +
           "COUNT(CASE WHEN gp.postType = 'DISCUSSION' THEN 1 END) as discussionPosts, " +
           "COUNT(CASE WHEN gp.postType = 'RESOURCE_SHARE' THEN 1 END) as resourcePosts, " +
           "COUNT(CASE WHEN gp.postType = 'ANNOUNCEMENT' THEN 1 END) as announcementPosts, " +
           "SUM(gp.likesCount) as totalLikes, " +
           "SUM(gp.commentsCount) as totalComments, " +
           "AVG(gp.likesCount) as avgLikes " +
           "FROM GroupPost gp WHERE gp.studyGroup.id = :groupId")
    Object[] getGroupPostStatistics(@Param("groupId") UUID groupId);

    /**
     * Find most active authors in a group
     */
    @Query("SELECT gp.author.id, COUNT(gp) as postCount FROM GroupPost gp WHERE gp.studyGroup.id = :groupId GROUP BY gp.author.id ORDER BY postCount DESC")
    List<Object[]> findMostActiveAuthorsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find trending posts (recent posts with high engagement)
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.createdAt >= :since ORDER BY (gp.likesCount + gp.commentsCount) DESC, gp.createdAt DESC")
    List<GroupPost> findTrendingPostsByGroupId(@Param("groupId") UUID groupId, @Param("since") LocalDateTime since);

    /**
     * Find unanswered questions
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.postType = 'QUESTION' AND gp.commentsCount = 0")
    List<GroupPost> findUnansweredQuestionsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find posts that need moderation
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND gp.flaggedCount > 0")
    List<GroupPost> findFlaggedPostsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Find posts by tag
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup.id = :groupId AND :tag MEMBER OF gp.tags")
    List<GroupPost> findPostsByGroupIdAndTag(@Param("groupId") UUID groupId, @Param("tag") String tag);

    /**
     * Find all unique tags in a group
     */
    @Query("SELECT DISTINCT t FROM GroupPost gp JOIN gp.tags t WHERE gp.studyGroup.id = :groupId")
    List<String> findAllTagsByGroupId(@Param("groupId") UUID groupId);

    /**
     * Delete posts by author ID
     */
    void deleteByAuthorId(UUID authorId);

    /**
     * Delete posts by study group ID
     */
    void deleteByStudyGroupId(UUID studyGroupId);

    /**
     * Find posts by study group and status ordered by created date
     */
    List<GroupPost> findByStudyGroupAndStatusOrderByCreatedAtDesc(com.careeros.entity.StudyGroup studyGroup, PostStatus status, org.springframework.data.domain.Pageable pageable);

    /**
     * Count posts by study group and status
     */
    Long countByStudyGroupAndStatus(com.careeros.entity.StudyGroup studyGroup, PostStatus status);

    /**
     * Find posts by study group, post type, title/content containing and status
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup = :studyGroup AND gp.postType = :postType AND (LOWER(gp.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(gp.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND gp.status = :status")
    List<GroupPost> findByStudyGroupAndPostTypeAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(
        @Param("studyGroup") com.careeros.entity.StudyGroup studyGroup, 
        @Param("postType") PostType postType, 
        @Param("title") String title, 
        @Param("content") String content, 
        @Param("status") PostStatus status, 
        org.springframework.data.domain.Pageable pageable);

    /**
     * Find posts by study group, title/content containing and status
     */
    @Query("SELECT gp FROM GroupPost gp WHERE gp.studyGroup = :studyGroup AND (LOWER(gp.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(gp.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND gp.status = :status")
    List<GroupPost> findByStudyGroupAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(
        @Param("studyGroup") com.careeros.entity.StudyGroup studyGroup, 
        @Param("title") String title, 
        @Param("content") String content, 
        @Param("status") PostStatus status, 
        org.springframework.data.domain.Pageable pageable);

    /**
     * Find trending tags for a group
     */
    @Query("SELECT t FROM GroupPost gp JOIN gp.tags t WHERE gp.studyGroup.id = :groupId AND gp.createdAt >= :since GROUP BY t ORDER BY COUNT(t) DESC")
    List<String> findTrendingTags(@Param("groupId") UUID groupId, @Param("since") LocalDateTime since, int limit);

    // Add enum for PostStatus if not exists
    enum PostStatus {
        ACTIVE, HIDDEN, DELETED, FLAGGED
    }

    enum PostType {
        QUESTION, DISCUSSION, RESOURCE_SHARE, ANNOUNCEMENT
    }
}
