package com.spriton.therapypi.components;

import com.google.gson.JsonArray;

public class Repetition {

    public int minAngle;
    public int maxAngle;

    public Repetition(int angle) {
        minAngle = angle;
        maxAngle = angle;
    }

    public void updateAngle(int angle) {
        if(angle < minAngle) {
            minAngle = angle;
        }
        if(angle > maxAngle) {
            maxAngle = angle;
        }
    }

    public JsonArray toJson() {
        JsonArray array = new JsonArray();
        array.add(minAngle);
        array.add(minAngle);
        array.add(minAngle);
        array.add(maxAngle);
        array.add(maxAngle);
        return array;
    }

}
