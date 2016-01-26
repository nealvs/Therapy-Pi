package com.spriton.therapypi.components.hardware;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import java.io.IOException;

public class SpiInterface {

    private static SpiDevice spi = null;
    private static byte INIT_CMD = (byte) 0x01;

    public static void init() throws Exception {
        spi = SpiFactory.getInstance(SpiChannel.CS0,
                SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
                SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
    }

    public static int read(int channel) throws IOException {
        byte packet[] = new byte[3];
        packet[0] = INIT_CMD;
        packet[1] = (byte) ((0x08 + channel) << 4);
        packet[2] = 0x00;
        byte[] result = spi.write(packet);
        int value = ((result[1] & 0x03 ) << 8) | (result[2] & 0xff);
        return value;
    }

}
