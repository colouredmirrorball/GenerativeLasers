package be.generativelasers.procedures.test;

import be.generativelasers.procedures.Procedure;
import processing.core.PApplet;

/**
 * @author Florian Created on 24/07/2020
 */
public class SimpleCircle extends Procedure
{

    public SimpleCircle(PApplet applet)
    {
        super(applet);
    }

    @Override
    public synchronized void updateRender()
    {
        renderer.background();
        renderer.stroke(255, 255, 255);
        renderer.ellipse(parent.width * 0.5f, parent.width * 0.5f, parent.width * 0.25f, parent.width * 0.25f);
    }

    @Override
    public void trigger(float value)
    {

    }
}