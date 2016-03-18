package com.spriton.therapypi.components;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

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

    public Stopwatch sessionStopwatch = Stopwatch.createUnstarted();
    public Stopwatch holdStopwatch = Stopwatch.createUnstarted();

    public Motor getCurrentMotor() {
        return rotationMotor;
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

                        Motor currentMotor = getCurrentMotor();
                        if(currentMotor != null) {
                            Motor.State currentMotorState = currentMotor.getState();
                            Motor.State newMotorState = Motor.getStateFromJoystickValue(joystick.value);

                            currentMotor.setState(newMotorState);
                            currentMotor.applyState();

                            // For software only.  Uses the motor state to update the angle virutally.
                            angle.update(newMotorState);

                            if(angle.isMaxAngle() || angle.isMinAngle()) {
                                rotationMotor.setState(Motor.State.STOPPED);
                            }
                        }


                        Thread.sleep(100);
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
        sessionStopwatch = Stopwatch.createUnstarted();
        holdStopwatch = Stopwatch.createUnstarted();
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
        if (type != null) {
            info.addProperty("type", type.name());
        }
        if (joystick != null) {
            info.addProperty("joystick", joystick.value);
        }
        if (angle != null) {
            info.addProperty("angle", angle.value);
        }
        if (rotationMotor != null) {
            info.addProperty("rotationMotor", rotationMotor.getState().name());
        }
        if (motorSwitch != null) {
            info.addProperty("motorSwitch", motorSwitch.getState().name());
        }
        if (holdStopwatch != null) {
            info.addProperty("holdTime", holdStopwatch.elapsed(TimeUnit.SECONDS));
        }
        if (sessionStopwatch != null) {
            info.addProperty("sessionTime", sessionStopwatch.elapsed(TimeUnit.SECONDS));
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
