package be.generativelasers.output.etherdream;

import cmb.soft.cgui.CGui;
import ilda.IldaPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Etherdream
{
    private final InetAddress address;
    private boolean connected = false;
    private EtherdreamBroadcast broadcast;
    private Socket socket;
    private OutputStream output;
    private InputStream input;

    public Etherdream(InetAddress address, EtherdreamBroadcast broadcast)
    {
        this.address = address;
        this.broadcast = broadcast;
    }

    public static boolean isFlag(short flags, int position)
    {
        return ((flags >> position) & 0x01) == 1;
    }

    public void update(EtherdreamBroadcast broadcast)
    {
        this.broadcast = broadcast;
    }

    public void prepareStream() throws IOException
    {
        ensureConnection();
        if (broadcast == null) return;
        EtherdreamStatus status = broadcast.getStatus();
        EtherdreamLightEngineState lightEngineState = status.getLightEngineState();
        if (!EtherdreamLightEngineState.READY.equals(lightEngineState))
        {
            CGui.log("Warning: Etherdream state is not ready but " + lightEngineState.name());
        }
        write(new byte[]{'p'});
    }

    private void write(byte[] bytes) throws IOException
    {
        try
        {
            output.write(bytes);
            processResponse();
        } catch (Exception exception)
        {
            connected = false;
            socket.close();
            CGui.log(exception);
        }
    }

    private void processResponse() throws IOException
    {
        byte[] buffer = new byte[512];
        int i = 0;
        boolean received = false;
        while (input.available() > 0)
        {
            buffer[i++] = (byte) input.read();
            received = true;
        }
        if (received)
        {
            EtherdreamResponse response = new EtherdreamResponse(buffer);
            if (!EtherdreamResponseStatus.ACK.equals(response.getResponse()))
            {
                CGui.log("No acknowledge received from Etherdream!");
            }
        } else
        {
            CGui.log("No response from Etherdream...");
        }

    }


    private void ensureConnection() throws IOException
    {
        if (!connected)
        {
            connect();
        }
    }

    private void connect() throws IOException
    {
        socket = new Socket(address, 7765);

        output = socket.getOutputStream();
        input = socket.getInputStream();
        connected = true;
        processResponse();

    }

    public void writeData(List<IldaPoint> points)
    {
    }

    public void beginPlayback(int pps) throws IOException
    {
        if (broadcast.getMaxPointRate() < pps)
        {
            throw new RuntimeException("Etherdream max point rate is " + broadcast.getMaxPointRate() + " but " +
                    "requested " + pps);
        }
        EtherdreamCommand command = new BeginPlaybackCommand(pps);
        write(command.getBytes());
    }
}
