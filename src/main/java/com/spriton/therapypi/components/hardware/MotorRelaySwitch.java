package com.spriton.therapypi.components.hardware;

import com.pi4j.io.gpio.*;
import com.spriton.therapypi.components.Switch;

public class MotorRelaySwitch extends Switch {

    private GpioController gpio;
    private GpioPinDigitalOutput switchPin;

    public MotorRelaySwitch(State state) {
        super(state);
        //gpio = GpioFactory.getInstance();
        //switchPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Motor Relay Control", PinState.HIGH);
    }

    @Override
    public void applyState() throws Exception {
        if(switchPin != null) {
            if (getState() == State.OFF) {
                switchPin.high();
            } else if (getState() == State.ON) {
                switchPin.low();
            }
        }
    }

}
