package com.careeros.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an event in a study group
 */
@Entity
@Table(name = "group_events")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "virtual_meeting_url")
    private String virtualMeetingUrl;
    
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.SCHEDULED;
    
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;
    
    @Column(name = "recurrence_pattern")
    private String recurrencePattern;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventParticipant> participants = new ArrayList<>();
    
    public enum EventType {
        STUDY_SESSION,
        WORKSHOP,
        WEBINAR,
        NETWORKING,
        PROJECT_REVIEW,
        EXAM_PREP,
        CAREER_TALK,
        SOCIAL
    }
    
    public enum EventStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        POSTPONED
    }
    
    // Entity for event participants
    @Entity
    @Table(name = "event_participants")
    @Data
    public static class EventParticipant {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "event_id", nullable = false)
        private GroupEvent event;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        
        @Column(name = "rsvp_status")
        @Enumerated(EnumType.STRING)
        private RSVPStatus rsvpStatus = RSVPStatus.PENDING;
        
        @CreationTimestamp
        @Column(name = "registered_at")
        private LocalDateTime registeredAt;
        
        public enum RSVPStatus {
            PENDING,
            ATTENDING,
            NOT_ATTENDING,
            MAYBE
        }
    }
}
