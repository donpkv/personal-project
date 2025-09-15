package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Skill entity representing various skills that users can learn and track
 */
@Entity
@Table(name = "skills", indexes = {
    @Index(name = "idx_skill_name", columnList = "name"),
    @Index(name = "idx_skill_category", columnList = "category")
})
public class Skill extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Size(max = 500)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SkillCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "is_trending", nullable = false)
    private Boolean isTrending = false;

    @Column(name = "demand_score")
    private Integer demandScore = 0;

    @ElementCollection
    @CollectionTable(name = "skill_keywords", joinColumns = @JoinColumn(name = "skill_id"))
    @Column(name = "keyword")
    private Set<String> keywords = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "skill_prerequisites",
        joinColumns = @JoinColumn(name = "skill_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Skill> prerequisites = new HashSet<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Set<UserSkill> userSkills = new HashSet<>();

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private Set<LearningResource> learningResources = new HashSet<>();

    // Enums
    public enum SkillCategory {
        PROGRAMMING_LANGUAGES,
        WEB_DEVELOPMENT,
        MOBILE_DEVELOPMENT,
        DATA_SCIENCE,
        MACHINE_LEARNING,
        CLOUD_COMPUTING,
        DEVOPS,
        CYBERSECURITY,
        UI_UX_DESIGN,
        PROJECT_MANAGEMENT,
        SOFT_SKILLS,
        BUSINESS_ANALYSIS,
        QUALITY_ASSURANCE,
        DATABASE,
        NETWORKING,
        OTHER
    }

    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    // Constructors
    public Skill() {}

    public Skill(String name, SkillCategory category, DifficultyLevel difficultyLevel) {
        this.name = name;
        this.category = category;
        this.difficultyLevel = difficultyLevel;
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

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(SkillCategory category) {
        this.category = category;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Boolean getIsTrending() {
        return isTrending;
    }

    public void setIsTrending(Boolean isTrending) {
        this.isTrending = isTrending;
    }

    public Integer getDemandScore() {
        return demandScore;
    }

    public void setDemandScore(Integer demandScore) {
        this.demandScore = demandScore;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<Skill> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<Skill> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Set<UserSkill> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(Set<UserSkill> userSkills) {
        this.userSkills = userSkills;
    }

    public Set<LearningResource> getLearningResources() {
        return learningResources;
    }

    public void setLearningResources(Set<LearningResource> learningResources) {
        this.learningResources = learningResources;
    }
}
