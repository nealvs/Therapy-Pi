package com.spriton.therapypi.components;

public abstract class Angle {

    public double rawValue = 0.0;
    public double value = DEFAULT_ANGLE;

    public static double DEFAULT_ANGLE = 90;
    public static double MAX_ANGLE = 160;
    public static double MIN_ANGLE = -5;

    public void reset() {
        rawValue = 0.0;
        value = DEFAULT_ANGLE;
    }

    public void update(Motor.State motorState) {
        if(motorState == Motor.State.UP_SLOW) {
            value -= 0.5;
        } else if(motorState == Motor.State.UP_FAST) {
            value -= 1;
        } else if(motorState == Motor.State.DOWN_SLOW) {
            value += 0.5;
        } else if(motorState == Motor.State.DOWN_FAST) {
            value += 1;
        }
        // Keep within bounds
        value = Math.max(-5, Math.min(160, value));
    }

    public boolean isMaxAngle() {
        return value >= MAX_ANGLE;
    }

    public boolean isMinAngle() {
        return value <= MIN_ANGLE;
    }
}
