package com.careeros.dto.social;

import lombok.Data;

import java.util.List;

/**
 * DTO for creating a study group
 */
@Data
public class CreateStudyGroupRequest {
    private String name;
    private String description;
    private List<String> skillsFocus;
    private boolean isPrivate;
    private Integer maxMembers;
    private String category;
    private List<String> tags;
    private String meetingSchedule;
    private String timezone;
    private String privacyType;
    private String coverImageUrl;
    private List<String> rules;
}
