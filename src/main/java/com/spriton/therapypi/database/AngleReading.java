package com.spriton.therapypi.database;

import java.time.LocalDateTime;

public class AngleReading {

    public int angle;
    public LocalDateTime timestamp = LocalDateTime.now();

    public AngleReading(int angle) {
        this.angle = angle;
    }

}
