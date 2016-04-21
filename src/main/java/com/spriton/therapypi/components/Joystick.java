package com.spriton.therapypi.components;

public abstract class Joystick {

    public double rawValue = 0.0;
    public double value = 0.0;

    public void reset() {
        rawValue = 2.5;
        value = 2.5;
    }

    public abstract void read() throws Exception;
}
