package be.generativelasers.output.etherdream;

import cmb.soft.cgui.CGui;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

/**
 * This class will listen to incoming broadcasts sent every second by the device. It will discover new devices and
 * update the status of existing devices.
 */

public class EtherdreamDiscoverer implements Runnable
{

    private final Map<String, Etherdream> devices;

    private boolean interrupted = false;

    public EtherdreamDiscoverer(Map<String, Etherdream> devices)
    {
        this.devices = devices;
    }

    @Override
    public void run()
    {
        while (!interrupted)
        {
            try (DatagramSocket socket = new DatagramSocket(7654))
            {
                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                InetAddress address = response.getAddress();
                EtherdreamBroadcast broadcast = new EtherdreamBroadcast(buffer);
                String mac = broadcast.getMac();
                synchronized (devices)
                {
                    CGui.log("found device in discoverer");
                    Etherdream etherdream = devices.get(mac);
                    if (etherdream == null)
                    {
                        CGui.log("Found an Etherdream: " + mac);
                        devices.put(mac, new Etherdream(address, broadcast));
                    } else
                    {
                        etherdream.update(broadcast);
                    }
                }
            } catch (IOException exception)
            {

                exception.printStackTrace();
                /*
                try
                {
                    // Wait for a bit to save the CPU
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    interrupted = true;
                    Thread.currentThread().interrupt();
                }

                 */
            }
        }
    }

    public void stop()
    {
        interrupted = true;
    }
}
