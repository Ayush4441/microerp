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

import microerp.model.Autho;
import java.sql.*;

public class AuthoDAO {
    private final Connection conn;

    public AuthoDAO(Connection conn) {
        this.conn = conn;
    }

    public int insert(Autho autho) throws SQLException {
        String sql = "INSERT INTO autho (username, user_password) VALUES (?, ?) RETURNING user_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, autho.getUsername());
            stmt.setString(2, autho.getUserPassword());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    throw new SQLException("Insert failed, no ID returned.");
                }
            }
        }
    }

    public Autho findById(int userId) throws SQLException {
        String sql = "SELECT * FROM autho WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Autho autho = new Autho();
                    autho.setUserId(rs.getInt("user_id"));
                    autho.setUsername(rs.getString("username"));
                    autho.setUserPassword(rs.getString("user_password"));
                    return autho;
                } else {
                    return null;
                }
            }
        }
    }

    public Autho findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM autho WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Autho autho = new Autho();
                    autho.setUserId(rs.getInt("user_id"));
                    autho.setUsername(rs.getString("username"));
                    autho.setUserPassword(rs.getString("user_password"));
                    return autho;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean update(Autho autho) throws SQLException {
        String sql = "UPDATE autho SET username=?, user_password=? WHERE user_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, autho.getUsername());
            stmt.setString(2, autho.getUserPassword());
            stmt.setInt(3, autho.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM autho WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
