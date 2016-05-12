package com.spriton.therapypi.components;

public abstract class Joystick {

    public double rawValue = 2.5;
    public double value = 2.5;

    public void reset() {
        rawValue = 2.5;
        value = 2.5;
    }

    public abstract void read() throws Exception;
}
