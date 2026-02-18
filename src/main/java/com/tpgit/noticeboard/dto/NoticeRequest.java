package com.tpgit.noticeboard.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Data
public class NoticeRequest {
    private String title;
    private String description;
    private String noticeType;
    private String category;
    private String sentBy;
    private String priorityLevel;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;
    private boolean urgent;
    private String tags;               // comma-separated
    private MultipartFile file;        // uploaded file
}
