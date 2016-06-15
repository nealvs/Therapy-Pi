package com.spriton.therapypi.components.hardware;

import com.pi4j.io.gpio.*;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Switch;
import org.apache.log4j.Logger;

public class MotorRelaySwitch extends Switch {

    private static Logger log = Logger.getLogger(MotorRelaySwitch.class);
    private GpioController gpio;
    private GpioPinDigitalOutput switchPin;

    public MotorRelaySwitch(State state) {
        super(state);
        gpio = GpioFactory.getInstance();
        Pin pin = RaspiPin.GPIO_00;
        switch(Config.values.getInt("MOTOR_SWITCH_PIN", 6)) {
            case 0:
                pin = RaspiPin.GPIO_00;
                break;
            case 1:
                pin = RaspiPin.GPIO_01;
                break;
            case 2:
                pin = RaspiPin.GPIO_02;
                break;
            case 3:
                pin = RaspiPin.GPIO_03;
                break;
            case 4:
                pin = RaspiPin.GPIO_04;
                break;
            case 5:
                pin = RaspiPin.GPIO_05;
                break;
            case 6:
                pin = RaspiPin.GPIO_06;
                break;
            case 7:
                pin = RaspiPin.GPIO_07;
                break;
            case 8:
                pin = RaspiPin.GPIO_08;
                break;
            case 9:
                pin = RaspiPin.GPIO_09;
                break;
            case 10:
                pin = RaspiPin.GPIO_10;
                break;
        }
        switchPin = gpio.provisionDigitalOutputPin(pin, "Motor Relay Control", PinState.HIGH);
    }

    @Override
    public void applyState() throws Exception {
        if(switchPin != null) {
            if (getState() == State.ON && !switchPin.isHigh()) {
                switchPin.high();
                log.debug("Motor Switch: ON");
            } else if (getState() == State.OFF && !switchPin.isLow()) {
                switchPin.low();
                log.debug("Motor Switch: OFF");
            }
        }
    }

}
