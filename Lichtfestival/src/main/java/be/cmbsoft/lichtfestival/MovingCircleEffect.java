package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PVector;

public class MovingCircleEffect extends Effect
{
    private PVector position;
    private PVector target;
    private int     colour;
    private int     targetColour;

    @Override
    public void initialize(Lichtfestival parent)
    {
        position = parent.newRandomPosition();
        target = parent.newRandomPosition();
        target.x = target.x / 2;
        colour = parent.newRandomColour();
    }

    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent)
    {
        if (parent.frameCount % 120 == 0)
        {
            target = parent.newRandomPosition();
            target.x = target.x / 2;
            targetColour = parent.newRandomColour();
        }
        PVector direction = PVector.sub(target, position);
        float   ease      = 0.75f;
        direction.setMag(ease);
        position.add(direction);
        if (position.x < 0)
        {
            position.x = renderer.width;
        }
        if (position.x > renderer.width)
        {
            position.x = 0;
        }
        if (position.y < 0)
        {
            position.y = renderer.height;
        }
        if (position.y > renderer.height)
        {
            position.y = 0;
        }
        renderer.stroke(targetColour);
        renderer.ellipse(position.x, position.y, 30, 30);

    }

}
