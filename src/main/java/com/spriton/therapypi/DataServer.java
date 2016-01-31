package com.spriton.therapypi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spriton.therapypi.components.Machine;
import org.apache.log4j.Logger;

import static spark.Spark.*;

public class DataServer {

    private static Logger log = Logger.getLogger(DataServer.class);

    public static void init() {
        int port = Config.config.getInt("server_port", 8686);
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
        stopSession();
        updateJoystick();
        login();
        userList();
        createUser();
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

    public static void stopSession() {
        post("/stopSession",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();

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

    public static void userList() {
        get("/userList",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void login() {
        post("/login",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void createUser() {
        post("/createUser",  "application/json", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

}
