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

import microerp.model.Attendance;
import java.sql.*;

public class AttendanceDAO {
    private final Connection conn;

    public AttendanceDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, date, status) VALUES (?, ?, ?, ?) RETURNING attendance_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getSubjectId());
            stmt.setDate(3, attendance.getDate());
            stmt.setString(4, attendance.getStatus());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("attendance_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Attendance findById(int attendanceId) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Attendance attendance = new Attendance();
                    attendance.setAttendanceId(rs.getInt("attendance_id"));
                    attendance.setStudentId(rs.getInt("student_id"));
                    attendance.setSubjectId(rs.getInt("subject_id"));
                    attendance.setDate(rs.getDate("date"));
                    attendance.setStatus(rs.getString("status"));
                    return attendance;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Attendance attendance) throws SQLException {
        String sql = "UPDATE attendance SET student_id=?, subject_id=?, date=?, status=? WHERE attendance_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getStudentId());
            stmt.setInt(2, attendance.getSubjectId());
            stmt.setDate(3, attendance.getDate());
            stmt.setString(4, attendance.getStatus());
            stmt.setInt(5, attendance.getAttendanceId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int attendanceId) throws SQLException {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            return stmt.executeUpdate() > 0;
        }
    }
}
