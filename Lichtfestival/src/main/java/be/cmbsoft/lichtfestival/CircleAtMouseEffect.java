package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public class CircleAtMouseEffect extends Effect
{

    private int targetColour;

    @Override
    public void initialize(Lichtfestival parent)
    {
        setAlias("Circle following mouse");
        targetColour = parent.newRandomColour();
    }

    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent, float offset, Laser laser)
    {
        if (parent.frameCount % 120 == 0) {

            targetColour = parent.newRandomColour();
        }

        renderer.stroke(targetColour);
        renderer.ellipse(parent.mouseX, parent.mouseY, 30, 30);

    }

}
