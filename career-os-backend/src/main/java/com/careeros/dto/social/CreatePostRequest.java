package com.careeros.dto.social;

import lombok.Data;

import java.util.List;

/**
 * DTO for creating a group post
 */
@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private String postType;
    private List<String> tags;
    private String attachmentUrl;
    private boolean isPinned;
    private java.util.UUID groupId;
    private Boolean isAnonymous;
    private String codeSnippet;
    private String codeLanguage;
    private java.util.List<String> attachmentUrls;
}
