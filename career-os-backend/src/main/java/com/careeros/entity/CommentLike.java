package com.careeros.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a like on a comment
 */
@Entity
@Table(name = "comment_likes")
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentLike {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private PostComment comment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "like_type")
    @Enumerated(EnumType.STRING)
    private LikeType likeType = LikeType.LIKE;
    
    public enum LikeType {
        LIKE,
        LOVE,
        HELPFUL,
        INSIGHTFUL
    }
}
