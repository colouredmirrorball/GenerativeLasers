package be.generativelasers.output.etherdream;

import ilda.IldaPoint;

import java.net.InetAddress;
import java.util.List;

public class Etherdream
{
    private final EtherdreamCommunicationThread thread;
    private EtherdreamBroadcast broadcast;
    private boolean connectionFailed = false;


    public Etherdream(InetAddress address, EtherdreamBroadcast broadcast)
    {
        this.broadcast = broadcast;
        thread = new EtherdreamCommunicationThread(address, this);
        thread.start();
    }

    public static boolean isFlag(short flags, int position)
    {
        return ((flags >> position) & 0x01) == 1;
    }

    public void update(EtherdreamBroadcast broadcast)
    {
        this.broadcast = broadcast;
    }

    public void project(List<IldaPoint> points, int pps)
    {
        thread.project(points, pps);
    }

    public void stop()
    {
        thread.halt();
    }

    public boolean connectionFailed()
    {
        return connectionFailed;
    }

    void setConnectionFailed()
    {
        this.connectionFailed = true;
    }

    public EtherdreamBroadcast getBroadcast()
    {
        return broadcast;
    }
}
