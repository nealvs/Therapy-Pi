package com.spriton.therapypi.components;

public abstract class Motor {

    public static enum State { STOPPED, DOWN_SLOW, DOWN_FAST, UP_SLOW, UP_FAST };
    private State state = State.STOPPED;

    public static State getStateFromJoystickValue(double value) {
        State state = Motor.State.STOPPED;
        if (value > 100 && value < 612) {
            state = Motor.State.UP_SLOW;
        } else if(value >= 612) {
            state = Motor.State.UP_FAST;
        } else if (value < -100 && value > -612) {
            state = Motor.State.DOWN_SLOW;
        } else if(value <= -612) {
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
