package com.college.erp.dao;

import com.college.erp.entity.Notice;
import java.util.List;

public interface NoticeDAO {

    void saveNotice(Notice notice);

    List<Notice> getAllNotices();

    List<Notice> getNoticesByRole(String role);

    Notice getNoticeById(int noticeId);

    void deleteNotice(int noticeId);
}
