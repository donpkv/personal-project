package com.careeros.dto.social;

import lombok.Data;

import java.util.UUID;

/**
 * DTO for creating a comment
 */
@Data
public class CreateCommentRequest {
    private String content;
    private UUID parentCommentId;
    private UUID postId;
    private Boolean isAnonymous;
}
