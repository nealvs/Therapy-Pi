package com.spriton.therapypi.components;

import com.google.common.base.Stopwatch;

public class Machine {

    private static Machine instance;

    public Angle angle;
    public Joystick joystick;
    public LiftMotor liftMotor;
    public RotationMotor rotationMotor;
    public LimitSwitch limitSwitch;
    public MotorType activeMotor = MotorType.NONE;

    public static enum MotorType { NONE, LIFT_MOTOR, ROTATION_MOTOR };

    public Stopwatch sessionStopwatch = Stopwatch.createUnstarted();
    public Stopwatch holdStopwatch = Stopwatch.createUnstarted();

    public void run() {

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
