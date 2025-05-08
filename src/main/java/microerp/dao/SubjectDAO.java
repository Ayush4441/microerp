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

import microerp.model.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    private final Connection conn;

    public SubjectDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (subject_name, branch, year, assigned_teacher_id) " +
                     "VALUES (?, ?, ?, ?) RETURNING subject_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getBranch());
            stmt.setInt(3, subject.getYear());
            stmt.setObject(4, subject.getAssignedTeacherId(), Types.INTEGER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("subject_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Subject findById(int subjectId) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE subject_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubject(rs);
                } else {
                    return null;
                }
            }
        }
    }

    // 🚀 NEW FUNCTION 1
    public Subject findByName(String subjectName) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE LOWER(subject_name) = LOWER(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subjectName.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubject(rs);
                } else {
                    return null;
                }
            }
        }
    }

    // 🚀 NEW FUNCTION 2
    public List<Subject> findAll() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        }
        return subjects;
    }

    public boolean update(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET subject_name=?, branch=?, year=?, assigned_teacher_id=? WHERE subject_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getBranch());
            stmt.setInt(3, subject.getYear());
            stmt.setObject(4, subject.getAssignedTeacherId(), Types.INTEGER);
            stmt.setInt(5, subject.getSubjectId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int subjectId) throws SQLException {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            return stmt.executeUpdate() > 0;
        }
    }

    // ✨ DRY helper to map ResultSet → Subject object
    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setSubjectId(rs.getInt("subject_id"));
        subject.setSubjectName(rs.getString("subject_name"));
        subject.setBranch(rs.getString("branch"));
        subject.setYear(rs.getInt("year"));
        subject.setAssignedTeacherId((Integer) rs.getObject("assigned_teacher_id"));
        return subject;
    }
}
