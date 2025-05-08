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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import microerp.util.Setting;

public final class Database {
    private static Connection activeConnection = null;

    public static void setupDriver(String driver) throws ClassNotFoundException {
        Class.forName(driver);
    }

    public static void connect(String address, String username, String password) throws SQLException {
        if (activeConnection != null && !activeConnection.isClosed())
            activeConnection.close();
        
        activeConnection = DriverManager.getConnection(address, username, password);
        
        if (activeConnection != null) {
            System.out.println("Database Connected Successfully");
            saveDatabaseInfo(address, username, password);
        }
    }

    public static void disconnect() throws SQLException {
        if (activeConnection != null && !activeConnection.isClosed()) {
            activeConnection.close();
            activeConnection = null;
            clearDatabaseInfo();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (activeConnection == null || activeConnection.isClosed()) {
            if (Setting.DB_ADDRESS == null) {
                throw new SQLException("No database configuration saved.");
            }
            // Auto-reconnect
            activeConnection = DriverManager.getConnection(
                Setting.DB_ADDRESS,
                Setting.DB_USERNAME,
                Setting.DB_PASSWORD
            );
        }
        return activeConnection;
    }

    public static boolean hasSavedDatabase() {
        try {
            Setting.loadSettings();
            return (Setting.DB_ADDRESS != null && !Setting.DB_ADDRESS.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkConnection() 
    {
        try {
            if (activeConnection != null && !activeConnection.isClosed())
                return true;
            else 
            {
                if (Setting.DB_ADDRESS != null) 
                {
                    activeConnection = DriverManager.getConnection(
                        Setting.DB_ADDRESS,
                        Setting.DB_USERNAME,
                        Setting.DB_PASSWORD
                    );
                    return activeConnection != null && !activeConnection.isClosed();
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Database connection check failed: " + e.getMessage());
        }
        return false;
    }

    public static void clearDatabaseInfo() {
        Setting.DB_ADDRESS = null;
        Setting.DB_USERNAME = null;
        Setting.DB_PASSWORD = null;
        Setting.saveSettings();
    }

    private static void saveDatabaseInfo(String address, String username, String password) {
        Setting.DB_ADDRESS = address;
        Setting.DB_USERNAME = username;
        Setting.DB_PASSWORD = password;
        Setting.saveSettings();
    }
}
