package com.careeros.repository;

import com.careeros.entity.ResourceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ResourceReview entity
 */
@Repository
public interface ResourceReviewRepository extends JpaRepository<ResourceReview, UUID> {

    /**
     * Find reviews by resource ID ordered by created date
     */
    List<ResourceReview> findByResourceIdOrderByCreatedAtDesc(UUID resourceId);

    /**
     * Find reviews by reviewer ID ordered by created date
     */
    List<ResourceReview> findByReviewerIdOrderByCreatedAtDesc(UUID reviewerId);

    /**
     * Find review by resource ID and reviewer ID
     */
    Optional<ResourceReview> findByResourceIdAndReviewerId(UUID resourceId, UUID reviewerId);

    /**
     * Find reviews by rating
     */
    List<ResourceReview> findByRating(Integer rating);

    /**
     * Find reviews by minimum rating
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.rating >= :minRating ORDER BY rr.rating DESC, rr.createdAt DESC")
    List<ResourceReview> findByMinRating(@Param("minRating") Integer minRating);

    /**
     * Find reviews by resource ID and minimum rating
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND rr.rating >= :minRating ORDER BY rr.rating DESC, rr.createdAt DESC")
    List<ResourceReview> findByResourceIdAndMinRating(@Param("resourceId") UUID resourceId, @Param("minRating") Integer minRating);

    /**
     * Find recent reviews by resource ID
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND rr.createdAt >= :since ORDER BY rr.createdAt DESC")
    List<ResourceReview> findRecentReviewsByResourceId(@Param("resourceId") UUID resourceId, @Param("since") LocalDateTime since);

    /**
     * Find recent reviews by reviewer ID
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.reviewer.id = :reviewerId AND rr.createdAt >= :since ORDER BY rr.createdAt DESC")
    List<ResourceReview> findRecentReviewsByReviewerId(@Param("reviewerId") UUID reviewerId, @Param("since") LocalDateTime since);

    /**
     * Find reviews by date range
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.createdAt BETWEEN :startDate AND :endDate ORDER BY rr.createdAt DESC")
    List<ResourceReview> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count reviews by resource ID
     */
    Long countByResourceId(UUID resourceId);

    /**
     * Count reviews by reviewer ID
     */
    Long countByReviewerId(UUID reviewerId);

    /**
     * Count reviews by resource ID and rating
     */
    Long countByResourceIdAndRating(UUID resourceId, Integer rating);

    /**
     * Get average rating for resource
     */
    @Query("SELECT AVG(rr.rating) FROM ResourceReview rr WHERE rr.resource.id = :resourceId")
    Double getAverageRatingByResourceId(@Param("resourceId") UUID resourceId);

    /**
     * Get rating distribution for resource
     */
    @Query("SELECT rr.rating, COUNT(rr) FROM ResourceReview rr WHERE rr.resource.id = :resourceId GROUP BY rr.rating ORDER BY rr.rating DESC")
    List<Object[]> getRatingDistributionByResourceId(@Param("resourceId") UUID resourceId);

    /**
     * Find helpful reviews (with high helpful count)
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.helpfulCount >= :minHelpful ORDER BY rr.helpfulCount DESC, rr.createdAt DESC")
    List<ResourceReview> findHelpfulReviews(@Param("minHelpful") Integer minHelpful);

    /**
     * Find helpful reviews by resource ID
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND rr.helpfulCount >= :minHelpful ORDER BY rr.helpfulCount DESC, rr.createdAt DESC")
    List<ResourceReview> findHelpfulReviewsByResourceId(@Param("resourceId") UUID resourceId, @Param("minHelpful") Integer minHelpful);

    /**
     * Find reviews with comments
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.comment IS NOT NULL AND LENGTH(rr.comment) > 0 ORDER BY rr.createdAt DESC")
    List<ResourceReview> findReviewsWithComments();

    /**
     * Find reviews with comments by resource ID
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND rr.comment IS NOT NULL AND LENGTH(rr.comment) > 0 ORDER BY rr.createdAt DESC")
    List<ResourceReview> findReviewsWithCommentsByResourceId(@Param("resourceId") UUID resourceId);

    /**
     * Search reviews by comment content
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND LOWER(rr.comment) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ResourceReview> searchReviewsByComment(@Param("resourceId") UUID resourceId, @Param("keyword") String keyword);

    /**
     * Get review statistics for resource
     */
    @Query("SELECT COUNT(rr) as totalReviews, " +
           "AVG(rr.rating) as avgRating, " +
           "COUNT(CASE WHEN rr.rating = 5 THEN 1 END) as fiveStarCount, " +
           "COUNT(CASE WHEN rr.rating = 4 THEN 1 END) as fourStarCount, " +
           "COUNT(CASE WHEN rr.rating = 3 THEN 1 END) as threeStarCount, " +
           "COUNT(CASE WHEN rr.rating = 2 THEN 1 END) as twoStarCount, " +
           "COUNT(CASE WHEN rr.rating = 1 THEN 1 END) as oneStarCount, " +
           "SUM(rr.helpfulCount) as totalHelpfulVotes " +
           "FROM ResourceReview rr WHERE rr.resource.id = :resourceId")
    Object[] getResourceReviewStatistics(@Param("resourceId") UUID resourceId);

    /**
     * Find most active reviewers
     */
    @Query("SELECT rr.reviewer.id, COUNT(rr) as reviewCount FROM ResourceReview rr GROUP BY rr.reviewer.id ORDER BY reviewCount DESC")
    List<Object[]> findMostActiveReviewers();

    /**
     * Find top rated resources by average review rating
     */
    @Query("SELECT rr.resource.id, AVG(rr.rating) as avgRating, COUNT(rr) as reviewCount FROM ResourceReview rr GROUP BY rr.resource.id HAVING COUNT(rr) >= :minReviews ORDER BY avgRating DESC")
    List<Object[]> findTopRatedResources(@Param("minReviews") Long minReviews);

    /**
     * Check if user has reviewed resource
     */
    @Query("SELECT CASE WHEN COUNT(rr) > 0 THEN true ELSE false END FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND rr.reviewer.id = :reviewerId")
    boolean hasUserReviewedResource(@Param("resourceId") UUID resourceId, @Param("reviewerId") UUID reviewerId);

    /**
     * Find reviews that need moderation (flagged)
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.flaggedCount > 0 ORDER BY rr.flaggedCount DESC, rr.createdAt DESC")
    List<ResourceReview> findFlaggedReviews();

    /**
     * Find reviews by verified reviewers
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.isVerifiedPurchase = true ORDER BY rr.createdAt DESC")
    List<ResourceReview> findVerifiedReviews();

    /**
     * Find reviews by verified reviewers for resource
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.resource.id = :resourceId AND rr.isVerifiedPurchase = true ORDER BY rr.createdAt DESC")
    List<ResourceReview> findVerifiedReviewsByResourceId(@Param("resourceId") UUID resourceId);

    /**
     * Find latest review by reviewer
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.reviewer.id = :reviewerId ORDER BY rr.createdAt DESC")
    List<ResourceReview> findLatestReviewByReviewerId(@Param("reviewerId") UUID reviewerId);

    /**
     * Find reviews by rating range
     */
    @Query("SELECT rr FROM ResourceReview rr WHERE rr.rating BETWEEN :minRating AND :maxRating ORDER BY rr.rating DESC, rr.createdAt DESC")
    List<ResourceReview> findByRatingRange(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);

    /**
     * Get reviewer statistics
     */
    @Query("SELECT COUNT(rr) as totalReviews, " +
           "AVG(rr.rating) as avgRating, " +
           "COUNT(CASE WHEN rr.isVerifiedPurchase = true THEN 1 END) as verifiedReviews, " +
           "SUM(rr.helpfulCount) as totalHelpfulVotes " +
           "FROM ResourceReview rr WHERE rr.reviewer.id = :reviewerId")
    Object[] getReviewerStatistics(@Param("reviewerId") UUID reviewerId);

    /**
     * Delete reviews by reviewer ID
     */
    void deleteByReviewerId(UUID reviewerId);

    /**
     * Delete reviews by resource ID
     */
    void deleteByResourceId(UUID resourceId);
}
