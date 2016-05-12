package com.spriton.therapypi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spriton.therapypi.components.Machine;
import com.spriton.therapypi.database.DataAccess;
import com.spriton.therapypi.database.Patient;
import com.spriton.therapypi.database.PatientSession;
import org.apache.log4j.Logger;

import static spark.Spark.*;

public class DataServer {

    private static Logger log = Logger.getLogger(DataServer.class);

    public static void init() {
        int port = Config.values.getInt("server_port", 8686);
        log.info("Starting Data Server on port: " + port);
        port(port);

        exception(Exception.class, (ex, request, response) -> {
            response.status(500);
            response.body("ERROR");
            log.error("Request Error: " + request.contextPath(), ex);
        });

        // Setup endpoints
        setupOptions();
        status();
        reset();
        startSession();
        stopSession();
        updateJoystick();
        login();
        patientList();
        patient();
        session();
        createPatient();
        deletePatient();
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
                    Patient patient = DataAccess.getPatient(request.get("patientId").getAsInt());
                    session.setPatient(patient);
                    session.start();
                    Machine.instance().currentSession = session;
                }
            }
            return result.toString();
        });
    }

    public static void stopSession() {
        post("/stopSession",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            Machine.instance().currentSession.stop();
            // Record
            Machine.instance().currentSession = null;
            return result.toString();
        });
    }

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
            for(Patient patient : DataAccess.getAllPatients()) {
                patients.add(patient.toJson());
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
    }

    public static void session() {
        get("/session/:id",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();
            PatientSession session = DataAccess.getSession(Integer.parseInt(req.params("id")));
            if(session != null) {
                result.add("session", session.toJson());
            }
            return result.toString();
        });
    }

    public static void createPatient() {
        post("/createPatient",  "application/json", (req, res) -> {
            JsonObject requestJson = (JsonObject) new JsonParser().parse(req.body());
            Patient patient = new Patient(requestJson);
            DataAccess.createPatient(patient);
            return patient.toJson();
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

}
