package com.spriton.therapypi.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Network {

    private static Logger log = Logger.getLogger(Network.class);
    private static Network instance;

    private String hostname = "";
    private String publicIp = "";
    private String privateIp = "";
    private JsonObject geoIpInfo = null;

    private Network() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            privateIp = addr.getHostAddress();
        } catch (UnknownHostException ex) {
            log.error("Hostname can not be resolved");
        }
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            try(BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()))) {
                publicIp = in.readLine();
            }
        } catch(IOException ex) {
            log.error("Error checking for public ip");
        }

        if(publicIp != null && !publicIp.isEmpty()) {
            geoIpInfo = getPublicGeoIpInformation(publicIp);
        }
    }

    public static Network instance() {
        if(instance == null) {
            instance = new Network();
        }
        return instance;
    }

    public JsonObject toJson() {
        JsonObject info = new JsonObject();
        info.addProperty("hostname", hostname);
        info.addProperty("publicIp", publicIp);
        info.addProperty("privateIp", privateIp);
        info.addProperty("isOnline", isOnline());
        info.add("geoip", geoIpInfo);
        return info;
    }

    public static boolean isOnline() {
        return isReachable("google.com", 443, 1000);
    }

    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        try(Socket soc = new Socket()) {
            soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /*
    {
        ip: "192.30.253.113",
        country_code: "US",
        country_name: "United States",
        region_code: "CA",
        region_name: "California",
        city: "San Francisco",
        zip_code: "94107",
        time_zone: "America/Los_Angeles",
        latitude: 37.7697,
        longitude: -122.3933,
        metro_code: 807
    }
     */
    private JsonObject getPublicGeoIpInformation(String ip) {
        JsonObject info = null;
        try {
            String response = getText("http://freegeoip.net/json/" + ip);
            if (response != null && !response.isEmpty()) {
                info = (JsonObject) (new JsonParser()).parse(response);
            }
        } catch(Exception ex) {
            log.error("Error getting geoip info for ip=" + ip, ex);
        }
        return info;
    }

    public static String getText(String url) {
        StringBuilder response = new StringBuilder();
        try {
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        } catch(Exception ex) {
            log.error("Unable to get geoip information", ex);
        }
        return response.toString();
    }

}
