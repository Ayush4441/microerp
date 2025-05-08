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

import java.io.IOException;
import java.sql.SQLException;

import microerp.controller.page.*;
import microerp.dao.Database;
import microerp.service.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class DashboardController extends Controller {
	private final String CSSPATH = "/styles/Dashboard.css", FXMLPATH = "/fxml/Dashboard.fxml";

	protected static DashboardController instance = null;

	@FXML
	private Label usernameLabel;
	@FXML
	private ImageView userIcon;
	@FXML
	private MenuItem disconnectItem,
			logoutItem;

	@FXML
	private GridPane dashboard;
	@FXML
	private StackPane pageContainer;

	@FXML
	private VBox attendanceCard,
			gradesCard,
			assignmentsCard,
			settingsCard,
			notificationCard,
			submissionCard;

	private Page currentPage = null;

	public static DashboardController getInstance() {
		if (instance == null)
			instance = new DashboardController();

		return instance;
	}

	@Override
	public String getName() {
		return "Dashboard";
	}

	@Override
	public Parent loadRoot() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPATH));
		loader.setController(this);
		return loader.load();
	}

	@FXML
	private void initialize() {
		usernameLabel.setText(Session.currentUser.getUsername());

		disconnectItem.setOnAction(e -> {
			try {
				disconnect();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		logoutItem.setOnAction(e -> logout());

		attendanceCard.setOnMouseClicked(event -> {
			openAttendance();
		});
		gradesCard.setOnMouseClicked(event -> {
			openGrades();
		});
		assignmentsCard.setOnMouseClicked(event -> {
			openAssignments();
		});
		settingsCard.setOnMouseClicked(event -> {
			openSettings();
		});
		notificationCard.setOnMouseClicked(event -> {
			openNotifications();
		});
		submissionCard.setOnMouseClicked(event -> {
			openSubmissions();
		});
	}

	private void disconnect() throws SQLException {
		try {
			Database.disconnect();
			Database.clearDatabaseInfo();
			Session.clearUserData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Disconnected from server.", ButtonType.OK);
		alert.showAndWait();
		ControllerManager.switchController(ConnectionController.getInstance());
	}

	private void logout() {
		Session.logout();
		try {
			ControllerManager.switchController(LoginController.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openAttendance() {
		switchToPage(AttendancePage.getInstance());
	}

	private void openGrades() {
		switchToPage(GradesPage.getInstance());
	}

	private void openAssignments() {
		switchToPage(AssignmentsPage.getInstance());
	}

	private void openSettings() {
		switchToPage(SettingsPage.getInstance());
	}

	private void openNotifications() {
		switchToPage(NotificationsPage.getInstance());
	}

	private void openSubmissions() {
		switchToPage(SubmissionsPage.getInstance());
	}

	@Override
	public void onEntered() {
		ControllerManager.addCSS(CSSPATH);
	}

	@Override
	public void onExited() {
		ControllerManager.removeCSS(CSSPATH);
	}

	public void switchToPage(Page page) {
		if (currentPage != null) {
			currentPage.stop();
		}

		dashboard.setVisible(false);
		dashboard.setDisable(true);

		pageContainer.setVisible(true);
		pageContainer.setDisable(false);

		page.start();
		pageContainer.getChildren().setAll(page.getNode());
		currentPage = page;
	}

	public void switchToDashboard() {
		if (currentPage != null) {
			currentPage.stop();
			pageContainer.getChildren().clear();
			currentPage = null;
		}

		dashboard.setVisible(true);
		dashboard.setDisable(false);

		pageContainer.setVisible(false);
		pageContainer.setDisable(true);
	}
}
