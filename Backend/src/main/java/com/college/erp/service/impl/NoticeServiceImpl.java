package com.college.erp.service.impl;

import com.college.erp.dao.NoticeDAO;
import com.college.erp.dao.impl.NoticeDAOImpl;
import com.college.erp.entity.Notice;
import com.college.erp.service.NoticeService;

import java.time.LocalDateTime;
import java.util.List;

public class NoticeServiceImpl implements NoticeService {

    private final NoticeDAO noticeDAO;

    public NoticeServiceImpl() {
        this.noticeDAO = new NoticeDAOImpl();
    }

    public NoticeServiceImpl(NoticeDAO noticeDAO) {
        this.noticeDAO = noticeDAO;
    }

    @Override
    public void saveNotice(Notice notice) {
        if (notice == null) {
            throw new IllegalArgumentException("Notice cannot be empty");
        }
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Notice title is required");
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Notice content is required");
        }
        if (notice.getPublishDate() == null) {
            notice.setPublishDate(LocalDateTime.now());
        }
        if (notice.getTargetRole() == null || notice.getTargetRole().trim().isEmpty()) {
            notice.setTargetRole("ALL");
        }
        notice.setActive(true);
        noticeDAO.saveNotice(notice);
    }

    @Override
    public List<Notice> getAllNotices() {
        return noticeDAO.getAllNotices();
    }

    @Override
    public List<Notice> getNoticesByRole(String role) {
        if (role == null || role.isBlank()) {
            return noticeDAO.getAllNotices();
        }
        return noticeDAO.getNoticesByRole(role.toUpperCase());
    }

    @Override
    public Notice getNoticeById(int noticeId) {
        return noticeDAO.getNoticeById(noticeId);
    }

    @Override
    public void deleteNotice(int noticeId) {
        noticeDAO.deleteNotice(noticeId);
    }
}
