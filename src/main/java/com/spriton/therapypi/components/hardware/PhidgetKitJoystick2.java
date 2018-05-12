package com.spriton.therapypi.components.hardware;

//import com.phidgets.event.InputChangeEvent;
//import com.phidgets.event.InputChangeListener;
//import com.phidgets.event.SensorChangeEvent;
//import com.phidgets.event.SensorChangeListener;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.Joystick;
import com.spriton.therapypi.components.Machine;
import org.apache.log4j.Logger;

public class PhidgetKitJoystick2 extends Joystick {

    private static Logger log = Logger.getLogger(PhidgetKitJoystick2.class);
    private PhidgetsInterfaceBoard2 phidgetBoard;
    private Machine machine;

    public PhidgetKitJoystick2(PhidgetsInterfaceBoard2 phidgetBoard) {
        this.phidgetBoard = phidgetBoard;

        // Add listener for joystick value S1 on config index 0
//        phidgetBoard.getKit().addSensorChangeListener(new SensorChangeListener() {
//            @Override
//            public void sensorChanged(SensorChangeEvent sensorChangeEvent) {
//                try {
//                    int inputIndex = Config.values.getInt("PHIDGET_JOYSTICK_ANALOG_INPUT", 0);
//                    if (sensorChangeEvent.getIndex() == inputIndex) {
//                        log.debug("Phidget Sensor " + inputIndex + " value=" + sensorChangeEvent.getValue());
//                        value = sensorChangeEvent.getValue();
//
//                        if(machine != null) {
//                            machine.updateStateBasedOnCurrentInputs();
//                            machine.updateSessionBasedOnInputs();
//                        }
//                    }
//                } catch(Exception ex) {
//                    log.error("Error handling sensor input change", ex);
//                }
//            }
//        });

        // Add listener for joystick direction signal 1
//        phidgetBoard.getKit().addInputChangeListener(new InputChangeListener() {
//            public void inputChanged(InputChangeEvent oe) {
//                try {
//                    int inputIndex = Config.values.getInt("PHIDGET_JOYSTICK_DIRECTION_INPUT1", 0);
//                    int reverseOutputIndex = Config.values.getInt("PHIDGET_JOYSTICK_REVERSE_OUTPUT1", 0);
//                    if (oe.getIndex() == inputIndex) {
//                        log.debug("Phidget Joystick Direction 1 - " + inputIndex + " Pin Changed. ON=" + oe.getState());
//                        directionPin1On = oe.getState();
//                        phidgetBoard.getKit().setOutputState(reverseOutputIndex, !directionPin1On);
//                    }
//                } catch(Exception ex) {
//                    log.error("Error handling signal reversal for direction signal 1", ex);
//                }
//            }
//        });

        // Ad listener for joystick direction signal 2
//        phidgetBoard.getKit().addInputChangeListener(new InputChangeListener() {
//            public void inputChanged(InputChangeEvent oe) {
//                try {
//                    int inputIndex = Config.values.getInt("PHIDGET_JOYSTICK_DIRECTION_INPUT2", 1);
//                    int reverseOutputIndex = Config.values.getInt("PHIDGET_JOYSTICK_REVERSE_OUTPUT2", 1);
//                    if (oe.getIndex() == inputIndex) {
//                        log.debug("Phidget Joystick Direction 2 - " + inputIndex + " Pin Changed. ON=" + oe.getState());
//                        directionPin2On = oe.getState();
//                        phidgetBoard.getKit().setOutputState(reverseOutputIndex, !directionPin2On);
//                    }
//                } catch(Exception ex) {
//                    log.error("Error handling signal reversal for direction signal 2", ex);
//                }
//            }
//        });

    }

    @Override
    public void read() throws Exception {
        // Reading is event-based
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }
}
