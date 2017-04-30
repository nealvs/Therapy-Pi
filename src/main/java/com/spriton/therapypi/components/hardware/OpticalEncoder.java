package com.spriton.therapypi.components.hardware;

import com.phidgets.*;
import com.phidgets.event.*;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.*;
import org.apache.log4j.Logger;

public class OpticalEncoder extends Angle {

    private static Logger log = Logger.getLogger(OpticalEncoder.class);

    private EncoderPhidget encoder;
    private double startPosition;
    private double startAngle;
    private static double OPTICAL_CLICKS_PER_DEGREE = Config.values.getDouble("OPTICAL_CLICKS_PER_DEGREE", 30);

    public OpticalEncoder() {
        try {
            log.info("Loading Phidget Optical Encoder Library: " + Phidget.getLibraryVersion());
            encoder = new EncoderPhidget();
            setupEncoderListeners();

            encoder.openAny();
            log.info("Waiting for Optical Encoder attachment...");
            log.info("OPTICAL_CLICKS_PER_DEGREE=" + OPTICAL_CLICKS_PER_DEGREE);
            encoder.waitForAttachment();
            log.info("Optical Encoder: deviceName=" + encoder.getDeviceName() +
                    " deviceType=" + encoder.getDeviceType() +
                    " deviceId=" + encoder.getDeviceID() +
                    " serialNumber=" + encoder.getSerialNumber() +
                    " inputCount=" + encoder.getInputCount()
            );

            this.setStartPosition(encoder.getPosition(0));
            this.startAngle = Config.values.getInt("OPTICAL_START_ANGLE", 90);
            log.info("OPTICAL_START_ANGLE=" + startAngle);
        } catch(Exception ex) {
            log.error("Error loading optical encoder", ex);
        }
    }

    @Override
    public void read() throws Exception {

        log.debug("Optical Encoder Raw Value=" + this.rawValue);
        this.value = getAngleFromRawPosition(this.rawValue, this.getStartPosition(), this.startAngle);
        log.debug("Optical Encoder Angle=" + this.value);

        AngleReading reading = new AngleReading((int)this.value);
        cleanUpReadings(reading.timestamp);
        readings.add(reading);
    }

    @Override
    public void update(Motor.State motorState) {

    }

    public static double getAngleFromRawPosition(double rawValue, double startPosition, double startAngle) {
        double positionDifference = startPosition - rawValue;
        if(Config.values.getBoolean("INVERT_ANGLE_DIRECTION", true)) {
            positionDifference = -positionDifference;
        }
        double result = startAngle + (positionDifference / OPTICAL_CLICKS_PER_DEGREE);
        return result;
    }

    private void setupEncoderListeners() {
        encoder.addAttachListener(new AttachListener() {
            public void attached(AttachEvent ae) {
                log.info("Device Attached: " + ae);
            }
        });
        encoder.addDetachListener(new DetachListener() {
            public void detached(DetachEvent ae) {
                log.info("Device Detached: " + ae);
            }
        });
        encoder.addErrorListener(new ErrorListener() {
            public void error(ErrorEvent ee) {
                log.info("Device Error: " + ee);
            }
        });
        encoder.addInputChangeListener(new InputChangeListener() {
            public void inputChanged(InputChangeEvent oe) {
                log.info("Device Input Changed: " + oe);
            }
        });
        encoder.addEncoderPositionChangeListener(new EncoderPositionChangeListener() {
            public void encoderPositionChanged(EncoderPositionChangeEvent oe) {
                try {
                    EncoderPhidget source = (EncoderPhidget) oe.getSource();
                    rawValue = source.getPosition(oe.getIndex());
                    log.debug("rawValue=" + rawValue + " angle=" + getAngleFromRawPosition(rawValue, getStartPosition(), startAngle));
                } catch(Exception ex) {
                    log.error("Error reading optical encoder position.", ex);
                }
            }
        });
    }

    public double getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(double startPosition) {
        this.startPosition = startPosition;
    }
}
