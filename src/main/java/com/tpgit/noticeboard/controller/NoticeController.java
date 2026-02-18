package com.tpgit.noticeboard.controller;

import com.tpgit.noticeboard.dto.NoticeDto;
import com.tpgit.noticeboard.dto.NoticeRequest;
import com.tpgit.noticeboard.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public List<NoticeDto> getNotices(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        return noticeService.getNotices(type, category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDto> getNotice(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNotice(id));
    }

    @GetMapping("/archived")
    public List<NoticeDto> getArchivedNotices() {
        return noticeService.getArchivedNotices();
    }

    @DeleteMapping("/archived/{id}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Void> deleteArchivedNotice(@PathVariable Long id, Authentication authentication) {
        noticeService.deleteArchivedNotice(id, extractRole(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/archived")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Void> clearArchivedNotices(Authentication authentication) {
        noticeService.clearArchivedNotices(extractRole(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PRINCIPAL','HOD','PLACEMENT','EXAMCELL','EVENTS','HOSTEL','LIBRARY','SPORTS')")
    public ResponseEntity<NoticeDto> createNotice(@ModelAttribute @Valid NoticeRequest request, Authentication authentication) throws IOException {
        NoticeDto created = noticeService.createNotice(request, request.getFile(), extractRole(authentication));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PRINCIPAL','HOD','PLACEMENT','EXAMCELL','EVENTS','HOSTEL','LIBRARY','SPORTS')")
    public ResponseEntity<NoticeDto> updateNotice(
            @PathVariable Long id,
            @ModelAttribute @Valid NoticeRequest request,
            Authentication authentication) throws IOException {
        NoticeDto updated = noticeService.updateNotice(id, request, request.getFile(), extractRole(authentication));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRINCIPAL','HOD','PLACEMENT','EXAMCELL','EVENTS','HOSTEL','LIBRARY','SPORTS')")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id, Authentication authentication) {
        noticeService.deleteNotice(id, extractRole(authentication));
        return ResponseEntity.noContent().build();
    }

    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("PUBLIC");
    }
}
