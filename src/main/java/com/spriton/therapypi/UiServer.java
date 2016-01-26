package com.spriton.therapypi;

import static spark.Spark.get;
import static spark.Spark.port;

public class UiServer {

    public static void init() {
        port(8080);
        get("/hello", (req, res) -> "Hello World");
    }


}
