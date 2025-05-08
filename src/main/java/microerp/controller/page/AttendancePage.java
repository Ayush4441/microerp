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


package microerp.controller.page;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import microerp.dao.*;
import microerp.model.*;
import microerp.service.Session;

import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class AttendancePage extends Page {

    private static AttendancePage instance = null;

    public static AttendancePage getInstance() {
        if (instance == null)
            instance = new AttendancePage();

        return instance;
    }

    @Override
    public void start() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Attendance Page");
        titleLabel.getStyleClass().add("page-title");

        TableView<String> attendanceTable = new TableView<>();
        TableColumn<String, String> dateColumn = new TableColumn<>("Date");
        TableColumn<String, String> statusColumn = new TableColumn<>("Status");
        attendanceTable.getColumns().addAll(dateColumn, statusColumn);
        attendanceTable.setPrefHeight(400);
        attendanceTable.setPlaceholder(new Label("No attendance records."));

        root.getChildren().add(titleLabel);
        try {

            UserDAO userDAO = new UserDAO(Database.getConnection());

            if (userDAO.isTeacher(Session.currentUser)) {
                // Teacher - can mark attendance
                Button markAttendanceButton = new Button("Mark Attendance");
                markAttendanceButton.setOnAction(e -> {
                    Stage dialog = new Stage();
                    dialog.setTitle("Mark Attendance");
                
                    VBox dialogVBox = new VBox(10);
                    dialogVBox.setPadding(new Insets(15));
                    dialogVBox.setAlignment(Pos.CENTER);
                
                    Label studentLabel = new Label("Student ID:");
                    TextField studentField = new TextField();
                    studentField.setPromptText("Enter Student ID");
                
                    Label subjectLabel = new Label("Subject ID:");
                    TextField subjectField = new TextField();
                    subjectField.setPromptText("Enter Subject ID");
                
                    Label dateLabel = new Label("Date:");
                    DatePicker datePicker = new DatePicker();
                    datePicker.setValue(java.time.LocalDate.now());
                
                    Label statusLabel = new Label("Status:");
                    ComboBox<String> statusComboBox = new ComboBox<>();
                    statusComboBox.getItems().addAll("Present", "Absent");
                    statusComboBox.setValue("Present");
                
                    Button submitButton = new Button("Submit");
                    submitButton.setOnAction(event -> {
                        try {
                            int studentId = Integer.parseInt(studentField.getText());
                            int subjectId = Integer.parseInt(subjectField.getText());
                            java.sql.Date date = java.sql.Date.valueOf(datePicker.getValue());
                            String status = statusComboBox.getValue();
                
                            Attendance attendance = new Attendance();
                            attendance.setStudentId(studentId);
                            attendance.setSubjectId(subjectId);
                            attendance.setDate(date);
                            attendance.setStatus(status);
                
                            AttendanceDAO attendanceDAO = new AttendanceDAO(Database.getConnection());
                            int insertedId = attendanceDAO.insert(attendance);
                
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance marked successfully! ID: " + insertedId, ButtonType.OK);
                            alert.showAndWait();
                            dialog.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Error marking attendance: " + ex.getMessage(), ButtonType.OK);
                            alert.showAndWait();
                        }
                    });
                
                    dialogVBox.getChildren().addAll(
                        studentLabel, studentField,
                        subjectLabel, subjectField,
                        dateLabel, datePicker,
                        statusLabel, statusComboBox,
                        submitButton
                    );
                
                    Scene dialogScene = new Scene(dialogVBox, 300, 400);
                    dialog.setScene(dialogScene);
                    dialog.show();
                });
                root.getChildren().add(markAttendanceButton);
            } else if (userDAO.isStudent(Session.currentUser)) {
                // Student - just view
                Label infoLabel = new Label("You can view your attendance records here.");
                root.getChildren().add(infoLabel);
            }

            root.getChildren().add(attendanceTable);

            Button backButton = new Button("Back to Dashboard");
            backButton.setOnAction(e -> {
                microerp.controller.DashboardController.getInstance().switchToDashboard();
            });

            root.getChildren().add(backButton);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        setNode(root);
    }

    @Override
    public void stop() {
    }
}
