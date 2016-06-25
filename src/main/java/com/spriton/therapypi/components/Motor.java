package com.spriton.therapypi.components;

import com.spriton.therapypi.Config;

public abstract class Motor {

    public static enum State { STOPPED, DOWN_SLOW, DOWN_FAST, UP_SLOW, UP_FAST };
    private State state = State.STOPPED;

    public static State getStateFromJoystickValue(double value) {
        State state = Motor.State.STOPPED;
        if (value < Config.values.getInt("JOYSTICK_VOLTAGE_MOTOR_UP_FAST", 1)) {
            state = Motor.State.UP_FAST;
        } else if(value <= Config.values.getInt("JOYSTICK_VOLTAGE_MOTOR_UP_SLOW", 2)) {
            state = Motor.State.UP_SLOW;
        } else if (value > Config.values.getInt("JOYSTICK_VOLTAGE_MOTOR_DOWN_SLOW", 3) && value < Config.values.getInt("JOYSTICK_VOLTAGE_MOTOR_DOWN_FAST", 4)) {
            state = Motor.State.DOWN_SLOW;
        } else if(value >= Config.values.getInt("JOYSTICK_VOLTAGE_MOTOR_DOWN_FAST", 4)) {
            state = Motor.State.DOWN_FAST;
        }
        return state;
    }

    public Motor() {

    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public abstract void applyState() throws Exception;

}
