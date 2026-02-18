package com.tpgit.noticeboard.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeDto {
    private Long id;
    private String title;
    private String description;
    private String noticeType;
    private String category;
    private String sentBy;
    private String priorityLevel;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean urgent;
    private boolean archived;
    private String filePath;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
