CREATE DATABASE learning_journey;

USE learning_journey;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    topic VARCHAR(100),
    timespan VARCHAR(20),
    start_date DATE,
    completion_date DATE,
    FOREIGN KEY (username) REFERENCES users(username)
);
