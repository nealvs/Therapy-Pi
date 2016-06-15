

CREATE TABLE config_value (
    config_key VARCHAR(100) PRIMARY KEY NOT NULL,
    config_value VARCHAR(100) NOT NULL,
    created DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated DATETIME,
    deleted DATETIME
);

INSERT INTO config_value (config_key, config_value) VALUES ('ANGLE_CALIBRATION_VOLTAGE', '2.1');
