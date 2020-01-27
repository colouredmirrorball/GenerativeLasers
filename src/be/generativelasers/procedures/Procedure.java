package be.generativelasers.procedures;

import be.generativelasers.output.LaserOutput;
import ilda.IldaFrame;
import ilda.IldaRenderer;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian
 * Created on 27/01/2020
 */
public abstract class Procedure
{
    protected List<LaserOutput> outputs;
    protected IldaFrame frame = new IldaFrame();
    protected IldaRenderer renderer;

    public Procedure(PApplet applet)
    {
        renderer = new IldaRenderer(applet);
        outputs = new ArrayList<>();
    }

    public abstract void update();

    public abstract void project();

    public abstract void trigger(float value);
}
