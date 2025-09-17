package com.careeros.service;

import com.careeros.dto.ai.ResumeAnalysisRequest;
import com.careeros.dto.ai.ResumeAnalysisResponse;
import com.careeros.entity.Resume;
import com.careeros.entity.User;
import com.careeros.repository.ResumeRepository;
import com.careeros.service.ai.OpenAIService;
// import com.itextpdf.html2pdf.HtmlConverter; // Would be imported with iText dependency
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resume Builder Service with ATS optimization
 */
@Service
@Transactional
public class ResumeBuilderService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeBuilderService.class);

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private OpenAIService openAIService;

    @Value("${storage.local.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * Generate PDF resume from resume entity
     */
    public byte[] generatePdfResume(Resume resume, String templateName) {
        try {
            String htmlContent = generateHtmlResume(resume, templateName);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // In production, use HtmlConverter.convertToPdf(htmlContent, baos) with iText dependency
            logger.warn("PDF generation not available - iText dependency needed");
            
            // Save PDF file path
            String fileName = "resume_" + resume.getId() + "_" + System.currentTimeMillis() + ".pdf";
            String filePath = saveResumeFile(baos.toByteArray(), fileName);
            resume.setPdfFilePath(filePath);
            resumeRepository.save(resume);
            
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating PDF resume for resume ID: {}", resume.getId(), e);
            throw new RuntimeException("Failed to generate PDF resume", e);
        }
    }

    /**
     * Generate DOCX resume from resume entity
     */
    public byte[] generateDocxResume(Resume resume) {
        try {
            XWPFDocument document = new XWPFDocument();
            
            // Add header with personal information
            addPersonalInfo(document, resume);
            
            // Add professional summary
            if (resume.getProfessionalSummary() != null && !resume.getProfessionalSummary().isEmpty()) {
                addSection(document, "PROFESSIONAL SUMMARY", resume.getProfessionalSummary());
            }
            
            // Add work experience
            addWorkExperience(document, resume);
            
            // Add education
            addEducation(document, resume);
            
            // Add skills
            addSkills(document, resume);
            
            // Add projects
            addProjects(document, resume);
            
            // Add certifications
            addCertifications(document, resume);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            document.close();
            
            // Save DOCX file path
            String fileName = "resume_" + resume.getId() + "_" + System.currentTimeMillis() + ".docx";
            String filePath = saveResumeFile(baos.toByteArray(), fileName);
            resume.setDocxFilePath(filePath);
            resumeRepository.save(resume);
            
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating DOCX resume for resume ID: {}", resume.getId(), e);
            throw new RuntimeException("Failed to generate DOCX resume", e);
        }
    }

    /**
     * Analyze resume for ATS compliance
     */
    public ResumeAnalysisResponse analyzeResumeATS(Resume resume, String jobDescription) {
        try {
            String resumeContent = extractResumeContent(resume);
            
            ResumeAnalysisRequest request = new ResumeAnalysisRequest();
            request.setUserId(resume.getUser().getId());
            request.setResumeId(resume.getId());
            request.setResumeContent(resumeContent);
            request.setJobDescription(jobDescription);
            request.setAnalysisType("ATS_FOCUSED");
            
            ResumeAnalysisResponse analysis = openAIService.analyzeResume(request);
            
            // Update resume with ATS feedback
            updateResumeWithAnalysis(resume, analysis);
            
            return analysis;
        } catch (Exception e) {
            logger.error("Error analyzing resume ATS for resume ID: {}", resume.getId(), e);
            throw new RuntimeException("Failed to analyze resume", e);
        }
    }

    /**
     * Calculate ATS score based on resume content and job description
     */
    public int calculateATSScore(Resume resume, String jobDescription) {
        try {
            String resumeContent = extractResumeContent(resume);
            
            int score = 0;
            
            // Basic structure score (30 points)
            score += calculateStructureScore(resume);
            
            // Keyword matching score (40 points)
            score += calculateKeywordScore(resumeContent, jobDescription);
            
            // Formatting score (20 points)
            score += calculateFormattingScore(resume);
            
            // Content quality score (10 points)
            score += calculateContentScore(resume);
            
            return Math.min(100, Math.max(0, score));
        } catch (Exception e) {
            logger.error("Error calculating ATS score for resume ID: {}", resume.getId(), e);
            return 0;
        }
    }

    /**
     * Get keyword suggestions for resume optimization
     */
    public Set<String> getKeywordSuggestions(String jobDescription, String targetRole) {
        Set<String> keywords = new HashSet<>();
        
        if (jobDescription != null && !jobDescription.isEmpty()) {
            keywords.addAll(extractKeywordsFromText(jobDescription));
        }
        
        // Add role-specific keywords
        keywords.addAll(getRoleSpecificKeywords(targetRole));
        
        return keywords;
    }

    private String generateHtmlResume(Resume resume, String templateName) {
        // Load template
        String template = loadResumeTemplate(templateName);
        
        // Replace placeholders with actual data
        template = template.replace("{{fullName}}", resume.getFullName() != null ? resume.getFullName() : "");
        template = template.replace("{{email}}", resume.getEmail() != null ? resume.getEmail() : "");
        template = template.replace("{{phone}}", resume.getPhone() != null ? resume.getPhone() : "");
        template = template.replace("{{address}}", buildFullAddress(resume));
        template = template.replace("{{linkedinUrl}}", resume.getLinkedinUrl() != null ? resume.getLinkedinUrl() : "");
        template = template.replace("{{githubUrl}}", resume.getGithubUrl() != null ? resume.getGithubUrl() : "");
        template = template.replace("{{portfolioUrl}}", resume.getPortfolioUrl() != null ? resume.getPortfolioUrl() : "");
        template = template.replace("{{professionalSummary}}", resume.getProfessionalSummary() != null ? resume.getProfessionalSummary() : "");
        
        // Add work experience
        template = template.replace("{{workExperience}}", buildWorkExperienceHtml(resume));
        
        // Add education
        template = template.replace("{{education}}", buildEducationHtml(resume));
        
        // Add skills
        template = template.replace("{{skills}}", buildSkillsHtml(resume));
        
        // Add projects
        template = template.replace("{{projects}}", buildProjectsHtml(resume));
        
        // Add certifications
        template = template.replace("{{certifications}}", buildCertificationsHtml(resume));
        
        return template;
    }

    private String loadResumeTemplate(String templateName) {
        try {
            // Default modern template
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>{{fullName}} - Resume</title>
                    <style>
                        body { font-family: 'Arial', sans-serif; margin: 0; padding: 20px; color: #333; }
                        .header { text-align: center; border-bottom: 2px solid #2c3e50; padding-bottom: 20px; margin-bottom: 30px; }
                        .name { font-size: 28px; font-weight: bold; color: #2c3e50; margin-bottom: 10px; }
                        .contact { font-size: 14px; color: #7f8c8d; }
                        .section { margin-bottom: 30px; }
                        .section-title { font-size: 18px; font-weight: bold; color: #2c3e50; border-bottom: 1px solid #bdc3c7; padding-bottom: 5px; margin-bottom: 15px; }
                        .job-title { font-weight: bold; color: #34495e; }
                        .company { font-style: italic; color: #7f8c8d; }
                        .date { float: right; color: #95a5a6; }
                        .description { margin-top: 10px; line-height: 1.6; }
                        .skills { display: flex; flex-wrap: wrap; gap: 10px; }
                        .skill { background: #ecf0f1; padding: 5px 10px; border-radius: 3px; font-size: 14px; }
                        ul { margin: 10px 0; padding-left: 20px; }
                        li { margin-bottom: 5px; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <div class="name">{{fullName}}</div>
                        <div class="contact">
                            {{email}} | {{phone}} | {{address}}<br>
                            {{linkedinUrl}} | {{githubUrl}} | {{portfolioUrl}}
                        </div>
                    </div>
                    
                    <div class="section">
                        <div class="section-title">PROFESSIONAL SUMMARY</div>
                        <p>{{professionalSummary}}</p>
                    </div>
                    
                    <div class="section">
                        <div class="section-title">WORK EXPERIENCE</div>
                        {{workExperience}}
                    </div>
                    
                    <div class="section">
                        <div class="section-title">EDUCATION</div>
                        {{education}}
                    </div>
                    
                    <div class="section">
                        <div class="section-title">SKILLS</div>
                        {{skills}}
                    </div>
                    
                    <div class="section">
                        <div class="section-title">PROJECTS</div>
                        {{projects}}
                    </div>
                    
                    <div class="section">
                        <div class="section-title">CERTIFICATIONS</div>
                        {{certifications}}
                    </div>
                </body>
                </html>
                """;
        } catch (Exception e) {
            logger.error("Error loading resume template: {}", templateName, e);
            throw new RuntimeException("Failed to load resume template", e);
        }
    }

    private void addPersonalInfo(XWPFDocument document, Resume resume) {
        XWPFParagraph namePara = document.createParagraph();
        XWPFRun nameRun = namePara.createRun();
        nameRun.setText(resume.getFullName() != null ? resume.getFullName() : "");
        nameRun.setBold(true);
        nameRun.setFontSize(18);
        
        XWPFParagraph contactPara = document.createParagraph();
        XWPFRun contactRun = contactPara.createRun();
        StringBuilder contact = new StringBuilder();
        
        if (resume.getEmail() != null) contact.append(resume.getEmail()).append(" | ");
        if (resume.getPhone() != null) contact.append(resume.getPhone()).append(" | ");
        if (resume.getCity() != null) contact.append(resume.getCity());
        
        contactRun.setText(contact.toString());
        contactRun.setFontSize(12);
    }

    private void addSection(XWPFDocument document, String title, String content) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(14);
        
        XWPFParagraph contentPara = document.createParagraph();
        XWPFRun contentRun = contentPara.createRun();
        contentRun.setText(content);
        contentRun.setFontSize(11);
    }

    private void addWorkExperience(XWPFDocument document, Resume resume) {
        addSection(document, "WORK EXPERIENCE", "");
        
        resume.getWorkExperiences().stream()
                .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
                .forEach(exp -> {
                    XWPFParagraph jobPara = document.createParagraph();
                    XWPFRun jobRun = jobPara.createRun();
                    jobRun.setText(exp.getJobTitle() + " - " + exp.getCompanyName());
                    jobRun.setBold(true);
                    jobRun.setFontSize(12);
                    
                    if (exp.getDescription() != null) {
                        XWPFParagraph descPara = document.createParagraph();
                        XWPFRun descRun = descPara.createRun();
                        descRun.setText(exp.getDescription());
                        descRun.setFontSize(11);
                    }
                });
    }

    private void addEducation(XWPFDocument document, Resume resume) {
        if (!resume.getEducations().isEmpty()) {
            addSection(document, "EDUCATION", "");
            
            resume.getEducations().forEach(edu -> {
                XWPFParagraph eduPara = document.createParagraph();
                XWPFRun eduRun = eduPara.createRun();
                String eduText = edu.getDegree() + " - " + edu.getInstitutionName();
                if (edu.getGpa() != null) {
                    eduText += " (GPA: " + edu.getGpa() + ")";
                }
                eduRun.setText(eduText);
                eduRun.setFontSize(11);
            });
        }
    }

    private void addSkills(XWPFDocument document, Resume resume) {
        if (!resume.getSkills().isEmpty()) {
            addSection(document, "SKILLS", "");
            
            Map<String, List<String>> skillsByCategory = new HashMap<>();
            resume.getSkills().forEach(skill -> {
                String category = skill.getCategory() != null ? skill.getCategory().name() : "OTHER";
                skillsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(skill.getSkillName());
            });
            
            skillsByCategory.forEach((category, skills) -> {
                XWPFParagraph skillPara = document.createParagraph();
                XWPFRun skillRun = skillPara.createRun();
                skillRun.setText(category + ": " + String.join(", ", skills));
                skillRun.setFontSize(11);
            });
        }
    }

    private void addProjects(XWPFDocument document, Resume resume) {
        if (!resume.getProjects().isEmpty()) {
            addSection(document, "PROJECTS", "");
            
            resume.getProjects().forEach(project -> {
                XWPFParagraph projectPara = document.createParagraph();
                XWPFRun projectRun = projectPara.createRun();
                projectRun.setText(project.getName());
                projectRun.setBold(true);
                projectRun.setFontSize(12);
                
                if (project.getDescription() != null) {
                    XWPFParagraph descPara = document.createParagraph();
                    XWPFRun descRun = descPara.createRun();
                    descRun.setText(project.getDescription());
                    descRun.setFontSize(11);
                }
            });
        }
    }

    private void addCertifications(XWPFDocument document, Resume resume) {
        if (!resume.getCertifications().isEmpty()) {
            addSection(document, "CERTIFICATIONS", "");
            
            resume.getCertifications().forEach(cert -> {
                XWPFParagraph certPara = document.createParagraph();
                XWPFRun certRun = certPara.createRun();
                String certText = cert.getName();
                if (cert.getIssuingOrganization() != null) {
                    certText += " - " + cert.getIssuingOrganization();
                }
                certRun.setText(certText);
                certRun.setFontSize(11);
            });
        }
    }

    private String buildFullAddress(Resume resume) {
        StringBuilder address = new StringBuilder();
        if (resume.getCity() != null) address.append(resume.getCity());
        if (resume.getState() != null) address.append(", ").append(resume.getState());
        if (resume.getCountry() != null) address.append(", ").append(resume.getCountry());
        return address.toString();
    }

    private String buildWorkExperienceHtml(Resume resume) {
        StringBuilder html = new StringBuilder();
        
        resume.getWorkExperiences().stream()
                .sorted((a, b) -> b.getStartDate().compareTo(a.getStartDate()))
                .forEach(exp -> {
                    html.append("<div style='margin-bottom: 20px;'>");
                    html.append("<div class='job-title'>").append(exp.getJobTitle()).append("</div>");
                    html.append("<div class='company'>").append(exp.getCompanyName());
                    html.append("<span class='date'>").append(exp.getStartDate()).append(" - ");
                    html.append(exp.getEndDate() != null ? exp.getEndDate() : "Present").append("</span></div>");
                    
                    if (exp.getDescription() != null) {
                        html.append("<div class='description'>").append(exp.getDescription()).append("</div>");
                    }
                    
                    if (!exp.getAchievements().isEmpty()) {
                        html.append("<ul>");
                        exp.getAchievements().forEach(achievement -> 
                            html.append("<li>").append(achievement).append("</li>"));
                        html.append("</ul>");
                    }
                    
                    html.append("</div>");
                });
        
        return html.toString();
    }

    private String buildEducationHtml(Resume resume) {
        StringBuilder html = new StringBuilder();
        
        resume.getEducations().forEach(edu -> {
            html.append("<div style='margin-bottom: 15px;'>");
            html.append("<div class='job-title'>").append(edu.getDegree());
            if (edu.getFieldOfStudy() != null) {
                html.append(" in ").append(edu.getFieldOfStudy());
            }
            html.append("</div>");
            html.append("<div class='company'>").append(edu.getInstitutionName());
            if (edu.getEndDate() != null) {
                html.append("<span class='date'>").append(edu.getEndDate()).append("</span>");
            }
            html.append("</div>");
            
            if (edu.getGpa() != null) {
                html.append("<div>GPA: ").append(edu.getGpa()).append("</div>");
            }
            
            html.append("</div>");
        });
        
        return html.toString();
    }

    private String buildSkillsHtml(Resume resume) {
        StringBuilder html = new StringBuilder("<div class='skills'>");
        
        resume.getSkills().stream()
                .sorted((a, b) -> Boolean.compare(b.getIsFeatured(), a.getIsFeatured()))
                .forEach(skill -> {
                    html.append("<span class='skill'>").append(skill.getSkillName()).append("</span>");
                });
        
        html.append("</div>");
        return html.toString();
    }

    private String buildProjectsHtml(Resume resume) {
        StringBuilder html = new StringBuilder();
        
        resume.getProjects().forEach(project -> {
            html.append("<div style='margin-bottom: 20px;'>");
            html.append("<div class='job-title'>").append(project.getName()).append("</div>");
            
            if (project.getDescription() != null) {
                html.append("<div class='description'>").append(project.getDescription()).append("</div>");
            }
            
            if (!project.getTechnologies().isEmpty()) {
                html.append("<div><strong>Technologies:</strong> ");
                html.append(String.join(", ", project.getTechnologies()));
                html.append("</div>");
            }
            
            html.append("</div>");
        });
        
        return html.toString();
    }

    private String buildCertificationsHtml(Resume resume) {
        StringBuilder html = new StringBuilder();
        
        resume.getCertifications().forEach(cert -> {
            html.append("<div style='margin-bottom: 15px;'>");
            html.append("<div class='job-title'>").append(cert.getName()).append("</div>");
            
            if (cert.getIssuingOrganization() != null) {
                html.append("<div class='company'>").append(cert.getIssuingOrganization());
                if (cert.getIssueDate() != null) {
                    html.append("<span class='date'>").append(cert.getIssueDate()).append("</span>");
                }
                html.append("</div>");
            }
            
            html.append("</div>");
        });
        
        return html.toString();
    }

    private String saveResumeFile(byte[] content, String fileName) {
        try {
            Path uploadPath = Paths.get(uploadDir, "resumes");
            Files.createDirectories(uploadPath);
            
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, content);
            
            return filePath.toString();
        } catch (IOException e) {
            logger.error("Error saving resume file: {}", fileName, e);
            throw new RuntimeException("Failed to save resume file", e);
        }
    }

    private String extractResumeContent(Resume resume) {
        StringBuilder content = new StringBuilder();
        
        // Add personal information
        if (resume.getFullName() != null) content.append(resume.getFullName()).append(" ");
        if (resume.getEmail() != null) content.append(resume.getEmail()).append(" ");
        if (resume.getPhone() != null) content.append(resume.getPhone()).append(" ");
        
        // Add professional summary
        if (resume.getProfessionalSummary() != null) {
            content.append(resume.getProfessionalSummary()).append(" ");
        }
        
        // Add work experience
        resume.getWorkExperiences().forEach(exp -> {
            content.append(exp.getJobTitle()).append(" ");
            content.append(exp.getCompanyName()).append(" ");
            if (exp.getDescription() != null) content.append(exp.getDescription()).append(" ");
            exp.getAchievements().forEach(achievement -> content.append(achievement).append(" "));
        });
        
        // Add education
        resume.getEducations().forEach(edu -> {
            if (edu.getDegree() != null) content.append(edu.getDegree()).append(" ");
            content.append(edu.getInstitutionName()).append(" ");
            if (edu.getFieldOfStudy() != null) content.append(edu.getFieldOfStudy()).append(" ");
        });
        
        // Add skills
        resume.getSkills().forEach(skill -> content.append(skill.getSkillName()).append(" "));
        
        // Add projects
        resume.getProjects().forEach(project -> {
            content.append(project.getName()).append(" ");
            if (project.getDescription() != null) content.append(project.getDescription()).append(" ");
            project.getTechnologies().forEach(tech -> content.append(tech).append(" "));
        });
        
        // Add certifications
        resume.getCertifications().forEach(cert -> {
            content.append(cert.getName()).append(" ");
            if (cert.getIssuingOrganization() != null) content.append(cert.getIssuingOrganization()).append(" ");
        });
        
        return content.toString();
    }

    private void updateResumeWithAnalysis(Resume resume, ResumeAnalysisResponse analysis) {
        resume.setAtsScore(analysis.getAtsScore());
        resume.setAtsFeedback(analysis.getOverallFeedback());
        
        if (analysis.getKeywordAnalysis() != null) {
            resume.setTargetKeywords(new HashSet<>(analysis.getKeywordAnalysis().getFoundKeywords()));
            resume.setMissingKeywords(new HashSet<>(analysis.getKeywordAnalysis().getMissingKeywords()));
            resume.setKeywordDensity(analysis.getKeywordAnalysis().getKeywordDensity());
        }
        
        resumeRepository.save(resume);
    }

    private int calculateStructureScore(Resume resume) {
        int score = 0;
        
        // Check for essential sections
        if (resume.getProfessionalSummary() != null && !resume.getProfessionalSummary().isEmpty()) score += 5;
        if (!resume.getWorkExperiences().isEmpty()) score += 10;
        if (!resume.getEducations().isEmpty()) score += 5;
        if (!resume.getSkills().isEmpty()) score += 5;
        if (resume.getEmail() != null && !resume.getEmail().isEmpty()) score += 3;
        if (resume.getPhone() != null && !resume.getPhone().isEmpty()) score += 2;
        
        return score;
    }

    private int calculateKeywordScore(String resumeContent, String jobDescription) {
        if (jobDescription == null || jobDescription.isEmpty()) {
            return 20; // Default score if no job description provided
        }
        
        Set<String> jobKeywords = extractKeywordsFromText(jobDescription);
        Set<String> resumeKeywords = extractKeywordsFromText(resumeContent);
        
        int matches = 0;
        for (String keyword : jobKeywords) {
            if (resumeKeywords.contains(keyword.toLowerCase())) {
                matches++;
            }
        }
        
        double matchPercentage = jobKeywords.isEmpty() ? 0 : (double) matches / jobKeywords.size();
        return (int) (matchPercentage * 40);
    }

    private int calculateFormattingScore(Resume resume) {
        int score = 20; // Base score for proper structure
        
        // Deduct points for common formatting issues
        if (resume.getFullName() == null || resume.getFullName().isEmpty()) score -= 5;
        if (resume.getEmail() == null || !isValidEmail(resume.getEmail())) score -= 5;
        
        return Math.max(0, score);
    }

    private int calculateContentScore(Resume resume) {
        int score = 0;
        
        // Check content quality
        if (resume.getProfessionalSummary() != null && resume.getProfessionalSummary().length() > 100) score += 3;
        if (!resume.getWorkExperiences().isEmpty()) {
            boolean hasDescriptions = resume.getWorkExperiences().stream()
                    .anyMatch(exp -> exp.getDescription() != null && exp.getDescription().length() > 50);
            if (hasDescriptions) score += 4;
        }
        if (resume.getSkills().size() >= 5) score += 3;
        
        return score;
    }

    private Set<String> extractKeywordsFromText(String text) {
        Set<String> keywords = new HashSet<>();
        
        if (text == null || text.isEmpty()) {
            return keywords;
        }
        
        // Simple keyword extraction - in production, use more sophisticated NLP
        String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s+#.-]", "")
                .split("\\s+");
        
        for (String word : words) {
            if (word.length() > 2 && !isStopWord(word)) {
                keywords.add(word);
            }
        }
        
        return keywords;
    }

    private Set<String> getRoleSpecificKeywords(String role) {
        Set<String> keywords = new HashSet<>();
        
        if (role == null) return keywords;
        
        String roleLower = role.toLowerCase();
        
        if (roleLower.contains("developer") || roleLower.contains("engineer")) {
            keywords.addAll(Arrays.asList("programming", "coding", "development", "software", 
                    "algorithms", "debugging", "testing", "git", "agile", "scrum"));
        }
        
        if (roleLower.contains("data")) {
            keywords.addAll(Arrays.asList("python", "sql", "analytics", "machine learning", 
                    "statistics", "visualization", "pandas", "numpy"));
        }
        
        if (roleLower.contains("manager")) {
            keywords.addAll(Arrays.asList("leadership", "management", "team", "project", 
                    "planning", "strategy", "communication", "stakeholder"));
        }
        
        return keywords;
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "and", "or", "but", "in", "on", "at", "to", "for", 
                "of", "with", "by", "is", "are", "was", "were", "be", "been", "have", "has", "had");
        return stopWords.contains(word.toLowerCase());
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
        return pattern.matcher(email).matches();
    }
}
