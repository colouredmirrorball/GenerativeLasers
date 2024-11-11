package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;
import processing.core.PVector;

public class BeamEffectSource extends BeamSource
{

    private final LiveControl parent;
    int       amount       = 10;
    float     speed        = 0.1f;
    PVector[] positions    = new PVector[amount];
    PVector[] oldPositions = new PVector[amount];

    public BeamEffectSource(LiveControl parent)
    {
        super(parent);
        this.parent = parent;
        assignRandomPositions();
        System.arraycopy(positions, 0, oldPositions, 0, positions.length);
    }

    @Override
    public void render()
    {
        IldaRenderer renderer = getRenderer();
        renderer.beginDraw();
        for (int i = 0; i < positions.length; i++) {
            renderer.stroke(i % 2 == 0 ? firstColor : secondColor);
            float x = oldPositions[i].x;
            float y = oldPositions[i].y;
            renderer.point(x, y);
            oldPositions[i].x = x + (positions[i].x - x) * speed;
            oldPositions[i].y = y + (positions[i].y - y) * speed;
        }
        renderer.endDraw();
    }

    @Override
    protected void trigger()
    {
        assignRandomPositions();
    }

    void assignRandomPositions()
    {
        IldaRenderer renderer = getRenderer();
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new PVector((int) parent.random(renderer.width), (int) parent.random(renderer.height));
        }
    }
}
