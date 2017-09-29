package com.spriton.therapypi.components.hardware;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Joystick;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class HardJoystick extends Joystick {

    private static Logger log = Logger.getLogger(HardJoystick.class);

    public HardJoystick() {

    }

    @Override
    public void read() throws Exception {
        String command = Config.values.getString("ANALOG_TO_DIGITAL_BIN", "adc");
        int channel = Config.values.getInt("JOYSTICK_INPUT_CHANNEL", 6);
        Process process = new ProcessBuilder()
                .redirectErrorStream(true)
                .command(command, Integer.toString(channel))
                .start();
        InputStream stdOut = process.getInputStream();
        if(!process.waitFor(Config.values.getInt("JOYSTICK_READ_TIMEOUT_MS", 500), TimeUnit.MILLISECONDS)) {
            log.error("Read from hardware joystick timed out.");
            process.destroy();
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(stdOut));
            String response = in.readLine();
            log.debug("Joystick Value: " + response);
            this.value = Double.parseDouble(response);
        }
    }


}
