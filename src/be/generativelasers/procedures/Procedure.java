package be.generativelasers.procedures;


import java.util.List;

import ilda.IldaFrame;
import ilda.IldaPoint;
import ilda.IldaRenderer;
import processing.core.PApplet;



/**
 * @author Florian
 * Created on 27/01/2020
 */
public abstract class Procedure
{
    protected IldaFrame frame = new IldaFrame();
    protected final IldaRenderer renderer;
    protected final PApplet parent;

    protected Procedure(PApplet applet)
    {
        this.parent = applet;
        renderer = new IldaRenderer(applet);
        renderer.setOverwrite(true);
    }

    public abstract void update();

    public synchronized IldaFrame getRenderedFrame()
    {
        if(frame == null) return null;
        IldaFrame ildaFrame = new IldaFrame();
        List<IldaPoint> points = frame.getPoints();
        points.forEach(ildaFrame::addPoint);
        return ildaFrame;
    }

    public abstract void trigger(float value);
}
