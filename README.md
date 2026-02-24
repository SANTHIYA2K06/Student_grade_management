<<<<<<< HEAD
# Student Management System - Comp0010 Group24

## Project Overview

The **Student Management System** is a Java-based web application designed for managing student records and academic information. This system includes key features such as managing student grades, computing average grades for modules and students, and recording academic year data.

## Table of Contents

- [Technical Stack](#Technical-Stack)
- [Setup](#Installation)
- [Backend Structure](#Backend-Structure)
- [Features](#Features)
- [How to use](#how-to-use)

## Technical Stack

### Backend

- Java 17
- Spring Boot 3.3.4
- H2 Database
- [Mybatisplus 3.5.7](https://mybatis.plus/en/)
- Maven 3.6+

### Frontend

- React 18
- Ant Design UI Framework
- Axios for API communication
- PDF Generation with jsPDF

## Installation

### Prerequisites

- **Java 17** (Make sure your `JAVA_HOME` is set to JDK 17)
- **Maven 3.6+**
- **Mybatisplus 3.5.7**
- **Spring Boot 3.3.4**
- **Jacoco for Code Coverage Reporting**

### Backend Setup

#### 1. Clone the project

`git clone https://github.com/ucl-comp0010-2024/G-24java.git`

#### 2. Navigate to the backend directory

`cd StudentManagementSystemBackend`

#### 2. Install project dependencies

`mvn clean install`

#### 3. Run the project

Run StudentManagementSystemApplication.java file

### Frontend Setup

#### 1. Navigate to the frontend directory:

`cd student-management-system-frontend`

#### 2. Install project dependencies

`npm install`

#### 3. Run the project

`npm start`

### Backend Structure

- **annotation** (module storing annotations that will be used in the project)
- **aspect** (module defining annotation actions)
- **config** (config files in the project)
- **controller** (springboot controllers)
- **dto** (Data Object used for modeling json parameters in HTTP requests)
- **entity** (model for tables in the databases)
- **enums** (Enums in the project)
- **exception** (Custom exceptions in the project)
- **exceptionhandler** (Handle exceptions in the project)
- **generator** (Data generators in the project)
- **mapper** (Handles database operations)
- **model** (Other data models in the project)
- **response** (Custom response in the project)
- **service** (module that stores actual service logic)
- **utils** (Utils in the project)
- **vo** (Stores for data objects used for responses)

### Features

- **Server Features:**

  - User Authentication:

    - Supports login for students, staff, and admin users, by using Spring Security with JWT
      token-based authentication and token refresh functionality to maintain secure access.
  - Student Management:

    - Add, Edit, and Delete Students: Allows admin users to create, update, and delete student
      information.
    - Student Information Retrieval: Admin and staff can retrieve detailed information about
      students, excluding sensitive data like passwords.
    - Bulk Import Students via CSV: Enables efficient batch importing of student data through CSV
      files, simplifying data management.
  - Staff Management:

    - Add, Edit, and Delete Staff: Allows admin users to create, update, and delete staff
      information.
    - Staff Information Retrieval: Admin users can retrieve detailed information about staff,
      ensuring sensitive data is excluded.
    - Bulk Import Staff via CSV: Allows batch importing of staff information from CSV files, making
      it easier to manage staff records.
  - Module Management:

    - Add, Edit, and Delete Modules: Allows admin users to manage academic modules by creating,
      updating, and deleting module information.
    - List Modules: Provides a way to retrieve and display all available academic modules.
  - Password Management:

    - Reset Password: Allows authenticated users (students, staff, and admin) to securely reset
      their passwords by providing their old and new passwords.
  - Test-Driven Development (TDD):

    - Ensures all major features are developed following TDD practices, covering core
      functionalities with comprehensive tests to maintain code quality.
  - Academic Year Tracking:

    - Records and manages academic year information within the grade system to facilitate accurate
      tracking of student progress over time.
  - Student Grade Management:

    - Add, Update, and Delete Grades: Allows authorized users to manage student grades by adding,
      updating, or deleting records.
    - Average Grade Calculation: Automatically calculates average grades for students across
      modules.
- **Web Features:**

  - User Authentication Interface:

    - Login form with username/password fields
    - Token-based authentication management
    - Automatic token refresh handling
    - Role-based access control (Student/Staff/Admin views)
  - Student Management Interface:

    - Student profile viewing and editing
    - Grade history display with filtering options
    - Average grade calculations and visualizations
    - Personal information management
  - Staff Management Interface:

    - Staff profile management
    - Student grade entry and modification
    - Module management capabilities
    - Performance analytics dashboard
  - Admin Dashboard:

    - User management (add/edit/delete users)
    - System-wide statistics and reports
    - Access control management
    - System configuration settings
  - Data Import/Export Features:

    - CSV Import Capabilities:
      - Bulk student data import
      - Batch staff information upload
      - Mass grade data import
      - Module information batch upload
    - PDF Generation Features:
      - Student grade transcript
      - Individual student performance summary

  - General Features:
    - Real-time data updates
    - Breadcrumb navigation for intuitive page hierarchy and navigation
    - Interactive data tables with searching/filtering
    - Comprehensive form validation
    - Loading states and progress indicators

## How to use

### 1. Log on to administration portal

On the login page, use the following credentials to log on to the administration portal:

- Username: admin
- Password: 123456 (default password for admin user, can be changed later)

### 2. Add data

#### Add Academic Staff

- In the administration portal, go to the "Academic Staff" tab and add academic staff information.
- Users can add academic staff by directly pressing the "Add" button or uploading a CSV file containing staff data.
- CSV file template can be found when hovering on the "Import from CSV" button.

#### Add student data

- In the administration portal and staff portal, go to the "Student Management" tab and add student information.
- Users can add student by directly pressing the "Add" button or uploading a CSV file containing student data.
- CSV file template can be found when hovering on the "Import from CSV" button.

#### Add module data

- In the administration portal, go to the "Module" tab and add module information.
- Users can add module by directly pressing the "Add" button.

#### Add Assessment Record

- In the administration portal, go to the "Assessment Record" tab and add assessment record.
- Users can add assessment record by directly pressing the "Add" button or uploading a CSV file containing assessment record data.
- CSV file template can be found when hovering on the "Import from CSV" button.

### 3. View and search data

#### For Administrative Users and Academic Staff

##### Student Management

- In the administration portal and staff portal, go to the "Student Management" tab and view student information. Users can also search student by student ID, student name, or username, and can also filter student by programme of study and degree level.
- To view a student's personal information or examination records, click on their record in the table. Administrative users have the ability to edit or delete student records. Examination records and comprehensive statistics can be accessed from this view. Available statistics include the student's average score, pass rate, total number of examinations taken, score distribution range, and grade distribution, as well as the average score for each module.

##### Academic Staff Management

- In the administration portal and staff portal, go to the "Academic Staff" tab and view academic staff information. Users can also search academic staff by staff ID, staff name, or filter by title and department.
- To view a staff's personal information and module records with statistics, click on the staff record in the table. Administrative users have the ability to edit or delete staff records. Module records include the module code, module name, credits, MNC, number of students, and also average score and pass rate of every record of the module. Statistics include the module's average score, pass rate, number of students participated, and total number of modules taught.

##### Module Management

- In the administration portal and staff portal, go to the "Module Management" tab and view module information. Users can also search module by module code, module name, or module leader.
- To view a module's information, click on the module record in the table. Administrative users have the ability to edit or delete module records. Also, exam records and statistics of a module can be viewed here. Listed exam records include the record ID, date of exam, number of students participated, average score, and pass rate. Statistics include the module's average score, pass rate, participant number, performance trend of each record.

##### Assessment Record Management

- In the administration portal and staff portal, go to the "Assessment Record" tab and view assessment record information. Users can also filter assessment record by module code, year and month of each record.
- To view a assessment record's information, click on the record in the table. Administrative users have the ability to edit or delete record. Registration information shown includes student ID, student name, score, grade, and time of registration. Statistics shown include average score, pass rate, median score, standard deviation, score range, and grade distribution.

#### For Academic Staff

- In the staff portal, go to the "Personal Information" tab to view personal information. Information includes staff ID, name, email, title, and department. Listed modules include module code, module name, credits, MNC, module leader, average score, pass rate, and grade distribution. There is also a statistics of the staff's teaching performance, including the number of students taught, average score, pass rate, and grade distribution.

#### For Students

- In the student portal, go to the "Personal Information" tab to view personal information. Information includes student ID, name, email, programme of study, department, and also expected graduation year.
- To view a assessment record's information, click on the "Academic Records" tab. This page shows all the assessment records of the student, and provides statistics including overall score, pass rate, median score, standard deviation, score range, and grade distribution.Registration information shown includes module code, module name, credits, MNC, module leader, score, grade, and exam date.

### 5. Generate Transcript

- In the administration portal and staff portal, go to the "Student" tab and click on the student record, then click the "Download Transcript" button to generate transcript.
- Students can also generate their own transcript by clicking the "Download Transcript" button in the Academic Records portal of student portal.
- The transcript will be downloaded in PDF format.
- The transcript will include the student's personal information, record of all modules, and a simplified summary of the student's academic performance.

### 6. Password Management

- Administrative users, academic staff, and students can reset their passwords by clicking the "Reset Password" button in the the hover menu of the top right corner.
- Users need to provide their old and new passwords to reset their passwords.

### 7. Logout

- Users can logout by clicking the "Logout" button in the the hover menu of the top right corner.
=======
# Student_grade_management
>>>>>>> 01ca3cef0d25ab733434923f65ef620252e2733a
