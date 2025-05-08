/*
 * The MIT License (MIT)
 *
 * Copyright © 2025 Ayush Samantaray (@Ayush4441)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package microerp.dao;

import microerp.model.Notification;
import java.sql.*;

public class NotificationDAO {
    private final Connection conn;

    public NotificationDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, created_at, is_read) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING notification_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setTimestamp(4, notification.getCreatedAt());
            stmt.setBoolean(5, notification.isRead());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("notification_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Notification findById(int notificationId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Notification notification = new Notification();
                    notification.setNotificationId(rs.getInt("notification_id"));
                    notification.setUserId(rs.getInt("user_id"));
                    notification.setTitle(rs.getString("title"));
                    notification.setMessage(rs.getString("message"));
                    notification.setCreatedAt(rs.getTimestamp("created_at"));
                    notification.setRead(rs.getBoolean("is_read"));
                    return notification;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Notification notification) throws SQLException {
        String sql = "UPDATE notifications SET title=?, message=?, created_at=?, is_read=? WHERE notification_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, notification.getTitle());
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, notification.getCreatedAt());
            stmt.setBoolean(4, notification.isRead());
            stmt.setInt(5, notification.getNotificationId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }
}
