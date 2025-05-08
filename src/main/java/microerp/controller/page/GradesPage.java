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
import microerp.dao.Database;
import microerp.dao.UserDAO;
import microerp.service.Session;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.SQLException;

public class GradesPage extends Page {

    private static GradesPage instance = null;

    public static GradesPage getInstance() {
        if (instance == null)
            instance = new GradesPage();

        return instance;
    }

    @Override
    public void start() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Grades Page");
        titleLabel.getStyleClass().add("page-title");

        TableView<String> gradesTable = new TableView<>();
        TableColumn<String, String> subjectColumn = new TableColumn<>("Subject");
        TableColumn<String, String> gradeColumn = new TableColumn<>("Grade");
        gradesTable.getColumns().addAll(subjectColumn, gradeColumn);
        gradesTable.setPrefHeight(400);
        gradesTable.setPlaceholder(new Label("No grades available."));

        root.getChildren().add(titleLabel);

        try {
            UserDAO userDAO = new UserDAO(Database.getConnection());

            if (userDAO.isTeacher(Session.currentUser)) {
                Button addGradeButton = new Button("Add/Edit Grade");
                addGradeButton.setOnAction(e -> {
                    System.out.println("Adding/editing grades... (Feature to be implemented)");
                });
                root.getChildren().add(addGradeButton);
            } else if (userDAO.isStudent(Session.currentUser)) {
                Label infoLabel = new Label("You can view your grades here.");
                root.getChildren().add(infoLabel);
            }

            root.getChildren().add(gradesTable);

            Button backButton = new Button("Back to Dashboard");
            backButton.setOnAction(e -> {
                microerp.controller.DashboardController.getInstance().switchToDashboard();
            });

            root.getChildren().add(backButton);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setNode(root);
    }

    @Override
    public void stop() {
    }
}
