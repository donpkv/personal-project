package com.careeros.repository;

import com.careeros.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for PostComment entity
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {

    /**
     * Find comments by post ID ordered by created date
     */
    List<PostComment> findByPostIdOrderByCreatedAtAsc(UUID postId);

    /**
     * Find comments by author ID ordered by created date
     */
    List<PostComment> findByAuthorIdOrderByCreatedAtDesc(UUID authorId);

    /**
     * Find comments by post ID and author ID
     */
    List<PostComment> findByPostIdAndAuthorId(UUID postId, UUID authorId);

    /**
     * Find top-level comments (no parent) by post ID
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.parentComment IS NULL ORDER BY pc.createdAt ASC")
    List<PostComment> findTopLevelCommentsByPostId(@Param("postId") UUID postId);

    /**
     * Find replies to a comment
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.parentComment.id = :parentId ORDER BY pc.createdAt ASC")
    List<PostComment> findRepliesByParentId(@Param("parentId") UUID parentId);

    /**
     * Find recent comments by post ID
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.createdAt >= :since ORDER BY pc.createdAt DESC")
    List<PostComment> findRecentCommentsByPostId(@Param("postId") UUID postId, @Param("since") LocalDateTime since);

    /**
     * Find popular comments (by likes count)
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.likesCount DESC, pc.createdAt ASC")
    List<PostComment> findPopularCommentsByPostId(@Param("postId") UUID postId);

    /**
     * Search comments by content
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND LOWER(pc.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PostComment> searchCommentsInPost(@Param("postId") UUID postId, @Param("keyword") String keyword);

    /**
     * Find comments by date range
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.createdAt BETWEEN :startDate AND :endDate ORDER BY pc.createdAt ASC")
    List<PostComment> findCommentsByPostIdAndDateRange(@Param("postId") UUID postId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Count comments by post ID
     */
    Long countByPostId(UUID postId);

    /**
     * Count comments by author ID
     */
    Long countByAuthorId(UUID authorId);

    /**
     * Count top-level comments by post ID
     */
    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.id = :postId AND pc.parentComment IS NULL")
    Long countTopLevelCommentsByPostId(@Param("postId") UUID postId);

    /**
     * Count replies to a comment
     */
    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.parentComment.id = :parentId")
    Long countRepliesByParentId(@Param("parentId") UUID parentId);

    /**
     * Find comments with minimum likes
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.likesCount >= :minLikes ORDER BY pc.likesCount DESC")
    List<PostComment> findCommentsWithMinLikesByPostId(@Param("postId") UUID postId, @Param("minLikes") Integer minLikes);

    /**
     * Get comment statistics for a post
     */
    @Query("SELECT COUNT(pc) as totalComments, " +
           "COUNT(CASE WHEN pc.parentComment IS NULL THEN 1 END) as topLevelComments, " +
           "COUNT(CASE WHEN pc.parentComment IS NOT NULL THEN 1 END) as replies, " +
           "SUM(pc.likesCount) as totalLikes, " +
           "AVG(pc.likesCount) as avgLikes " +
           "FROM PostComment pc WHERE pc.post.id = :postId")
    Object[] getPostCommentStatistics(@Param("postId") UUID postId);

    /**
     * Find most active commenters on a post
     */
    @Query("SELECT pc.author.id, COUNT(pc) as commentCount FROM PostComment pc WHERE pc.post.id = :postId GROUP BY pc.author.id ORDER BY commentCount DESC")
    List<Object[]> findMostActiveCommentersByPostId(@Param("postId") UUID postId);

    /**
     * Find comments that need moderation
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.flaggedCount > 0")
    List<PostComment> findFlaggedCommentsByPostId(@Param("postId") UUID postId);

    /**
     * Find comments by study group (through post)
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.studyGroup.id = :groupId ORDER BY pc.createdAt DESC")
    List<PostComment> findCommentsByStudyGroupId(@Param("groupId") UUID groupId);

    /**
     * Find recent comments by study group
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.studyGroup.id = :groupId AND pc.createdAt >= :since ORDER BY pc.createdAt DESC")
    List<PostComment> findRecentCommentsByStudyGroupId(@Param("groupId") UUID groupId, @Param("since") LocalDateTime since);

    /**
     * Count comments by study group
     */
    @Query("SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.studyGroup.id = :groupId")
    Long countCommentsByStudyGroupId(@Param("groupId") UUID groupId);

    /**
     * Find comments by author in a specific study group
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.author.id = :authorId AND pc.post.studyGroup.id = :groupId ORDER BY pc.createdAt DESC")
    List<PostComment> findCommentsByAuthorIdAndStudyGroupId(@Param("authorId") UUID authorId, @Param("groupId") UUID groupId);

    /**
     * Find comment thread (comment and all its replies)
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.id = :commentId OR pc.parentComment.id = :commentId ORDER BY pc.createdAt ASC")
    List<PostComment> findCommentThread(@Param("commentId") UUID commentId);

    /**
     * Find nested replies (replies to replies)
     */
    @Query(value = "WITH RECURSIVE comment_tree AS (" +
           "SELECT id, content, author_id, post_id, parent_comment_id, created_at, 0 as depth " +
           "FROM post_comments WHERE id = :rootCommentId " +
           "UNION ALL " +
           "SELECT pc.id, pc.content, pc.author_id, pc.post_id, pc.parent_comment_id, pc.created_at, ct.depth + 1 " +
           "FROM post_comments pc " +
           "INNER JOIN comment_tree ct ON pc.parent_comment_id = ct.id " +
           "WHERE ct.depth < 10" +
           ") SELECT * FROM comment_tree ORDER BY created_at ASC", nativeQuery = true)
    List<Object[]> findNestedReplies(@Param("rootCommentId") UUID rootCommentId);

    /**
     * Delete comments by author ID
     */
    void deleteByAuthorId(UUID authorId);

    /**
     * Delete comments by post ID
     */
    void deleteByPostId(UUID postId);

    /**
     * Find comments with no replies
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND NOT EXISTS (SELECT r FROM PostComment r WHERE r.parentComment.id = pc.id)")
    List<PostComment> findCommentsWithNoRepliesByPostId(@Param("postId") UUID postId);

    /**
     * Find latest comment on a post
     */
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.createdAt DESC")
    List<PostComment> findLatestCommentByPostId(@Param("postId") UUID postId);
}
