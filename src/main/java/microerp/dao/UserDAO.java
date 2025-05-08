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

import microerp.model.User;
import java.sql.*;
import java.util.Collections;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, user_pic, phone_number, email, gender, address, last_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING user_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setBytes(2, user.getUserPic());
            stmt.setString(3, getFirstOrNull(user.getPhoneNumbers()));
            stmt.setString(4, getFirstOrNull(user.getEmails()));
            stmt.setString(5, user.getGender());
            stmt.setString(6, user.getAddress());
            stmt.setTimestamp(7, user.getLastActive());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id"); // Explicit column name (cleaner)
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public User findById(int userId) throws Exception, SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setUserPic(rs.getBytes("user_pic"));
                    user.setPhoneNumbers(wrapInList(rs.getString("phone_number")));
                    user.setEmails(wrapInList(rs.getString("email")));
                    user.setGender(rs.getString("gender"));
                    user.setAddress(rs.getString("address"));
                    user.setLastActive(rs.getTimestamp("last_active"));
                    return user;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID for update: " + user.getUserId());
        }
    
        String sql = "UPDATE users SET username=?, user_pic=?, phone_number=?, email=?, gender=?, address=?, last_active=? WHERE user_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setBytes(2, user.getUserPic());
            stmt.setString(3, getFirstOrNull(user.getPhoneNumbers()));
            stmt.setString(4, getFirstOrNull(user.getEmails()));
            stmt.setString(5, user.getGender());
            stmt.setString(6, user.getAddress());
            stmt.setTimestamp(7, user.getLastActive());
            stmt.setInt(8, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }    

    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // --- Helper methods ---
    private String getFirstOrNull(java.util.List<String> list) {
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    private java.util.List<String> wrapInList(String value) {
        return (value != null) ? Collections.singletonList(value) : Collections.emptyList();
    }

    public boolean isStudent(User user) {
        if (user == null) return false;
    
        String sql = "SELECT 1 FROM students WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isTeacher(User user) {
        if (user == null) return false;
    
        String sql = "SELECT 1 FROM teachers WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
