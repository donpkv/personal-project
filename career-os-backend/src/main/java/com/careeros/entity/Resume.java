package com.careeros.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Resume entity representing user resumes with ATS optimization features
 */
@Entity
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resume_user", columnList = "user_id"),
    @Index(name = "idx_resume_title", columnList = "title"),
    @Index(name = "idx_resume_status", columnList = "status")
})
public class Resume extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 200)
    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ResumeStatus status = ResumeStatus.DRAFT;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    // Personal Information
    @Size(max = 100)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 100)
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "country")
    private String country;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "professional_summary", length = 2000)
    private String professionalSummary;

    // ATS Optimization
    @Column(name = "ats_score")
    private Integer atsScore;

    @Column(name = "keyword_density")
    private Double keywordDensity;

    @ElementCollection
    @CollectionTable(name = "resume_target_keywords", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "keyword")
    private Set<String> targetKeywords = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "resume_missing_keywords", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "keyword")
    private Set<String> missingKeywords = new HashSet<>();

    @Column(name = "ats_feedback", length = 2000)
    private String atsFeedback;

    // File Information
    @Column(name = "pdf_file_path")
    private String pdfFilePath;

    @Column(name = "docx_file_path")
    private String docxFilePath;

    @Column(name = "json_content", columnDefinition = "TEXT")
    private String jsonContent; // Structured resume data

    // Relations
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkExperience> workExperiences = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Education> educations = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ResumeSkill> skills = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certification> certifications = new HashSet<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();

    // Enums
    public enum ResumeStatus {
        DRAFT,
        ACTIVE,
        ARCHIVED,
        DELETED
    }

    // Constructors
    public Resume() {}

    public Resume(User user, String title) {
        this.user = user;
        this.title = title;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ResumeStatus getStatus() {
        return status;
    }

    public void setStatus(ResumeStatus status) {
        this.status = status;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
    }

    public Double getKeywordDensity() {
        return keywordDensity;
    }

    public void setKeywordDensity(Double keywordDensity) {
        this.keywordDensity = keywordDensity;
    }

    public Set<String> getTargetKeywords() {
        return targetKeywords;
    }

    public void setTargetKeywords(Set<String> targetKeywords) {
        this.targetKeywords = targetKeywords;
    }

    public Set<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(Set<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public String getAtsFeedback() {
        return atsFeedback;
    }

    public void setAtsFeedback(String atsFeedback) {
        this.atsFeedback = atsFeedback;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public String getDocxFilePath() {
        return docxFilePath;
    }

    public void setDocxFilePath(String docxFilePath) {
        this.docxFilePath = docxFilePath;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public Set<WorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(Set<WorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public Set<Education> getEducations() {
        return educations;
    }

    public void setEducations(Set<Education> educations) {
        this.educations = educations;
    }

    public Set<ResumeSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<ResumeSkill> skills) {
        this.skills = skills;
    }

    public Set<Certification> getCertifications() {
        return certifications;
    }

    public void setCertifications(Set<Certification> certifications) {
        this.certifications = certifications;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
}
