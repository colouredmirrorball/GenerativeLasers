package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;

public class LineEffect extends BeamSource
{
    float effectSpeed = 0.05f;

    float offset;
    float angle = 0;

    float oldOffset = offset;
    float oldAngle  = angle;

    boolean dots = false;

    public LineEffect(LiveControl parent)
    {
        super(parent);
    }

    @Override
    public void render()
    {
        IldaRenderer renderer = getRenderer();
        renderer.beginDraw();
        offset = renderer.height / 2;
        oldAngle  = oldAngle + (angle - oldAngle) * effectSpeed;
        oldOffset = oldOffset + (offset - oldOffset) * effectSpeed;

        renderer.pushMatrix();
        renderer.translate(renderer.width / 2, renderer.height / 2);
        renderer.rotateZ(oldAngle);
        renderer.translate(-renderer.width / 2, -renderer.height / 2 + oldOffset);
        if (dots) {
            renderer.stroke(secondColor);
            renderer.point(10, 0);
            renderer.point(20, 0);
            renderer.point(30, 0);
        }
        renderer.stroke(firstColor);
        renderer.line(dots ? 40 : 0, 0, dots ? renderer.width - 40 : renderer.width, 0);
        if (dots) {
            renderer.stroke(secondColor);
            renderer.point(renderer.width - 30, 0);
            renderer.point(renderer.width - 20, 0);
            renderer.point(renderer.width - 10, 0);
        }
        renderer.popMatrix();
        renderer.endDraw();
    }

    @Override
    protected void trigger()
    {

    }
}
