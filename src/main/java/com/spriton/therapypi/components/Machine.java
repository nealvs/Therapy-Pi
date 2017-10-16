package com.spriton.therapypi.components;

import com.google.gson.JsonObject;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;
import com.spriton.therapypi.database.ConfigValue;
import com.spriton.therapypi.database.DataAccess;
import com.spriton.therapypi.database.PatientSession;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class Machine {

    private static Logger log = Logger.getLogger(Machine.class);
    private static Machine instance;

    public static enum Type { HARDWARE, SOFTWARE };

    public int holdTimeConfig = 30;
    public String password = "knee";
    public TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of("America/Denver"));
    public boolean running = true;
    public boolean applyLimits = true;
    public Type type = Type.HARDWARE;
    public Angle angle;
    public Joystick joystick;
    public Motor.State joystickMotorState = Motor.State.STOPPED;
    public Switch motorSwitch1;
    public Switch motorSwitch2;
    public boolean phidgetKit;
    public boolean joystickCenterOnZero;

    public PatientSession currentSession = new PatientSession();

    public Machine() {
        ConfigValue holdTime = DataAccess.getConfigValue("HOLD_TIME_SECONDS");
        if(holdTime != null) {
            holdTimeConfig = Integer.parseInt(holdTime.getConfigValue());
        }
        ConfigValue passwd = DataAccess.getConfigValue("PASSWORD");
        if(passwd != null) {
            password = passwd.getConfigValue();
        }
        ConfigValue timeZ = DataAccess.getConfigValue("TIMEZONE");
        if(timeZ != null) {
            timeZone = TimeZone.getTimeZone(timeZ.getConfigValue());
        }
        joystickCenterOnZero = Config.values.getBoolean("JOYSTICK_CENTER_ON_0", true);
    }

    public void setHoldTimeConfig(int holdTime) {
        if(holdTime > 0) {
            holdTimeConfig = holdTime;
            // Save into the database
            ConfigValue config = DataAccess.getConfigValue("HOLD_TIME_SECONDS");
            if(config == null) {
                config = new ConfigValue();
                config.setConfigKey("HOLD_TIME_SECONDS");
                config.setConfigValue(Integer.toString(holdTime));
                DataAccess.saveConfigValue(config);
            } else {
                config.setConfigValue(Integer.toString(holdTime));
                DataAccess.updateConfigValue(config);
            }
        }
    }

    public void setVolumeConfig(int newVolume) {
        newVolume = Math.min(100, newVolume);
        newVolume = Math.max(0, newVolume);
        Sound.setVolume(newVolume);
        // Save into the database
        ConfigValue config = DataAccess.getConfigValue("VOLUME");
        if(config == null) {
            config = new ConfigValue();
            config.setConfigKey("VOLUME");
            config.setConfigValue(Integer.toString(newVolume));
            DataAccess.saveConfigValue(config);
        } else {
            config.setConfigValue(Integer.toString(newVolume));
            DataAccess.updateConfigValue(config);
        }
    }

    public void setPassword(String newPassword) {
        if(newPassword != null && newPassword.length() > 0) {
            password = newPassword;
            // Save into the database
            ConfigValue config = DataAccess.getConfigValue("PASSWORD");
            if(config == null) {
                config = new ConfigValue();
                config.setConfigKey("PASSWORD");
                config.setConfigValue(newPassword);
                DataAccess.saveConfigValue(config);
            } else {
                config.setConfigValue(newPassword);
                DataAccess.updateConfigValue(config);
            }
        }
    }


    public void setTimeZoneConfig(String newTimeZone) {
        if(newTimeZone != null && newTimeZone.length() > 0) {
            timeZone = TimeZone.getTimeZone(newTimeZone);
            // Save into the database
            ConfigValue config = DataAccess.getConfigValue("TIMEZONE");
            if(config == null) {
                config = new ConfigValue();
                config.setConfigKey("TIMEZONE");
                config.setConfigValue(newTimeZone);
                DataAccess.saveConfigValue(config);
            } else {
                config.setConfigValue(newTimeZone);
                DataAccess.updateConfigValue(config);
            }
        }
    }

    public void shutdown() {
        try {
            motorSwitch1.setState(Switch.State.OFF);
            motorSwitch2.setState(Switch.State.OFF);
            motorSwitch1.applyState();
            motorSwitch2.applyState();
        } catch(Exception ex) {
            log.error("Error shutting down.", ex);
        }
    }

    // Event driven run
    public void startEventHandling() {
        Sound.playTimerAlarm();
        if(angle instanceof OpticalEncoder) {
            ((OpticalEncoder) angle).setMachine(this);
        }
        if(joystick != null && joystick instanceof PhidgetKitJoystick) {
            ((PhidgetKitJoystick) joystick).setMachine(this);
        }
    }

    // Loop driven run
    // Deprecated
    public void run() {
        running = true;

        new Thread() {
            @Override
            public void run() {
                startEventHandling();

                // With the Phidget board doing the data rate sampling with events,
                // we could eliminate this loop and run this on each angle and joystick change
                // to determine if the relay switches should be on or off
                while(running) {
                    try {
                        angle.read();
                        angle.calculateAndSetAverage();

                        if(joystick != null) {
                            joystick.read();
                            updateStateBasedOnCurrentInputs();
                        }

                        updateSessionBasedOnInputs();

                        Thread.sleep(Config.values.getInt("MACHINE_LOOP_DELAY_MS", 100));
                    } catch(Exception ex) {
                        log.error("Error in machine run thread", ex);
                    }
                }
            }


        }.start();
    }

    public synchronized void updateSessionBasedOnInputs() {
        if(currentSession != null) {
            currentSession.update(new AngleReading(angle.getKneeValue()), joystickMotorState);
        }
    }

    public void updateStateBasedOnCurrentInputs() throws Exception {
        if(joystick != null) {
            joystickMotorState = Motor.getStateFromJoystick(joystick, joystickCenterOnZero, phidgetKit);

            // For software only.  Uses the motor state to update the angle virtually.
            angle.update(joystickMotorState);

            if (applyLimits && angle.isMinAngle() &&
                    !(joystickMotorState.equals(Motor.State.UP_SLOW) ||
                            joystickMotorState.equals(Motor.State.UP_MEDIUM) ||
                            joystickMotorState.equals(Motor.State.UP_FAST))) {
                motorSwitch1.setState(Switch.State.OFF);
            } else if (applyLimits && angle.isMaxAngle() &&
                    !(joystickMotorState.equals(Motor.State.DOWN_SLOW) ||
                            joystickMotorState.equals(Motor.State.DOWN_MEDIUM) ||
                            joystickMotorState.equals(Motor.State.DOWN_FAST))) {
                motorSwitch2.setState(Switch.State.OFF);
            } else if (joystickMotorState.equals(Motor.State.STOPPED)) {
                motorSwitch1.setState(Switch.State.OFF);
                motorSwitch2.setState(Switch.State.OFF);
            } else {
                motorSwitch1.setState(Switch.State.ON);
                motorSwitch2.setState(Switch.State.ON);
            }
            motorSwitch1.applyState();
        }
    }

    public void reset() throws Exception {
        joystick.reset();
        angle.reset();
        currentSession.reset();
        running = false;
        Thread.sleep(100);
        run();
    }

    public static void setInstance(Type type) throws Exception {
        boolean phidgetKit = Config.values.getBoolean("PHIDGET_KIT", false);
        boolean opticalEncoder = Config.values.getBoolean("OPTICAL_ENCODER", false);
        boolean hasJoystick = Config.values.getBoolean("HAS_JOYSTICK", true);
        boolean hasMotorRelays = Config.values.getBoolean("HAS_MOTOR_RELAYS", true);

        log.info("Encoder type=" + (opticalEncoder ? "optical" : "absolute"));
        log.info("Phidget kit=" + phidgetKit);

        if(type == Type.HARDWARE) {

            PhidgetsInterfaceBoard phidgetBoard = phidgetKit ? new PhidgetsInterfaceBoard() : null;
            Angle angle = opticalEncoder ? new OpticalEncoder() : new HardEncoder();
            Joystick joystick = hasJoystick ? (phidgetKit ? new PhidgetKitJoystick(phidgetBoard) : new HardJoystick()) : null;

            int switchPin1 = Config.values.getInt("PHIDGET_SWITCH_OUTPUT1", 6);
            int switchPin2 = Config.values.getInt("PHIDGET_SWITCH_OUTPUT2", 7);

            Switch motorSwitch1 = hasMotorRelays ? (phidgetKit ?
                    new PhidgetKitMotorRelaySwitch(phidgetBoard, switchPin1, Switch.State.ON) :
                    new MotorRelaySwitch(Switch.State.OFF)) : null;
            Switch motorSwitch2 = hasMotorRelays ? (phidgetKit ?
                    new PhidgetKitMotorRelaySwitch(phidgetBoard, switchPin2, Switch.State.ON) :
                    new MotorRelaySwitch(Switch.State.OFF)) : null;

            Machine machine = Machine.create()
                    .type(type)
                    .phidgetKit(phidgetKit)
                    .angle(angle)
                    .joystick(joystick)
                    .motorSwitch1(motorSwitch1)
                    .motorSwitch2(motorSwitch2);
            Machine.setInstance(machine);

        } else if(type == Type.SOFTWARE) {

            Angle angle = opticalEncoder ? new OpticalEncoder() : new SoftEncoder();
            Machine machine = Machine.create()
                    .type(type)
                    .phidgetKit(phidgetKit)
                    .angle(angle)
                    .joystick(hasJoystick ? new SoftJoystick() : null)
                    .motorSwitch1(new SoftSwitch(Switch.State.ON))
                    .motorSwitch2(new SoftSwitch(Switch.State.ON));
            Machine.setInstance(machine);
        }

    }

    public void calibrate() {
        if(Config.values.getBoolean("OPTICAL_ENCODER", false)) {
            OpticalEncoder opticalEncoder = (OpticalEncoder) angle;
            opticalEncoder.setStartPosition(opticalEncoder.rawValue);
            opticalEncoder.setStartAngle(Config.values.getInt("OPTICAL_START_ANGLE", 90));
            log.info("Optical Calibrate. startPosition=" + opticalEncoder.getStartPosition() + " startAngle=" + opticalEncoder.getStartAngle());
        } else {
            angle.ANGLE_CALIBRATION_VOLTAGE = angle.rawValue;
            ConfigValue value = DataAccess.getConfigValue("ANGLE_CALIBRATION_VOLTAGE");
            if (value == null) {
                value = new ConfigValue();
                value.setConfigKey("ANGLE_CALIBRATION_VOLTAGE");
                value.setConfigValue(Double.toString(angle.ANGLE_CALIBRATION_VOLTAGE));
                DataAccess.saveConfigValue(value);
            } else {
                value.setConfigValue(Double.toString(angle.ANGLE_CALIBRATION_VOLTAGE));
                DataAccess.updateConfigValue(value);
            }
        }
    }

    public JsonObject toJson() {
        JsonObject info = new JsonObject();
        if(type != null) {
            info.addProperty("type", type.name());
        }
        if(joystick != null) {
            info.addProperty("joystick", joystick.value);
            info.addProperty("joystickDirection1", joystick.directionPin1On);
            info.addProperty("joystickDirection2", joystick.directionPin2On);
        }
        if(angle != null) {
            info.addProperty("shaftAngle", angle.getAveragedValue());
            info.addProperty("angle", angle.getKneeValue());
        }
        if(angle != null) {
            if(Config.values.getBoolean("OPTICAL_ENCODER", false)) {
                OpticalEncoder opticalEncoder = (OpticalEncoder) angle;
                info.addProperty("startPosition", opticalEncoder.getStartPosition());
                info.addProperty("currentPosition", opticalEncoder.rawValue);
            } else {
                info.addProperty("rawAngle", angle.rawValue);
                info.addProperty("angleCalibrationVoltage", angle.ANGLE_CALIBRATION_VOLTAGE);
            }
            info.addProperty("angleCalibrationDegree", angle.ANGLE_CALIBRATION_DEGREE);
        }
        if(motorSwitch1 != null) {
            info.addProperty("motorSwitch1", motorSwitch1.getState().name());
        }
        if(motorSwitch2 != null) {
            info.addProperty("motorSwitch2", motorSwitch2.getState().name());
        }
        if(currentSession != null) {
            info.add("session", currentSession.toJson());
        } else {
            info.add("session", new PatientSession().toJson());
        }
        info.addProperty("joystickMotorState", joystickMotorState.name());

        info.addProperty("applyAngleLimits", applyLimits);
        info.addProperty("holdTimeConfig", holdTimeConfig);
        info.addProperty("password", password);
        if(timeZone != null) {
            info.addProperty("timeZone", timeZone.getID());
        }
        info.addProperty("timestamp", new Date().getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - MMM d, yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        SimpleDateFormat monthFormat = new SimpleDateFormat("M");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("H");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("m");
        if(timeZone != null) {
            dateFormat.setTimeZone(timeZone);
            dayFormat.setTimeZone(timeZone);
            monthFormat.setTimeZone(timeZone);
            yearFormat.setTimeZone(timeZone);
            hourFormat.setTimeZone(timeZone);
            minuteFormat.setTimeZone(timeZone);
        }
        info.addProperty("dateTime", dateFormat.format(new Date()));
        info.addProperty("day",     dayFormat.format(new Date()));
        info.addProperty("month",   monthFormat.format(new Date()));
        info.addProperty("year",    yearFormat.format(new Date()));
        info.addProperty("hour",    hourFormat.format(new Date()));
        info.addProperty("minute",  minuteFormat.format(new Date()));
        info.addProperty("hasJoystick", Config.values.getBoolean("HAS_JOYSTICK", true));
        info.addProperty("opticalEncoder", Config.values.getBoolean("OPTICAL_ENCODER", false));
        info.addProperty("volume", Sound.getVolume());
        return info;
    }

    public static void stopAndSaveSession() {
        if(Machine.instance().currentSession != null) {
            Machine.instance().currentSession.stop();
            if(Machine.instance().currentSession.getPatient() != null) {
                Machine.instance().currentSession.setPatientId(Machine.instance().currentSession.getPatient().getId());
            }
            if(Machine.instance().currentSession.getPatientId() != null) {
                DataAccess.createOrUpdateSession(Machine.instance().currentSession);
            } else {
                log.warn("Session doesn't have a patientId.  Not saving.");
            }
            Machine.instance().currentSession = null;
        }
    }

    public static void setInstance(Machine instance) {
        Machine.instance = instance;
    }

    public static Machine instance() {
        return instance;
    }

    public static Machine create() {
        return new Machine();
    }

    public Machine angle(Angle angle) {
        this.angle = angle;
        return this;
    }

    public Machine type(Type type) {
        this.type = type;
        return this;
    }

    public Machine joystick(Joystick joystick) {
        this.joystick = joystick;
        return this;
    }

    public Machine motorSwitch1(Switch motorSwitch1) {
        this.motorSwitch1 = motorSwitch1;
        return this;
    }

    public Machine motorSwitch2(Switch motorSwitch2) {
        this.motorSwitch2 = motorSwitch2;
        return this;
    }

    public Machine phidgetKit(boolean phidgetKit) {
        this.phidgetKit = phidgetKit;
        return this;
    }

}
