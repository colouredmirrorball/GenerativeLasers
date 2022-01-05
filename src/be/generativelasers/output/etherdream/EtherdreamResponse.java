package be.generativelasers.output.etherdream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EtherdreamResponse
{

    private final EtherdreamResponseStatus response;
    private final char command;
    private final EtherdreamStatus status;

    public EtherdreamResponse(byte[] bytes)
    {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
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
