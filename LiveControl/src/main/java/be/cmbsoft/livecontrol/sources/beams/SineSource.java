package be.cmbsoft.livecontrol.sources.beams;

import be.cmbsoft.ilda.IldaRenderer;
import static be.cmbsoft.laseroutput.LsxOscOutput.map;
import be.cmbsoft.livecontrol.LiveControl;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.lerpColor;
import static processing.core.PConstants.LINES;
import static processing.core.PConstants.TWO_PI;


public class SineSource extends BeamSource
{
    private final LiveControl parent;

    int amount = 200;

    float frequency = 0;
    float amplitude = 0f;
    float speed     = 1f;

    float oldFrequency = frequency;
    float oldAmplitude = amplitude;
    float oldSpeed     = 0;

    float  effectSpeed  = 0.1f;
    double previousTime = 0;
    double time         = 0;

    boolean dots      = false;
    int     shapeKind = LINES;

    public SineSource(LiveControl parent)
    {
        super(parent);
        this.parent  = parent;
        previousTime = parent.millis();
    }

    @Override
    public void update()
    {
        IldaRenderer renderer = getRenderer();
        renderer.beginDraw();
        oldFrequency = oldFrequency + (frequency - oldFrequency) * effectSpeed * 0.2f;
        oldAmplitude = oldAmplitude + (amplitude - oldAmplitude) * effectSpeed;
        oldSpeed     = oldSpeed + (speed - oldSpeed) * effectSpeed;
        time         = time + (parent.millis() - previousTime) * oldSpeed;
        previousTime = parent.millis();
        renderer.beginShape(shapeKind);

        for (int i = 0; i < amount; i++) {

            double phase = TWO_PI * (map(i - amount / 2, 0, amount, 0, oldFrequency) + time);
            renderer.stroke(lerpColor(firstColor, secondColor, abs(map(i, 0, amount, 0, 2) - 1), 3));
            renderer.vertex((parent.width * i) / amount,
                (float) (parent.height * (0.5 + 0.5 * oldAmplitude * Math.sin(phase))));
        }
        renderer.endShape();
        renderer.endDraw();
    }
}
