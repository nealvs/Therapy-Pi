package com.spriton.therapypi.database;

import com.google.common.base.Stopwatch;
import com.google.gson.JsonObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name="patient_session")
public class PatientSession {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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

    public PatientSession() {}

    public void start() {
        startTime = new Date();
        sessionStopwatch.start();
    }

    public void stop() {
        sessionStopwatch.stop();
        holdStopwatch.stop();
    }

    public void reset() {
        sessionStopwatch = Stopwatch.createUnstarted();
        holdStopwatch = Stopwatch.createStarted();
        repetitions = 0;
    }

    public void addAngleReading(int angle) {
        if(lowAngle == null) {
            lowAngle = angle;
        }
        if(highAngle == null) {
            highAngle = angle;
        }
    }

    public JsonObject toJson() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        JsonObject result = new JsonObject();
        result.addProperty("id", id);
        result.addProperty("patientId", patientId);
        result.addProperty("highAngle", highAngle);
        result.addProperty("lowAngle", lowAngle);
        result.addProperty("highHoldSeconds", highHoldSeconds);
        result.addProperty("lowHoldSeconds", lowHoldSeconds);
        result.addProperty("repetitions", repetitions);
        result.addProperty("sessionTime", sessionStopwatch.elapsed(TimeUnit.SECONDS));
        result.addProperty("holdTime", sessionStopwatch.elapsed(TimeUnit.SECONDS));

        if(startTime != null) {
            result.addProperty("startTime", dateFormat.format(startTime));
        }
        if(endTime != null) {
            result.addProperty("endTime", dateFormat.format(endTime));
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
}
