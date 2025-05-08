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

import microerp.model.Assignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {
    private final Connection conn;

    public AssignmentDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Assignment assignment) throws SQLException {
        String sql = "INSERT INTO assignments (subject_id, assigned_date, due_date, title, description) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING assignment_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignment.getSubjectId());
            stmt.setDate(2, assignment.getAssignedDate());
            stmt.setDate(3, assignment.getDueDate());
            stmt.setString(4, assignment.getTitle());
            stmt.setString(5, assignment.getDescription());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("assignment_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Assignment findById(int assignmentId) throws SQLException {
        String sql = "SELECT * FROM assignments WHERE assignment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Assignment assignment = new Assignment();
                    assignment.setAssignmentId(rs.getInt("assignment_id"));
                    assignment.setSubjectId(rs.getInt("subject_id"));
                    assignment.setAssignedDate(rs.getDate("assigned_date"));
                    assignment.setDueDate(rs.getDate("due_date"));
                    assignment.setTitle(rs.getString("title"));
                    assignment.setDescription(rs.getString("description"));
                    return assignment;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Assignment assignment) throws SQLException {
        String sql = "UPDATE assignments SET subject_id=?, assigned_date=?, due_date=?, title=?, description=? WHERE assignment_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignment.getSubjectId());
            stmt.setDate(2, assignment.getAssignedDate());
            stmt.setDate(3, assignment.getDueDate());
            stmt.setString(4, assignment.getTitle());
            stmt.setString(5, assignment.getDescription());
            stmt.setInt(6, assignment.getAssignmentId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int assignmentId) throws SQLException {
        String sql = "DELETE FROM assignments WHERE assignment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assignmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Assignment> findAll() throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
        }
        return assignments;
    }
    
    public List<Assignment> findBySubjectId(int subjectId) throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE subject_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToAssignment(rs));
                }
            }
        }
        return assignments;
    }
    
    private Assignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        Assignment a = new Assignment();
        a.setAssignmentId(rs.getInt("assignment_id"));
        a.setSubjectId(rs.getInt("subject_id"));
        a.setAssignedDate(rs.getDate("assigned_date"));
        a.setDueDate(rs.getDate("due_date"));
        a.setTitle(rs.getString("title"));
        a.setDescription(rs.getString("description"));
        return a;
    }    
}
