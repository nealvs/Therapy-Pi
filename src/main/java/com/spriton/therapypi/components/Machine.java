package com.spriton.therapypi.components;

import com.google.gson.JsonObject;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;
import com.spriton.therapypi.database.PatientSession;
import org.apache.log4j.Logger;

public class Machine {

    private static Logger log = Logger.getLogger(Machine.class);
    private static Machine instance;
    public static enum Type { HARDWARE, SOFTWARE };

    public boolean running = true;
    public Type type = Type.HARDWARE;
    public Angle angle;
    public Joystick joystick;
    public RotationMotor rotationMotor;
    public Switch motorSwitch;

    public PatientSession currentSession = new PatientSession();

    public void run() {
        running = true;

        new Thread() {
            @Override
            public void run() {
                while(running) {
                    try {
                        joystick.read();
                        angle.read();

                        Motor.State originalMotorState = rotationMotor.getState();

                        Motor.State joystickMotorState = Motor.getStateFromJoystickValue(joystick.value);
                        rotationMotor.setState(joystickMotorState);

                        // For software only.  Uses the motor state to update the angle virtually.
                        angle.update(joystickMotorState);

                        if(angle.isMaxAngle() && (joystickMotorState.equals(Motor.State.DOWN_SLOW) || joystickMotorState.equals(Motor.State.DOWN_FAST))) {
                            motorSwitch.setState(Switch.State.OFF);
                            rotationMotor.setState(Motor.State.STOPPED);
                        } else if(angle.isMinAngle() && (joystickMotorState.equals(Motor.State.UP_SLOW) || joystickMotorState.equals(Motor.State.UP_FAST))) {
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
                            currentSession.addAngleReading(new AngleReading((int)angle.value), rotationMotor.getState());
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
                    .motorSwitch(new MotorRelaySwitch(Switch.State.OFF))
                    .rotationMotor(new HardRotationMotor()));
        } else if(type == Type.SOFTWARE) {
            Machine.setInstance(Machine.create()
                    .type(type)
                    .angle(new SoftAngle())
                    .joystick(new SoftJoystick())
                    .motorSwitch(new SoftSwitch(Switch.State.OFF))
                    .rotationMotor(new SoftRotationMotor()));
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
            info.addProperty("angle", angle.value);
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
