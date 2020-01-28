package be.generativelasers.procedures.trees;

import be.generativelasers.output.LaserOutput;
import be.generativelasers.procedures.Procedure;
import ilda.IldaFrame;
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

    }

    @Override
    public void update()
    {
        renderer.beginDraw();
        renderer.stroke(255, 255, 255);
        renderer.ellipse(200,200,200,200);
        renderer.endDraw();
        frame = renderer.getCurrentFrame();
    }

    @Override
    public void trigger(float value)
    {

    }
}
