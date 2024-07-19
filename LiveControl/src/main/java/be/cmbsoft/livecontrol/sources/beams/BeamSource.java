package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.sources.Source;

public abstract class BeamSource extends Source
{
    private final IldaRenderer renderer;

    int firstColor;
    int secondColor;

    protected BeamSource(LiveControl parent)
    {
        renderer = new IldaRenderer(parent);
        renderer.setOptimise(true);
        //parent.colorMode(PConstants.HSB);
        firstColor  = parent.color(parent.random(255), 255, 255);
        secondColor = parent.color(parent.random(255), 255, 255);
    }

    @Override
    public IldaFrame getFrame()
    {
        return renderer.getCurrentFrame();
    }

    public int getFirstColor()
    {
        return firstColor;
    }

    public int getSecondColor()
    {
        return secondColor;
    }

    protected IldaRenderer getRenderer()
    {
        return renderer;
    }
}
