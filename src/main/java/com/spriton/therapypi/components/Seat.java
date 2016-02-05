package com.spriton.therapypi.components;

public abstract class Seat {

    public static double MIN_HEIGHT = 0;
    public static double MAX_HEIGHT = 100;
    public double value = 0.0;

    public void reset() {
        value = 0.0;
    }

    public void update(Motor.State motorState) {
        if(motorState == Motor.State.UP_SLOW) {
            value += 1;
        } else if(motorState == Motor.State.UP_FAST) {
            value += 3;
        } else if(motorState == Motor.State.DOWN_SLOW) {
            value -= 1;
        } else if(motorState == Motor.State.DOWN_FAST) {
            value -= 3;
        }
        // Keep within bounds
        value = Math.max(0, Math.min(100, value));
    }

    public boolean isMaxHeight() {
        return value >= MAX_HEIGHT;
    }
    public boolean isMinHeight() {
        return value <= MIN_HEIGHT;
    }
}
