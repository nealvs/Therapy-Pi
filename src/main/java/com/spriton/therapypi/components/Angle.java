package com.spriton.therapypi.components;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.KneeAngleLookup;
import com.spriton.therapypi.database.DataAccess;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Angle {

    protected boolean connected = false;
    protected double rawValue = 2.0;
    protected double value = DEFAULT_ANGLE;
    private double averagedValue = DEFAULT_ANGLE;
    private int kneeValue = (int) DEFAULT_ANGLE;

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
        kneeValue = KneeAngleLookup.getKneeAngle(value);
    }

    public void cleanUpReadings(LocalDateTime current) {
        Iterator<AngleReading> iter = readings.iterator();
        while(iter.hasNext()) {
            AngleReading reading = iter.next();
            Duration duration = Duration.between(reading.timestamp, current);
            if(duration.toMillis() > Config.values.getInt("RAW_READING_SPAN", 0)) {
                iter.remove();
            }
        }
    }

    public void calculateAndSetAverage() {
        if(readings.size() > 0) {
            double total = 0.0;
            for(AngleReading reading : readings) {
                total += reading.angle;
            }
            setAveragedValue(total / readings.size());
        } else {
            setAveragedValue(this.value);
        }
    }

    public abstract void update(Motor.State motorState);

    public boolean isMaxAngle() {
        return getAveragedValue() >= MAX_ANGLE || value >= MAX_ANGLE;
    }

    public boolean isMinAngle() {
        return getAveragedValue() <= MIN_ANGLE || value <= MIN_ANGLE;
    }

    public boolean isConnected() {
        return connected;
    }

    public abstract void read() throws Exception;

    public double getAveragedValue() {
        return averagedValue;
    }

    public void setAveragedValue(double averagedValue) {
        this.averagedValue = averagedValue;
        this.kneeValue = KneeAngleLookup.getKneeAngle(this.averagedValue);
    }

    public int getKneeValue() {
        return kneeValue;
    }

    public void setKneeValue(int kneeValue) {
        this.kneeValue = kneeValue;
    }
}
