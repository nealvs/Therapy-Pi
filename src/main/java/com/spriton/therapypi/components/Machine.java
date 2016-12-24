package com.spriton.therapypi.components;

import com.google.gson.JsonObject;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;
import com.spriton.therapypi.database.ConfigValue;
import com.spriton.therapypi.database.DataAccess;
import com.spriton.therapypi.database.PatientSession;
import org.apache.log4j.Logger;

import java.time.ZoneId;
import java.util.TimeZone;

public class Machine {

    private static Logger log = Logger.getLogger(Machine.class);
    private static Machine instance;
    public static enum Type { HARDWARE, SOFTWARE };

    public int holdTimeConfig = 30;
    public String password = "knee";
    public TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of("America/Denver"));
    public boolean running = true;
    public Type type = Type.HARDWARE;
    public Angle angle;
    public Joystick joystick;
    public RotationMotor rotationMotor;
    public Switch motorSwitch;

    public PatientSession currentSession = new PatientSession();

    public Machine() {
        ConfigValue holdTime = DataAccess.getConfigValue("HOLD_TIME_SECONDS");
        if(holdTime != null) {
            holdTimeConfig = Integer.parseInt(holdTime.getConfigValue());
        }
        ConfigValue passwd = DataAccess.getConfigValue("PASSWORD");
        if(passwd != null) {
            password = passwd.getConfigValue();
        }
        ConfigValue timeZ = DataAccess.getConfigValue("TIMEZONE");
        if(timeZ != null) {
            timeZone = TimeZone.getTimeZone(ZoneId.of(timeZ.getConfigValue()));
        }
    }

    public void run() {
        running = true;

        new Thread() {
            @Override
            public void run() {
                while(running) {
                    try {
                        joystick.read();
                        angle.read();
                        angle.calculateAndSetAverage();

                        Motor.State originalMotorState = rotationMotor.getState();

                        Motor.State joystickMotorState = Motor.getStateFromJoystickValue(joystick.value);
                        rotationMotor.setState(joystickMotorState);

                        //log.info("Joystick State: " + joystickMotorState + " - " + joystick.value + " - " + angle.isMaxAngle() + ":" + angle.isMinAngle() + " - " + angle.getAveragedValue());

                        // For software only.  Uses the motor state to update the angle virtually.
                        angle.update(joystickMotorState);

                        if(angle.isMinAngle() && !(joystickMotorState.equals(Motor.State.UP_SLOW) || joystickMotorState.equals(Motor.State.UP_FAST))) {
                            motorSwitch.setState(Switch.State.OFF);
                            rotationMotor.setState(Motor.State.STOPPED);
                        } else if(angle.isMaxAngle() && !(joystickMotorState.equals(Motor.State.DOWN_SLOW) || joystickMotorState.equals(Motor.State.DOWN_FAST))) {
                            motorSwitch.setState(Switch.State.OFF);
                            rotationMotor.setState(Motor.State.STOPPED);
                        } else {
                            motorSwitch.setState(Switch.State.ON);
                        }
                        motorSwitch.applyState();

                        if (rotationMotor.getState() != originalMotorState) {
                            log.debug("Changing motor state to: " + joystickMotorState.name());
                        }
                        rotationMotor.applyState();

                        if(currentSession != null) {
                            currentSession.update(new AngleReading((int) angle.getAveragedValue()), rotationMotor.getState());
                        }

                        Thread.sleep(Config.values.getInt("MACHINE_LOOP_DELAY_MS", 100));
                    } catch(Exception ex) {
                        log.error("Error in machine run thread", ex);
                    }
                }
            }
        }.start();
    }

    public void reset() throws Exception {
        joystick.reset();
        angle.reset();
        currentSession.reset();
        running = false;
        Thread.sleep(100);
        run();
    }

    public static void setInstance(Type type) {
        if(type == Type.HARDWARE) {
            Machine.setInstance(Machine.create()
                    .type(type)
                    .angle(new HardEncoder())
                    .joystick(new HardJoystick())
                    .motorSwitch(new MotorRelaySwitch(Switch.State.ON))
                    .rotationMotor(new HardRotationMotor()));
        } else if(type == Type.SOFTWARE) {
            Machine.setInstance(Machine.create()
                    .type(type)
                    .angle(new SoftEncoder())
                    .joystick(new SoftJoystick())
                    .motorSwitch(new SoftSwitch(Switch.State.ON))
                    .rotationMotor(new SoftRotationMotor()));
        }
    }

    public void calibrate() {
        angle.ANGLE_CALIBRATION_VOLTAGE = angle.rawValue;
        angle.ANGLE_CALIBRATION_VOLTAGE = 3.2;
        ConfigValue value = DataAccess.getConfigValue("ANGLE_CALIBRATION_VOLTAGE");
        if(value == null) {
            value = new ConfigValue();
            value.setConfigKey("ANGLE_CALIBRATION_VOLTAGE");
            value.setConfigValue(Double.toString(angle.ANGLE_CALIBRATION_VOLTAGE));
            DataAccess.saveConfigValue(value);
        } else {
            value.setConfigValue(Double.toString(angle.ANGLE_CALIBRATION_VOLTAGE));
            DataAccess.updateConfigValue(value);
        }
    }

    public JsonObject toJson() {
        JsonObject info = new JsonObject();
        if(type != null) {
            info.addProperty("type", type.name());
        }
        if(joystick != null) {
            info.addProperty("joystick", joystick.value);
        }
        if(angle != null) {
            info.addProperty("angle", angle.getAveragedValue());
        }
        if(angle != null) {
            info.addProperty("rawAngle", angle.rawValue);
            info.addProperty("angleCalibrationVoltage", angle.ANGLE_CALIBRATION_VOLTAGE);
            info.addProperty("angleCalibrationDegree", angle.ANGLE_CALIBRATION_DEGREE);
        }
        if(rotationMotor != null) {
            info.addProperty("rotationMotor", rotationMotor.getState().name());
        }
        if(motorSwitch != null) {
            info.addProperty("motorSwitch", motorSwitch.getState().name());
        }
        if(currentSession != null) {
            info.add("session", currentSession.toJson());
        }

        info.addProperty("holdTimeConfig", holdTimeConfig);
        info.addProperty("password", password);
        if(timeZone != null) {
            info.addProperty("timeZone", timeZone.getDisplayName());
        }

        return info;
    }


    public static void setInstance(Machine instance) {
        Machine.instance = instance;
    }

    public static Machine instance() {
        return instance;
    }

    public static Machine create() {
        return new Machine();
    }

    public Machine angle(Angle angle) {
        this.angle = angle;
        return this;
    }

    public Machine type(Type type) {
        this.type = type;
        return this;
    }

    public Machine joystick(Joystick joystick) {
        this.joystick = joystick;
        return this;
    }

    public Machine motorSwitch(Switch motorSwitch) {
        this.motorSwitch = motorSwitch;
        return this;
    }

    public Machine rotationMotor(RotationMotor rotationMotor) {
        this.rotationMotor = rotationMotor;
        return this;
    }

}
