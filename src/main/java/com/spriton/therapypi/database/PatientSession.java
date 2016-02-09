package com.spriton.therapypi.database;

import com.google.gson.JsonObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public PatientSession() {}

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
        if(startTime != null) {
            result.addProperty("startTime", dateFormat.format(startTime));
        }
        if(endTime != null) {
            result.addProperty("endTime", dateFormat.format(endTime));
        }
        result.addProperty("totalSeconds", totalSeconds);
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
}
