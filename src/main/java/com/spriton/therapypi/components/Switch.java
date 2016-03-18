package com.spriton.therapypi.components;

public abstract class Switch {

    public static enum State { ON, OFF };

    private State state;

    public Switch(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public abstract void applyState() throws Exception;

}
