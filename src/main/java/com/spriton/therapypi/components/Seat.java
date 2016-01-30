package com.spriton.therapypi.components;

public abstract class Seat {

    public double rawValue = 0.0;
    public double value = 0.0;

    public void reset() {
        rawValue = 0.0;
        value = 0.0;
    }

}
