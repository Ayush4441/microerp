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


package microerp.service;

import microerp.dao.AuthoDAO;
import microerp.dao.Database;
import microerp.dao.UserDAO;
import microerp.model.Autho;
import microerp.model.User;
import microerp.util.Setting;

import java.sql.Connection;
import java.sql.SQLException;

public final class Session {
    public static User currentUser = null;

    public static boolean login(String username, String password) {
        try (Connection connection = Database.getConnection()) {
            if (connection == null) {
                System.err.println("Unable to connect to the database.");
                return false;
            }

            AuthoDAO authoDAO = new AuthoDAO(connection);
            Autho autho = authoDAO.findByUsername(username);

            if (autho != null && autho.getUserPassword().equals(password)) {
                UserDAO userDAO = new UserDAO(connection);
                User fullUser = userDAO.findById(autho.getUserId());

                if (fullUser == null) {
                    System.err.println("User info not found for user ID: " + autho.getUserId());
                    return false;
                }

                currentUser = fullUser;
                System.out.println("User Logged in Successfully");
                return true;
            } else 
            {
                System.out.println("Invalid username or password.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error during login.");

            if (e.getSQLState() != null && e.getSQLState().startsWith("08"))
                System.out.println("Database connection issue detected. Try reconnecting.");
            else if ("42501".equals(e.getSQLState()))
                System.out.println("Invalid login info saved, resetting it.");
            else
                e.printStackTrace();
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void logout() {
        currentUser = null;
        clearUserData();
        System.out.println("User Logged out Successfully");
    }

    public static boolean hasSavedUser() {
        Setting.loadSettings();
        if (Setting.USER_NAME != null && !Setting.USER_NAME.isEmpty() && Setting.USER_PASSWORD != null
                && !Setting.USER_PASSWORD.isEmpty()) {
            return login(Setting.USER_NAME, Setting.USER_PASSWORD);
        }
        return false;
    }

    public static void clearUserData() {
        Setting.USER_NAME = null;
        Setting.USER_PASSWORD = null;
        Setting.saveSettings();

        System.out.println("User data cleared successfully.");
    }

    public static boolean isDatabaseConnected() {
        try {
            Connection conn = Database.getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
