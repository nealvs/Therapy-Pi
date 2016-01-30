package com.spriton.therapypi;

import com.google.gson.JsonObject;
import com.spriton.therapypi.components.Machine;
import org.apache.log4j.Logger;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

public class DataServer {

    private static Logger log = Logger.getLogger(DataServer.class);

    public static void init() {
        int port = Config.config.getInt("server_port", 8080);
        log.info("Starting Data Server on port: " + port);
        port(port);

        get("/", (req, res) -> "OK");

        exception(Exception.class, (ex, request, response) -> {
            response.status(500);
            response.body("ERROR");
            log.error(ex);
        });

        // Setup endpoints
        status();
        reset();
        stopSession();
        updateJoystick();
        login();
        userList();
        createUser();
    }

    public static void status() {
        get("/status", (req, res) -> {
            JsonObject info = new JsonObject();
            if(Machine.instance() != null) {
                if (Machine.instance().type != null) {
                    info.addProperty("type", Machine.instance().type.name());
                }
                if (Machine.instance().seat != null) {
                    info.addProperty("seat", Machine.instance().seat.value);
                }
                if (Machine.instance().joystick != null) {
                    info.addProperty("joystick", Machine.instance().joystick.value);
                }
                if (Machine.instance().angle != null) {
                    info.addProperty("angle", Machine.instance().angle.value);
                }
                if (Machine.instance().rotationMotor != null) {
                    info.addProperty("rotationMotor", Machine.instance().rotationMotor.getState().name());
                }
                if (Machine.instance() != null) {
                    info.addProperty("activeMotor", Machine.instance().activeMotor.name());
                }
                if ( Machine.instance().liftMotor != null) {
                    info.addProperty("liftMotor", Machine.instance().liftMotor.getState().name());
                }
                if (Machine.instance().holdStopwatch != null) {
                    info.addProperty("holdTime", Machine.instance().holdStopwatch.elapsed(TimeUnit.SECONDS));
                }
                if ( Machine.instance().sessionStopwatch != null) {
                    info.addProperty("sessionTime", Machine.instance().sessionStopwatch.elapsed(TimeUnit.SECONDS));
                }
            }
            return info.toString();
        });
    }

    public static void reset() {
        post("/reset", (req, res) -> {
            JsonObject result = new JsonObject();
            Machine.instance().reset();
            return result.toString();
        });
    }

    public static void stopSession() {
        post("/stopSession", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void updateJoystick() {
        post("/updateJoystick", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void userList() {
        get("/userList", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void login() {
        post("/login", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

    public static void createUser() {
        post("/createUser", (req, res) -> {
            JsonObject result = new JsonObject();

            return result.toString();
        });
    }

}
