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


package microerp.util;

import java.util.prefs.Preferences;

import microerp.controller.ControllerManager;

public final class Setting {
    public static String DB_ADDRESS = null, DB_USERNAME = null, DB_PASSWORD = null;
    public static String USER_NAME = null, USER_PASSWORD = null;
    public static ControllerManager.Theme APP_THEME = ControllerManager.Theme.PRIMER_DARK;

    private static final Preferences prefs = Preferences.userNodeForPackage(Setting.class);

    public static void saveSettings() {
        if (prefs == null) {
            System.err.println("Preferences object is null. Unable to save settings.");
            return;
        }

        if (DB_ADDRESS != null)
            prefs.put("DB_ADDRESS", DB_ADDRESS);
        if (DB_USERNAME != null)
            prefs.put("DB_USERNAME", DB_USERNAME);
        if (DB_PASSWORD != null)
            prefs.put("DB_PASSWORD", DB_PASSWORD);
        if (USER_NAME != null)
            prefs.put("USER_NAME", USER_NAME);
        if (USER_PASSWORD != null)
            prefs.put("USER_PASSWORD", USER_PASSWORD);
        if (APP_THEME != null)
            prefs.put("APP_THEME", APP_THEME.name());

        System.out.println("Settings saved successfully.");
    }

    public static void loadSettings() {
        DB_ADDRESS = prefs.get("DB_ADDRESS", "default_address");
        DB_USERNAME = prefs.get("DB_USERNAME", "default_username");
        DB_PASSWORD = prefs.get("DB_PASSWORD", "default_password");
        USER_NAME = prefs.get("USER_NAME", "default_user");
        USER_PASSWORD = prefs.get("USER_PASSWORD", "default_password");

        String themeName = prefs.get("APP_THEME", ControllerManager.Theme.PRIMER_LIGHT.name());
        APP_THEME = ControllerManager.Theme.valueOf(themeName);
    }

    public static void clearSettings() {
        try {
            prefs.clear();
            System.out.println("Settings cleared successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to clear settings.");
        }
    }
}
