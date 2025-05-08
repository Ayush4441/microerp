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

import javafx.scene.control.*;
import javafx.scene.layout.*;
import microerp.controller.*;
import microerp.util.Setting;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.geometry.*;

import microerp.dao.*;
import microerp.model.*;
import microerp.service.Session;

public class SettingsPage extends Page {

    private static SettingsPage instance = null;

    public static SettingsPage getInstance() {
        if (instance == null)
            instance = new SettingsPage();

        return instance;
    }

    @Override
    public void start() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label pageTitle = new Label("Settings Page");
        pageTitle.getStyleClass().add("page-title");

        // --- Sections ---
        VBox userInfoSection = createUserInfoSection();
        VBox preferencesSection = createPreferencesSection();

        // --- Back button ---
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> {
            DashboardController.getInstance().switchToDashboard();
        });

        VBox roleInfoSection = null;
        try {
            UserDAO userDAO = new UserDAO(Database.getConnection());

            if (userDAO.isTeacher(Session.currentUser))
                roleInfoSection = createTeacherInfoSection();
            else if (userDAO.isStudent(Session.currentUser))
                roleInfoSection = createStudentInfoSection();
        } 
        catch (Exception e) 
        {
            System.out.println("Something is Wrong");
            e.printStackTrace();
        }
        root.getChildren().addAll(pageTitle, userInfoSection, roleInfoSection, preferencesSection, backButton);

        setNode(root);
    }

    @Override
    public void stop() {
    }

    private VBox createUserInfoSection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: grey; -fx-border-radius: 5;");

        Label title = new Label("User Information");
        title.getStyleClass().add("section-title");

        User currentUser = Session.currentUser;

        // --- Username ---
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setText(currentUser.getUsername());

        // --- Phone ---
        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        String phone = (currentUser.getPhoneNumbers() != null && !currentUser.getPhoneNumbers().isEmpty())
                ? currentUser.getPhoneNumbers().get(0)
                : "";
        phoneField.setText(phone);

        // --- Email ---
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        String email = (currentUser.getEmails() != null && !currentUser.getEmails().isEmpty())
                ? currentUser.getEmails().get(0)
                : "";
        emailField.setText(email);

        // --- Gender ---
        Label genderLabel = new Label("Gender:");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setValue(currentUser.getGender() != null ? currentUser.getGender() : "Other");

        // --- Address ---
        Label addressLabel = new Label("Address:");
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        addressArea.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");

        // --- Save button ---
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            currentUser.setUsername(usernameField.getText().trim());
            currentUser.setPhoneNumbers(Collections.singletonList(phoneField.getText().trim()));
            currentUser.setEmails(Collections.singletonList(emailField.getText().trim()));
            currentUser.setGender(genderComboBox.getValue());
            currentUser.setAddress(addressArea.getText().trim());

            try {
                Connection conn = Database.getConnection();
                UserDAO userDAO = new UserDAO(conn);
                boolean updated = userDAO.update(currentUser);
                if (updated) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "User information updated successfully!",
                            ButtonType.OK);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update user information.", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        box.getChildren().addAll(
                title,
                usernameLabel, usernameField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                genderLabel, genderComboBox,
                addressLabel, addressArea,
                saveButton);

        return box;
    }

    private VBox createStudentInfoSection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: grey; -fx-border-radius: 5;");
    
        Label title = new Label("Student Information");
        title.getStyleClass().add("section-title");
    
        // --- Load student ---
        Student currentStudent = new Student();
        try {
            Connection conn = Database.getConnection();
            StudentDAO studentDAO = new StudentDAO(conn);
            Student student = studentDAO.findById(Session.currentUser.getUserId());
            if (student != null) {
                currentStudent = student;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
        // --- Fields ---
        Label branchLabel = new Label("Branch:");
        TextField branchField = new TextField(currentStudent.getBranch() != null ? currentStudent.getBranch() : "");
    
        Label sectionLabel = new Label("Section:");
        TextField sectionField = new TextField(currentStudent.getSection() != null ? currentStudent.getSection() : "");
    
        Label groupNumberLabel = new Label("Group Number:");
        TextField groupNumberField = new TextField(currentStudent.getGroupNumber() != null ? currentStudent.getGroupNumber().toString() : "");
    
        Label admissionYearLabel = new Label("Admission Year:");
        TextField admissionYearField = new TextField(currentStudent.getAdmissionYear() != 0 ? String.valueOf(currentStudent.getAdmissionYear()) : "");
    
        // --- New Subjects Field ---
        Label subjectsLabel = new Label("Subjects (comma separated):");
        TextField subjectsField = new TextField();
    
        // --- Save Button ---
        Button saveButton = new Button("Save Student Info");
        Student finalCurrentStudent = currentStudent;
        saveButton.setOnAction(e -> {
            try {
                Connection conn = Database.getConnection();
                StudentDAO studentDAO = new StudentDAO(conn);
                SubjectDAO subjectDAO = new SubjectDAO(conn);
                GradeDAO gradeDAO = new GradeDAO(conn);
                TeacherDAO teacherDAO = new TeacherDAO(conn);
    
                finalCurrentStudent.setUserId(Session.currentUser.getUserId());
                finalCurrentStudent.setBranch(branchField.getText().trim());
                finalCurrentStudent.setSection(sectionField.getText().trim());
    
                String groupText = groupNumberField.getText().trim();
                finalCurrentStudent.setGroupNumber(!groupText.isEmpty() ? Integer.parseInt(groupText) : null);
    
                String admissionYearText = admissionYearField.getText().trim();
                finalCurrentStudent.setAdmissionYear(!admissionYearText.isEmpty() ? Integer.parseInt(admissionYearText) : 0);
    
                boolean success;
                if (finalCurrentStudent.getStudentId() == 0) {
                    studentDAO.insert(finalCurrentStudent);
                    success = true;
                } else {
                    success = studentDAO.update(finalCurrentStudent);
                }
    
                // ---- SUBJECTS LOGIC ----
                String subjectsText = subjectsField.getText().trim();
                if (!subjectsText.isEmpty()) {
                    String[] subjectNames = subjectsText.split(",");
                    for (String subjectNameRaw : subjectNames) {
                        String subjectName = subjectNameRaw.trim();
                        if (subjectName.isEmpty()) continue;
    
                        // Check if subject exists
                        Subject subject = subjectDAO.findByName(subjectName);
                        if (subject == null) {
                            subject = new Subject();
                            subject.setSubjectName(subjectName);
                            subject.setBranch(finalCurrentStudent.getBranch());
                            subject.setYear(finalCurrentStudent.getAdmissionYear());
    
                            // --- Auto assign teacher ---
                            List<Teacher> allTeachers = teacherDAO.findAll();
                            Integer assignedTeacherId = null;
                            for (Teacher t : allTeachers) {
                                if (t.getSubject() != null) {
                                    List<String> teacherSubjects = Arrays.asList(t.getSubject().split(","));
                                    if (teacherSubjects.stream().map(String::trim).anyMatch(s -> s.equalsIgnoreCase(subjectName))) {
                                        assignedTeacherId = t.getTeacherId();
                                        break;
                                    }
                                }
                            }
                            subject.setAssignedTeacherId(assignedTeacherId);
    
                            subjectDAO.insert(subject);
                        }
    
                        // ---- Link student to subject (Grade with 0 marks) ----
                        Grade grade = new Grade();
                        grade.setStudentId(finalCurrentStudent.getUserId());
                        grade.setSubjectId(subject.getSubjectId());
                        grade.setMarksObtained(0.0f);
                        gradeDAO.insert(grade);
                    }
                }
    
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        success ? "Student info & subjects saved!" : "Failed to save student info.", ButtonType.OK);
                alert.showAndWait();
    
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    
        box.getChildren().addAll(
                title,
                branchLabel, branchField,
                sectionLabel, sectionField,
                groupNumberLabel, groupNumberField,
                admissionYearLabel, admissionYearField,
                subjectsLabel, subjectsField,
                saveButton
        );
    
        return box;
    }    

    private VBox createTeacherInfoSection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: grey; -fx-border-radius: 5;");
    
        Label title = new Label("Teacher Information");
        title.getStyleClass().add("section-title");
    
        // --- Load teacher ---
        Teacher currentTeacher = new Teacher();
        try {
            Connection conn = Database.getConnection();
            TeacherDAO teacherDAO = new TeacherDAO(conn);
            Teacher teacher = teacherDAO.findById(Session.currentUser.getUserId());
            if (teacher != null) {
                currentTeacher = teacher;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
        // --- Fields ---
        Label departmentLabel = new Label("Department:");
        TextField departmentField = new TextField(currentTeacher.getDepartment() != null ? currentTeacher.getDepartment() : "");
    
        Label designationLabel = new Label("Designation:");
        TextField designationField = new TextField(currentTeacher.getDesignation() != null ? currentTeacher.getDesignation() : "");
    
        Label joiningYearLabel = new Label("Joining Year:");
        TextField joiningYearField = new TextField(currentTeacher.getJoiningYear() != null ? currentTeacher.getJoiningYear().toString() : "");
    
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField(currentTeacher.getSubject() != null ? currentTeacher.getSubject() : "");
    
        // --- Save Button ---
        Button saveButton = new Button("Save Teacher Info");
        Teacher finalCurrentTeacher = currentTeacher;
        saveButton.setOnAction(e -> {
            try {
                Connection conn = Database.getConnection();
                TeacherDAO teacherDAO = new TeacherDAO(conn);
    
                finalCurrentTeacher.setUserId(Session.currentUser.getUserId());
                finalCurrentTeacher.setDepartment(departmentField.getText().trim());
                finalCurrentTeacher.setDesignation(designationField.getText().trim());
    
                String joinYearText = joiningYearField.getText().trim();
                finalCurrentTeacher.setJoiningYear(!joinYearText.isEmpty() ? Integer.parseInt(joinYearText) : null);
    
                finalCurrentTeacher.setSubject(subjectField.getText().trim());
    
                boolean success;
                if (finalCurrentTeacher.getTeacherId() == 0) {
                    teacherDAO.insert(finalCurrentTeacher);
                    success = true;
                } else {
                    success = teacherDAO.update(finalCurrentTeacher);
                }
    
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        success ? "Teacher info saved!" : "Failed to save teacher info.", ButtonType.OK);
                alert.showAndWait();
    
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    
        box.getChildren().addAll(
                title,
                departmentLabel, departmentField,
                designationLabel, designationField,
                joiningYearLabel, joiningYearField,
                subjectLabel, subjectField,
                saveButton
        );
    
        return box;
    }    

    private VBox createPreferencesSection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: grey; -fx-border-radius: 5;");

        Label title = new Label("Preferences");
        title.getStyleClass().add("section-title");

        Label themeLabel = new Label("Select Theme:");
        ComboBox<ControllerManager.Theme> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(ControllerManager.Theme.values());
        themeComboBox.setValue(Setting.APP_THEME);

        Button applyThemeButton = new Button("Apply Theme");
        applyThemeButton.setOnAction(e -> {
            ControllerManager.setTheme(themeComboBox.getValue());
            Setting.APP_THEME = themeComboBox.getValue();
            Setting.saveSettings();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Theme changed successfully!", ButtonType.OK);
            alert.showAndWait();
        });

        Button clearLoginButton = new Button("Clear Saved Login");
        clearLoginButton.setOnAction(e -> {
            Setting.USER_NAME = null;
            Setting.USER_PASSWORD = null;
            Setting.saveSettings();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved login cleared!", ButtonType.OK);
            alert.showAndWait();
        });

        box.getChildren().addAll(title, themeLabel, themeComboBox, applyThemeButton, clearLoginButton);
        return box;
    }
}
