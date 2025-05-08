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


package microerp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

import microerp.dao.Database;
import microerp.service.Session;
import microerp.util.Setting;

public class LoginController extends Controller {
    protected static Controller instance = null;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label errorLabel;
    @FXML
    private CheckBox rememberMeCheckbox;

    private final String CSSPATH = "/styles/Login.css", FXMLPATH = "/fxml/Login.fxml";

    public static Controller getInstance() {
        if (instance == null) {
            instance = new LoginController();
            if (Session.hasSavedUser()) {
                Platform.runLater(() -> {
                    System.out.println("Found Saved User");
                    try {
                        ControllerManager.switchController(DashboardController.getInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Login";
    }

    @Override
    public Parent loadRoot() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPATH));
        loader.setController(this);
        return loader.load();
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);

        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
            errorLabel.setVisible(false);
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
            errorLabel.setVisible(false);
        });
    }

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean rememberMe = rememberMeCheckbox.isSelected();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Username and password are required.");
            errorLabel.setVisible(true);
            return;
        }

        boolean loginSuccessful = Session.login(username, password);

        if (loginSuccessful) {
            if (rememberMe) {
                Setting.USER_NAME = username;
                Setting.USER_PASSWORD = password;
                Setting.saveSettings();
            }

            try {
                ControllerManager.switchController(DashboardController.getInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password. Please try again.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void onDisconnect() {
        try {
            Database.disconnect();
            Database.clearDatabaseInfo();
            Session.clearUserData();
            ControllerManager.switchController(ConnectionController.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onRegister() {
        try {
            ControllerManager.switchController(RegisterController.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEntered() {
        ControllerManager.addCSS(CSSPATH);
    }

    @Override
    public void onExited() {
        ControllerManager.removeCSS(CSSPATH);
    }
}
