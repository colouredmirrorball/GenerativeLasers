package be.generativelasers.procedures.test;

import java.util.List;

import be.generativelasers.procedures.Procedure;
import ilda.IldaFrame;
import ilda.IldaReader;
import processing.core.PApplet;

public class ManipulateIlda extends Procedure
{

    private final List<IldaFrame> ildaFrames;
    private       int             idx;
    private       int             counter;

    public ManipulateIlda(PApplet applet)
    {
        super(applet);
        ildaFrames = IldaReader.readFile("data/CanGoose.ild");
    }

    @Override
    public void updateRender()
    {
        renderer.background();
        renderer.drawIldaFrame(ildaFrames.get(idx));
        counter++;
        if (counter % 10 == 0) idx++;
        idx = idx % ildaFrames.size();
    }

    @Override
    public void trigger(float value)
    {

    }
}
