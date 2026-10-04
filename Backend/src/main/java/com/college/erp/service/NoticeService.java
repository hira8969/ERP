package com.college.erp.service;

import com.college.erp.entity.Notice;
import java.util.List;

public interface NoticeService {

    void saveNotice(Notice notice);

    List<Notice> getAllNotices();

    List<Notice> getNoticesByRole(String role);

    Notice getNoticeById(int noticeId);

    void deleteNotice(int noticeId);
}
