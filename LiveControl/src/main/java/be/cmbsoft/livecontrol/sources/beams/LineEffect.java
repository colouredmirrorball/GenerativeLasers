package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;

public class LineEffect extends BeamSource
{
    private final LiveControl parent;
    float effectSpeed = 0.05f;

    float offset;
    float angle = 0;

    float oldOffset = offset;
    float oldAngle  = angle;

    boolean dots = false;

    public LineEffect(LiveControl parent)
    {
        super(parent);
        this.parent = parent;
    }

    @Override
    public void update()
    {
        IldaRenderer renderer = getRenderer();
        renderer.beginDraw();
        offset    = parent.height / 2;
        oldAngle  = oldAngle + (angle - oldAngle) * effectSpeed;
        oldOffset = oldOffset + (offset - oldOffset) * effectSpeed;

        renderer.pushMatrix();
        renderer.translate(parent.width / 2, parent.height / 2);
        renderer.rotateZ(oldAngle);
        renderer.translate(-parent.width / 2, -parent.height / 2 + oldOffset);
        if (dots) {
            renderer.stroke(secondColor);
            renderer.point(10, 0);
            renderer.point(20, 0);
            renderer.point(30, 0);
        }
        renderer.stroke(firstColor);
        renderer.line(dots ? 40 : 0, 0, dots ? parent.width - 40 : parent.width, 0);
        if (dots) {
            renderer.stroke(secondColor);
            renderer.point(parent.width - 30, 0);
            renderer.point(parent.width - 20, 0);
            renderer.point(parent.width - 10, 0);
        }
        renderer.popMatrix();
        renderer.endDraw();
    }
}
