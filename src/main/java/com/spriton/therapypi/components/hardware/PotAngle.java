package com.spriton.therapypi.components.hardware;

import com.spriton.therapypi.components.Angle;

public class PotAngle extends Angle {

    public static int SpiChannel = 0;
    public static int TotalDegrees = 300;

    public PotAngle() {



    }

    @Override
    public void calculateAndSetAverage() {
        setAveragedValue(this.value);
    }

    @Override
    public void read() {

    }
}
