package com.tpgit.noticeboard.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Data
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String noticeType;      // COMMON, ANNOUNCEMENT
    private String category;         // DEPARTMENT, PLACEMENT, EXAM_CELL, HOSTEL, SPORTS, EVENTS, LIBRARY
    private String sentBy;
    private String priorityLevel;    // URGENT, IMPORTANT, GENERAL

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    private boolean urgent;

    @Column(nullable = false)
    private boolean archived = false;

    private String filePath;         // stored file path

    private String tags;             // comma-separated tags

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
