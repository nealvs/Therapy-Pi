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
    public Seat seat;
    public LiftMotor liftMotor;
    public RotationMotor rotationMotor;
    public LimitSwitch limitSwitch;
    public MotorType activeMotor = MotorType.LIFT_MOTOR;

    public static enum MotorType { NONE, LIFT_MOTOR, ROTATION_MOTOR };

    public Stopwatch sessionStopwatch = Stopwatch.createUnstarted();
    public Stopwatch holdStopwatch = Stopwatch.createUnstarted();

    public Motor getCurrentMotor() {
        if(activeMotor == MotorType.LIFT_MOTOR) {
            return liftMotor;
        } else if(activeMotor == MotorType.ROTATION_MOTOR) {
            return rotationMotor;
        }
        return null;
    }

    public void run() {
        running = true;

        new Thread() {
            @Override
            public void run() {
                while(running) {
                    try {

                        Motor currentMotor = getCurrentMotor();
                        if(currentMotor != null) {
                            Motor.State currentState = currentMotor.getState();
                            Motor.State newState = Motor.getStateFromJoystickValue(joystick.value);

                            if(currentState != newState) {
                                currentMotor.setState(newState);
                            }

                            if(activeMotor == MotorType.LIFT_MOTOR) {
                                seat.update(newState);
                            } else if(activeMotor == MotorType.ROTATION_MOTOR) {
                                angle.update(newState);
                            }

                            if(seat.isMaxHeight()) {
                                activeMotor = MotorType.ROTATION_MOTOR;
                            }

                            if(seat.isMaxHeight() || seat.isMinHeight()) {
                                liftMotor.setState(Motor.State.STOPPED);
                            }

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
        seat.reset();
        activeMotor = MotorType.LIFT_MOTOR;
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
                    .angle(new PotAngle())
                    .joystick(new HardJoystick())
                    .seat(new HardSeat())
                    .liftMotor(new HardLiftMotor())
                    .rotationMotor(new HardRotationMotor())
                    .limitSwitch(new HardLimitSwitch(Switch.State.OFF)));
        } else if(type == Type.SOFTWARE) {
            Machine.setInstance(Machine.create()
                    .type(type)
                    .angle(new SoftAngle())
                    .joystick(new SoftJoystick())
                    .seat(new SoftSeat())
                    .liftMotor(new SoftLiftMotor())
                    .rotationMotor(new SoftRotationMotor())
                    .limitSwitch(new SoftLimitSwitch(Switch.State.OFF)));
        }
    }

    public JsonObject toJson() {
        JsonObject info = new JsonObject();
        if (type != null) {
            info.addProperty("type", type.name());
        }
        if (seat != null) {
            info.addProperty("seat", seat.value);
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
        if (Machine.instance() != null) {
            info.addProperty("activeMotor", activeMotor.name());
        }
        if (liftMotor != null) {
            info.addProperty("liftMotor", liftMotor.getState().name());
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

    public Machine seat(Seat seat) {
        this.seat = seat;
        return this;
    }

    public Machine liftMotor(LiftMotor liftMotor) {
        this.liftMotor = liftMotor;
        return this;
    }

    public Machine rotationMotor(RotationMotor rotationMotor) {
        this.rotationMotor = rotationMotor;
        return this;
    }

    public Machine limitSwitch(LimitSwitch limitSwitch) {
        this.limitSwitch = limitSwitch;
        return this;
    }

}
