package com.careeros.dto.social;

import lombok.Data;

/**
 * DTO for joining a study group request
 */
@Data
public class JoinGroupRequest {
    private String message;
    private String motivation;
    private boolean acceptRules = true;
    private String joinCode;
}
