package com.spriton.therapypi;


import com.spriton.therapypi.components.hardware.HardEncoder;
import junit.framework.Assert;
import org.junit.Test;

public class EncoderTest {

    @Test
    public void testEncoderVoltageConvert() {
        double calibrationDegree = 0.0;
        double calibrationValue = 1.0;
        Assert.assertEquals(292.0, HardEncoder.getAngleFromRawVoltage(5.1, calibrationValue, calibrationDegree));
        Assert.assertEquals(292.0, HardEncoder.getAngleFromRawVoltage(5.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(255.5, HardEncoder.getAngleFromRawVoltage(4.5, calibrationValue, calibrationDegree));
        Assert.assertEquals(219.0, HardEncoder.getAngleFromRawVoltage(4.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(182.5, HardEncoder.getAngleFromRawVoltage(3.5, calibrationValue, calibrationDegree));
        Assert.assertEquals(146.0, HardEncoder.getAngleFromRawVoltage(3.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(109.5, HardEncoder.getAngleFromRawVoltage(2.5, calibrationValue, calibrationDegree));
        Assert.assertEquals(73.0,  HardEncoder.getAngleFromRawVoltage(2.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(36.5,  HardEncoder.getAngleFromRawVoltage(1.5, calibrationValue, calibrationDegree));
        Assert.assertEquals(0.0,   HardEncoder.getAngleFromRawVoltage(1.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(-36.5, HardEncoder.getAngleFromRawVoltage(0.5, calibrationValue, calibrationDegree));
        Assert.assertEquals(-73.0, HardEncoder.getAngleFromRawVoltage(0.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(-73.0, HardEncoder.getAngleFromRawVoltage(-0.1, calibrationValue, calibrationDegree));

        calibrationDegree = 90.0;
        calibrationValue = 2.0;
        Assert.assertEquals(-56.0,   HardEncoder.getAngleFromRawVoltage(0.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(17.0,   HardEncoder.getAngleFromRawVoltage(1.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(90.0,   HardEncoder.getAngleFromRawVoltage(2.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(163.0,   HardEncoder.getAngleFromRawVoltage(3.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(236.0,   HardEncoder.getAngleFromRawVoltage(4.0, calibrationValue, calibrationDegree));
        Assert.assertEquals(309.0,   HardEncoder.getAngleFromRawVoltage(5.0, calibrationValue, calibrationDegree));
    }
}
