

CREATE TABLE patient (
    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    low_goal INT,
    high_goal INT,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated DATETIME,
    deleted DATETIME
);

CREATE TABLE patient_session (
    id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    patient_id INT NOT NULL REFERENCES patient(id),
    high_angle INT,
    low_angle INT,
    high_hold_seconds INT,
    low_hold_seconds INT,
    repetitions INT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    total_seconds INT,
    deleted DATETIME
);

INSERT INTO patient (id, first_name, last_name) VALUES (1, 'Test', 'User');
INSERT INTO patient_session (patient_id, high_angle, low_angle, high_hold_seconds, low_hold_seconds, repetitions, start_time, end_time, total_seconds)
             VALUES (1, 110, 5, 10, 4, 20, '2016-01-01 12:00:00', '2016-01-01 12:20:00', 1200);
