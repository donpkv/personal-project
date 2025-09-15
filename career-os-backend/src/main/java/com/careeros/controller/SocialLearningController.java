package com.careeros.controller;

import com.careeros.dto.social.*;
import com.careeros.entity.*;
import com.careeros.security.UserPrincipal;
import com.careeros.service.SocialLearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Social Learning REST Controller for study groups and peer collaboration
 */
@RestController
@RequestMapping("/api/v1/social")
@Tag(name = "Social Learning", description = "Study groups and peer collaboration endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = {"http://localhost:3000", "https://career-os.com"})
public class SocialLearningController {

    private static final Logger logger = LoggerFactory.getLogger(SocialLearningController.class);

    @Autowired
    private SocialLearningService socialLearningService;

    /**
     * Create a new study group
     */
    @PostMapping("/groups")
    @Operation(summary = "Create study group", description = "Create a new study group for peer learning")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<StudyGroup> createStudyGroup(
            @Valid @RequestBody CreateStudyGroupRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Creating study group request from user: {}", userPrincipal.getId());
        
        StudyGroup group = socialLearningService.createStudyGroup(request, userPrincipal.getId());
        return ResponseEntity.ok(group);
    }

    /**
     * Join a study group
     */
    @PostMapping("/groups/{groupId}/join")
    @Operation(summary = "Join study group", description = "Join an existing study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<GroupMembership> joinStudyGroup(
            @PathVariable UUID groupId,
            @RequestBody(required = false) JoinGroupRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("User {} joining group {}", userPrincipal.getId(), groupId);
        
        String joinCode = request != null ? request.getJoinCode() : null;
        GroupMembership membership = socialLearningService.joinStudyGroup(
                groupId, userPrincipal.getId(), joinCode);
        
        return ResponseEntity.ok(membership);
    }

    /**
     * Leave a study group
     */
    @PostMapping("/groups/{groupId}/leave")
    @Operation(summary = "Leave study group", description = "Leave a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> leaveStudyGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("User {} leaving group {}", userPrincipal.getId(), groupId);
        
        socialLearningService.leaveStudyGroup(groupId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Get study groups for discovery
     */
    @GetMapping("/groups/discover")
    @Operation(summary = "Discover study groups", description = "Get study groups for discovery")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<StudyGroup>> getDiscoverGroups(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StudyGroup.GroupCategory category,
            @RequestParam(required = false) StudyGroup.PrivacyType privacy,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<StudyGroup> groups = socialLearningService.getDiscoverGroups(
                search, category, privacy, pageable);
        
        return ResponseEntity.ok(groups);
    }

    /**
     * Get user's study groups
     */
    @GetMapping("/groups/my")
    @Operation(summary = "Get my study groups", description = "Get user's joined study groups")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<List<StudyGroup>> getMyStudyGroups(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        List<StudyGroup> groups = socialLearningService.getUserStudyGroups(userPrincipal.getId());
        return ResponseEntity.ok(groups);
    }

    /**
     * Get featured study groups
     */
    @GetMapping("/groups/featured")
    @Operation(summary = "Get featured groups", description = "Get featured study groups")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<List<StudyGroup>> getFeaturedGroups() {
        
        List<StudyGroup> groups = socialLearningService.getFeaturedGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * Get group details
     */
    @GetMapping("/groups/{groupId}")
    @Operation(summary = "Get group details", description = "Get detailed information about a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<StudyGroup> getGroupDetails(@PathVariable UUID groupId) {
        
        // Implementation would fetch group details
        // For now, return placeholder response
        return ResponseEntity.ok().build();
    }

    /**
     * Create a post in a study group
     */
    @PostMapping("/groups/{groupId}/posts")
    @Operation(summary = "Create group post", description = "Create a new post in a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<GroupPost> createPost(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Creating post in group {} by user {}", groupId, userPrincipal.getId());
        
        request.setGroupId(groupId);
        GroupPost post = socialLearningService.createPost(request, userPrincipal.getId());
        
        return ResponseEntity.ok(post);
    }

    /**
     * Get posts in a study group
     */
    @GetMapping("/groups/{groupId}/posts")
    @Operation(summary = "Get group posts", description = "Get posts in a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<GroupPost>> getGroupPosts(
            @PathVariable UUID groupId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Page<GroupPost> posts = socialLearningService.getGroupPosts(
                groupId, userPrincipal.getId(), pageable);
        
        return ResponseEntity.ok(posts);
    }

    /**
     * Add comment to a post
     */
    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "Add comment", description = "Add a comment to a post")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<PostComment> addComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        logger.info("Adding comment to post {} by user {}", postId, userPrincipal.getId());
        
        request.setPostId(postId);
        PostComment comment = socialLearningService.addComment(request, userPrincipal.getId());
        
        return ResponseEntity.ok(comment);
    }

    /**
     * Like/Unlike a post
     */
    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "Toggle post like", description = "Like or unlike a post")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<LikeResponse> togglePostLike(
            @PathVariable UUID postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        boolean isLiked = socialLearningService.togglePostLike(postId, userPrincipal.getId());
        
        LikeResponse response = new LikeResponse();
        response.setIsLiked(isLiked);
        response.setMessage(isLiked ? "Post liked" : "Post unliked");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search posts within a group
     */
    @GetMapping("/groups/{groupId}/posts/search")
    @Operation(summary = "Search group posts", description = "Search posts within a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<GroupPost>> searchGroupPosts(
            @PathVariable UUID groupId,
            @RequestParam String query,
            @RequestParam(required = false) GroupPost.PostType type,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Page<GroupPost> posts = socialLearningService.searchGroupPosts(
                groupId, query, type, userPrincipal.getId(), pageable);
        
        return ResponseEntity.ok(posts);
    }

    /**
     * Get group statistics
     */
    @GetMapping("/groups/{groupId}/stats")
    @Operation(summary = "Get group stats", description = "Get statistics for a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<GroupStatsResponse> getGroupStats(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        GroupStatsResponse stats = socialLearningService.getGroupStats(groupId, userPrincipal.getId());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get trending topics in a group
     */
    @GetMapping("/groups/{groupId}/trending")
    @Operation(summary = "Get trending topics", description = "Get trending topics in a study group")
    @PreAuthorize("hasRole('USER') or hasRole('PREMIUM_USER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getTrendingTopics(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> topics = socialLearningService.getTrendingTopics(groupId, limit);
        return ResponseEntity.ok(topics);
    }
}
