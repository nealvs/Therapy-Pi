package com.spriton.therapypi.components;

import com.google.common.base.Stopwatch;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;

public class Machine {

    private static Machine instance;
    public static enum Type { HARDWARE, SOFTWARE };

    public Type type = Type.HARDWARE;
    public Angle angle;
    public Joystick joystick;
    public Seat seat;
    public LiftMotor liftMotor;
    public RotationMotor rotationMotor;
    public LimitSwitch limitSwitch;
    public MotorType activeMotor = MotorType.NONE;

    public static enum MotorType { NONE, LIFT_MOTOR, ROTATION_MOTOR };

    public Stopwatch sessionStopwatch = Stopwatch.createUnstarted();
    public Stopwatch holdStopwatch = Stopwatch.createUnstarted();

    public void run() {

    }

    public void reset() {
        joystick.reset();
        angle.reset();
        activeMotor = MotorType.NONE;
        sessionStopwatch = Stopwatch.createUnstarted();
        holdStopwatch = Stopwatch.createUnstarted();
    }

    public static void setInstance(Type type) {
        if(type == Type.HARDWARE) {
            Machine.setInstance(Machine.create()
                    .angle(new PotAngle())
                    .joystick(new HardJoystick())
                    .seat(new HardSeat())
                    .liftMotor(new HardLiftMotor())
                    .rotationMotor(new HardRotationMotor())
                    .limitSwitch(new HardLimitSwitch(Switch.State.OFF)));
        } else if(type == Type.SOFTWARE) {
            Machine.setInstance(Machine.create()
                    .angle(new SoftAngle())
                    .joystick(new SoftJoystick())
                    .seat(new SoftSeat())
                    .liftMotor(new SoftLiftMotor())
                    .rotationMotor(new SoftRotationMotor())
                    .limitSwitch(new SoftLimitSwitch(Switch.State.OFF)));
        }
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
