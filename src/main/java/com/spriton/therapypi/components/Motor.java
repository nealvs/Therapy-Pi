package com.spriton.therapypi.components;

public abstract class Motor {

    public static enum State { STOPPED, DOWN_SLOW, DOWN_FAST, UP_SLOW, UP_FAST };

    private State state = State.STOPPED;

    public Motor() {

    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
