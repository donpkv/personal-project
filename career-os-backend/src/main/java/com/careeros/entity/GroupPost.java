package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Group Post entity for social learning discussions
 */
@Entity
@Table(name = "group_posts", indexes = {
    @Index(name = "idx_post_group", columnList = "study_group_id"),
    @Index(name = "idx_post_author", columnList = "author_id"),
    @Index(name = "idx_post_type", columnList = "post_type"),
    @Index(name = "idx_post_status", columnList = "status")
})
public class GroupPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title")
    private String title;

    @NotBlank
    @Size(max = 5000)
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType = PostType.DISCUSSION;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status = PostStatus.PUBLISHED;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount = 0;

    @Column(name = "views_count", nullable = false)
    private Integer viewsCount = 0;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    @ElementCollection
    @CollectionTable(name = "group_post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "group_post_attachments", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "attachment_url")
    private Set<String> attachmentUrls = new HashSet<>();

    @Column(name = "code_snippet", length = 10000)
    private String codeSnippet;

    @Column(name = "code_language")
    private String codeLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_resource_id")
    private LearningResource sharedResource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_id")
    private GroupPost parentPost; // For replies/threads

    @OneToMany(mappedBy = "parentPost", cascade = CascadeType.ALL)
    private Set<GroupPost> replies = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostView> views = new HashSet<>();

    // Enums
    public enum PostType {
        DISCUSSION,      // General discussion
        QUESTION,        // Question seeking help
        ANSWER,          // Answer to a question
        RESOURCE_SHARE,  // Sharing learning resources
        PROJECT_SHARE,   // Sharing projects
        ACHIEVEMENT,     // Celebrating achievements
        STUDY_NOTE,      // Study notes and summaries
        CODE_REVIEW,     // Code review requests
        ANNOUNCEMENT,    // Important announcements
        POLL,           // Polls and surveys
        EVENT           // Event announcements
    }

    public enum PostStatus {
        DRAFT,
        PUBLISHED,
        MODERATED,
        HIDDEN,
        DELETED
    }

    // Constructors
    public GroupPost() {}

    public GroupPost(StudyGroup studyGroup, User author, String content, PostType postType) {
        this.studyGroup = studyGroup;
        this.author = author;
        this.content = content;
        this.postType = postType;
    }

    // Getters and Setters
    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getAttachmentUrls() {
        return attachmentUrls;
    }

    public void setAttachmentUrls(Set<String> attachmentUrls) {
        this.attachmentUrls = attachmentUrls;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public String getCodeLanguage() {
        return codeLanguage;
    }

    public void setCodeLanguage(String codeLanguage) {
        this.codeLanguage = codeLanguage;
    }

    public LearningResource getSharedResource() {
        return sharedResource;
    }

    public void setSharedResource(LearningResource sharedResource) {
        this.sharedResource = sharedResource;
    }

    public GroupPost getParentPost() {
        return parentPost;
    }

    public void setParentPost(GroupPost parentPost) {
        this.parentPost = parentPost;
    }

    public Set<GroupPost> getReplies() {
        return replies;
    }

    public void setReplies(Set<GroupPost> replies) {
        this.replies = replies;
    }

    public Set<PostComment> getComments() {
        return comments;
    }

    public void setComments(Set<PostComment> comments) {
        this.comments = comments;
    }

    public Set<PostLike> getLikes() {
        return likes;
    }

    public void setLikes(Set<PostLike> likes) {
        this.likes = likes;
    }

    public Set<PostView> getViews() {
        return views;
    }

    public void setViews(Set<PostView> views) {
        this.views = views;
    }

    // Helper methods
    public void incrementLikesCount() {
        this.likesCount++;
    }

    public void decrementLikesCount() {
        if (this.likesCount > 0) {
            this.likesCount--;
        }
    }

    public void incrementCommentsCount() {
        this.commentsCount++;
    }

    public void decrementCommentsCount() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }

    public void incrementViewsCount() {
        this.viewsCount++;
    }

    public boolean isPublished() {
        return status == PostStatus.PUBLISHED;
    }

    public boolean isQuestion() {
        return postType == PostType.QUESTION;
    }

    public boolean isAnswer() {
        return postType == PostType.ANSWER;
    }

    public boolean hasCode() {
        return codeSnippet != null && !codeSnippet.trim().isEmpty();
    }

    public boolean hasAttachments() {
        return !attachmentUrls.isEmpty();
    }

    public boolean isThread() {
        return parentPost != null;
    }

    public int getTotalEngagement() {
        return likesCount + commentsCount + (viewsCount / 10); // Views count less
    }
}
