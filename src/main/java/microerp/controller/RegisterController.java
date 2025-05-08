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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import microerp.dao.AuthoDAO;
import microerp.dao.Database;
import microerp.dao.StudentDAO;
import microerp.dao.TeacherDAO;
import microerp.dao.UserDAO;
import microerp.model.Autho;
import microerp.model.Student;
import microerp.model.Teacher;
import microerp.model.User;

public class RegisterController extends Controller {
	protected static Controller instance = null;

	@FXML
	private Label formTitle, errorLabel;
	@FXML
	private StackPane transitionPane;
	@FXML
	private VBox roleSelectionPane, accountInfoPane, studentRegistrationPane, teacherRegistrationPane;
	@FXML
	private ComboBox<String> roleComboBox, genderComboBox;
	@FXML
	private TextField fullNameField, emailField, phoneField, usernameField;
	@FXML
	private PasswordField passwordField, confirmPasswordField;
	@FXML
	private TextField studentBranchField, studentSectionField, studentGroupField, studentAdmissionYearField;
	@FXML
	private TextField teacherDepartmentField, teacherDesignationField, teacherJoiningYearField;
	@FXML
	private Button nextButton;

	private final String CSSPATH = "/styles/Register.css", FXMLPATH = "/fxml/Register.fxml";

	private enum Step {
		ROLE_SELECTION, ACCOUNT_INFO, STUDENT_INFO, TEACHER_INFO
	}

	private Step currentStep = Step.ROLE_SELECTION;
	private boolean isStudentSelected = false;

	public static Controller getInstance() {
		if (instance == null)
			instance = new RegisterController();
		return instance;
	}

	@Override
	public String getName() {
		return "Register";
	}

	@Override
	public Parent loadRoot() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPATH));
		loader.setController(this);
		return loader.load();
	}

	@FXML
	private void initialize() {
		roleComboBox.getItems().addAll("Student", "Teacher");
		genderComboBox.getItems().addAll("Male", "Female", "Other");
		showPane(roleSelectionPane);
		formTitle.setText("Registration");
		errorLabel.setVisible(false);
	}

	@FXML
	private void onNext() {
		clearError();
		switch (currentStep) {
			case ROLE_SELECTION:
				String selectedRole = roleComboBox.getValue();
				if (selectedRole == null) {
					showError("Please select a role.");
					return;
				}
				isStudentSelected = selectedRole.equals("Student");
				currentStep = Step.ACCOUNT_INFO;
				formTitle.setText(isStudentSelected ? "Student - Basic Info" : "Teacher - Basic Info");
				transitionToPane(accountInfoPane);
				break;
			case ACCOUNT_INFO:
				// Placeholder for validation → You’ll hook your actual validation here
				if (!validateAccountInfo())
					return;
				currentStep = isStudentSelected ? Step.STUDENT_INFO : Step.TEACHER_INFO;
				formTitle.setText(isStudentSelected ? "Student Details" : "Teacher Details");
				transitionToPane(isStudentSelected ? studentRegistrationPane : teacherRegistrationPane);
				nextButton.setText("Register");
				break;
			case STUDENT_INFO:
			case TEACHER_INFO:
				// Placeholder for final form submission logic
				onRegister();
				break;
		}
	}

	@FXML
	private void onBack() {
		clearError();
		switch (currentStep) {
			case ACCOUNT_INFO:
				currentStep = Step.ROLE_SELECTION;
				formTitle.setText("Registration");
				transitionToPane(roleSelectionPane);
				nextButton.setText("Next");
				break;
			case STUDENT_INFO:
			case TEACHER_INFO:
				currentStep = Step.ACCOUNT_INFO;
				formTitle.setText(isStudentSelected ? "Student - Basic Info" : "Teacher - Basic Info");
				transitionToPane(accountInfoPane);
				nextButton.setText("Next");
				break;
			case ROLE_SELECTION:
				ControllerManager.switchController(LoginController.getInstance());
				break;
			default:
				break;
		}
	}

	private boolean validateAccountInfo() {
		if (fullNameField.getText().isEmpty() || usernameField.getText().isEmpty()) {
			showError("Please fill out all required fields.");
			return false;
		}
		if (!passwordField.getText().equals(confirmPasswordField.getText())) {
			showError("Passwords do not match.");
			return false;
		}
		return true;
	}

	@FXML
	private void onRegister() {
		try {
			Connection connection = Database.getConnection();

			User user = new User();
			Autho autho = new Autho();

			List<String> emails = new ArrayList<String>(), phones = new ArrayList<String>();
			emails.add(emailField.getText());
			phones.add(phoneField.getText());

			user.setUsername(fullNameField.getText());
			autho.setUsername(usernameField.getText());
			autho.setUserPassword(passwordField.getText());
			user.setEmails(emails);
			user.setPhoneNumbers(phones);
			user.setGender(genderComboBox.getValue());

			int userId = new UserDAO(connection).insert(user);
			if (userId == -1) 
				throw new Exception("Fail to insert user credentials to the Database.");

			// Feed values to Student or Teacher object
			if (isStudentSelected) {
				Student student = new Student();
				student.setUserId(userId);
				student.setBranch(studentBranchField.getText());
				student.setSection(studentSectionField.getText());
				student.setGroupNumber(Integer.parseInt(studentGroupField.getText()));
				student.setAdmissionYear(Integer.parseInt(studentAdmissionYearField.getText()));

				new StudentDAO(connection).insert(student);
			} else {
				Teacher teacher = new Teacher();
				teacher.setUserId(userId);
				teacher.setDepartment(teacherDepartmentField.getText());
				teacher.setDesignation(teacherDesignationField.getText());
				teacher.setJoiningYear(Integer.parseInt(teacherJoiningYearField.getText()));

				new TeacherDAO(connection).insert(teacher);
			}

			new AuthoDAO(connection).insert(autho);

			showError("Registration successful!");
			ControllerManager.switchController(LoginController.getInstance());
		} catch (SQLException e) {
			showError("Database error occurred.");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			showError("Please enter valid numeric values in year fields.");
			e.printStackTrace();
		} catch (Exception e) {
			showError("Failed to register due to unexpected error.");
			e.printStackTrace();
		}
	}

	private void showPane(VBox paneToShow) {
		VBox[] allPanes = { roleSelectionPane, accountInfoPane, studentRegistrationPane, teacherRegistrationPane };
		for (VBox pane : allPanes) {
			boolean show = pane == paneToShow;
			pane.setVisible(show);
			pane.setManaged(show);
			pane.setOpacity(show ? 1.0 : 0.0);
		}
	}

	private void transitionToPane(VBox targetPane) {
		VBox[] allPanes = { roleSelectionPane, accountInfoPane, studentRegistrationPane, teacherRegistrationPane };
		for (VBox pane : allPanes) {
			if (pane.isVisible() && pane != targetPane) {
				fadeOutPane(pane, () -> fadeInPane(targetPane));
				return;
			}
		}
		if (currentStep == Step.ROLE_SELECTION)
			nextButton.setText("Next");
		fadeInPane(targetPane);
	}

	private void fadeOutPane(VBox pane, Runnable onFinished) {
		FadeTransition fadeOut = new FadeTransition(Duration.millis(250), pane);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setOnFinished(e -> {
			pane.setVisible(false);
			pane.setManaged(false);
			onFinished.run();
		});
		fadeOut.play();
	}

	private void fadeInPane(VBox pane) {
		pane.setVisible(true);
		pane.setManaged(true);
		pane.setOpacity(0.0);
		FadeTransition fadeIn = new FadeTransition(Duration.millis(250), pane);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	private void clearError() {
		errorLabel.setVisible(false);
		errorLabel.setText("");
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
