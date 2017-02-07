package com.spriton.therapypi.database;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spriton.therapypi.Config;
import com.spriton.therapypi.components.*;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name="patient_session")
public class PatientSession {

    private static Logger log = Logger.getLogger(PatientSession.class);

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    @Column(name="patient_id")
    private Integer patientId;

    @Column(name="high_angle")
    private Integer highAngle;
    @Column(name="low_angle")
    private Integer lowAngle;

    @Column(name="high_hold_seconds")
    private int highHoldSeconds;
    @Column(name="low_hold_seconds")
    private int lowHoldSeconds;

    @Column(name="repetitions")
    private int repetitions;

    @Column(name="start_time")
    private Date startTime;
    @Column(name="end_time")
    private Date endTime;

    @Column(name="total_seconds")
    private int totalSeconds;

    @Column(name="deleted")
    private Date deleted;

    @Transient
    private Patient patient;
    @Transient
    private Stopwatch sessionStopwatch = Stopwatch.createUnstarted();
    @Transient
    private Stopwatch holdStopwatch = Stopwatch.createUnstarted();

    @Transient
    private List<AngleReading> readings = new LinkedList<>();

    @Transient
    private List<Repetition> repetitionList = new LinkedList<>();

    @Transient
    private AngleReading lastHold = null;
    @Transient
    private boolean angleGoingUp = true;
    @Transient
    private boolean endSession = false;


    public PatientSession() {}

    public void start() {
        startTime = new Date();
        sessionStopwatch.start();
    }

    public void stop() {
        if(sessionStopwatch.isRunning()) {
            sessionStopwatch.stop();
        }
        if(holdStopwatch.isRunning()) {
            holdStopwatch.stop();
        }
        endTime = new Date();
        totalSeconds = (int) sessionStopwatch.elapsed(TimeUnit.SECONDS);
        //LocalDateTime start = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        //LocalDateTime end = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        //totalSeconds = (int) Duration.between(start, end).getSeconds();
    }

    public void reset() {
        startTime = new Date();
        sessionStopwatch = Stopwatch.createStarted();
        holdStopwatch = Stopwatch.createStarted();
        repetitionList = new ArrayList<>();
        repetitions = 0;
        highAngle = null;
        lowAngle = null;
        endSession = false;
    }

    public void update(AngleReading angleReading, Motor.State state) {
        int angleValue = angleReading.angle;

        long holdSeconds = holdStopwatch.elapsed(TimeUnit.SECONDS);
        if(holdSeconds >= Config.values.getInt("HOLD_MINIMUM_SECONDS", 5)) {
            if (lowAngle == null || angleValue <= lowAngle) {
                lowAngle = angleValue;
            }
            if (lowAngle >= angleValue) {
                lowHoldSeconds = (int) holdSeconds;
            }
            if (highAngle == null || angleValue >= highAngle) {
                highAngle = angleValue;
            }
            if (highAngle <= angleValue) {
                highHoldSeconds = (int) holdSeconds;
            }
        }

        readings.add(angleReading);

        if(repetitionList.isEmpty()) {
            Repetition newRepetition = new Repetition(angleReading.angle);
            repetitionList.add(newRepetition);
        }
        repetitionList.get(repetitionList.size() - 1).updateAngle(angleReading.angle);

        cleanUpReadingsAndUpdateRepetitions(angleReading);
        //updateRepetitions();

        // Consider it a hold unless in the motor medium or fast states.  Lightly bumping the joystick shouldn't reset the hold timer
        if(state != null && (state == Motor.State.STOPPED || state == Motor.State.UP_SLOW || state == Motor.State.DOWN_SLOW)) {
            if (!holdStopwatch.isRunning()) {
                holdStopwatch = Stopwatch.createStarted();
            }
        } else {
            holdStopwatch = Stopwatch.createUnstarted();
        }

        if(holdStopwatch.elapsed(TimeUnit.SECONDS) >= Config.values.getInt("IDLE_MACHINE_SECONDS", 300)) {
            endSession = true;
            Machine.stopAndSaveSession();
        }

//        if(duration.toMillis() >= Config.values.getInt("READING_SPAN", 3000) - 1000) {
//            determineHold(reading);
//        }
    }

    private void updateRepetitions() {
        if(readings.size() > 0) {
            int firstAngle = readings.get(0).angle;
            int lastAngle = readings.get(readings.size() - 1).angle;
            boolean goingUp = firstAngle < lastAngle;
            if (firstAngle != lastAngle && Math.abs(firstAngle - lastAngle) >= Config.values.getInt("DIRECTION_CHANGE_MINIMUM", 10)) {
                if(angleGoingUp != goingUp) {
                    // Direction has changed
                    angleGoingUp = goingUp;
                    if(angleGoingUp) {
                        repetitions++;
                    }
                }
                holdStopwatch = Stopwatch.createStarted();
            }
        }
    }

    public void cleanUpReadingsAndUpdateRepetitions(AngleReading currentReading) {
        if(readings.size() > 1) {
            Iterator<AngleReading> iter = readings.iterator();
            int minAngle = Integer.MAX_VALUE;
            int maxAngle = -Integer.MAX_VALUE;
            AngleReading minReading = null;
            AngleReading maxReading = null;
            boolean goingUp = true;
            while (iter.hasNext()) {
                AngleReading reading = iter.next();
                Duration duration = Duration.between(reading.timestamp, currentReading.timestamp);
                if (duration.toMillis() > Config.values.getInt("SESSION_READING_SPAN", 300_000)) { // 5 min of readings max
                    iter.remove();
                }
                if(reading.angle > maxAngle) {
                    maxAngle = reading.angle;
                    maxReading = reading;
                    goingUp = true;
                }
                if(reading.angle < minAngle) {
                    minAngle = reading.angle;
                    minReading = reading;
                    goingUp = false;
                }
            }

            if (Math.abs(maxAngle - minAngle) >= Config.values.getInt("DIRECTION_CHANGE_MINIMUM", 10)) {
                readings.clear();
                // Keep around the last extreme reading
                if(goingUp) {
                    readings.add(maxReading);
                } else {
                    readings.add(minReading);
                }
                if(angleGoingUp != goingUp) {
                    // Direction has changed
                    angleGoingUp = goingUp;
                    if(angleGoingUp) {
                        repetitions++;
                        Repetition newRepetition = new Repetition(currentReading.angle);
                        repetitionList.add(newRepetition);
                    }
                }
            }

        }
    }

//    private void determineHold(AngleReading newReading) {
//        if(readings.size() > 0) {
//            int min = Integer.MAX_VALUE;
//            int max = -Integer.MAX_VALUE;
//            int total = 0;
//
//            for (AngleReading reading : readings) {
//                if(reading.angle > max) {
//                    max = reading.angle;
//                }
//                if(reading.angle < min) {
//                    min = reading.angle;
//                }
//                total += reading.angle;
//            }
//            int avg = total / readings.size();
//
//            if(max - min < Config.values.getInt("HOLD_RANGE", 3)) {
//                AngleReading avgReading = new AngleReading(avg);
//                avgReading.timestamp = newReading.timestamp;
//                lastHold = avgReading;
//                if(!holdStopwatch.isRunning()) {
//                    log.info("Hold Stopwatch Created. Started.");
//                    holdStopwatch = Stopwatch.createStarted();
//                }
//            } else {
//                log.info("Hold Stopwatch Stopped.");
//                holdStopwatch = Stopwatch.createUnstarted();
//            }
//        }
//    }


    public JsonObject toJson() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        JsonObject result = new JsonObject();
        result.addProperty("id", id);
        result.addProperty("patientId", patientId);
        result.addProperty("highAngle", highAngle);
        result.addProperty("lowAngle", lowAngle);
        result.addProperty("highHoldSeconds", highHoldSeconds);
        result.addProperty("lowHoldSeconds", lowHoldSeconds);
        result.addProperty("repetitions", repetitions);
        result.addProperty("sessionTime", sessionStopwatch.elapsed(TimeUnit.SECONDS));
        result.addProperty("holdTime", holdStopwatch.elapsed(TimeUnit.SECONDS));
        result.addProperty("endSession", endSession);

        int MAX_REPS = 10;
        JsonArray repetitionNumbers = new JsonArray();
        JsonArray repList = new JsonArray();
        if(repetitionList.size() <= MAX_REPS) {
            int count = 1;
            for (Repetition repetition : repetitionList) {
                repetitionNumbers.add(count++);
                repList.add(repetition.toJson());
            }
        } else {
            for(int i=repetitionList.size() - MAX_REPS; i < repetitionList.size(); i++) {
                repetitionNumbers.add(i + 1);
                repList.add(repetitionList.get(i).toJson());
            }
        }
        result.add("repetitionList", repList);
        result.add("repetitionNumbers", repetitionNumbers);

        if(Machine.instance().timeZone != null) {
            dateFormat.setTimeZone(Machine.instance().timeZone);
            dateTimeFormat.setTimeZone(Machine.instance().timeZone);
        }

        if(startTime != null) {
            result.addProperty("startDate", dateFormat.format(startTime));
            result.addProperty("startDateTime", dateTimeFormat.format(startTime));
        }
        if(endTime != null) {
            result.addProperty("endDate", dateFormat.format(endTime));
            result.addProperty("endDateTime", dateTimeFormat.format(endTime));
        }
        result.addProperty("totalSeconds", totalSeconds);

        if(patient != null) {
            result.add("patient", patient.toJson());
        }

        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getHighAngle() {
        return highAngle;
    }

    public void setHighAngle(Integer highAngle) {
        this.highAngle = highAngle;
    }

    public Integer getLowAngle() {
        return lowAngle;
    }

    public void setLowAngle(Integer lowAngle) {
        this.lowAngle = lowAngle;
    }

    public int getHighHoldSeconds() {
        return highHoldSeconds;
    }

    public void setHighHoldSeconds(int highHoldSeconds) {
        this.highHoldSeconds = highHoldSeconds;
    }

    public int getLowHoldSeconds() {
        return lowHoldSeconds;
    }

    public void setLowHoldSeconds(int lowHoldSeconds) {
        this.lowHoldSeconds = lowHoldSeconds;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Stopwatch getSessionStopwatch() {
        return sessionStopwatch;
    }

    public void setSessionStopwatch(Stopwatch sessionStopwatch) {
        this.sessionStopwatch = sessionStopwatch;
    }

    public Stopwatch getHoldStopwatch() {
        return holdStopwatch;
    }

    public void setHoldStopwatch(Stopwatch holdStopwatch) {
        this.holdStopwatch = holdStopwatch;
    }

    public AngleReading getLastHold() {
        return lastHold;
    }
}
