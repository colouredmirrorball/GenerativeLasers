package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.fx.Parameter;
import be.cmbsoft.livecontrol.sources.Source;
import processing.core.PConstants;

public abstract class BeamSource extends Source
{
    private final IldaRenderer renderer;
    private final LiveControl parent;
    private final Parameter   firstTrigger;
    private final Parameter   secondTrigger;
    private final Parameter   thirdTrigger;
    int firstColor;
    int secondColor;
    private float oldFirstValue;
    private float oldSecondValue;
    private float oldThirdValue;

    protected BeamSource(LiveControl parent)
    {
        renderer = new IldaRenderer(parent);
        renderer.setOptimise(true);
        this.parent = parent;
        //parent.colorMode(PConstants.HSB);
        firstColor  = parent.color(parent.random(255), 255, 255);
        secondColor = parent.color(parent.random(255), 255, 255);

        firstTrigger  = new Parameter("Beam one");
        secondTrigger = new Parameter("Beam two");
        thirdTrigger  = new Parameter("Beam three");
        parent.newParameter("Beam one", firstTrigger);
        parent.newParameter("Beam two", secondTrigger);
        parent.newParameter("Beam three", thirdTrigger);
    }

    @Override
    public void update()
    {
        float firstValue = firstTrigger.getValue();
        if (firstValue != oldFirstValue)
        {
            if (firstValue > 100)
            {
                parent.colorMode(PConstants.HSB);
                firstColor = parent.color(parent.random(255), 255, 255);
                parent.colorMode(PConstants.RGB);
            }
            oldFirstValue = firstValue;
        }
        float secondValue = secondTrigger.getValue();
        if (secondValue != oldSecondValue)
        {
            if (secondValue > 100)
            {

                parent.colorMode(PConstants.HSB);
                secondColor = parent.color(parent.random(255), 255, 255);
                parent.colorMode(PConstants.RGB);
            }

            oldSecondValue = firstValue;
        }
        float thirdValue = thirdTrigger.getValue();
        if (thirdValue != oldThirdValue && thirdValue > 100)
        {
            trigger();
            oldThirdValue = thirdValue;
        }
        render();
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

    protected abstract void trigger();

    protected abstract void render();

    protected IldaRenderer getRenderer()
    {
        return renderer;
    }
}
