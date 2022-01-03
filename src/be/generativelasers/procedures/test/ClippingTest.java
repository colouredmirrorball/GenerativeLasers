package be.generativelasers.procedures.test;

import be.generativelasers.procedures.Procedure;
import processing.core.PApplet;

import static processing.core.PConstants.HALF_PI;

public class ClippingTest extends Procedure
{
    public ClippingTest(PApplet applet)
    {
        super(applet);
    }

    @Override
    public void updateRender()
    {
        renderer.background();
        renderer.fill(255, 255, 255);
        renderer.rotate(HALF_PI);
        renderer.rect(0, 0, parent.width, parent.height);
    }

    @Override
    public void trigger(float value)
    {

    }
}
