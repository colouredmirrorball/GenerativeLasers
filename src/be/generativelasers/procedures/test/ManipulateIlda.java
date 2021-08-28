package be.generativelasers.procedures.test;

import be.generativelasers.procedures.Procedure;
import ilda.IldaFrame;
import ilda.IldaReader;
import processing.core.PApplet;

import java.io.FileNotFoundException;
import java.util.List;

public class ManipulateIlda extends Procedure
{

    private List<IldaFrame> ildaFrames;
    private int idx;
    private int counter;

    public ManipulateIlda(PApplet applet)
    {
        super(applet);
        try
        {
            ildaFrames = IldaReader.readFile("data/CanGoose.ild");
        } catch (FileNotFoundException e)
        {
            // ignore
        }
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
