package com.spriton.therapypi.components;

public abstract class Joystick {

    public boolean directionPin1On = false;
    public boolean directionPin2On = false;
    public double value = 2.5;

    public void reset() {
        value = 2.5;
    }

    public abstract void read() throws Exception;
}
