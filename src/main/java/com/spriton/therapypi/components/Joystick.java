package com.spriton.therapypi.components;

import java.io.IOException;

public abstract class Joystick {

    public double rawValue = 0.0;
    public double value = 0.0;

    public void reset() {
        rawValue = 0.0;
        value = 0.0;
    }

    public abstract void read() throws Exception;
}
