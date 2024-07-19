package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.fx.Parameter;
import static processing.core.PApplet.map;
import static processing.core.PConstants.LINE;

public class WaveformSource extends AudioSource
{


    private final int                width;
    private final int                height;
    private final Parameter hueParameter;

    public WaveformSource(LiveControl parent)
    {
        super(parent);
        width = parent.width;
        height = parent.height;
        hueParameter = new Parameter("waveformHue");
        parent.newParameter("waveformHue", hueParameter);
    }

    @Override
    public void update()
    {
        IldaRenderer r         = getRenderer();
        float[]      samples   = getProcessor().getWaveform().analyze();
        int hue = (int) hueParameter.getValue();
        float        intensity = getProcessor().getAmplitudeLeft().analyze();
        r.beginDraw();
        r.stroke(hue, 255, map(intensity, 0, 0.3f, 25, 255));

        r.beginShape(LINE);
        for (int i = 0; i < getProcessor().getSamplesAmount(); i++)
        {
            r.vertex(map(i, 0, getProcessor().getSamplesAmount(), 0, width), map(samples[i], -1, 1, 0, height));
        }
        r.endShape();
        r.endDraw();
    }


}
