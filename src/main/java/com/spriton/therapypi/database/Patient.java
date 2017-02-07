package com.spriton.therapypi.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="patient")
public class Patient {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;

    @Column(name="low_goal")
    private Integer lowGoal;
    @Column(name="high_goal")
    private Integer highGoal;

    @Column(name="created")
    private Date created;
    @Column(name="updated")
    private Date updated;
    @Column(name="deleted")
    private Date deleted;

    @Transient
    private List<PatientSession> sessions;

    public Patient() {}

    public Patient(JsonObject obj) {
        if(obj.has("firstName")) {
            this.firstName = obj.get("firstName").getAsString();
            this.lastName = obj.get("lastName").getAsString();
        }
    }

    public JsonObject toJson() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("firstName", firstName);
        json.addProperty("lastName", lastName);
        json.addProperty("lowGoal", lowGoal);
        json.addProperty("highGoal", highGoal);
        if(created != null) {
            json.addProperty("created", dateFormat.format(created));
        }
        if(sessions != null) {
            JsonArray sessionArray = new JsonArray();
            for(PatientSession session : sessions) {
                sessionArray.add(session.toJson());
            }
            json.add("sessions", sessionArray);
        }
        return json;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public List<PatientSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<PatientSession> sessions) {
        this.sessions = sessions;
    }

    public Integer getLowGoal() {
        return lowGoal;
    }

    public void setLowGoal(Integer lowGoal) {
        this.lowGoal = lowGoal;
    }

    public Integer getHighGoal() {
        return highGoal;
    }

    public void setHighGoal(Integer highGoal) {
        this.highGoal = highGoal;
    }
}
