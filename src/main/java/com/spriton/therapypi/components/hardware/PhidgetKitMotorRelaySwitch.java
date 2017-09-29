package com.spriton.therapypi.components.hardware;

import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Switch;
import org.apache.log4j.Logger;

public class PhidgetKitMotorRelaySwitch extends Switch {

    private static Logger log = Logger.getLogger(PhidgetKitMotorRelaySwitch.class);

    private PhidgetsInterfaceBoard phidgetBoard;
    private int switchPin = 0;

    public PhidgetKitMotorRelaySwitch(PhidgetsInterfaceBoard phidgetBoard, int switchPin, State state) throws Exception {
        super(state);
        this.phidgetBoard = phidgetBoard;
        this.switchPin = switchPin;
        applyState();
    }

    @Override
    public void applyState() throws Exception {
        // Adjust output pin
        if (getState() == State.ON && !phidgetBoard.getKit().getOutputState(switchPin)) {
            phidgetBoard.getKit().setOutputState(switchPin, true);
            log.debug("Phidget Motor Switch " + switchPin + ": ON");
        } else if (getState() == State.OFF && phidgetBoard.getKit().getOutputState(switchPin)) {
            phidgetBoard.getKit().setOutputState(switchPin, false);
            log.debug("Phidget Motor Switch " + switchPin + ": OFF");
        }

    }

}
