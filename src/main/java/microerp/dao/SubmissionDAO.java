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

import microerp.model.Submission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    private final Connection conn;

    public SubmissionDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Submission submission) throws SQLException {
        String sql = "INSERT INTO submissions (assignment_id, student_id, submission_date, file_data, marks_awarded) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING submission_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submission.getAssignmentId());
            stmt.setInt(2, submission.getStudentId());
            stmt.setTimestamp(3, submission.getSubmissionDate());
            stmt.setBytes(4, submission.getFileData());
            stmt.setFloat(5, submission.getMarksAwarded());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("submission_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Submission findById(int submissionId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE submission_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubmission(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Submission submission) throws SQLException {
        String sql = "UPDATE submissions SET assignment_id=?, student_id=?, submission_date=?, file_data=?, marks_awarded=? WHERE submission_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submission.getAssignmentId());
            stmt.setInt(2, submission.getStudentId());
            stmt.setTimestamp(3, submission.getSubmissionDate());
            stmt.setBytes(4, submission.getFileData());
            stmt.setFloat(5, submission.getMarksAwarded());
            stmt.setInt(6, submission.getSubmissionId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int submissionId) throws SQLException {
        String sql = "DELETE FROM submissions WHERE submission_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Submission> findByAssignmentId(int assignmentId) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE assignment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapResultSetToSubmission(rs));
                }
            }
        }
        return submissions;
    }

    public List<Submission> findByStudentId(int studentId) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapResultSetToSubmission(rs));
                }
            }
        }
        return submissions;
    }

    public Submission findByAssignmentAndStudent(int assignmentId, int studentId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE assignment_id = ? AND student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapResultSetToSubmission(rs);
                else 
                    return null;
            }
        }
    }
    

    public List<Submission> findByTeacherSubjects(String subject) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        String sql = """
            SELECT s.*
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.assignment_id
            WHERE a.subject ILIKE ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + subject.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapResultSetToSubmission(rs));
                }
            }
        }
        return submissions;
    }

    private Submission mapResultSetToSubmission(ResultSet rs) throws SQLException {
        Submission s = new Submission();
        s.setSubmissionId(rs.getInt("submission_id"));
        s.setAssignmentId(rs.getInt("assignment_id"));
        s.setStudentId(rs.getInt("student_id"));
        s.setSubmissionDate(rs.getTimestamp("submission_date"));
        s.setFileData(rs.getBytes("file_data"));
        s.setMarksAwarded(rs.getFloat("marks_awarded"));
        return s;
    }
}
