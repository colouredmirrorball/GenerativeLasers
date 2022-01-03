package be.generativelasers.output.etherdream;

import java.nio.ByteBuffer;

public class EtherdreamResponse
{

    private final EtherdreamResponseStatus response;
    private final char command;
    private final EtherdreamStatus status;

    public EtherdreamResponse(byte[] bytes)
    {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        response = EtherdreamResponseStatus.get((char) buffer.get());
        command = (char) buffer.get();
        status = new EtherdreamStatus(buffer);
    }

    public EtherdreamResponseStatus getResponse()
    {
        return response;
    }

    public char getCommand()
    {
        return command;
    }

    public EtherdreamStatus getStatus()
    {
        return status;
    }
}
