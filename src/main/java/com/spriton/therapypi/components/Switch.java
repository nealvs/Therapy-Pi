package com.spriton.therapypi.components;

public abstract class Switch {

    public static enum STATE { ON, OFF };

    private STATE state;

    public Switch(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

}
