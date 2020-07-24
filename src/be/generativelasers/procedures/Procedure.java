package be.generativelasers.procedures;


import ilda.IldaFrame;
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

    public Procedure(PApplet applet)
    {
        this.parent = applet;
        renderer = new IldaRenderer(applet);
        renderer.setOverwrite(false);
    }

    public abstract void update();

    public IldaFrame getRenderedFrame()
    {
        if(frame == null) return null;
        return frame;
    }

    public abstract void trigger(float value);
}
