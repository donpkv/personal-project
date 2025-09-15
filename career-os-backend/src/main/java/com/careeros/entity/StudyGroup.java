package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Study Group entity for peer collaboration and social learning
 */
@Entity
@Table(name = "study_groups", indexes = {
    @Index(name = "idx_group_name", columnList = "name"),
    @Index(name = "idx_group_category", columnList = "category"),
    @Index(name = "idx_group_status", columnList = "status"),
    @Index(name = "idx_group_privacy", columnList = "privacy_type")
})
public class StudyGroup extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 2000)
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private GroupCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_type", nullable = false)
    private PrivacyType privacyType = PrivacyType.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GroupStatus status = GroupStatus.ACTIVE;

    @Column(name = "max_members")
    private Integer maxMembers = 100;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount = 1; // Creator is first member

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "join_code")
    private String joinCode;

    @ElementCollection
    @CollectionTable(name = "study_group_tags", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "study_group_rules", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "rule", length = 500)
    private Set<String> rules = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "study_group_skills",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> focusSkills = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "study_group_learning_paths",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "learning_path_id")
    )
    private Set<LearningPath> sharedLearningPaths = new HashSet<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMembership> memberships = new HashSet<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupPost> posts = new HashSet<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupEvent> events = new HashSet<>();

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupChallenge> challenges = new HashSet<>();

    // Enums
    public enum GroupCategory {
        PROGRAMMING,
        WEB_DEVELOPMENT,
        MOBILE_DEVELOPMENT,
        DATA_SCIENCE,
        AI_MACHINE_LEARNING,
        CLOUD_COMPUTING,
        CYBERSECURITY,
        DEVOPS,
        UI_UX_DESIGN,
        PROJECT_MANAGEMENT,
        BUSINESS_ANALYSIS,
        SOFT_SKILLS,
        CERTIFICATION_PREP,
        INTERVIEW_PREP,
        CAREER_TRANSITION,
        GENERAL
    }

    public enum PrivacyType {
        PUBLIC,      // Anyone can see and join
        PRIVATE,     // Invite only, not searchable
        RESTRICTED   // Searchable but requires approval
    }

    public enum GroupStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED,
        SUSPENDED
    }

    // Constructors
    public StudyGroup() {}

    public StudyGroup(String name, User creator, GroupCategory category) {
        this.name = name;
        this.creator = creator;
        this.category = category;
        generateJoinCode();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public GroupCategory getCategory() {
        return category;
    }

    public void setCategory(GroupCategory category) {
        this.category = category;
    }

    public PrivacyType getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(PrivacyType privacyType) {
        this.privacyType = privacyType;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getRules() {
        return rules;
    }

    public void setRules(Set<String> rules) {
        this.rules = rules;
    }

    public Set<Skill> getFocusSkills() {
        return focusSkills;
    }

    public void setFocusSkills(Set<Skill> focusSkills) {
        this.focusSkills = focusSkills;
    }

    public Set<LearningPath> getSharedLearningPaths() {
        return sharedLearningPaths;
    }

    public void setSharedLearningPaths(Set<LearningPath> sharedLearningPaths) {
        this.sharedLearningPaths = sharedLearningPaths;
    }

    public Set<GroupMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<GroupMembership> memberships) {
        this.memberships = memberships;
    }

    public Set<GroupPost> getPosts() {
        return posts;
    }

    public void setPosts(Set<GroupPost> posts) {
        this.posts = posts;
    }

    public Set<GroupEvent> getEvents() {
        return events;
    }

    public void setEvents(Set<GroupEvent> events) {
        this.events = events;
    }

    public Set<GroupChallenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(Set<GroupChallenge> challenges) {
        this.challenges = challenges;
    }

    // Helper methods
    public void incrementMemberCount() {
        this.memberCount++;
    }

    public void decrementMemberCount() {
        if (this.memberCount > 0) {
            this.memberCount--;
        }
    }

    public boolean isFull() {
        return maxMembers != null && memberCount >= maxMembers;
    }

    public boolean isPublic() {
        return privacyType == PrivacyType.PUBLIC;
    }

    public boolean isPrivate() {
        return privacyType == PrivacyType.PRIVATE;
    }

    public boolean canJoin() {
        return status == GroupStatus.ACTIVE && !isFull();
    }

    private void generateJoinCode() {
        this.joinCode = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void regenerateJoinCode() {
        generateJoinCode();
    }
}
