package be.generativelasers.output;

import ilda.IldaPoint;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static processing.core.PApplet.constrain;
import static processing.core.PApplet.map;

/**
 * @author Florian Created on 27/01/2020
 */
public class LsxOscOutput extends LaserOutput
{
    private final ByteBuffer b;
    private final OscP5 osc;
    private int timeline;
    private int destinationFrame;
    private NetAddress destination;

    public LsxOscOutput(PApplet parent, int timeline, int destinationFrame, NetAddress destination)
    {
        osc = new OscP5(parent, 4850);
        this.timeline = timeline;
        this.destinationFrame = destinationFrame;
        this.destination = destination;
        b = ByteBuffer.allocate(45068); //largest point count LSX can handle is 4096
        b.order(ByteOrder.LITTLE_ENDIAN);
        setName("LSX OSC output");
    }

    @Override
    public synchronized void project(List<IldaPoint> points)
    {

        OscMessage m = new OscMessage("/LSX_0/Frame");

        int pointCount = points.size();
        b.position(0);

        // LSX frame OSC message
        //HEADER

        b.put((byte) 2);         //type: 0=XYRGB; 1=XYZRGB; 2=XYZPPrRGB
        b.put((byte) 1);         //store: 0 = buffer, 1 = store in frame
        b.put((byte) timeline);  //scanner/timeline
        b.put((byte) 0);         //future

        b.putShort((short) destinationFrame);
        b.putShort((short) pointCount);
        b.putShort((short) 0); //start point
        b.putShort((short) pointCount);

        int max = 32767;
        for (int i = 0; i < Math.min(pointCount, 4096); i++)
        {
            IldaPoint p = points.get(i);
            short x = (short) constrain(map(p.getPosition().x, -1, 1, -max, max), -max, max);
            b.putShort(x);


            short y = (short) constrain(map(p.getPosition().y, -1, 1, -max, max), -max, max);
            b.putShort(y);


            short z = (short) constrain(map(p.getPosition().z, -1, 1, -max, max), -max, max);
            b.putShort(z);

            // Palette byte:
            //    First bit: normal vector    1 = regular point    0 = normal vector
            //    Second bit: blanking        1 = blanked          0 = unblanked
            //    Third to eighth bit: palette idx (0-63)
            b.put((byte) (1 << 7 | (p.isBlanked() ? 1 << 6 : 0)));

            // Parts-Repeats byte
            //    First to fourth bit: parts (0-15)
            //    Fifth to eighth bit: repeats (0-15)
            b.put((byte) 0);


            int red = (p.getColour() >> 16) & 0xFF;
            int green = (p.getColour() >> 8) & 0xFF;
            int blue = (p.getColour() & 0xFF);

            if (p.isBlanked())
            {
                red = 0;
                green = 0;
                blue = 0;
            }

            b.put((byte) red);
            b.put((byte) green);
            b.put((byte) blue);
        }

        //Add the blob to the OSC message
        m.add(b.array());

        osc.send(m, destination);              // send the OSC message to the remote location defined in setup()
        b.clear();
    }

    public int getTimeline()
    {
        return timeline;
    }

    public void setTimeline(int timeline)
    {
        this.timeline = timeline;
    }

    public int getDestinationFrame()
    {
        return destinationFrame;
    }

    public void setDestinationFrame(int destinationFrame)
    {
        this.destinationFrame = destinationFrame;
    }

    public NetAddress getDestination()
    {
        return destination;
    }

    public void setDestination(NetAddress destination)
    {
        this.destination = destination;
    }


}
