package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;

import static be.cmbsoft.laseroutput.LsxOscOutput.map;
import static processing.core.PConstants.TWO_PI;

public class ConeSource extends BeamSource
{
    private final LiveControl parent;

    int   amount       = 6;
    float radius       = 50;
    float effectRadius = 0.8f;
    float speed        = 1f;

    float oldRadius       = radius;
    float oldEffectRadius = effectRadius;
    float oldSpeed        = 0;

    float  effectSpeed  = 0.1f;
    double previousTime = 0;
    double time         = 0;

    public ConeSource(LiveControl parent)
    {
        super(parent);
        this.parent = parent;
    }

    @Override
    public void render()
    {
        IldaRenderer renderer = getRenderer();
        renderer.beginDraw();
        renderer.setEllipseDetail(0.4f);
        renderer.setEllipseCorrection(1);
        oldRadius       = oldRadius + (radius - oldRadius) * effectSpeed;
        oldEffectRadius = oldEffectRadius + (effectRadius - oldEffectRadius) * effectSpeed;
        oldSpeed        = oldSpeed + (speed - oldSpeed) * effectSpeed;
        time            = time + (parent.millis() - previousTime) * oldSpeed;
        previousTime    = parent.millis();

        for (int i = 0; i < amount; i++) {

            double phase = TWO_PI * (map(i, 0, amount, 0, 1) + time);
            renderer.stroke(i % 2 == 0 ? firstColor : secondColor);
            renderer.ellipse((float) (renderer.width * (0.5 + 0.5 * oldEffectRadius * Math.sin(phase))),
                (float) (renderer.height * (0.5 + 0.5 * oldEffectRadius * Math.cos(phase))), oldRadius, oldRadius);
        }
        renderer.endDraw();
    }

    @Override
    protected void trigger()
    {
        amount = (int) parent.random(7);
        radius = parent.random(50);
    }
}
