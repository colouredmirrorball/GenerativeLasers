package be.cmbsoft.livecontrol;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.laseroutput.LaserOutput;
import processing.core.PConstants;
import processing.core.PGraphics;

public class LaserOutputWrapper
{
    private final LaserOutput output;
    private       IldaFrame   ildaFrame;
    private       PGraphics   renderer;

    public LaserOutputWrapper(LaserOutput output) {this.output = output;}

    public void project(IldaFrame ildaFrame)
    {
        this.ildaFrame = ildaFrame;
        output.project(ildaFrame);
    }

    public void display(LiveControl parent, int x, int y, int w, int h)
    {
        if (output.isConnected())
        {
            parent.fill(0, 255, 0);
        }
        else
        {
            parent.fill(255, 0, 0);
        }
        parent.rect(x, y, w, h);
        if (renderer == null)
        {
            renderer = parent.createGraphics(w, h, PConstants.P3D);
        }
        if (ildaFrame != null)
        {
            renderer.beginDraw();
            renderer.background(0);
            ildaFrame.renderFrame(renderer, true);
            renderer.endDraw();
            parent.image(renderer, x, y);
        }
    }

}
