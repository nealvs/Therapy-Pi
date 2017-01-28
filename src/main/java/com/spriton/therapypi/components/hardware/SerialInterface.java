package com.spriton.therapypi.components.hardware;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;

import java.io.IOException;

public class SerialInterface {

    private static Serial serial;

    public static void init() throws IOException {
        serial = SerialFactory.createInstance();
        serial.open(Serial.DEFAULT_COM_PORT, 38400);
    }

    public static void write(int value) throws IOException {
        serial.write((byte) value);
    }

}
