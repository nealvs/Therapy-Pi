//package com.spriton.therapypi.components.hardware;
//
//import com.phidget22.*;
//import com.spriton.therapypi.Config;
//import com.spriton.therapypi.components.*;
//import org.apache.log4j.Logger;
//import org.codehaus.plexus.util.FileUtils;
//
//import java.io.File;
//import java.io.FileOutputStream;
//
//public class OpticalEncoder2 extends Angle {
//
//    private static Logger log = Logger.getLogger(OpticalEncoder2.class);
//
//    private Encoder encoder;
//    private Machine machine;
//    private double startPosition;
//    private double startAngle;
//
//    // 720 CPR or 2880 index changes
//    private static double OPTICAL_CLICKS_PER_DEGREE = Config.values.getDouble("OPTICAL_CLICKS_PER_DEGREE", 8.0);
//    private File angleStorageFile = new File("angleStorageFile.data");
//
//    public OpticalEncoder2() {
//        try {
//            log.info("Loading Phidget Optical Encoder Library: " + Phidget.getLibraryVersion());
//            encoder = new Encoder();
//            setupEncoderListeners();
//
//            log.info("Waiting for Optical Encoder attachment...");
//            log.info("OPTICAL_CLICKS_PER_DEGREE=" + OPTICAL_CLICKS_PER_DEGREE);
//            encoder.open();
//            log.info("Optical Encoder: deviceName=" + encoder.getDeviceName() +
//                    " deviceLabel=" + encoder.getDeviceLabel() +
//                    " deviceName=" + encoder.getDeviceName() +
//                    " deviceId=" + encoder.getDeviceID() +
//                    " deviceSku=" + encoder.getDeviceSKU() +
//                    " deviceVersion=" + encoder.getDeviceVersion() +
//                    " phidgetIdString=" + encoder.getPhidgetIDString()
//            );
//
//            this.setStartPosition(encoder.getPosition());
//            this.setStartAngle(Config.values.getInt("OPTICAL_START_ANGLE", 90));
//
//            if(angleStorageFile.exists()) {
//                String fileContents = FileUtils.fileRead(angleStorageFile);
//                if(fileContents != null && !fileContents.isEmpty()) {
//                    log.info("Reading optical start angle from file=" + angleStorageFile.getAbsolutePath() + " contents=" + fileContents);
//                    if(tryParseInt(fileContents)) {
//                        this.setStartAngle(Integer.parseInt(fileContents));
//                    }
//                }
//            }
//            log.info("OPTICAL_START_ANGLE=" + getStartAngle());
//        } catch(Exception ex) {
//            log.error("Error loading optical encoder", ex);
//        }
//    }
//
//    boolean tryParseInt(String value) {
//        try {
//            Integer.parseInt(value);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public void read() throws Exception {
//
//        log.debug("Optical Encoder Raw Value=" + this.rawValue);
//        int oldValue = (int) value;
//        this.value = getAngleFromRawPosition(this.rawValue, this.getStartPosition(), this.getStartAngle());
//        log.debug("Optical Encoder Angle=" + this.value);
//
//        if(oldValue != (int) value) {
//            log.debug("Writing angle to storage file value=" + (int) value);
//            FileOutputStream angleStorage = new FileOutputStream(angleStorageFile, false);
//            angleStorage.write(Integer.toString((int) value).getBytes());
//            angleStorage.close();
//        }
//
//        AngleReading reading = new AngleReading((int)this.value);
//        cleanUpReadings(reading.timestamp);
//        readings.add(reading);
//    }
//
//    @Override
//    public void update(Motor.State motorState) {
//
//    }
//
//    public static double getAngleFromRawPosition(double rawValue, double startPosition, double startAngle) {
//        double positionDifference = startPosition - rawValue;
//        if(Config.values.getBoolean("INVERT_ANGLE_DIRECTION", true)) {
//            positionDifference = -positionDifference;
//        }
//        double result = startAngle + (positionDifference / OPTICAL_CLICKS_PER_DEGREE);
//        return result;
//    }
//
//    private void setupEncoderListeners() {
//        encoder.addAttachListener(new AttachListener() {
//            @Override
//            public void onAttach(AttachEvent attachEvent) {
//                connected = true;
//                log.info("Encoder Attached: " + attachEvent);
//            }
//        });
//        encoder.addDetachListener(new DetachListener() {
//            @Override
//            public void onDetach(DetachEvent detachEvent) {
//                connected = false;
//                log.info("Encoder Detached: " + detachEvent);
//            }
//        });
//        encoder.addErrorListener(new ErrorListener() {
//            @Override
//            public void onError(ErrorEvent errorEvent) {
//                log.error("Encoder Error: " + errorEvent);
//            }
//        });
//        encoder.addPositionChangeListener(new EncoderPositionChangeListener() {
//            @Override
//            public void onPositionChange(EncoderPositionChangeEvent encoderPositionChangeEvent) {
//                try {
//                    Encoder source = encoderPositionChangeEvent.getSource();
//                    rawValue = source.getPosition();
//                    long indexPosition = source.getIndexPosition();
//                    log.debug("rawValue=" + rawValue + " indexPosition=" + indexPosition + " angle=" + getAngleFromRawPosition(rawValue, getStartPosition(), getStartAngle()));
//
//                    if(encoderPositionChangeEvent.getIndexTriggered()) {
//                        // Todo: Calibrate
//
//                    }
//
//                    if(machine != null) {
//                        read();
//                        calculateAndSetAverage();
//                        machine.updateStateBasedOnCurrentInputs();
//                        machine.updateSessionBasedOnInputs();
//                    }
//
//                } catch(Exception ex) {
//                    log.error("Error reading optical encoder position.", ex);
//                }
//            }
//        });
//    }
//
//    public double getStartPosition() {
//        return startPosition;
//    }
//
//    public void setStartPosition(double startPosition) {
//        this.startPosition = startPosition;
//    }
//
//    public double getStartAngle() {
//        return startAngle;
//    }
//
//    public void setStartAngle(double startAngle) {
//        this.startAngle = startAngle;
//    }
//
//    public void setMachine(Machine machine) {
//        this.machine = machine;
//    }
//}
