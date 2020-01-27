package be.generativelasers.procedures.trees;

import be.generativelasers.output.LaserOutput;
import be.generativelasers.procedures.Procedure;
import processing.core.PApplet;

/**
 * @author Florian
 * Created on 27/01/2020
 */
public class Trees extends Procedure
{
    public Trees(PApplet applet)
    {
        super(applet);
        renderer.setOverwrite(true);
    }

    @Override
    public void update()
    {
        renderer.beginDraw();

        renderer.endDraw();
        frame = renderer.getCurrentFrame();
    }

    @Override
    public void project()
    {
        if(frame == null) return;
        for (LaserOutput output : outputs)
        {
            output.project(frame);
        }
    }

    @Override
    public void trigger(float value)
    {

    }
}
