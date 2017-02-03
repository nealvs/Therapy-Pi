package com.spriton.therapypi.components;

import com.spriton.therapypi.Config;

public abstract class Motor {

    public static enum State { STOPPED, DOWN_SLOW, DOWN_MEDIUM, DOWN_FAST, UP_SLOW, UP_MEDIUM, UP_FAST };
    private State state = State.STOPPED;

    public static State getStateFromJoystickValue(double value) {
        State state = Motor.State.STOPPED;
        // Todo: Add scenarios where the voltage indicates that the joystick has been disconnected (ex: > 4.9 or < 0.1)
        // Wild and random joystick movements on a disconnect need to be figured out
        if (value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_UP_FAST", 4.0)) {
            state = Motor.State.UP_FAST;
        } else if(value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_UP_MEDIUM", 2.7)) {
            state = Motor.State.UP_MEDIUM;
        } else if(value >= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_UP_SLOW", 2.6)) {
            state = Motor.State.UP_SLOW;

            // Stopped state - 2.4 -> 2.6
        } else if (value <= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_SLOW", 2.4)
                && value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_MEDIUM", 2.3)) {
            state = Motor.State.DOWN_SLOW;
        } else if (value <= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_MEDIUM", 2.3)
                && value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_FAST", 1.0)) {
            state = Motor.State.DOWN_MEDIUM;
        } else if(value <= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_FAST", 1.0)) {
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
