package be.generativelasers.output.etherdream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static processing.core.PApplet.hex;

public class EtherdreamBroadcast
{
    private final byte[] macAddress = new byte[6];
    private final int hardwareRevision;
    private final int softwareRevision;
    private final int bufferCapacity;
    private final int maxPointRate;
    private final EtherdreamStatus status;

    public EtherdreamBroadcast(byte[] buffer)
    {
        /*
        struct j4cDAC_broadcast {
            uint8_t mac_address[6];
            uint16_t hw_revision;
            uint16_t sw_revision;
            uint16_t buffer_capacity;
            uint32_t max_point_rate;
            struct dac_status status;
        };
        */

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 6; i++)
        {
            macAddress[i] = byteBuffer.get();
        }
        hardwareRevision = byteBuffer.getShort();
        softwareRevision = byteBuffer.getShort();
        bufferCapacity = byteBuffer.getShort();
        maxPointRate = byteBuffer.getInt();
        status = new EtherdreamStatus(byteBuffer);
    }

    public String getMac()
    {
        StringBuilder builder = new StringBuilder();
        for (byte address : macAddress)
        {
            builder.append(hex(address));
        }
        return builder.toString();
    }

    public byte[] getMacAddress()
    {
        return macAddress;
    }

    public int getHardwareRevision()
    {
        return hardwareRevision;
    }

    public int getSoftwareRevision()
    {
        return softwareRevision;
    }

    public int getBufferCapacity()
    {
        return bufferCapacity;
    }

    public int getMaxPointRate()
    {
        return maxPointRate;
    }

    public EtherdreamStatus getStatus()
    {
        return status;
    }
}
