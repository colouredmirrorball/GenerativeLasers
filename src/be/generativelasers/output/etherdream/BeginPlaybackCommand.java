package be.generativelasers.output.etherdream;

import java.nio.ByteBuffer;

public class BeginPlaybackCommand extends EtherdreamCommand
{

    private final byte[] bytes;

    public BeginPlaybackCommand(int pps)
    {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) 'b');
        buffer.putInt(pps);
        bytes = buffer.array();
    }

    @Override
    public byte[] getBytes()
    {
        return bytes;
    }
}
