package com.spriton.therapypi;

import com.phidget22.*;

public class EncoderExample {

    public static void main(String[] args) throws Exception {
        //Enable logging to stdout
        com.phidget22.Log.enable(LogLevel.DEBUG, null);

        Encoder ch = new Encoder();

        ch.addAttachListener(new AttachListener() {
            public void onAttach(AttachEvent ae) {
                Encoder phid = (Encoder) ae.getSource();
                try {
                    if(phid.getDeviceClass() != DeviceClass.VINT){
                        System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " attached");
                    }
                    else{
                        System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " attached");
                    }
                } catch (PhidgetException ex) {
                    System.out.println(ex.getDescription());
                }
            }
        });

        ch.addDetachListener(new DetachListener() {
            public void onDetach(DetachEvent de) {
                Encoder phid = (Encoder) de.getSource();
                try {
                    if (phid.getDeviceClass() != DeviceClass.VINT) {
                        System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " detached");
                    } else {
                        System.out.println("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " detached");
                    }
                } catch (PhidgetException ex) {
                    System.out.println(ex.getDescription());
                }
            }
        });

        ch.addErrorListener(new ErrorListener() {
            public void onError(ErrorEvent ee) {
                System.out.println("Error: " + ee.getDescription());
            }
        });

        ch.addPositionChangeListener(new EncoderPositionChangeListener() {
            public void onPositionChange(EncoderPositionChangeEvent e) {
                System.out.println("\nEncoder Changed: " + e.getPositionChange() + " " + e.getTimeChange() + " " + e.getIndexTriggered());
            }
        });

        try {
            /*
             * Please review the Phidget22 channel matching documentation for details on the device
             * and class architecture of Phidget22, and how channels are matched to device features.
             */

            /*
             * Specifies the serial number of the device to attach to.
             * For VINT devices, this is the hub serial number.
             *
             * The default is any device.
             */
            //ch.setDeviceSerialNumber(495947);
            /*
             * For VINT devices, this specifies the port the VINT device must be plugged into.
             *
             * The default is any port.
             */
            //ch.setHubPort(0);

            /*
             * Specifies that the channel should only match a VINT hub port.
             * The only valid channel id is 0.
             *
             * The default is 0 (false), meaning VINT hub ports will never match
             */
            //ch.setIsHubPortDevice(true);

            /*
             * Specifies which channel to attach to.  It is important that the channel of
             * the device is the same class as the channel that is being opened.
             *
             * The default is any channel.
             */
            //ch.setChannel(0);

            /*
             * In order to attach to a network Phidget, the program must connect to a Phidget22 Network Server.
             * In a normal environment this can be done automatically by enabling server discovery, which
             * will cause the client to discovery and connect to available servers.
             *
             * To force the channel to only match a network Phidget, set remote to 1.
             */
            // Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);
            // ch.setIsRemote(true);

            System.out.println("Opening and waiting 5 seconds for attachment...");
            ch.open(5000);
            System.out.println(ch.getAttached());

            if(ch.getDeviceID() == DeviceID.PN_1047){
                System.out.println("Setting enabled");
                ch.setEnabled(true);
            }

            System.out.println("\n\nGathering data for 10 seconds\n\n");
            Thread.sleep(10000);

            ch.close();
            System.out.println("\nClosed Encoder");
        } catch (PhidgetException ex) {
            ex.printStackTrace();
        }
    }
}
