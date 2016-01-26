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
        Machine machine = null;

        if(Config.config.getBoolean("hardware", false)) {
            machine = Machine.create()
                    .angle(new PotAngle())
                    .joystick(new HardJoystick())
                    .liftMotor(new HardLiftMotor())
                    .rotationMotor(new HardRotationMotor())
                    .limitSwitch(new HardLimitSwitch(Switch.STATE.OFF));

        } else {
            machine = Machine.create()
                    .angle(new SoftAngle())
                    .joystick(new SoftJoystick())
                    .liftMotor(new SoftLiftMotor())
                    .rotationMotor(new SoftRotationMotor())
                    .limitSwitch(new SoftLimitSwitch(Switch.STATE.OFF));
        }

        machine.run();
    }

}
