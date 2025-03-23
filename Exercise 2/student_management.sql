CREATE DATABASE student_management;
USE student_management;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    score DOUBLE,
    image varchar(255),
    address VARCHAR(255),
    note TEXT
);