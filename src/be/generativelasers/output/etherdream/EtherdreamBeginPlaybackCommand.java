package be.generativelasers.output.etherdream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EtherdreamBeginPlaybackCommand implements EtherdreamCommand
{

    private final byte[] bytes;

    public EtherdreamBeginPlaybackCommand(int pps)
    {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 'b');
        buffer.putShort((short) 0);
        buffer.putInt(pps);
        bytes = buffer.array();
    }

    @Override
    public byte[] getBytes()
    {
        return bytes;
    }

    @Override
    public char getCommandChar()
    {
        return 'b';
    }
}
