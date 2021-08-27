package be.generativelasers.procedures.test;

import be.generativelasers.procedures.Procedure;
import processing.core.PApplet;

public class StringTest extends Procedure
{

    String text = "hello";

    public StringTest(PApplet applet)
    {
        super(applet);
    }

    @Override
    public void updateRender()
    {
        renderer.background();
        renderer.stroke(255);
        renderer.textSize(150);
        renderer.text(text, 20, 200);
    }

    @Override
    public void trigger(float value)
    {

    }
}
