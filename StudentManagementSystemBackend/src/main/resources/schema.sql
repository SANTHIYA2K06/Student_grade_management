DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS record;
DROP TABLE IF EXISTS module;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS admin;


CREATE TABLE admin
(
    id SERIAL PRIMARY KEY,
    username VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256)        NOT NULL
);

CREATE TABLE student
(
    id SERIAL PRIMARY KEY,
    first_name       VARCHAR(30),
    last_name        VARCHAR(30),
    username         VARCHAR(30) UNIQUE NOT NULL,
    password         VARCHAR(256)       NOT NULL,
    email            VARCHAR(50),
    birth_date       date,
    program_of_study VARCHAR(30),
    graduation_year  INT,
    department       VARCHAR(256)
);

CREATE TABLE staff
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(30),
    last_name  VARCHAR(30),
    username   VARCHAR(30) UNIQUE NOT NULL,
    password   VARCHAR(256)       NOT NULL,
    email      VARCHAR(50),
    title      VARCHAR(30),
    department VARCHAR(256)
);

CREATE TABLE module
(
    code    VARCHAR(20) PRIMARY KEY,
    name     VARCHAR(100),
    mnc      BOOLEAN DEFAULT FALSE,
    credits INT DEFAULT 0,
    staff_id INT NOT NULL,
    FOREIGN KEY (staff_id)
        REFERENCES staff (id)
        ON DELETE CASCADE
);

CREATE TABLE record
(
    id BIGSERIAL PRIMARY KEY,
    module_code VARCHAR(20) NOT NULL,
    date        date        NOT NULL,
    FOREIGN KEY (module_code)
        REFERENCES module (code) ON DELETE CASCADE
);

CREATE TABLE registration
(
    id                BIGSERIAL PRIMARY KEY,
    student_id        INT       NOT NULL,
    record_id         BIGINT    NOT NULL,
    score             INT,
    registration_time TIMESTAMP NOT NULL,
    FOREIGN KEY (student_id)
        REFERENCES student (id) ON DELETE CASCADE,
    FOREIGN KEY (record_id)
        REFERENCES record (id) ON DELETE CASCADE,
    CONSTRAINT unique_student_record UNIQUE (student_id, record_id)
);

