package com.spriton.therapypi;

import com.spriton.therapypi.components.*;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;
import org.apache.log4j.Logger;

public class MainController {

    private static Logger log = Logger.getLogger(MainController.class);

    public static void main(String[] args) throws Exception {
        Config.init(args, "therapypi.properties");
        DataServer.init();


        if(Config.config.getBoolean("hardware", false)) {
            log.info("Setting up hardware machine");
            Machine.setInstance(Machine.create()
                    .angle(new PotAngle())
                    .joystick(new HardJoystick())
                    .liftMotor(new HardLiftMotor())
                    .rotationMotor(new HardRotationMotor())
                    .limitSwitch(new HardLimitSwitch(Switch.State.OFF)));

        } else {
            log.info("Setting up software machine");
            Machine.setInstance(Machine.create()
                    .angle(new SoftAngle())
                    .joystick(new SoftJoystick())
                    .liftMotor(new SoftLiftMotor())
                    .rotationMotor(new SoftRotationMotor())
                    .limitSwitch(new SoftLimitSwitch(Switch.State.OFF)));
        }

        log.info("Running machine...");
        Machine.instance().run();
    }

}
