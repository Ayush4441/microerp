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

import microerp.model.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    private final Connection conn;

    public GradeDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Grade grade) throws SQLException {
        String sql = "INSERT INTO grades (student_id, subject_id, marks_obtained) " +
                "VALUES (?, ?, ?) RETURNING grade_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grade.getStudentId());
            stmt.setInt(2, grade.getSubjectId());
            stmt.setFloat(3, grade.getMarksObtained());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("grade_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public List<Grade> findByStudentId(int studentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade();
                    grade.setGradeId(rs.getInt("grade_id"));
                    grade.setStudentId(rs.getInt("student_id"));
                    grade.setSubjectId(rs.getInt("subject_id"));
                    grade.setMarksObtained(rs.getFloat("marks_obtained"));
                    grades.add(grade);
                }
            }
        }
        return grades;
    }

    public Grade findById(int gradeId) throws SQLException {
        String sql = "SELECT * FROM grades WHERE grade_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gradeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Grade grade = new Grade();
                    grade.setGradeId(rs.getInt("grade_id"));
                    grade.setStudentId(rs.getInt("student_id"));
                    grade.setSubjectId(rs.getInt("subject_id"));
                    grade.setMarksObtained(rs.getFloat("marks_obtained"));
                    return grade;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET student_id=?, subject_id=?, marks_obtained=? WHERE grade_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grade.getStudentId());
            stmt.setInt(2, grade.getSubjectId());
            stmt.setFloat(3, grade.getMarksObtained());
            stmt.setInt(4, grade.getGradeId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int gradeId) throws SQLException {
        String sql = "DELETE FROM grades WHERE grade_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gradeId);
            return stmt.executeUpdate() > 0;
        }
    }
}
