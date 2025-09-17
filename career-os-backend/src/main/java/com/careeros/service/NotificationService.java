package com.careeros.service;

import com.careeros.entity.User;
import com.careeros.entity.DigitalCertificate;
import com.careeros.entity.StudyGroup;
import com.careeros.entity.MentorshipRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Notification Service for sending various types of notifications
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService;

    @Value("${app.notifications.push.enabled:false}")
    private boolean pushNotificationsEnabled;

    @Value("${app.notifications.sms.enabled:false}")
    private boolean smsNotificationsEnabled;

    @Value("${app.base-url:https://career-os.com}")
    private String baseUrl;

    /**
     * Send welcome notification to new user
     */
    public void sendWelcomeNotification(User user) {
        logger.info("Sending welcome notification to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("dashboardUrl", baseUrl + "/dashboard");

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Welcome to Career OS!",
                    "welcome",
                    templateData
                );

                // Send push notification if enabled
                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Welcome to Career OS!", 
                        "Start your learning journey today!");
                }

            } catch (Exception e) {
                logger.error("Error sending welcome notification", e);
            }
        });
    }

    /**
     * Send certificate earned notification
     */
    public void sendCertificateNotification(User user, DigitalCertificate certificate) {
        logger.info("Sending certificate notification to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("skillName", certificate.getSkillName());
                templateData.put("certificateUrl", certificate.getVerificationUrl());
                templateData.put("downloadUrl", certificate.getPdfUrl());

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Congratulations! You've earned a new certificate",
                    "certificate",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Certificate Earned!", 
                        "You've earned a certificate in " + certificate.getSkillName());
                }

            } catch (Exception e) {
                logger.error("Error sending certificate notification", e);
            }
        });
    }

    /**
     * Send study group invitation notification
     */
    public void sendStudyGroupInvitation(User user, StudyGroup studyGroup, User invitedBy) {
        logger.info("Sending study group invitation to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("groupName", studyGroup.getName());
                templateData.put("inviterName", invitedBy.getFullName());
                templateData.put("groupUrl", baseUrl + "/groups/" + studyGroup.getId());

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "You've been invited to join a study group",
                    "study_group_invitation",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Study Group Invitation", 
                        invitedBy.getFullName() + " invited you to join " + studyGroup.getName());
                }

            } catch (Exception e) {
                logger.error("Error sending study group invitation", e);
            }
        });
    }

    /**
     * Send mentorship request notification
     */
    public void sendMentorshipRequestNotification(User mentor, MentorshipRequest request) {
        logger.info("Sending mentorship request notification to mentor {}", mentor.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("mentorName", mentor.getFullName());
                templateData.put("menteeName", request.getMentee().getFullName());
                templateData.put("subject", request.getSubject());
                templateData.put("message", request.getMessage());
                templateData.put("requestUrl", baseUrl + "/mentorship/requests/" + request.getId());

                emailService.sendTemplateEmail(
                    mentor.getEmail(),
                    "New Mentorship Request",
                    "mentorship_request",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(mentor, "New Mentorship Request", 
                        request.getMentee().getFullName() + " wants to connect with you");
                }

            } catch (Exception e) {
                logger.error("Error sending mentorship request notification", e);
            }
        });
    }

    /**
     * Send learning path completion notification
     */
    public void sendLearningPathCompletionNotification(User user, String pathName) {
        logger.info("Sending learning path completion notification to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("pathName", pathName);
                templateData.put("dashboardUrl", baseUrl + "/dashboard");

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Congratulations! Learning Path Completed",
                    "path_completion",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Learning Path Completed!", 
                        "You've completed the " + pathName + " learning path");
                }

            } catch (Exception e) {
                logger.error("Error sending learning path completion notification", e);
            }
        });
    }

    /**
     * Send skill assessment reminder
     */
    public void sendAssessmentReminder(User user, String skillName) {
        logger.info("Sending assessment reminder to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("skillName", skillName);
                templateData.put("assessmentUrl", baseUrl + "/assessments");

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Complete your " + skillName + " assessment",
                    "assessment_reminder",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Assessment Reminder", 
                        "Complete your " + skillName + " assessment");
                }

            } catch (Exception e) {
                logger.error("Error sending assessment reminder", e);
            }
        });
    }

    /**
     * Send job recommendation notification
     */
    public void sendJobRecommendationNotification(User user, int jobCount) {
        logger.info("Sending job recommendation notification to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("jobCount", jobCount);
                templateData.put("jobsUrl", baseUrl + "/jobs");

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "New job recommendations available",
                    "job_recommendations",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "New Job Recommendations", 
                        jobCount + " new jobs match your skills");
                }

            } catch (Exception e) {
                logger.error("Error sending job recommendation notification", e);
            }
        });
    }

    /**
     * Send daily learning reminder
     */
    public void sendDailyLearningReminder(User user) {
        logger.info("Sending daily learning reminder to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("dashboardUrl", baseUrl + "/dashboard");

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Continue your learning journey",
                    "daily_reminder",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Keep Learning!", 
                        "Continue your learning streak today");
                }

            } catch (Exception e) {
                logger.error("Error sending daily learning reminder", e);
            }
        });
    }

    /**
     * Send push notification (placeholder implementation)
     */
    private void sendPushNotification(User user, String title, String message) {
        if (!pushNotificationsEnabled) {
            return;
        }

        logger.info("Sending push notification to user {}: {} - {}", user.getId(), title, message);
        
        // In a real implementation, this would integrate with:
        // - Firebase Cloud Messaging (FCM) for mobile apps
        // - Web Push API for web browsers
        // - Apple Push Notification Service (APNs) for iOS
        
        // For now, just log the notification
        logger.info("Push notification sent successfully");
    }

    /**
     * Send SMS notification (placeholder implementation)
     */
    private void sendSmsNotification(User user, String message) {
        if (!smsNotificationsEnabled || user.getPhoneNumber() == null) {
            return;
        }

        logger.info("Sending SMS to user {}: {}", user.getId(), message);
        
        // In a real implementation, this would integrate with:
        // - Twilio
        // - AWS SNS
        // - Other SMS providers
        
        logger.info("SMS sent successfully");
    }

    /**
     * Send group welcome notification
     */
    public void sendGroupWelcomeNotification(User user, StudyGroup studyGroup) {
        logger.info("Sending group welcome notification to user {}", user.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("userName", user.getFullName());
                templateData.put("groupName", studyGroup.getName());
                templateData.put("groupUrl", baseUrl + "/groups/" + studyGroup.getId());

                emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Welcome to " + studyGroup.getName(),
                    "group_welcome",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(user, "Welcome to Study Group", 
                        "You've joined " + studyGroup.getName());
                }

            } catch (Exception e) {
                logger.error("Error sending group welcome notification", e);
            }
        });
    }

    /**
     * Send new post notification
     */
    public void sendNewPostNotification(com.careeros.entity.GroupPost post) {
        logger.info("Sending new post notification for post {}", post.getId());
        // Implementation for notifying group members about new posts
    }

    /**
     * Send comment notification
     */
    public void sendCommentNotification(com.careeros.entity.PostComment comment) {
        logger.info("Sending comment notification for comment {}", comment.getId());
        // Implementation for notifying post authors about new comments
    }

    /**
     * Send mentorship session scheduled notification
     */
    public void sendMentorshipSessionScheduledNotification(User mentor, User mentee, com.careeros.entity.MentorshipSession session) {
        logger.info("Sending mentorship session scheduled notification");
        // Implementation for notifying about scheduled sessions
    }

    // Duplicate method removed - keeping the first implementation

    /**
     * Send mentorship accepted notification
     */
    public void sendMentorshipAcceptedNotification(User mentee, User mentor) {
        logger.info("Sending mentorship accepted notification to mentee {}", mentee.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("menteeName", mentee.getFullName());
                templateData.put("mentorName", mentor.getFullName());
                templateData.put("mentorshipUrl", baseUrl + "/mentorship/dashboard");

                emailService.sendTemplateEmail(
                    mentee.getEmail(),
                    "Mentorship Request Accepted",
                    "mentorship_accepted",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(mentee, "Mentorship Accepted", 
                        mentor.getFullName() + " has accepted your mentorship request");
                }

            } catch (Exception e) {
                logger.error("Error sending mentorship accepted notification", e);
            }
        });
    }

    /**
     * Send mentorship declined notification
     */
    public void sendMentorshipDeclinedNotification(User mentee, User mentor, String reason) {
        logger.info("Sending mentorship declined notification to mentee {}", mentee.getId());

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("menteeName", mentee.getFullName());
                templateData.put("mentorName", mentor.getFullName());
                templateData.put("reason", reason);
                templateData.put("findMentorsUrl", baseUrl + "/mentors");

                emailService.sendTemplateEmail(
                    mentee.getEmail(),
                    "Mentorship Request Declined",
                    "mentorship_declined",
                    templateData
                );

                if (pushNotificationsEnabled) {
                    sendPushNotification(mentee, "Mentorship Request Declined", 
                        mentor.getFullName() + " has declined your mentorship request");
                }

            } catch (Exception e) {
                logger.error("Error sending mentorship declined notification", e);
            }
        });
    }
}
