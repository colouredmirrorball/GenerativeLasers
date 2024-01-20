package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PVector;

public class HighlightEffect extends Effect
{
    private final PVector position;
    private       int     time;
    private       int     color;

    public HighlightEffect(PVector position)
    {
        this.position = position;
        setType(Type.TOGGLE);
    }

    @Override
    public void initialize(Lichtfestival parent)
    {
        time = 0;
        color = parent.newRandomColour();
    }

    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent)
    {
        renderer.stroke(color);
        renderer.ellipse(position.x, position.y, 50, 50);
        time++;
        if (time > 120)
        {
            expire();
        }
    }


}
