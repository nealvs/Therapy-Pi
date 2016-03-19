package com.spriton.therapypi.components.hardware;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.RotationMotor;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class HardRotationMotor extends RotationMotor {

    private static Logger log = Logger.getLogger(HardRotationMotor.class);

    @Override
    public void applyState() throws Exception {
        String command = Config.values.getString("DIGITAL_TO_ANALOG_BIN", "dac");
        double outputValue = getValue();
        Process process = new ProcessBuilder()
                .redirectErrorStream(true)
                .command("sudo", command, Double.toString(outputValue), Double.toString(outputValue))
                .start();
        InputStream stdOut = process.getInputStream();
        if(!process.waitFor(Config.values.getInt("MOTOR_WRITE_TIMEOUT_MS", 500), TimeUnit.MILLISECONDS)) {
            log.error("Write to motor controller timed out.");
            process.destroy();
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(stdOut));
            String response = in.readLine();
            log.debug("Motor Write Response: " + response);
        }
    }

    public double getValue() {
        double value = Config.values.getDouble("MOTOR_STOPPED_VOLTAGE", 2.5);
        if(this.getState() == State.DOWN_FAST) {
            value = Config.values.getDouble("MOTOR_DOWN_FAST_VOLTAGE", 0);
        } else if(this.getState() == State.DOWN_SLOW) {
            value = Config.values.getDouble("MOTOR_DOWN_SLOW_VOLTAGE", 1.25);
        } else if(this.getState() == State.UP_FAST) {
            value = Config.values.getDouble("MOTOR_UP_FAST_VOLTAGE", 3.75);
        } else if(this.getState() == State.UP_SLOW) {
            value = Config.values.getDouble("MOTOR_UP_SLOW_VOLTAGE", 4.99);
        }
        return value;
    }

}
