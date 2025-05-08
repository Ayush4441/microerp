# Micro ERP - Setup Guide

Welcome to **Micro ERP**! This is a lightweight ERP system designed for managing student and teacher data.  
This guide will walk you through **setting up** the project, **configuring** the databases (PostgreSQL & Azure), and **running** the application.

---

## Prerequisites

Make sure you have the following tools installed:

- **Java JDK 17+** (for JavaFX support)
- **JavaFX SDK** (download from [openjfx.io](https://openjfx.io))
- **PostgreSQL** (version 12+ recommended)
- **Azure Database** (for cloud-hosted DB)
- **Maven** (for dependency management and build)
- **IDE**: IntelliJ IDEA, Eclipse, NetBeans, or VSCode

---

## Project Setup

### 1. Clone or Download the Repository

Clone the repository or download it as a ZIP file:

```bash
git clone https://github.com/your-username/microclgerp.git
cd microclgerp
```

2. Database Configuration
➡️ PostgreSQL Setup
Open pgAdmin or psql CLI and create a new database:

```sql
CREATE DATABASE microclgerp;
```

3. Execute the SQL Schema
Run the following SQL scripts on PostgreSQL or use pgAdmin to import the database schema. Here's the script for creating the necessary tables:

```sql
-- Connect to database
\c microclgerp;

CREATE SEQUENCE user_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY DEFAULT nextval('user_id_seq'),
    username VARCHAR(100) NOT NULL,
    user_pic BYTEA,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    gender VARCHAR(10),
    address TEXT,
    last_active TIMESTAMP
);

CREATE SEQUENCE autho_user_id_seq START 1 INCREMENT 1;

CREATE TABLE autho (
    user_id INTEGER PRIMARY KEY DEFAULT nextval('autho_user_id_seq'),
    username TEXT NOT NULL UNIQUE,
    user_password TEXT NOT NULL
);

CREATE SEQUENCE teacher_id_seq START 1 INCREMENT 1;

CREATE TABLE teachers (
    teacher_id INTEGER PRIMARY KEY DEFAULT nextval('teacher_id_seq'),
    user_id INTEGER,
    department TEXT,
    designation TEXT,
    joining_year INTEGER,
    subject TEXT
);

CREATE SEQUENCE student_id_seq START 1 INCREMENT 1;

CREATE TABLE students (
    student_id INTEGER PRIMARY KEY DEFAULT nextval('student_id_seq'),
    user_id INTEGER,
    branch TEXT,
    section TEXT,
    group_number INTEGER,
    admission_year INTEGER NOT NULL
);

CREATE SEQUENCE attendance_id_seq START 1 INCREMENT 1;

CREATE TABLE attendance (
    attendance_id INTEGER PRIMARY KEY DEFAULT nextval('attendance_id_seq'),
    student_id INTEGER NOT NULL,
    subject_id INTEGER NOT NULL,
    date DATE NOT NULL,
    status TEXT NOT NULL
);

CREATE SEQUENCE assignment_id_seq START 1 INCREMENT 1;

CREATE TABLE assignments (
    assignment_id INTEGER PRIMARY KEY DEFAULT nextval('assignment_id_seq'),
    subject_id INTEGER NOT NULL,
    assigned_date DATE NOT NULL,
    due_date DATE NOT NULL,
    title TEXT NOT NULL,
    description TEXT
);

CREATE SEQUENCE grade_id_seq START 1 INCREMENT 1;

CREATE TABLE grades (
    grade_id INTEGER PRIMARY KEY DEFAULT nextval('grade_id_seq'),
    student_id INTEGER NOT NULL,
    subject_id INTEGER NOT NULL,
    marks_obtained FLOAT NOT NULL
);

CREATE SEQUENCE notification_id_seq START 1 INCREMENT 1;

CREATE TABLE notifications (
    notification_id INTEGER PRIMARY KEY DEFAULT nextval('notification_id_seq'),
    user_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE SEQUENCE subject_id_seq START 1 INCREMENT 1;

CREATE TABLE subjects (
    subject_id INTEGER PRIMARY KEY DEFAULT nextval('subject_id_seq'),
    subject_name VARCHAR(255) NOT NULL,
    branch VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    assigned_teacher_id INTEGER
);

```

4. Database Switching Logic (PostgreSQL / Azure)
You can switch between PostgreSQL and Azure databases dynamically through the application. You’ll need to configure the DatabaseConnection.java file to toggle between the two databases.

You need to enter your Database Server address, username and password

5. JavaFX Setup
Download the JavaFX SDK from https://openjfx.io.

Add JavaFX libraries to your project in your IDE:

javafx.controls, javafx.fxml, and optionally javafx.graphics.

To run the project with JavaFX, add the following VM options:

```bash
--module-path /path/to/javafx-sdk-XX.X.X/lib --add-modules javafx.controls,javafx.fxml
```

6. Maven Setup
The project uses Maven for dependency management. To run the project with Maven, simply run the following command in the root project directory:

```bash
mvn clean install
```

To run the project:

```bash
mvn exec:java
```
Ensure that you have configured your pom.xml with the necessary dependencies for JavaFX and JDBC connections.

Example pom.xml dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.3.3</version>
    </dependency>
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <version>9.4.1.jre8</version>
    </dependency>
</dependencies>
```

7. Running the Application
Once everything is set up, you can run the application directly through your IDE or by using Maven:

```bash
mvn clean javafx:run
```
