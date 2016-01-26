package com.spriton.therapypi.components;

public class Machine {

    private Angle angle;
    private Joystick joystick;
    private LiftMotor liftMotor;
    private RotationMotor rotationMotor;
    private LimitSwitch limitSwitch;

    public void run() {


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
