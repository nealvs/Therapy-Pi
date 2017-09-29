package com.spriton.therapypi.components.network;

import com.spriton.therapypi.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WicdCli {

    private static Logger log = LoggerFactory.getLogger(WicdCli.class);

/*
    wicd-cli -y -S -l
    #   BSSID               Channel ESSID
    0   30:B5:CZ:7F:9A:65   1       HackerZone
    1   30:B5:CZ:7F:9A:65   1       HackerGuest
    2   30:B5:CZ:7F:9A:65   8       TestNetwork

    wicd-cli -y -d -n 0
    Essid: HackerGuest
    Bssid: 30:B5:CZ:7F:9A:65
    Encryption: On
    Encryption Method: WPA2
    Quality: 100
    Mode: Master
    Channel: 1
    Bit RAtes: 6 Mb/s
*/

    public static void refreshNetworkList() throws Exception {
        executeCommand("wicd-cli -y -S -l");
    }

    public static Map<String, NetworkDetails> getAvailableNetworks() throws Exception {
        List<String> result = executeCommand("wicd-cli -y -l");
        Map<String, NetworkDetails> networks = new LinkedHashMap<>();
        int index = 0;
        for(String line : result) {
            // Skip the first line
            if(!line.toLowerCase().contains("ESSID")) {
                NetworkDetails details = getNetworkDetails(index);
                if(details != null) {
                    networks.put(details.Essid, details);
                }
                index++;
            }
        }
        log.info("wicd-cli found networks count=" + networks.size());
        return networks;
    }

    public static NetworkDetails getNetworkDetails(int networkId) throws Exception {
        List<String> result = executeCommand("wicd-cli -y -d -n " + networkId);
        NetworkDetails details = null;
        if(result.size() == 8) {
            details = new NetworkDetails();
            details.Essid = getLineValue(result.get(0));
            details.Bssid = getLineValue(result.get(1));
            details.encryption = getLineValue(result.get(3)).equalsIgnoreCase("On");
            details.quality = tryParse(getLineValue(result.get(4)));
            details.mode = getLineValue(result.get(5));
            details.channel = tryParse(getLineValue(result.get(6)));
        }
        return details;
    }

    public static int tryParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ex) {}
        return 0;
    }

    public static String getLineValue(String line) {
        String value = null;
        if(line != null && !line.isEmpty()) {
            String[] parts = line.split(":");
            if(parts.length > 1) {
                value = parts[1].trim();
            }
        }
        return value;
    }

    public static List<String> executeCommand(String command) throws Exception {
        List<String> resultLines = new ArrayList<>();
        Process process = new ProcessBuilder()
                .redirectErrorStream(true)
                .command(command)
                .start();
        InputStream stdOut = process.getInputStream();
        if(!process.waitFor(Config.values.getInt("WICD_COMMAND_TIMEOUT", 5000), TimeUnit.MILLISECONDS)) {
            log.error("wicd-cli command timed out command=" + command);
            process.destroy();
        } else {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(stdOut))) {
                String line = null;
                log.info("wicd-cli command=" + command);
                while((line = in.readLine()) != null) {
                    resultLines.add(line);
                    log.info(line);
                }
            }
        }
        return resultLines;
    }


}
