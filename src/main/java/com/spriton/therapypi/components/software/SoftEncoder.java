package com.spriton.therapypi.components.software;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Angle;
import com.spriton.therapypi.components.AngleReading;
import com.spriton.therapypi.components.Motor;
import com.spriton.therapypi.components.hardware.HardJoystick;
import org.apache.log4j.Logger;

public class SoftEncoder extends Angle {

    private static Logger log = Logger.getLogger(HardJoystick.class);

    public static double MAX_RAW = 5.0;
    public static double MIN_RAW = 0.0;
    private static double DEGREE_RANGE = 365;

    public SoftEncoder() {
        DEGREE_RANGE = Config.values.getDouble("ENCODER_DEGREE_RANGE", DEGREE_RANGE);
        MAX_RAW = Config.values.getDouble("MAX_RAW_ENCODER_VALUE", MAX_RAW);
        MIN_RAW = Config.values.getDouble("MIN_RAW_ENCODER_VALUE", MIN_RAW);
    }

    @Override
    public void read() throws Exception {

        this.value = getAngleFromRawVoltage(this.rawValue, ANGLE_CALIBRATION_VOLTAGE, ANGLE_CALIBRATION_DEGREE);
        log.debug("Encoder Angle: " + this.value);

        AngleReading reading = new AngleReading((int)this.value);
        readings.add(reading);
        cleanUpReadings(reading.timestamp);

    }

    @Override
    public void update(Motor.State motorState) {
        if(motorState == Motor.State.UP_SLOW) {
            rawValue += 0.005;
        } else if(motorState == Motor.State.UP_MEDIUM) {
            rawValue += 0.01;
        } else if(motorState == Motor.State.UP_FAST) {
            rawValue += 0.02;
        } else if(motorState == Motor.State.DOWN_SLOW) {
            rawValue -= 0.005;
        } else if(motorState == Motor.State.DOWN_MEDIUM) {
            rawValue -= 0.01;
        } else if(motorState == Motor.State.DOWN_FAST) {
            rawValue -= 0.02;
        }
        // Keep within bounds
        rawValue = Math.max(SoftEncoder.MIN_RAW, Math.min(SoftEncoder.MAX_RAW, rawValue));
    }

    public static double getAngleFromRawVoltage(double voltage, double angleCalibrationVoltage, double angleCalibrationDegree) {
        // I was inverting this with MAX_RAW - voltage to invert it, now inverting isn't needed...
        voltage = Math.max(MIN_RAW, Math.min(MAX_RAW, voltage));
        double voltageDifference = voltage - angleCalibrationVoltage;
        double degreePerVoltage = DEGREE_RANGE / MAX_RAW;
        double result = voltageDifference * degreePerVoltage + angleCalibrationDegree;
        return result;
    }

}
