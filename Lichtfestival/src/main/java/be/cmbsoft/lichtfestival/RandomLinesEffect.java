package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class RandomLinesEffect extends Effect
{

    PVector[] positions;
    PVector[] ends;
    int[]     colours;

    public void initialize(Lichtfestival parent)
    {
        int amt = 10;
        setAlias(amt + " random lines");
        positions = new PVector[amt];
        ends = new PVector[amt];
        colours = new int[amt];

        for (int i = 0; i < amt; i++)
        {
            positions[i] = new PVector(parent.random(parent.width), parent.random(parent.height));
            ends[i] = new PVector(parent.random(parent.width), parent.random(parent.height));
            colours[i] = parent.color(parent.random(255), parent.random(255), parent.random(255));
        }
    }


    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent, float offset)
    {
        renderer.translate(parent.width / 2, parent.height / 2);
        renderer.rotate(PApplet.map(parent.mouseX, 0, parent.width, 0, PConstants.TWO_PI));
        renderer.translate(-parent.width / 2, -parent.height / 2);
        for (int i = 0; i < positions.length; i++)
        {
            renderer.stroke(colours[i]);
            renderer.line(positions[i].x, positions[i].y, ends[i].x, ends[i].y);
        }
    }

}
