package com.tpgit.noticeboard.scheduler;

import com.tpgit.noticeboard.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ArchiveScheduler {
    @Autowired
    private NoticeService noticeService;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void archiveExpiredNotices() {
        noticeService.archiveExpiredNotices();
        noticeService.deleteArchivedNoticesOlderThanDays(90);
    }
}
