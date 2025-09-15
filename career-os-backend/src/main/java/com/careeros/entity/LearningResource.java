package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Learning Resource entity representing external courses, tutorials, and learning materials
 */
@Entity
@Table(name = "learning_resources", indexes = {
    @Index(name = "idx_resource_title", columnList = "title"),
    @Index(name = "idx_resource_provider", columnList = "provider"),
    @Index(name = "idx_resource_type", columnList = "resource_type"),
    @Index(name = "idx_resource_difficulty", columnList = "difficulty_level")
})
public class LearningResource extends BaseEntity {

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 2000)
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "url", nullable = false)
    private String url;

    @Size(max = 100)
    @Column(name = "provider", nullable = false)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

    @Column(name = "language")
    private String language = "English";

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "instructor_bio", length = 1000)
    private String instructorBio;

    @Column(name = "certificate_available", nullable = false)
    private Boolean certificateAvailable = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @ElementCollection
    @CollectionTable(name = "resource_tags", joinColumns = @JoinColumn(name = "resource_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @OneToMany(mappedBy = "learningResource", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LearningProgress> learningProgress = new HashSet<>();

    @OneToMany(mappedBy = "learningResource", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ResourceReview> reviews = new HashSet<>();

    // Enums
    public enum ResourceType {
        COURSE,
        TUTORIAL,
        DOCUMENTATION,
        BOOK,
        VIDEO,
        PODCAST,
        ARTICLE,
        INTERACTIVE,
        CERTIFICATION,
        BOOTCAMP,
        WORKSHOP
    }

    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    // Constructors
    public LearningResource() {}

    public LearningResource(String title, String url, String provider, ResourceType resourceType, Skill skill) {
        this.title = title;
        this.url = url;
        this.provider = provider;
        this.resourceType = resourceType;
        this.skill = skill;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorBio() {
        return instructorBio;
    }

    public void setInstructorBio(String instructorBio) {
        this.instructorBio = instructorBio;
    }

    public Boolean getCertificateAvailable() {
        return certificateAvailable;
    }

    public void setCertificateAvailable(Boolean certificateAvailable) {
        this.certificateAvailable = certificateAvailable;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Set<LearningProgress> getLearningProgress() {
        return learningProgress;
    }

    public void setLearningProgress(Set<LearningProgress> learningProgress) {
        this.learningProgress = learningProgress;
    }

    public Set<ResourceReview> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ResourceReview> reviews) {
        this.reviews = reviews;
    }
}
