package com.spriton.therapypi.components.software;

import com.spriton.therapypi.components.Angle;

public class SoftAngle extends Angle {

    @Override
    public void calculateAndSetAverage() {
        setAveragedValue(this.value);
    }

    @Override
    public void read() throws Exception {

    }
}
