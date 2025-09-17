package com.careeros.service;

import com.careeros.dto.social.*;
import com.careeros.entity.*;
import com.careeros.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Social Learning Service for study groups and peer collaboration
 */
@Service
@Transactional
public class SocialLearningService {

    private static final Logger logger = LoggerFactory.getLogger(SocialLearningService.class);

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMembershipRepository membershipRepository;

    @Autowired
    private GroupPostRepository postRepository;

    @Autowired
    private PostCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Create a new study group
     */
    public StudyGroup createStudyGroup(CreateStudyGroupRequest request, UUID creatorId) {
        logger.info("Creating study group '{}' by user {}", request.getName(), creatorId);

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudyGroup group = new StudyGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreator(creator);
        group.setCategory(StudyGroup.GroupCategory.valueOf(request.getCategory().toUpperCase()));
        group.setPrivacyType(StudyGroup.PrivacyType.valueOf(request.getPrivacyType().toUpperCase()));
        group.setMaxMembers(request.getMaxMembers());
        group.setCoverImageUrl(request.getCoverImageUrl());
        group.setTags(new HashSet<String>(request.getTags() != null ? request.getTags() : new ArrayList<>()));
        if (request.getRules() != null && !request.getRules().isEmpty()) {
            group.setRules(new HashSet<>(request.getRules()));
        } else {
            group.setRules(new HashSet<>());
        }

        StudyGroup savedGroup = studyGroupRepository.save(group);

        // Create membership for creator
        GroupMembership creatorMembership = new GroupMembership(
                savedGroup, creator, GroupMembership.MemberRole.CREATOR);
        membershipRepository.save(creatorMembership);

        logger.info("Study group '{}' created with ID {}", savedGroup.getName(), savedGroup.getId());
        return savedGroup;
    }

    /**
     * Join a study group
     */
    public GroupMembership joinStudyGroup(UUID groupId, UUID userId, String joinCode) {
        logger.info("User {} attempting to join group {}", userId, groupId);

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is already a member
        Optional<GroupMembership> existingMembership = 
                membershipRepository.findByStudyGroupAndUser(group, user);
        
        if (existingMembership.isPresent()) {
            if (existingMembership.get().isActive()) {
                throw new RuntimeException("User is already a member of this group");
            } else {
                // Reactivate membership
                GroupMembership membership = existingMembership.get();
                membership.setStatus(GroupMembership.MembershipStatus.ACTIVE);
                return membershipRepository.save(membership);
            }
        }

        // Validate join conditions
        if (!group.canJoin()) {
            throw new RuntimeException("Cannot join this group");
        }

        // Check privacy settings
        if (group.getPrivacyType() == StudyGroup.PrivacyType.PRIVATE) {
            if (joinCode == null || !joinCode.equals(group.getJoinCode())) {
                throw new RuntimeException("Invalid join code");
            }
        }

        // Create membership
        GroupMembership membership = new GroupMembership(group, user, GroupMembership.MemberRole.MEMBER);
        GroupMembership savedMembership = membershipRepository.save(membership);

        // Update group member count
        group.incrementMemberCount();
        studyGroupRepository.save(group);

        // Send welcome notification
        notificationService.sendGroupWelcomeNotification(user, group);

        logger.info("User {} successfully joined group {}", userId, groupId);
        return savedMembership;
    }

    /**
     * Leave a study group
     */
    public void leaveStudyGroup(UUID groupId, UUID userId) {
        logger.info("User {} leaving group {}", userId, groupId);

        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMembership membership = membershipRepository.findByStudyGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        if (membership.getRole() == GroupMembership.MemberRole.CREATOR) {
            throw new RuntimeException("Group creator cannot leave the group");
        }

        // Update membership status
        membership.setStatus(GroupMembership.MembershipStatus.LEFT);
        membershipRepository.save(membership);

        // Update group member count
        group.decrementMemberCount();
        studyGroupRepository.save(group);

        logger.info("User {} left group {}", userId, groupId);
    }

    /**
     * Create a post in a study group
     */
    public GroupPost createPost(CreatePostRequest request, UUID authorId) {
        logger.info("Creating post in group {} by user {}", request.getGroupId(), authorId);

        StudyGroup group = studyGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is a member and can post
        GroupMembership membership = membershipRepository.findByStudyGroupAndUser(group, author)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        if (!membership.canPost()) {
            throw new RuntimeException("User cannot post in this group");
        }

        GroupPost post = new GroupPost();
        post.setStudyGroup(group);
        post.setAuthor(author);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostType(GroupPost.PostType.valueOf(request.getPostType().toUpperCase()));
        post.setIsAnonymous(request.getIsAnonymous());
        post.setCodeSnippet(request.getCodeSnippet());
        post.setCodeLanguage(request.getCodeLanguage());
        post.setTags(new HashSet<>(request.getTags()));
        post.setAttachmentUrls(new HashSet<>(request.getAttachmentUrls()));

        GroupPost savedPost = postRepository.save(post);

        // Update member activity
        membership.incrementPostsCount();
        membershipRepository.save(membership);

        // Send notifications to group members
        notificationService.sendNewPostNotification(savedPost);

        logger.info("Post created with ID {}", savedPost.getId());
        return savedPost;
    }

    /**
     * Add comment to a post
     */
    public PostComment addComment(CreateCommentRequest request, UUID authorId) {
        logger.info("Adding comment to post {} by user {}", request.getPostId(), authorId);

        GroupPost post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is a member and can comment
        GroupMembership membership = membershipRepository
                .findByStudyGroupAndUser(post.getStudyGroup(), author)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        if (!membership.canPost()) {
            throw new RuntimeException("User cannot comment in this group");
        }

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(request.getContent());
        comment.setIsAnonymous(request.getIsAnonymous());

        if (request.getParentCommentId() != null) {
            PostComment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        PostComment savedComment = commentRepository.save(comment);

        // Update post comment count
        post.incrementCommentsCount();
        postRepository.save(post);

        // Update member activity
        membership.incrementCommentsCount();
        membershipRepository.save(membership);

        // Send notification to post author
        if (!post.getAuthor().getId().equals(authorId)) {
            notificationService.sendCommentNotification(savedComment);
        }

        logger.info("Comment added with ID {}", savedComment.getId());
        return savedComment;
    }

    /**
     * Get study groups for discovery
     */
    public Page<StudyGroup> getDiscoverGroups(String searchQuery, StudyGroup.GroupCategory category, 
                                             StudyGroup.PrivacyType privacyType, Pageable pageable) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return studyGroupRepository.findByNameContainingIgnoreCaseAndStatusAndPrivacyType(
                    searchQuery.trim(), StudyGroup.GroupStatus.ACTIVE, 
                    false, pageable); // false = not private (public)
        }

        if (category != null) {
            return studyGroupRepository.findByCategoryAndStatusAndPrivacyType(
                    category.name(), StudyGroup.GroupStatus.ACTIVE, false, pageable);
        }

        return studyGroupRepository.findByStatusAndPrivacyType(
                StudyGroup.GroupStatus.ACTIVE, false, pageable);
    }

    /**
     * Get user's study groups
     */
    public List<StudyGroup> getUserStudyGroups(UUID userId) {
        List<GroupMembership> memberships = membershipRepository
                .findByUserIdAndStatus(userId, GroupMembership.MembershipStatus.ACTIVE);

        return memberships.stream()
                .map(GroupMembership::getStudyGroup)
                .filter(group -> group.getStatus() == StudyGroup.GroupStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Get featured study groups
     */
    public List<StudyGroup> getFeaturedGroups() {
        return studyGroupRepository.findByIsFeaturedTrueAndStatusOrderByMemberCountDesc(
                StudyGroup.GroupStatus.ACTIVE);
    }

    /**
     * Get group posts with pagination
     */
    public Page<GroupPost> getGroupPosts(UUID groupId, UUID userId, Pageable pageable) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is a member
        membershipRepository.findByStudyGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        return postRepository.findByStudyGroupAndStatusOrderByCreatedAtDesc(
                group, com.careeros.repository.GroupPostRepository.PostStatus.ACTIVE, pageable);
    }

    /**
     * Like/Unlike a post
     */
    public boolean togglePostLike(UUID postId, UUID userId) {
        GroupPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already liked the post
        // Implementation would depend on PostLike entity
        // For now, just increment/decrement like count
        
        // This is simplified - in real implementation, you'd check PostLike entity
        boolean isLiked = false; // Check if user already liked
        
        if (isLiked) {
            post.decrementLikesCount();
        } else {
            post.incrementLikesCount();
        }
        
        postRepository.save(post);
        
        return !isLiked;
    }

    /**
     * Get group statistics
     */
    public GroupStatsResponse getGroupStats(UUID groupId, UUID userId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is a member
        GroupMembership membership = membershipRepository.findByStudyGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        GroupStatsResponse stats = new GroupStatsResponse();
        stats.setGroupId(groupId);
        stats.setMemberCount(group.getMemberCount());
        stats.setPostsCount(postRepository.countByStudyGroupAndStatus(group, com.careeros.repository.GroupPostRepository.PostStatus.ACTIVE));
        stats.setActiveMembers(membershipRepository.countActiveMembers(groupId, LocalDateTime.now().minusDays(7)));
        stats.setUserContributionScore(membership.getContributionScore());
        stats.setUserRank(calculateUserRank(groupId, userId));

        return stats;
    }

    /**
     * Search posts within a group
     */
    public Page<GroupPost> searchGroupPosts(UUID groupId, String query, GroupPost.PostType postType, 
                                          UUID userId, Pageable pageable) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Study group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is a member
        membershipRepository.findByStudyGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));

        if (postType != null) {
            return postRepository.findByStudyGroupAndPostTypeAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(
                    group, postType, query, query, com.careeros.repository.GroupPostRepository.PostStatus.ACTIVE, pageable);
        }

        return postRepository.findByStudyGroupAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(
                group, query, query, com.careeros.repository.GroupPostRepository.PostStatus.ACTIVE, pageable);
    }

    /**
     * Get trending topics in a group
     */
    public List<String> getTrendingTopics(UUID groupId, int limit) {
        // Get most used tags in recent posts
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        return postRepository.findTrendingTags(groupId, since, limit);
    }

    private int calculateUserRank(UUID groupId, UUID userId) {
        List<GroupMembership> memberships = membershipRepository
                .findByStudyGroupIdOrderByContributionScoreDesc(groupId);
        
        for (int i = 0; i < memberships.size(); i++) {
            if (memberships.get(i).getUser().getId().equals(userId)) {
                return i + 1;
            }
        }
        
        return memberships.size();
    }
}
