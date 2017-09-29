package com.spriton.therapypi.components.hardware;

import com.phidgets.*;
import com.phidgets.event.*;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.*;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;

public class OpticalEncoder extends Angle {

    private static Logger log = Logger.getLogger(OpticalEncoder.class);

    private EncoderPhidget encoder;
    private double startPosition;
    private double startAngle;
    private static double OPTICAL_CLICKS_PER_DEGREE = Config.values.getDouble("OPTICAL_CLICKS_PER_DEGREE", 30);
    private File angleStorageFile = new File("angleStorageFile.data");

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
            this.setStartAngle(Config.values.getInt("OPTICAL_START_ANGLE", 90));

            if(angleStorageFile.exists()) {
                String fileContents = FileUtils.fileRead(angleStorageFile);
                if(fileContents != null && !fileContents.isEmpty()) {
                    log.info("Reading optical start angle from file=" + angleStorageFile.getAbsolutePath() + " contents=" + fileContents);
                    if(tryParseInt(fileContents)) {
                        this.setStartAngle(Integer.parseInt(fileContents));
                    }
                }
            }
            log.info("OPTICAL_START_ANGLE=" + getStartAngle());
        } catch(Exception ex) {
            log.error("Error loading optical encoder", ex);
        }
    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void read() throws Exception {

        log.debug("Optical Encoder Raw Value=" + this.rawValue);
        int oldValue = (int) value;
        this.value = getAngleFromRawPosition(this.rawValue, this.getStartPosition(), this.getStartAngle());
        log.debug("Optical Encoder Angle=" + this.value);

        if(oldValue != (int) value) {
            log.debug("Writing angle to storage file value=" + (int) value);
            FileOutputStream angleStorage = new FileOutputStream(angleStorageFile, false);
            angleStorage.write(Integer.toString((int) value).getBytes());
            angleStorage.close();
        }

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
                log.info("Encoder Attached: " + ae);
            }
        });
        encoder.addDetachListener(new DetachListener() {
            public void detached(DetachEvent ae) {
                log.info("Encoder Detached: " + ae);
            }
        });
        encoder.addErrorListener(new ErrorListener() {
            public void error(ErrorEvent ee) {
                log.info("Encoder Error: " + ee);
            }
        });
        encoder.addInputChangeListener(new InputChangeListener() {
            public void inputChanged(InputChangeEvent oe) {
                log.info("Encoder Input Changed: " + oe);
            }
        });
        encoder.addEncoderPositionChangeListener(new EncoderPositionChangeListener() {
            public void encoderPositionChanged(EncoderPositionChangeEvent oe) {
                try {
                    EncoderPhidget source = (EncoderPhidget) oe.getSource();
                    rawValue = source.getPosition(oe.getIndex());
                    log.debug("rawValue=" + rawValue + " angle=" + getAngleFromRawPosition(rawValue, getStartPosition(), getStartAngle()));
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

    public double getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
    }
}
