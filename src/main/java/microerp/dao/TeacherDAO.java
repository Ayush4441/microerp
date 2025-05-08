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

import microerp.model.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    private final Connection conn;

    public TeacherDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teachers (user_id, department, designation, joining_year, subject) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING teacher_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getDepartment());
            stmt.setString(3, teacher.getDesignation());
            if (teacher.getJoiningYear() != null) {
                stmt.setInt(4, teacher.getJoiningYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, teacher.getSubject());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("teacher_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    // Rename existing function ↓↓↓
    public Teacher findByTeacherId(int teacherId) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacher(rs);
                } else {
                    return null;
                }
            }
        }
    }

    // NEW FUNCTION — by user_id ↓↓↓
    public Teacher findById(int userId) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacher(rs);
                } else {
                    return null;
                }
            }
        }
    }

    // 🚀 NEW FUNCTION 2
    public List<Teacher> findAll() throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teachers";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        }
        return teachers;
    }

    public boolean update(Teacher teacher) throws SQLException {
        String sql = "UPDATE teachers SET user_id=?, department=?, designation=?, joining_year=?, subject=? WHERE teacher_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacher.getUserId());
            stmt.setString(2, teacher.getDepartment());
            stmt.setString(3, teacher.getDesignation());
            if (teacher.getJoiningYear() != null) {
                stmt.setInt(4, teacher.getJoiningYear());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, teacher.getSubject());
            stmt.setInt(6, teacher.getTeacherId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int teacherId) throws SQLException {
        String sql = "DELETE FROM teachers WHERE teacher_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            return stmt.executeUpdate() > 0;
        }
    }

    // ✨ DRY helper to map ResultSet → Teacher object
    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(rs.getInt("teacher_id"));
        teacher.setUserId(rs.getInt("user_id"));
        teacher.setDepartment(rs.getString("department"));
        teacher.setDesignation(rs.getString("designation"));
        int joinYear = rs.getInt("joining_year");
        teacher.setJoiningYear(rs.wasNull() ? null : joinYear);
        teacher.setSubject(rs.getString("subject"));
        return teacher;
    }
}
