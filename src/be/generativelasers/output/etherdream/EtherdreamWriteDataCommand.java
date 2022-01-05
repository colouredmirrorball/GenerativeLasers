package be.generativelasers.output.etherdream;

import cmb.soft.cgui.CGui;
import ilda.IldaPoint;
import processing.core.PVector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class EtherdreamWriteDataCommand implements EtherdreamCommand
{
    private static final int HALF_FULL_SCALE = 32767;
    private static final float INVERSE_HALF_FULL_SCALE = 0.0000305185f;
    private final byte[] bytes;
    private final List<IldaPoint> points;   // for debug, remove when it's finally working!

    public EtherdreamWriteDataCommand(List<IldaPoint> points)
    {
        this.points = points;
        ByteBuffer buffer = ByteBuffer.allocate(points.size() * 18 + 3);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        /*
        struct data_command {
            uint8_t command; //‘d’ (0x64)
            uint16_t npoints;
            struct dac_point data[];
        };
         */


        buffer.put((byte) 'd');
        buffer.putShort((short) points.size());

        /*
        struct dac_point {
            uint16_t control;
            int16_t x;
            int16_t y;
            uint16_t r;
            uint16_t g;
            uint16_t b;
            uint16_t i;
            uint16_t u1;
            uint16_t u2;
        };
         */

        for (IldaPoint point : points)
        {
            /*
            The "control" field has the following fields defined:

            * [15]: Change point rate. If this bit is set, and there are any values in the point rate change buffer,
            then a new rate is read out of the buffer and set as the current playback rate. If the buffer is empty, the
            point rate is not changed.
            * Other bits: reserved for future expansion to support extra TTL outputs, etc.
            */
            buffer.putShort((short) 0);

            PVector position = point.getPosition();
            buffer.putShort(rescale(position.x));
            buffer.putShort(rescale(position.y));

            int red = rescaleColour(point.getRed());
            int green = rescaleColour(point.getGreen());
            int blue = rescaleColour(point.getBlue());
            buffer.putShort((short) red);
            buffer.putShort((short) green);
            buffer.putShort((short) blue);
            buffer.putShort((short) max(red, max(green, blue)));

            buffer.putShort((short) 0);
            buffer.putShort((short) 0);
        }

        bytes = buffer.array();
    }

    // TODO remove this when it's all working
    public boolean verify(byte[] bytes)
    {
        boolean verified = true;
        float checkSize = ((bytes.length - 3) / 18f) % 1;
        if (checkSize != 0)
        {
            CGui.log("Buffer size mismatch");
            verified = false;
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte commandCharacter = buffer.get();
        if (commandCharacter != 'd')
        {
            CGui.log("Invalid command character: " + commandCharacter);
            verified = false;
        }
        short pointCount = buffer.getShort();
        if (pointCount * 18 + 3 != bytes.length)
        {
            CGui.log("Byte array length mismatch, " + bytes.length + " bytes in message but announced " + pointCount + " points or " + (pointCount * 18 + 3) + " bytes");
        }
        List<IldaPoint> receivedPoints = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++)
        {
            if (buffer.getShort() != 0)
            {
                CGui.log("control was not 0");
                verified = false;
            }
            short x = buffer.getShort();
            short y = buffer.getShort();
            short red = buffer.getShort();
            short green = buffer.getShort();
            short blue = buffer.getShort();
            short intensity = buffer.getShort();
            if (buffer.getShort() != 0)
            {
                CGui.log("u1 was not 0");
                verified = false;
            }
            if (buffer.getShort() != 0)
            {
                CGui.log("u2 was not 0");
                verified = false;
            }
            IldaPoint point = new IldaPoint(x * INVERSE_HALF_FULL_SCALE, y * INVERSE_HALF_FULL_SCALE, 0,
                    (red & 0xffff) >> 8,
                    (green & 0xffff) >> 8, (blue & 0xffff) >> 8, intensity == 0);
            PVector position = point.getPosition();
            if (position.x < -1 || position.x > 1 || position.y < -1 || position.y > 1)
            {
                CGui.log("invalid position");
                verified = false;
            }
            receivedPoints.add(point);
        }
        for (int i = 0; i < receivedPoints.size(); i++)
        {
            IldaPoint receivedPoint = receivedPoints.get(i);
            IldaPoint originalPoint = points.get(i);
            if (!receivedPoint.equals(originalPoint))
            {
//                 CGui.log("Point mismatch");
                verified = false;
            }
        }
        return verified;
    }

    private int rescaleColour(byte c)
    {
        return ((c & 0xff) << 8);
    }

    private short rescale(float f)
    {
        return (short) ((f * HALF_FULL_SCALE));
    }

    @Override
    public byte[] getBytes()
    {
        return bytes;
    }

    @Override
    public char getCommandChar()
    {
        return 'd';
    }
}
