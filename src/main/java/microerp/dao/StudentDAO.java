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

import microerp.model.Student;
import java.sql.*;

public class StudentDAO {
    private final Connection conn;

    public StudentDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (user_id, branch, section, group_number, admission_year) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING student_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getBranch());
            stmt.setString(3, student.getSection());
            if (student.getGroupNumber() != null) {
                stmt.setInt(4, student.getGroupNumber());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, student.getAdmissionYear());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Student findByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setUserId(rs.getInt("user_id"));
                    student.setBranch(rs.getString("branch"));
                    student.setSection(rs.getString("section"));
                    int groupNum = rs.getInt("group_number");
                    student.setGroupNumber(rs.wasNull() ? null : groupNum);
                    student.setAdmissionYear(rs.getInt("admission_year"));
                    return student;
                } else {
                    return null;
                }
            }
        }
    }

    // NEW FUNCTION — by user_id ↓↓↓
    public Student findById(int userId) throws SQLException {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setUserId(rs.getInt("user_id"));
                    student.setBranch(rs.getString("branch"));
                    student.setSection(rs.getString("section"));
                    int groupNum = rs.getInt("group_number");
                    student.setGroupNumber(rs.wasNull() ? null : groupNum);
                    student.setAdmissionYear(rs.getInt("admission_year"));
                    return student;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET user_id=?, branch=?, section=?, group_number=?, admission_year=? WHERE student_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getBranch());
            stmt.setString(3, student.getSection());
            if (student.getGroupNumber() != null) {
                stmt.setInt(4, student.getGroupNumber());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, student.getAdmissionYear());
            stmt.setInt(6, student.getStudentId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        }
    }
}
