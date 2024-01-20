package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public class CircleAtMouseEffect extends Effect
{

    private int targetColour;

    @Override
    public void initialize(Lichtfestival parent)
    {

        targetColour = parent.newRandomColour();
    }

    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent)
    {
        if (parent.frameCount % 120 == 0) {

            targetColour = parent.newRandomColour();
        }

        renderer.stroke(targetColour);
        renderer.ellipse(parent.mouseX, parent.mouseY, 30, 30);

    }

}
