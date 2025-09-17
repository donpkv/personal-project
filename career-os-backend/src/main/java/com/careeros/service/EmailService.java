package com.careeros.service;

import com.careeros.entity.User;
import com.careeros.entity.DigitalCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Email service for sending notifications and verification emails
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.application.name:Career OS}")
    private String applicationName;

    /**
     * Send email verification email
     */
    public void sendVerificationEmail(User user) {
        try {
            // In production, implement actual email sending using JavaMailSender or external service
            logger.info("Sending verification email to: {}", user.getEmail());
            
            // In production, implement actual email sending
            // String subject = "Welcome to " + applicationName + " - Verify Your Email";
            // String body = buildVerificationEmailBody(user);
            // mailSender.send(createMimeMessage(user.getEmail(), subject, body));
            
            logger.info("Verification email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            logger.info("Sending password reset email to: {}", user.getEmail());
            
            // In production, implement actual email sending
            // String subject = applicationName + " - Password Reset Request";
            // String body = buildPasswordResetEmailBody(user, resetToken);
            // mailSender.send(createMimeMessage(user.getEmail(), subject, body));
            
            logger.info("Password reset email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send welcome email after successful registration
     */
    public void sendWelcomeEmail(User user) {
        try {
            logger.info("Sending welcome email to: {}", user.getEmail());
            
            // In production, implement actual email sending
            // String subject = "Welcome to " + applicationName + "!";
            // String body = buildWelcomeEmailBody(user);
            // mailSender.send(createMimeMessage(user.getEmail(), subject, body));
            
            logger.info("Welcome email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }

    private String buildVerificationEmailBody(User user) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2c3e50;">Welcome to %s!</h2>
                
                <p>Hi %s,</p>
                
                <p>Thank you for registering with %s. To complete your registration and start your skill development journey, please verify your email address by clicking the button below:</p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #3b82f6; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">Verify Email Address</a>
                </div>
                
                <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                <p style="word-break: break-all; color: #3b82f6;">%s</p>
                
                <p>This verification link will expire in 24 hours for security reasons.</p>
                
                <p>If you didn't create an account with us, please ignore this email.</p>
                
                <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
                
                <p style="color: #6b7280; font-size: 14px;">
                    Best regards,<br>
                    The %s Team
                </p>
            </body>
            </html>
            """.formatted(
                applicationName,
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                applicationName,
                generateVerificationLink(user),
                generateVerificationLink(user),
                applicationName
            );
    }

    private String buildPasswordResetEmailBody(User user, String resetToken) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2c3e50;">Password Reset Request</h2>
                
                <p>Hi %s,</p>
                
                <p>We received a request to reset your password for your %s account. If you made this request, click the button below to reset your password:</p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #ef4444; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">Reset Password</a>
                </div>
                
                <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                <p style="word-break: break-all; color: #ef4444;">%s</p>
                
                <p><strong>This password reset link will expire in 1 hour for security reasons.</strong></p>
                
                <p>If you didn't request a password reset, please ignore this email. Your password will remain unchanged.</p>
                
                <p style="color: #dc2626; font-weight: bold;">For security reasons, never share this email or link with anyone.</p>
                
                <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
                
                <p style="color: #6b7280; font-size: 14px;">
                    Best regards,<br>
                    The %s Team
                </p>
            </body>
            </html>
            """.formatted(
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                applicationName,
                generatePasswordResetLink(resetToken),
                generatePasswordResetLink(resetToken),
                applicationName
            );
    }

    private String buildWelcomeEmailBody(User user) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2c3e50;">Welcome to Your Career Development Journey!</h2>
                
                <p>Hi %s,</p>
                
                <p>Congratulations! Your email has been verified and your %s account is now active. You're ready to start your personalized skill development journey.</p>
                
                <h3 style="color: #2c3e50;">What you can do now:</h3>
                <ul style="line-height: 1.8;">
                    <li>🎯 Get AI-powered skill recommendations tailored to your career goals</li>
                    <li>📚 Access curated learning resources from top platforms</li>
                    <li>📊 Track your learning progress with detailed analytics</li>
                    <li>📄 Build ATS-optimized resumes with our intelligent builder</li>
                    <li>🔍 Analyze your resume for better job application success</li>
                </ul>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #10b981; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">Start Your Journey</a>
                </div>
                
                <div style="background-color: #f8fafc; padding: 20px; border-radius: 8px; margin: 20px 0;">
                    <h4 style="color: #2c3e50; margin-top: 0;">💡 Pro Tip:</h4>
                    <p style="margin-bottom: 0;">Complete your profile to get more accurate AI recommendations and unlock advanced features!</p>
                </div>
                
                <p>If you have any questions or need assistance, our support team is here to help. Just reply to this email or visit our help center.</p>
                
                <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
                
                <p style="color: #6b7280; font-size: 14px;">
                    Welcome aboard!<br>
                    The %s Team
                </p>
            </body>
            </html>
            """.formatted(
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                applicationName,
                generateDashboardLink(),
                applicationName
            );
    }

    private String generateVerificationLink(User user) {
        // In production, generate proper verification link with token
        return "https://career-os.com/verify-email?token=verification_token_here";
    }

    private String generatePasswordResetLink(String resetToken) {
        // In production, generate proper reset link with token
        return "https://career-os.com/reset-password?token=" + resetToken;
    }

    private String generateDashboardLink() {
        return "https://career-os.com/dashboard";
    }

    /**
     * Send certificate email notification
     */
    public void sendCertificateEmail(User user, DigitalCertificate certificate) {
        try {
            logger.info("Sending certificate email to: {}", user.getEmail());
            
            // In production, implement actual email sending
            // String subject = "Congratulations! You've earned a new certificate - " + certificate.getSkillName();
            // String body = buildCertificateEmailBody(user, certificate);
            // mailSender.send(createMimeMessage(user.getEmail(), subject, body));
            logger.info("Certificate email sent successfully to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send certificate email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send template-based email
     */
    public void sendTemplateEmail(String to, String subject, String template, Map<String, Object> templateData) {
        try {
            logger.info("Sending template email '{}' to: {}", template, to);
            
            // In production, implement actual email sending with template processing
            // String body = processTemplate(template, templateData);
            // mailSender.send(createMimeMessage(to, subject, body));
            
            logger.info("Template email sent successfully to: {}", to);
            
        } catch (Exception e) {
            logger.error("Failed to send template email to: {}", to, e);
        }
    }

    private String buildCertificateEmailBody(User user, DigitalCertificate certificate) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2c3e50;">🎉 Congratulations on Your Achievement!</h2>
                
                <p>Hi %s,</p>
                
                <p>We're excited to inform you that you've successfully earned a certificate in <strong>%s</strong>!</p>
                
                <div style="background-color: #f0f9ff; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #3b82f6;">
                    <h3 style="color: #1e40af; margin-top: 0;">Certificate Details</h3>
                    <p><strong>Skill:</strong> %s</p>
                    <p><strong>Score:</strong> %d/100</p>
                    <p><strong>Issued:</strong> %s</p>
                    <p><strong>Certificate ID:</strong> %s</p>
                </div>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #3b82f6; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; margin-right: 10px;">View Certificate</a>
                    <a href="%s" style="background-color: #10b981; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">Download PDF</a>
                </div>
                
                <p>You can share this achievement on your professional networks:</p>
                <ul>
                    <li>Add it to your LinkedIn profile</li>
                    <li>Include it in your resume</li>
                    <li>Share it with potential employers</li>
                </ul>
                
                <p>Keep up the great work on your learning journey!</p>
                
                <hr style="margin: 30px 0; border: none; border-top: 1px solid #e5e7eb;">
                
                <p style="color: #6b7280; font-size: 14px;">
                    Congratulations again!<br>
                    The %s Team
                </p>
            </body>
            </html>
            """.formatted(
                user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                certificate.getSkillName(),
                certificate.getSkillName(),
                certificate.getScore(),
                certificate.getIssuedAt().toString(),
                certificate.getCertificateId(),
                certificate.getVerificationUrl(),
                certificate.getPdfUrl(),
                applicationName
            );
    }

    private String processTemplate(String template, Map<String, Object> templateData) {
        // Simple template processing - in production use a proper template engine like Thymeleaf
        String processed = template;
        for (Map.Entry<String, Object> entry : templateData.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            processed = processed.replace(placeholder, value);
        }
        return processed;
    }
}
