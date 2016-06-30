package com.spriton.therapypi.components.hardware;

import com.pi4j.io.gpio.*;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Switch;
import org.apache.log4j.Logger;

public class MotorRelaySwitch extends Switch {

    private static Logger log = Logger.getLogger(MotorRelaySwitch.class);
    private GpioController gpio;
    private GpioPinDigitalOutput switchPin;
    private GpioPinDigitalOutput switchPin2;

    public MotorRelaySwitch(State state) {
        super(state);
        gpio = GpioFactory.getInstance();
        Pin pin = getPin(Config.values.getInt("MOTOR_SWITCH_PIN", 6));
        switchPin = gpio.provisionDigitalOutputPin(pin, "Motor Relay Control", PinState.HIGH);

        Pin pin2 = getPin(Config.values.getInt("MOTOR_SWITCH_PIN2", 7));
        switchPin2 = gpio.provisionDigitalOutputPin(pin2, "Motor Relay Control 2", PinState.HIGH);
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
        if(switchPin2 != null) {
            if (getState() == State.ON && !switchPin2.isHigh()) {
                switchPin2.high();
            } else if (getState() == State.OFF && !switchPin2.isLow()) {
                switchPin2.low();
            }
        }
    }

    private Pin getPin(int number) {
        Pin pin = RaspiPin.GPIO_00;
        switch(number) {
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
            case 11:
                pin = RaspiPin.GPIO_11;
                break;
            case 12:
                pin = RaspiPin.GPIO_12;
                break;
            case 13:
                pin = RaspiPin.GPIO_13;
                break;
            case 14:
                pin = RaspiPin.GPIO_14;
                break;
            case 15:
                pin = RaspiPin.GPIO_15;
                break;
            case 16:
                pin = RaspiPin.GPIO_16;
                break;
            case 17:
                pin = RaspiPin.GPIO_17;
                break;
            case 18:
                pin = RaspiPin.GPIO_18;
                break;
            case 19:
                pin = RaspiPin.GPIO_19;
                break;
            case 20:
                pin = RaspiPin.GPIO_20;
                break;
            case 21:
                pin = RaspiPin.GPIO_21;
                break;
            case 22:
                pin = RaspiPin.GPIO_22;
                break;
            case 23:
                pin = RaspiPin.GPIO_23;
                break;
            case 24:
                pin = RaspiPin.GPIO_24;
                break;
            case 25:
                pin = RaspiPin.GPIO_25;
                break;
            case 26:
                pin = RaspiPin.GPIO_26;
                break;
            case 27:
                pin = RaspiPin.GPIO_27;
                break;
            case 28:
                pin = RaspiPin.GPIO_28;
                break;
            case 29:
                pin = RaspiPin.GPIO_29;
                break;
        }
        return pin;
    }

}
