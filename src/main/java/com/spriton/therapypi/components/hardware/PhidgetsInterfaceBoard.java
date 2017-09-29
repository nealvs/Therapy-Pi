package com.spriton.therapypi.components.hardware;

import com.phidgets.*;
import com.phidgets.event.*;
import com.spriton.therapypi.Config;
import org.apache.log4j.Logger;

public class PhidgetsInterfaceBoard {

    private static Logger log = Logger.getLogger(PhidgetsInterfaceBoard.class);

    private InterfaceKitPhidget kit;

    public InterfaceKitPhidget getKit() {
        return kit;
    }

    public PhidgetsInterfaceBoard() throws Exception {
        init();
    }

    private void init() throws Exception {
        log.info("Loading Phidget Interface Kit Library: " + Phidget.getLibraryVersion());
        kit = new InterfaceKitPhidget();
        setupKitListeners();
        kit.openAny();
        log.info("Waiting for Phidget Interface Kit attachment...");
        kit.waitForAttachment();
        kit.setRatiometric(Config.values.getBoolean("PHIDGET_RATIOMETRIC", true));

        //for(int i=0; i<kit.getSensorCount(); i++) {
            // 1-1000 (milliseconds)
            //kit.setDataRate(i, 1);
        //}

        log.info("Phidget Interface Kit:" +
                        " deviceName=" + kit.getDeviceName() +
                        " deviceType=" + kit.getDeviceType() +
                        " deviceLabel=" + kit.getDeviceLabel() +
                        " deviceVersion=" + kit.getDeviceVersion() +
                        " deviceId=" + kit.getDeviceID() +
                        " serialNumber=" + kit.getSerialNumber() +
                        " inputCount=" + kit.getInputCount() +
                        " outputCount=" + kit.getOutputCount() +
                        " sensorCount=" + kit.getSensorCount() +
                        " ratioMetric=" + kit.getRatiometric() +
                        " dataRate0=" + kit.getDataRate(0) +
                        " dataRateMin=" + kit.getDataRateMin(0) +
                        " dataRateMax=" + kit.getDataRateMax(0)
        );

    }

    private void setupKitListeners() {
        kit.addAttachListener(new AttachListener() {
            public void attached(AttachEvent ae) {
                log.info("Kit Attached: " + ae);
            }
        });
        kit.addDetachListener(new DetachListener() {
            public void detached(DetachEvent ae) {
                log.info("Kit Detached: " + ae);
            }
        });
        kit.addErrorListener(new ErrorListener() {
            public void error(ErrorEvent ee) {
                log.info("Kit Error: " + ee);
            }
        });
        kit.addOutputChangeListener(new OutputChangeListener() {
            @Override
            public void outputChanged(OutputChangeEvent oe) {
                log.debug("Kit Output Changed: " + oe);
            }
        });

        // Inputs are read 125 samples/s
        kit.addInputChangeListener(new InputChangeListener() {
            public void inputChanged(InputChangeEvent oe) {
                log.debug("Kit Input Changed: " + oe);
            }
        });

        kit.addSensorChangeListener(new SensorChangeListener() {
            @Override
            public void sensorChanged(SensorChangeEvent sensorChangeEvent) {
                log.debug("Kit Sensor Changed: " + sensorChangeEvent);
            }
        });

    }

}
