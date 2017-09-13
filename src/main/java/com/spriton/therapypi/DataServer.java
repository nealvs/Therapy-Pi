package com.spriton.therapypi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spriton.therapypi.components.Machine;
import com.spriton.therapypi.database.DataAccess;
import com.spriton.therapypi.database.Patient;
import com.spriton.therapypi.database.PatientSession;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.TimeZone;

import static spark.Spark.*;

public class DataServer {

    private static Logger log = Logger.getLogger(DataServer.class);

    public static void init() {
        int port = Config.values.getInt("SERVER_PORT", 8686);
        log.info("Starting Data Server on port: " + port);
        port(port);

        exception(Exception.class, (ex, request, response) -> {
            response.status(500);
            response.body("ERROR");
            log.error("Request Error: " + request.contextPath(), ex);
        });

        staticFileLocation("www");

        // Setup endpoints
        setupOptions();
        status();
        reset();
        startSession();
        resetSession();
        stopSession();
        removeAngleLimits();
        applyAngleLimits();
        joystickUp();
        joystickStop();
        joystickDown();
        updateJoystick();
        login();
        patientList();
        patient();
        session();
        settings();
        createPatient();
        deletePatient();
        deleteSession();
    }

    public static void setupOptions() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });
    }

    public static void status() {
        get("/status", "application/json", (req, res) -> {
            JsonObject info = new JsonObject();
            if (Machine.instance() != null) {
                info = Machine.instance().toJson();
            }
            return info.toString();
        });
    }

    public static void reset() {
        post("/reset",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if (Machine.instance() != null) {
                Machine.instance().reset();
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    public static void startSession() {
        post("/startSession",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if(req.body() != null) {
                JsonParser parser = new JsonParser();
                JsonObject request = (JsonObject) parser.parse(req.body());
                if(request.has("patientId")) {
                    PatientSession session = new PatientSession();
                    if(request.has("minutes")) {
                        int minutes = request.get("minutes").getAsInt();
                        if(minutes >= 1) {
                            session.setTimerMinutes(minutes);
                        }
                    }
                    Patient patient = DataAccess.getPatient(request.get("patientId").getAsInt());
                    session.setPatient(patient);
                    session.start();
                    Machine.instance().currentSession = session;
                }
            }
            return result.toString();
        });
    }

    public static void resetSession() {
        post("/resetSession",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if(Machine.instance().currentSession != null) {
                Machine.instance().currentSession.reset();
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    public static void stopSession() {
        post("/stopSession",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            Machine.stopAndSaveSession();
            return result.toString();
        });
    }

    public static void removeAngleLimits() {
        post("/removeAngleLimits",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if (Machine.instance() != null) {
                Machine.instance().applyLimits = false;
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    public static void applyAngleLimits() {
        post("/applyAngleLimits",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if (Machine.instance() != null) {
                Machine.instance().applyLimits = true;
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    // Software Only
    public static void joystickUp() {
        post("/joystickUp",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if (Machine.instance() != null) {
                Machine.instance().joystick.value = 5;
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    // Software Only
    public static void joystickStop() {
        post("/joystickStop",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if (Machine.instance() != null) {
                Machine.instance().joystick.value = 2.5;
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    // Software Only
    public static void joystickDown() {
        post("/joystickDown",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if (Machine.instance() != null) {
                Machine.instance().joystick.value = 0;
                result = Machine.instance().toJson();
            }
            return result.toString();
        });
    }

    // Software Only
    public static void updateJoystick() {
        post("/updateJoystick",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            if(req.body() != null) {
                JsonParser parser = new JsonParser();
                JsonObject request = (JsonObject) parser.parse(req.body());
                if(request.has("value")) {
                    if (Machine.instance() != null) {
                        Machine.instance().joystick.value = request.get("value").getAsDouble();
                        result = Machine.instance().toJson();
                    }
                }
            }
            return result.toString();
        });
    }

    public static void login() {
        post("/login",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void patientList() {
        get("/patientList",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            JsonArray patients = new JsonArray();
            List<Patient> patientList = null;
            String all = req.queryParams("all");
            if(all != null && all.equalsIgnoreCase("true")) {
                patientList = DataAccess.getAllPatients();
            } else {
                patientList = DataAccess.getRecentPatients();
            }

            if(patientList != null) {
                for (Patient patient : patientList) {
                    patients.add(patient.toJson());
                }
            } else {
                log.warn("No patient list received from database.");
            }

            result.add("patients", patients);
            return result.toString();
        });
    }

    public static void patient() {
        get("/patient/:id",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            Patient patient = DataAccess.getPatient(Integer.parseInt(req.params("id")));
            if(patient != null) {
                result.add("patient", patient.toJson());
            }
            return result.toString();
        });

        post("/patient/setGoals",  "application/json", (req, res) -> {
            JsonObject errorResponse = new JsonObject();
            try {
                if (req.body() != null) {
                    JsonParser parser = new JsonParser();
                    JsonObject request = (JsonObject) parser.parse(req.body());
                    if (request.has("patientId")) {
                        int patientId = Integer.parseInt(request.get("patientId").getAsString());
                        Patient patient = DataAccess.getPatient(patientId);
                        if (patient != null) {
                            if (request.has("lowGoal")) {
                                String lowGoal = request.get("lowGoal").getAsString();
                                if (lowGoal != null) {
                                    patient.setLowGoal(Integer.parseInt(lowGoal));
                                }
                            }
                            if (request.has("highGoal")) {
                                String highGoal = request.get("highGoal").getAsString();
                                if (highGoal != null) {
                                    patient.setHighGoal(Integer.parseInt(highGoal));
                                }
                            }
                        } else {
                            errorResponse.addProperty("error", "Patient record not found");
                        }
                        DataAccess.updatePatient(patient);
                        return patient.toJson();
                    }
                }
                errorResponse.addProperty("error", "Submission Error");
            } catch(Exception ex) {
                errorResponse.addProperty("error", ex.getMessage());
            }
            return errorResponse;
        });
    }

    public static void settings() {
        post("/settings/calibrate",  "application/json", (req, res) -> {
            Machine.instance().calibrate();
            return Machine.instance().toJson();
        });
        post("/settings/setTimeZone",  "application/json", (req, res) -> {
            if(req.body() != null) {
                JsonParser parser = new JsonParser();
                JsonObject request = (JsonObject) parser.parse(req.body());
                if (request.has("value")) {
                    String timeZone = request.get("value").getAsString();
                    if (timeZone != null) {
                        Machine.instance().timeZone = TimeZone.getTimeZone(timeZone);
                    }
                }
            }
            return Machine.instance().toJson();
        });
        post("/settings/setHoldTime",  "application/json", (req, res) -> {
            if(req.body() != null) {
                JsonParser parser = new JsonParser();
                JsonObject request = (JsonObject) parser.parse(req.body());
                if (request.has("value")) {
                    String holdTime = request.get("value").getAsString();
                    if(holdTime != null) {
                        Machine.instance().setHoldTimeConfig(Integer.parseInt(holdTime));
                    }
                }
            }
            return Machine.instance().toJson();
        });

        post("/settings/clearDatabase", "application/json", (req, res) -> {
            DataAccess.clearDatabase();
            return Machine.instance().toJson();
        });
    }

    public static void session() {
        get("/session/:id",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            PatientSession session = DataAccess.getSession(Integer.parseInt(req.params("id")));
            if(session != null) {
                if(session.getPatientId() != null) {
                    session.setPatient(DataAccess.getPatient(session.getPatientId()));
                }
                result.add("session", session.toJson());
            }
            return result.toString();
        });
    }

    public static void createPatient() {
        post("/createPatient",  "application/json", (req, res) -> {
            try {
                JsonObject requestJson = (JsonObject) new JsonParser().parse(req.body());
                Patient patient = new Patient(requestJson);
                Patient existingPatient = DataAccess.getPatient(patient.getFirstName(), patient.getLastName());
                if(existingPatient == null) {
                    DataAccess.createPatient(patient);
                    return patient.toJson();
                } else {
                    JsonObject result = new JsonObject();
                    result.addProperty("error", "A patient with this name already exists");
                    return result;
                }
            } catch (Exception ex) {
                log.error("Error creating patient. " + req.body());
                JsonObject result = new JsonObject();
                result.addProperty("error", ex.getMessage());
                return result;
            }
        });
    }

    public static void deletePatient() {
        post("/deletePatient",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            JsonObject requestJson = (JsonObject) new JsonParser().parse(req.body());
            if(requestJson.has("id")) {
                DataAccess.deletePatient(requestJson.get("id").getAsInt());
            }
            return result.toString();
        });
    }

    public static void deleteSession() {
        post("/deleteSession",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            JsonObject requestJson = (JsonObject) new JsonParser().parse(req.body());
            if(requestJson.has("id")) {
                DataAccess.deleteSession(requestJson.get("id").getAsInt());
            }
            return result.toString();
        });
    }

}
