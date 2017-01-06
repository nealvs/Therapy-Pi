package com.spriton.therapypi.components;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.software.SoftEncoder;
import com.spriton.therapypi.database.DataAccess;

import javax.persistence.Transient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Angle {

    public double rawValue = 2.0;
    public double value = DEFAULT_ANGLE;
    private double averagedValue = DEFAULT_ANGLE;

    public static double DEFAULT_ANGLE = 90;
    public static double MAX_ANGLE = 170;
    public static double MIN_ANGLE = -5;

    public double ANGLE_CALIBRATION_VOLTAGE = 2.0;
    public double ANGLE_CALIBRATION_DEGREE = 90.0;

    protected List<AngleReading> readings = new LinkedList<>();

    public Angle() {
        DEFAULT_ANGLE = Config.values.getDouble("DEFAULT_ANGLE", DEFAULT_ANGLE);
        MAX_ANGLE = Config.values.getDouble("MAX_ANGLE", MAX_ANGLE);
        MIN_ANGLE = Config.values.getDouble("MIN_ANGLE", MIN_ANGLE);

        Double angleCalibrationVoltage = DataAccess.getDoubleConfig("ANGLE_CALIBRATION_VOLTAGE");
        ANGLE_CALIBRATION_DEGREE = Config.values.getDouble("ANGLE_CALIBRATION_DEGREE", ANGLE_CALIBRATION_DEGREE);
        ANGLE_CALIBRATION_VOLTAGE = angleCalibrationVoltage != null ? angleCalibrationVoltage : Config.values.getDouble("ANGLE_CALIBRATION_VOLTAGE", ANGLE_CALIBRATION_VOLTAGE);
    }

    public void reset() {
        rawValue = 0.0;
        value = DEFAULT_ANGLE;
    }

    public void cleanUpReadings(LocalDateTime current) {
        Iterator<AngleReading> iter = readings.iterator();
        while(iter.hasNext()) {
            AngleReading reading = iter.next();
            Duration duration = Duration.between(reading.timestamp, current);
            if(duration.toMillis() > Config.values.getInt("RAW_READING_SPAN", 5000)) {
                iter.remove();
            }
        }
    }

    public abstract void calculateAndSetAverage();

    public void update(Motor.State motorState) {
        if(motorState == Motor.State.UP_SLOW) {
            rawValue += 0.005;
        } else if(motorState == Motor.State.UP_FAST) {
            rawValue += 0.01;
        } else if(motorState == Motor.State.DOWN_SLOW) {
            rawValue -= 0.01;
        } else if(motorState == Motor.State.DOWN_FAST) {
            rawValue -= 0.005;
        }
        // Keep within bounds
        rawValue = Math.max(SoftEncoder.MIN_RAW, Math.min(SoftEncoder.MAX_RAW, rawValue));
    }

    public boolean isMaxAngle() {
        return getAveragedValue() >= MAX_ANGLE;
    }

    public boolean isMinAngle() {
        return getAveragedValue() <= MIN_ANGLE;
    }

    public abstract void read() throws Exception;

    public double getAveragedValue() {
        return averagedValue;
    }

    public void setAveragedValue(double averagedValue) {
        this.averagedValue = averagedValue;
    }
}
