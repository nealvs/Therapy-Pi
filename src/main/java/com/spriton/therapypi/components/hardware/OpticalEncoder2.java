package com.spriton.therapypi.components.hardware;

import com.phidget22.*;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.*;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;

public class OpticalEncoder2 extends Angle {

    private static Logger log = Logger.getLogger(OpticalEncoder2.class);

    private Encoder encoder;
    private Machine machine;
    private double startPosition;
    private double startAngle;
    private Double indexPinAngle;

    // 720 CPR or 2880 index changes
    private static double OPTICAL_CLICKS_PER_DEGREE = Config.values.getDouble("OPTICAL_CLICKS_PER_DEGREE", 8.0);
    private File angleStorageFile = new File("angleStorageFile.data");
    private File indexStorageFile = new File("indexStorageFile.data");

    public OpticalEncoder2() {
        setupEncoder();
    }

    private void setupEncoder() {
        try {
            log.info("Loading Phidget Optical Encoder Library: " + Phidget.getLibraryVersion());
            encoder = new Encoder();
            setupEncoderListeners();

            log.info("OPTICAL_CLICKS_PER_DEGREE=" + OPTICAL_CLICKS_PER_DEGREE);
            log.info("Waiting for Optical Encoder attachment...");

            // 5 minute connection time
            encoder.open(1_000 * Config.values.getInt("ENCODER_CONNECT_SECONDS", 300));
            int dataInterval = Math.min(encoder.getMaxDataInterval(), Math.max(encoder.getMinDataInterval(), Config.values.getInt("ENCODER_DATA_INTERVAL", 100)));
            encoder.setDataInterval(dataInterval);
            log.info("Optical Encoder: deviceName=" + encoder.getDeviceName() +
                    " deviceLabel=" + encoder.getDeviceLabel() +
                    " deviceName=" + encoder.getDeviceName() +
                    " deviceId=" + encoder.getDeviceID() +
                    " deviceSku=" + encoder.getDeviceSKU() +
                    " deviceVersion=" + encoder.getDeviceVersion() +
                    " phidgetIdString=" + encoder.getPhidgetIDString() +
                    " maxDataInterval=" + encoder.getMaxDataInterval() +
                    " minDataInterval=" + encoder.getMinDataInterval() +
                    " currentDataInterval=" + encoder.getDataInterval()
            );

            startPosition = encoder.getPosition();
            startAngle = Config.values.getInt("OPTICAL_START_ANGLE", 90);

            if(angleStorageFile.exists()) {
                String fileContents = FileUtils.fileRead(angleStorageFile);
                if(fileContents != null && !fileContents.isEmpty()) {
                    log.info("Reading optical start angle from file=" + angleStorageFile.getAbsolutePath() + " contents=" + fileContents);
                    if(tryParseDouble(fileContents)) {
                        startAngle = Double.parseDouble(fileContents);
                    }
                }
            }
            if(indexStorageFile.exists()) {
                String fileContents = FileUtils.fileRead(indexStorageFile);
                if(fileContents != null && !fileContents.isEmpty()) {
                    log.info("Reading index pin angle from file=" + indexStorageFile.getAbsolutePath() + " contents=" + fileContents);
                    if(tryParseDouble(fileContents)) {
                        this.indexPinAngle = Double.parseDouble(fileContents);
                    }
                }
            }

            log.info("OPTICAL_START_ANGLE=" + getStartAngle());
        } catch(Exception ex) {
            log.error("Error loading optical encoder", ex);
        }
    }

    public void resetIndexPinAngle() throws Exception {
        setIndexPinAngle(null);
    }

    boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void read() throws Exception {

        log.debug("Optical Encoder Raw Value=" + rawValue);
        int oldValue = (int) value;
        value = getAngleFromRawPosition(rawValue, startPosition, startAngle);
        log.debug("Optical Encoder Angle=" + value);

        if(oldValue != (int) value) {
            log.debug("Writing angle to storage file value=" + (int) value);
            FileOutputStream angleStorage = new FileOutputStream(angleStorageFile, false);
            angleStorage.write(Double.toString(value).getBytes());
            angleStorage.close();
        }

        AngleReading reading = new AngleReading((int)value);
        cleanUpReadings(reading.timestamp);
        readings.add(reading);
    }

    @Override
    public void update(Motor.State motorState) {

    }

    public static double getCalibratedStartPosition(double rawValue, double startAngle, double indexPinAngle) {
        if (Config.values.getBoolean("INVERT_ANGLE_DIRECTION", true)) {
            return rawValue - (OPTICAL_CLICKS_PER_DEGREE * (indexPinAngle - startAngle));
        } else {
            return rawValue + (OPTICAL_CLICKS_PER_DEGREE * (indexPinAngle - startAngle));
        }
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
            @Override
            public void onAttach(AttachEvent attachEvent) {
                connected = true;
                log.info("Encoder Attached: " + attachEvent);
            }
        });
        encoder.addDetachListener(new DetachListener() {
            @Override
            public void onDetach(DetachEvent detachEvent) {
                connected = false;
                log.info("Encoder Detached: " + detachEvent);
                setupEncoder();
            }
        });
        encoder.addErrorListener(new ErrorListener() {
            @Override
            public void onError(ErrorEvent errorEvent) {
                log.error("Encoder Error: " + errorEvent);
            }
        });
        encoder.addPositionChangeListener(new EncoderPositionChangeListener() {
            @Override
            public void onPositionChange(EncoderPositionChangeEvent encoderPositionChangeEvent) {
                try {
                    Encoder source = encoderPositionChangeEvent.getSource();
                    rawValue = source.getPosition();
                    Long indexPosition = getIndexPosition(source);
                    double angle = getAngleFromRawPosition(rawValue, startPosition, startAngle);
                    log.debug("rawValue=" + rawValue + " indexPosition=" + indexPosition + " angle=" + angle);

                    if(encoderPositionChangeEvent.getIndexTriggered()) {
                        // Calibrate
                        log.debug("Index pin triggered. position=" + indexPosition);
                        if(indexPinAngle == null) {
                            setIndexPinAngle(angle);
                        } else {
                            // Update start angle
                            double newStartPosition = getCalibratedStartPosition(indexPosition, startAngle, indexPinAngle);
                            if(startPosition != newStartPosition) {
                                log.info("Calibrating based on index pin. oldStartPosition=" + startPosition + " newStartPosition=" + startPosition +
                                        " rawValue=" + rawValue + " startAngle=" + startAngle + " indexPinAngle=" + indexPinAngle);
                                startPosition = newStartPosition;
                            }
                        }
                    }

                    if(machine != null) {
                        read();
                        calculateAndSetAverage();
                        machine.updateStateBasedOnCurrentInputs();
                        machine.updateSessionBasedOnInputs();
                    }

                } catch(Exception ex) {
                    log.error("Error reading optical encoder position.", ex);
                }
            }
        });
    }

    public Long getIndexPosition(Encoder source) {
        try {
            return source.getIndexPosition();
        } catch(PhidgetException ex) { }
        return null;
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

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Double getIndexPinAngle() {
        return indexPinAngle;
    }

    public void setIndexPinAngle(Double indexPinAngle) throws Exception {
        boolean updateFile = false;
        if(this.indexPinAngle == null && indexPinAngle != null) {
            updateFile = true;
        } else if(this.indexPinAngle != null && indexPinAngle == null) {
            updateFile = true;
        } else if(this.indexPinAngle != null && indexPinAngle != null && !this.indexPinAngle.equals(indexPinAngle)) {
            updateFile = true;
        }
        this.indexPinAngle = indexPinAngle;

        if(updateFile) {
            log.info("Writing index pin angle to storage file value=" + indexPinAngle);
            if(indexPinAngle == null) {
                indexStorageFile.delete();
            } else {
                FileOutputStream angleStorage = new FileOutputStream(indexStorageFile, false);
                angleStorage.write(Double.toString(indexPinAngle).getBytes());
                angleStorage.close();
            }
        }
    }
}
