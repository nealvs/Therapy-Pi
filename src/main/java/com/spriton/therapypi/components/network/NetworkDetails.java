package com.spriton.therapypi.components.network;

import com.google.gson.JsonObject;

public class NetworkDetails {

    public int networkId;           // 0-?
    public String Essid;            // HackerGuest
    public String Bssid;            // 2A:56:5A:91:EC:60
    public boolean encryption;
    public String encryptionMethod; // WPA2, WPA
    public int quality;             // 0-100
    public String mode;             // Master
    public int channel;             // 1-?

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("networkId", networkId);
        obj.addProperty("Essid", Essid);
        obj.addProperty("Bssid", Bssid);
        obj.addProperty("encryption", encryption);
        obj.addProperty("encryptionMethod", encryptionMethod);
        obj.addProperty("quality", quality);
        obj.addProperty("channel", channel);
        return obj;
    }

}
