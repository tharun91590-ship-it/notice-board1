package com.tpgit.noticeboard.service;

import com.tpgit.noticeboard.dto.NoticeDto;
import com.tpgit.noticeboard.dto.NoticeRequest;
import com.tpgit.noticeboard.entity.Notice;
import com.tpgit.noticeboard.repository.NoticeRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public NoticeDto createNotice(NoticeRequest request, MultipartFile file, String userRole) throws IOException {
        validateRoleForCategory(userRole, request.getCategory());
        Notice notice = mapToEntity(request);
        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            notice.setFilePath(fileName);
        }
        notice.setArchived(notice.getEndDateTime() != null && notice.getEndDateTime().isBefore(LocalDateTime.now()));
        notice = noticeRepository.save(notice);
        return mapToDto(notice);
    }

    @Transactional
    public NoticeDto updateNotice(Long id, NoticeRequest request, MultipartFile file, String userRole) throws IOException {
        Notice notice = noticeRepository.findByIdAndArchivedFalse(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        validateRoleForCategory(userRole, request.getCategory());
        // update fields
        notice.setTitle(request.getTitle());
        notice.setDescription(request.getDescription());
        notice.setNoticeType(request.getNoticeType());
        notice.setCategory(request.getCategory());
        notice.setSentBy(request.getSentBy());
        notice.setPriorityLevel(normalizePriority(request.getPriorityLevel(), request.isUrgent()));
        notice.setStartDateTime(request.getStartDateTime());
        notice.setEndDateTime(request.getEndDateTime());
        notice.setUrgent("URGENT".equals(notice.getPriorityLevel()));
        notice.setTags(request.getTags());
        notice.setArchived(notice.getEndDateTime() != null && notice.getEndDateTime().isBefore(LocalDateTime.now()));
        if (file != null && !file.isEmpty()) {
            String fileName = fileStorageService.storeFile(file);
            notice.setFilePath(fileName);
        }
        notice = noticeRepository.save(notice);
        return mapToDto(notice);
    }

    @Transactional
    public void deleteNotice(Long id, String userRole) {
        Notice notice = noticeRepository.findByIdAndArchivedFalse(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        validateRoleForCategory(userRole, notice.getCategory());
        noticeRepository.delete(notice);
    }

    public List<NoticeDto> getNotices(String type, String category) {
        archiveExpiredNotices();
        List<Notice> notices = noticeRepository.findActiveNotices(type, category);
        return notices.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public NoticeDto getNotice(Long id) {
        Notice notice = noticeRepository.findByIdAndArchivedFalse(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        return mapToDto(notice);
    }

    public List<NoticeDto> getArchivedNotices() {
        archiveExpiredNotices();
        return noticeRepository.findByArchivedTrueOrderByUpdatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void archiveExpiredNotices() {
        List<Notice> expired = noticeRepository.findExpiredNotices(LocalDateTime.now());
        if (expired.isEmpty()) {
            return;
        }
        expired.forEach(notice -> notice.setArchived(true));
        noticeRepository.saveAll(expired);
    }

    @Transactional
    public int deleteArchivedNoticesOlderThanDays(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return noticeRepository.deleteArchivedOlderThan(cutoff);
    }

    @Transactional
    public void deleteArchivedNotice(Long id, String userRole) {
        validatePrincipalRole(userRole);
        Notice notice = noticeRepository.findByIdAndArchivedTrue(id)
                .orElseThrow(() -> new RuntimeException("Archived notice not found"));
        noticeRepository.delete(notice);
    }

    @Transactional
    public int clearArchivedNotices(String userRole) {
        validatePrincipalRole(userRole);
        return noticeRepository.deleteAllArchived();
    }

    private Notice mapToEntity(NoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setDescription(request.getDescription());
        notice.setNoticeType(request.getNoticeType());
        notice.setCategory(request.getCategory());
        notice.setSentBy(request.getSentBy());
        notice.setPriorityLevel(normalizePriority(request.getPriorityLevel(), request.isUrgent()));
        notice.setStartDateTime(request.getStartDateTime());
        notice.setEndDateTime(request.getEndDateTime());
        notice.setUrgent("URGENT".equals(notice.getPriorityLevel()));
        notice.setTags(request.getTags());
        notice.setArchived(false);
        return notice;
    }

    private NoticeDto mapToDto(Notice notice) {
        NoticeDto dto = new NoticeDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setDescription(notice.getDescription());
        dto.setNoticeType(notice.getNoticeType());
        dto.setCategory(notice.getCategory());
        dto.setSentBy(notice.getSentBy());
        dto.setPriorityLevel(normalizePriority(notice.getPriorityLevel(), notice.isUrgent()));
        dto.setStartDateTime(notice.getStartDateTime());
        dto.setEndDateTime(notice.getEndDateTime());
        dto.setUrgent(notice.isUrgent());
        dto.setArchived(notice.isArchived());
        dto.setFilePath(notice.getFilePath());
        dto.setTags(notice.getTags());
        dto.setCreatedAt(notice.getCreatedAt());
        dto.setUpdatedAt(notice.getUpdatedAt());
        return dto;
    }

    private String normalizePriority(String priorityLevel, boolean urgent) {
        if (urgent) {
            return "URGENT";
        }
        if (priorityLevel == null || priorityLevel.trim().isEmpty()) {
            return "GENERAL";
        }
        String normalized = priorityLevel.trim().toUpperCase(Locale.ROOT);
        if ("URGENT".equals(normalized) || "IMPORTANT".equals(normalized) || "GENERAL".equals(normalized)) {
            return normalized;
        }
        return "GENERAL";
    }

    private void validateRoleForCategory(String userRole, String category) {
        String role = userRole == null ? "" : userRole.toUpperCase(Locale.ROOT);
        String normalizedCategory = category == null ? "" : category.toUpperCase(Locale.ROOT);

        if ("PRINCIPAL".equals(role)) {
            return;
        }
        if ("HOD".equals(role) && "DEPARTMENT".equals(normalizedCategory)) {
            return;
        }
        if ("PLACEMENT".equals(role) && "PLACEMENT".equals(normalizedCategory)) {
            return;
        }
        if ("EXAMCELL".equals(role) && "EXAM_CELL".equals(normalizedCategory)) {
            return;
        }
        if ("EVENTS".equals(role) && "EVENTS".equals(normalizedCategory)) {
            return;
        }
        if ("HOSTEL".equals(role) && "HOSTEL".equals(normalizedCategory)) {
            return;
        }
        if ("LIBRARY".equals(role) && "LIBRARY".equals(normalizedCategory)) {
            return;
        }
        if ("SPORTS".equals(role) && "SPORTS".equals(normalizedCategory)) {
            return;
        }

        throw new AccessDeniedException("Role " + role + " is not allowed to manage " + normalizedCategory + " notices");
    }

    private void validatePrincipalRole(String userRole) {
        String role = userRole == null ? "" : userRole.toUpperCase(Locale.ROOT);
        if (!"PRINCIPAL".equals(role)) {
            throw new AccessDeniedException("Only PRINCIPAL can delete archived notices");
        }
    }
}
