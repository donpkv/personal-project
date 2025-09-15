package com.careeros.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Group Membership entity for study group participation
 */
@Entity
@Table(name = "group_memberships", indexes = {
    @Index(name = "idx_membership_group", columnList = "study_group_id"),
    @Index(name = "idx_membership_user", columnList = "user_id"),
    @Index(name = "idx_membership_role", columnList = "role"),
    @Index(name = "idx_membership_status", columnList = "status")
})
public class GroupMembership extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status = MembershipStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "contribution_score", nullable = false)
    private Integer contributionScore = 0;

    @Column(name = "posts_count", nullable = false)
    private Integer postsCount = 0;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount = 0;

    @Column(name = "likes_given", nullable = false)
    private Integer likesGiven = 0;

    @Column(name = "likes_received", nullable = false)
    private Integer likesReceived = 0;

    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted = false;

    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;

    @Column(name = "invitation_code")
    private String invitationCode;

    @Column(name = "invited_by_user_id")
    private String invitedByUserId;

    // Enums
    public enum MemberRole {
        CREATOR,     // Group creator (highest permissions)
        ADMIN,       // Can manage group settings and members
        MODERATOR,   // Can moderate content and members
        MEMBER,      // Regular member
        RESTRICTED   // Limited permissions
    }

    public enum MembershipStatus {
        ACTIVE,      // Active member
        INACTIVE,    // Temporarily inactive
        SUSPENDED,   // Suspended by admin
        BANNED,      // Permanently banned
        LEFT         // User left the group
    }

    // Constructors
    public GroupMembership() {
        this.joinedAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }

    public GroupMembership(StudyGroup studyGroup, User user, MemberRole role) {
        this();
        this.studyGroup = studyGroup;
        this.user = user;
        this.role = role;
    }

    // Getters and Setters
    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MemberRole getRole() {
        return role;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public Integer getContributionScore() {
        return contributionScore;
    }

    public void setContributionScore(Integer contributionScore) {
        this.contributionScore = contributionScore;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getLikesGiven() {
        return likesGiven;
    }

    public void setLikesGiven(Integer likesGiven) {
        this.likesGiven = likesGiven;
    }

    public Integer getLikesReceived() {
        return likesReceived;
    }

    public void setLikesReceived(Integer likesReceived) {
        this.likesReceived = likesReceived;
    }

    public Boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
    }

    public LocalDateTime getMutedUntil() {
        return mutedUntil;
    }

    public void setMutedUntil(LocalDateTime mutedUntil) {
        this.mutedUntil = mutedUntil;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getInvitedByUserId() {
        return invitedByUserId;
    }

    public void setInvitedByUserId(String invitedByUserId) {
        this.invitedByUserId = invitedByUserId;
    }

    // Helper methods
    public void incrementPostsCount() {
        this.postsCount++;
        updateContributionScore();
        updateLastActive();
    }

    public void incrementCommentsCount() {
        this.commentsCount++;
        updateContributionScore();
        updateLastActive();
    }

    public void incrementLikesGiven() {
        this.likesGiven++;
        updateLastActive();
    }

    public void incrementLikesReceived() {
        this.likesReceived++;
        updateContributionScore();
    }

    public void updateLastActive() {
        this.lastActiveAt = LocalDateTime.now();
    }

    private void updateContributionScore() {
        // Calculate contribution score based on activity
        this.contributionScore = (postsCount * 5) + (commentsCount * 2) + likesReceived;
    }

    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }

    public boolean isModerator() {
        return role == MemberRole.MODERATOR || role == MemberRole.ADMIN || role == MemberRole.CREATOR;
    }

    public boolean isAdmin() {
        return role == MemberRole.ADMIN || role == MemberRole.CREATOR;
    }

    public boolean isCreator() {
        return role == MemberRole.CREATOR;
    }

    public boolean canModerate() {
        return isModerator() && isActive();
    }

    public boolean canPost() {
        return isActive() && !isMuted();
    }

    public boolean isMuted() {
        return isMuted && (mutedUntil == null || LocalDateTime.now().isBefore(mutedUntil));
    }

    public long getDaysInGroup() {
        return java.time.Duration.between(joinedAt, LocalDateTime.now()).toDays();
    }

    public long getDaysSinceLastActive() {
        if (lastActiveAt == null) return 0;
        return java.time.Duration.between(lastActiveAt, LocalDateTime.now()).toDays();
    }
}
