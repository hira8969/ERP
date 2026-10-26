package com.college.erp.dao.impl;

import com.college.erp.dao.NoticeDAO;
import com.college.erp.entity.Notice;
import com.college.erp.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAOImpl implements NoticeDAO {

    private static final String SELECT_COLUMNS = "notice_id, title, content, created_by, target_role, publish_date, expiry_date, active";

    @Override
    public void saveNotice(Notice n) {
        String sql = "INSERT INTO notices (title, content, created_by, target_role, publish_date, expiry_date, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, n.getTitle());
            stmt.setString(2, n.getContent());
            stmt.setInt(3, n.getCreatedBy());
            stmt.setString(4, n.getTargetRole() != null ? n.getTargetRole() : "ALL");
            stmt.setTimestamp(5, n.getPublishDate() != null ? Timestamp.valueOf(n.getPublishDate()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(6, n.getExpiryDate() != null ? Timestamp.valueOf(n.getExpiryDate()) : null);
            stmt.setBoolean(7, n.isActive());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    n.setNoticeId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving notice: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM notices WHERE active = true ORDER BY publish_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapNotice(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notices: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Notice> getNoticesByRole(String role) {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM notices WHERE active = true AND (target_role = 'ALL' OR target_role = ?) ORDER BY publish_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role != null ? role.toUpperCase() : "ALL");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNotice(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notices by role: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Notice getNoticeById(int noticeId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM notices WHERE notice_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noticeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapNotice(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notice by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void deleteNotice(int noticeId) {
        String sql = "DELETE FROM notices WHERE notice_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noticeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting notice: " + e.getMessage(), e);
        }
    }

    private Notice mapNotice(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setNoticeId(rs.getInt("notice_id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setCreatedBy(rs.getInt("created_by"));
        n.setTargetRole(rs.getString("target_role"));
        Timestamp pub = rs.getTimestamp("publish_date");
        if (pub != null) n.setPublishDate(pub.toLocalDateTime());
        Timestamp exp = rs.getTimestamp("expiry_date");
        if (exp != null) n.setExpiryDate(exp.toLocalDateTime());
        n.setActive(rs.getBoolean("active"));
        return n;
    }
}
