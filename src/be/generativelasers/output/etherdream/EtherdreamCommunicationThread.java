package be.generativelasers.output.etherdream;

import cmb.soft.cgui.CGui;
import ilda.IldaPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class EtherdreamCommunicationThread extends Thread
{
    private static final EtherdreamPrepareStreamCommand prepareStreamCommand = new EtherdreamPrepareStreamCommand();
    private static final EtherdreamStopCommand etherdreamStopCommand = new EtherdreamStopCommand();

    private final InetAddress address;
    private final ArrayBlockingQueue<EtherdreamCommand> messages = new ArrayBlockingQueue<>(20);
    private final Etherdream etherdream;

    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private boolean halted = false;
    private EtherdreamCommand lastMessage;
    private boolean sendStopCommand = false;
    private boolean sendPrepareStreamCommand = false;
    private EtherdreamStatus lastStatus;

    EtherdreamCommunicationThread(InetAddress address, Etherdream etherdream)
    {
        setName("EtherdreamCommunicationThread");
        this.address = address;
        this.etherdream = etherdream;
    }

    @Override
    public void run()
    {

        while (!halted)
        {
            try
            {
                if (socket == null || socket.isClosed())
                {
                    connect();
                }
                boolean endOfStream = false;
                ByteBuffer buffer = ByteBuffer.allocate(22);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                int receivedChars = 0;
                while (!endOfStream)
                {
                    int b = input.read();
                    if (b < 0 || ++receivedChars >= buffer.capacity())
                    {
                        endOfStream = true;
                    } else
                    {
                        buffer.put((byte) (b & 0xff));
//                        System.out.println((byte) (b & 0xff) + " | " + (char) b);
                    }
                }
                processResponse(buffer.array());

                EtherdreamCommand messageToSend;
                if (sendStopCommand)
                {
                    messageToSend = etherdreamStopCommand;
                    sendStopCommand = false;
                } else if (sendPrepareStreamCommand)
                {
                    messageToSend = prepareStreamCommand;
                    sendPrepareStreamCommand = false;
                } else messageToSend = messages.take();

                CGui.log("Sending command " + messageToSend.getCommandChar());
                output.write(messageToSend.getBytes());
                output.flush();
                lastMessage = messageToSend;
                if (sendStopCommand)
                {
                    halted = true;
                }
            } catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
                halted = true;
                Thread.currentThread().interrupt();
            }
        }
        if (socket != null)
        {
            try
            {
                socket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        etherdream.setConnectionFailed();

    }


    /**
     * Add a command to the queue. This operation might block if the queue is full.
     *
     * @param message a message
     */
    void addMessage(EtherdreamCommand message)
    {
        CGui.log("Adding message to queue: " + message.getCommandChar());
        try
        {
            messages.put(message);
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private void processResponse(byte[] array)
    {
        EtherdreamResponse response = new EtherdreamResponse(array);

        if (!EtherdreamResponseStatus.ACK.equals(response.getResponse()))
        {
            // The thing went wrong!
            switch (response.getResponse())
            {
                case NAK_FULL:
                    // BUG: the Etherdream does not send a NAK_FULL but a NAK_INVALID when buffer is full!
                    CGui.log("Buffer full, don't send so many points pl0x");
                    break;
                case NAK_INVALID:
                    // Some ad hoc troubleshooting
                    if (lastMessage.getCommandChar() == 'p' &&
                            response.getStatus().getPlaybackState() == EtherdreamPlaybackState.PREPARED)
                    {
                        // The thing that went wrong is that the ED, for some reason, is confused - it should be IDLE
                        sendPrepareStreamCommand = true;
                        CGui.log("Etherdream wasn't prepared...");
                    } else if (lastMessage.getCommandChar() == 'd')
                    {
                        CGui.log("Invalid data command: " + response.getCommand());
                        boolean passedCheck = ((EtherdreamWriteDataCommand) lastMessage).verify(lastMessage.getBytes());
                        if (!passedCheck)
                        {
                            CGui.log("Data was not properly formatted...");
                        }
                    } else
                    {
                        CGui.log("Invalid command: " + response.getCommand());
                    }
                    break;
                case NAK_STOP_CONDITION:
                    CGui.log("Etherdream is in a stop condition!");
                    break;
                default:
            }
        } else
        {
            CGui.log("ACK received for command " + response.getCommand());
        }
        lastStatus = response.getStatus();
    }

    private void connect() throws IOException
    {
        socket = new Socket(address, 7765);
        socket.setSoTimeout(5000);
        output = socket.getOutputStream();
        input = socket.getInputStream();
    }

    public void halt()
    {
        sendStopCommand = true;
    }

    public void project(List<IldaPoint> points, int pps)
    {

        EtherdreamBroadcast broadcast = etherdream.getBroadcast();

        int bufferCapacity = broadcast.getBufferCapacity();
        if (points.size() > bufferCapacity)
        {
            points.subList(0, bufferCapacity);
        }

        int bufferFullness = 0;
        if (lastStatus != null)
        {
            bufferFullness = lastStatus.getBufferFullness();
        }
        if (points.size() > bufferCapacity - bufferFullness)
        {
            // FIXME instead of skipping a frame we should be slightly more clever about it...
            addMessage(new EtherdreamWriteDataCommand(points));
        }


        if (broadcast.getMaxPointRate() < pps)
        {
            throw new RuntimeException("Etherdream max point rate is " + broadcast.getMaxPointRate() +
                    " but requested " + pps);
        }
        if (lastStatus == null || EtherdreamPlaybackState.IDLE == lastStatus.getPlaybackState())
        {
            sendPrepareStreamCommand = true;
            CGui.log("Put Etherdream in prepared state");
        }
        if (lastStatus != null && EtherdreamPlaybackState.PREPARED == lastStatus.getPlaybackState())
        {
            addMessage(new EtherdreamBeginPlaybackCommand(pps));
        }

    }

}
