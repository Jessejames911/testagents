package com.agents.builder.main.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DisplaySettingsDto {

    private Boolean showUserManual;

    private String userManualUrl;

    private Boolean showForum;

    private String forumUrl;

    private Boolean showProject;

    private String projectUrl;

    private Object loginLogo;

    private Object icon;

    private Object loginImage;

    private String title;

    private String slogan;
}
