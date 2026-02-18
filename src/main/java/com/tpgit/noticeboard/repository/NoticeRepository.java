package com.tpgit.noticeboard.repository;

import com.tpgit.noticeboard.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("""
            SELECT n FROM Notice n
            WHERE n.archived = false
              AND (:noticeType IS NULL OR n.noticeType = :noticeType)
              AND (:category IS NULL OR n.category = :category)
            ORDER BY n.createdAt DESC
            """)
    List<Notice> findActiveNotices(@Param("noticeType") String noticeType, @Param("category") String category);

    Optional<Notice> findByIdAndArchivedFalse(Long id);
    Optional<Notice> findByIdAndArchivedTrue(Long id);

    @Query("SELECT n FROM Notice n WHERE n.archived = false AND n.endDateTime < :now")
    List<Notice> findExpiredNotices(@Param("now") LocalDateTime now);

    List<Notice> findByArchivedTrueOrderByUpdatedAtDesc();

    @Modifying
    @Query("DELETE FROM Notice n WHERE n.archived = true AND n.endDateTime < :cutoff")
    int deleteArchivedOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM Notice n WHERE n.archived = true")
    int deleteAllArchived();
}
