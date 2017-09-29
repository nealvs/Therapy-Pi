package com.spriton.therapypi.components;

import com.spriton.therapypi.Config;

public abstract class Motor {

    public static enum State { STOPPED, DOWN_SLOW, DOWN_MEDIUM, DOWN_FAST, UP_SLOW, UP_MEDIUM, UP_FAST };
    private State state = State.STOPPED;

    public static State getStateFromJoystick(Joystick joystick, boolean centerOnZero, boolean phidgetJoystick) {
        if(phidgetJoystick) {
            return getStateFromPhidgetJoystickValue(joystick, centerOnZero);
        } else {
            return getStateFromADCVoltage(joystick.value);
        }
    }

    private static State getStateFromPhidgetJoystickValue(Joystick joystick, boolean centerOnZero) {
        State state = Motor.State.STOPPED;
        double value = joystick.value;
        if(centerOnZero) {
            boolean goingUp = joystick.directionPin1On;
            if(Config.values.getBoolean("JOYSTICK_INVERT_DIRECTION", false)) {
                goingUp = !goingUp;
            }
            double slow = Config.values.getDouble("PHIDGET_JOYSTICK_MINIMAL_MOVEMENT", 100);
            double medium = Config.values.getDouble("PHIDGET_JOYSTICK_MEDIUM_MOVEMENT", 150);
            double fast = Config.values.getDouble("PHIDGET_JOYSTICK_FAST_MOVEMENT", 500);
            if (value >= slow && value < medium) {
                state = goingUp ? State.UP_SLOW : State.DOWN_SLOW;
            } else if (value >= medium && value < fast) {
                state = goingUp ? State.UP_MEDIUM : State.DOWN_MEDIUM;
            } else if (value >= fast) {
                state = goingUp ? State.UP_FAST : State.DOWN_FAST;
            }
        } else {
            // Todo
        }
        return state;
    }

    private static State getStateFromADCVoltage(double value) {
        State state = Motor.State.STOPPED;
        // Todo: Add scenarios where the voltage indicates that the joystick has been disconnected (ex: > 4.9 or < 0.1)
        // Wild and random joystick movements on a disconnect need to be figured out
        if (value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_UP_FAST", 4.0)) {
            state = Motor.State.UP_FAST;
        } else if (value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_UP_MEDIUM", 2.7)) {
            state = Motor.State.UP_MEDIUM;
        } else if (value >= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_UP_SLOW", 2.6)) {
            state = Motor.State.UP_SLOW;

        // Stopped state - 2.4 -> 2.6
        } else if (value <= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_SLOW", 2.4)
                && value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_MEDIUM", 2.3)) {
            state = Motor.State.DOWN_SLOW;
        } else if (value <= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_MEDIUM", 2.3)
                && value > Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_FAST", 1.0)) {
            state = Motor.State.DOWN_MEDIUM;
        } else if (value <= Config.values.getDouble("JOYSTICK_VOLTAGE_MOTOR_DOWN_FAST", 1.0)) {
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
