package com.spriton.therapypi.components;

public abstract class Motor {

    public static enum STATE { STOPPED, DOWN_SLOW, DOWN_FAST, UP_SLOW, UP_FAST };

    private STATE state = STATE.STOPPED;

    public Motor() {

    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

}
