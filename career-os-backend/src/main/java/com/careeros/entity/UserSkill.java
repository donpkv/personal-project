package com.careeros.entity;

import jakarta.persistence.*;

/**
 * Junction entity representing user's skills and their proficiency levels
 */
@Entity
@Table(name = "user_skills", indexes = {
    @Index(name = "idx_user_skill_user", columnList = "user_id"),
    @Index(name = "idx_user_skill_skill", columnList = "skill_id")
})
public class UserSkill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level", nullable = false)
    private ProficiencyLevel proficiencyLevel;

    @Column(name = "self_assessment_score")
    private Integer selfAssessmentScore; // 1-10 scale

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "years_of_experience")
    private Double yearsOfExperience;

    @Column(name = "notes", length = 1000)
    private String notes;

    // Enum
    public enum ProficiencyLevel {
        BEGINNER,
        INTERMEDIATE, 
        ADVANCED,
        EXPERT
    }

    // Constructors
    public UserSkill() {}

    public UserSkill(User user, Skill skill, ProficiencyLevel proficiencyLevel) {
        this.user = user;
        this.skill = skill;
        this.proficiencyLevel = proficiencyLevel;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public ProficiencyLevel getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Integer getSelfAssessmentScore() {
        return selfAssessmentScore;
    }

    public void setSelfAssessmentScore(Integer selfAssessmentScore) {
        this.selfAssessmentScore = selfAssessmentScore;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Double getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
