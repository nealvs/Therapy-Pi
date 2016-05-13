package com.spriton.therapypi.components.hardware;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Angle;
import com.spriton.therapypi.components.AngleReading;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class HardEncoder extends Angle {

    private static Logger log = Logger.getLogger(HardJoystick.class);

    private static double MAX_RAW = 5.0;
    private static double MIN_RAW = 0.0;
    private static double DEGREE_RANGE = 365;
    private double ANGLE_CALIBRATION_VOLTAGE = 2.5;
    private double ANGLE_CALIBRATION_DEGREE = 1.0;

    public HardEncoder() {
        DEGREE_RANGE = Config.values.getDouble("ENCODER_DEGREE_RANGE", DEGREE_RANGE);
        MAX_RAW = Config.values.getDouble("MAX_RAW_ENCODER_VALUE", MAX_RAW);
        MIN_RAW = Config.values.getDouble("MIN_RAW_ENCODER_VALUE", MIN_RAW);
        ANGLE_CALIBRATION_DEGREE = Config.values.getDouble("ANGLE_CALIBRATION_DEGREE", ANGLE_CALIBRATION_DEGREE);
        ANGLE_CALIBRATION_VOLTAGE = Config.values.getDouble("ANGLE_CALIBRATION_VOLTAGE", ANGLE_CALIBRATION_VOLTAGE);
    }

    @Override
    public void read() throws Exception {
        String command = Config.values.getString("ANALOG_TO_DIGITAL_BIN", "adc");
        int channel = Config.values.getInt("ENCODER_INPUT_CHANNEL", 7);
        Process process = new ProcessBuilder()
                .redirectErrorStream(true)
                .command(command, Integer.toString(channel))
                .start();

        InputStream stdOut = process.getInputStream();
        if(!process.waitFor(Config.values.getInt("ENCODER_READ_TIMEOUT_MS", 500), TimeUnit.MILLISECONDS)) {
            log.error("Read from hardware encoder timed out.");
            process.destroy();
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(stdOut));
            String response = in.readLine();
            log.debug("Encoder Raw Value: " + response);
            this.rawValue = Double.parseDouble(response);
            this.value = getAngleFromRawVoltage(this.rawValue, ANGLE_CALIBRATION_VOLTAGE, ANGLE_CALIBRATION_DEGREE);
            log.debug("Encoder Angle: " + this.value);

            AngleReading reading = new AngleReading((int)this.value);
            readings.add(reading);
            cleanUpReadings(reading.timestamp);
        }
    }

    @Override
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

    public static double getAngleFromRawVoltage(double voltage, double angleCalibrationVoltage, double angleCalibrationDegree) {
        voltage = Math.max(MIN_RAW, Math.min(MAX_RAW, voltage));
        double voltageDifference = voltage - angleCalibrationVoltage;
        double degreePerVoltage = DEGREE_RANGE / MAX_RAW;
        double result = voltageDifference * degreePerVoltage + angleCalibrationDegree;
        return result;
    }

}
