package be.cmbsoft.livecontrol;

import java.util.List;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.laseroutput.LaserOutput;
import processing.core.PConstants;
import processing.core.PGraphics;

public class LaserOutputWrapper
{
    private final LaserOutput output;
    private       IldaFrame   ildaFrame;
    private       PGraphics   renderer;

    public LaserOutputWrapper(LaserOutput output) {this.output = output;}

    public void project(List<IldaPoint> ildaFrame)
    {
        this.ildaFrame = new IldaFrame();
        this.ildaFrame.getPoints().addAll(ildaFrame);
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
        parent.rect(x - 2, y - 2, w + 4, h + 4);
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
            parent.noStroke();
            parent.fill(parent.getGuiStrokeColor());
            parent.textAlign(PConstants.LEFT, PConstants.TOP);
            parent.text(ildaFrame.getPointCount(), x, y + h + 10);
        }
    }

    public LaserOutput getWrappedOutput()
    {
        return output;
    }

    public void halt()
    {
        output.halt();
    }

}
