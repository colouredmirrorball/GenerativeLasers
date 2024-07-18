package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PApplet;

import static processing.core.PApplet.map;
import static processing.core.PConstants.HSB;

public class VUBarsSource extends AudioSource
{

    private static final int BARS = 10;
    private final        int barWidth;
    private final        int width;
    private final        int height;


    public VUBarsSource(AudioProcessor processor, PApplet parent)
    {
        super(processor, parent);
        width = parent.width;
        height = parent.height;
        barWidth = height / BARS - 10;
    }

    @Override
    public void update()
    {
        IldaRenderer r = getRenderer();
        r.colorMode(HSB);
        float signalLeft  = map(getProcessor().getAmplitudeLeft().analyze(), 0, 0.5f, 0, BARS);
        float signalRight = map(getProcessor().getAmplitudeRight().analyze(), 0, 0.5f, 0, BARS);

        for (int i = 0; i < BARS; i++)
        {
            if (signalLeft >= i)
            {
                r.stroke(map(i, 0, BARS, 90, 0), 255, 255);
                r.rect(width / 2 - barWidth - 10, height - (i + 1) * (barWidth + 10), barWidth, barWidth);
            }
            if (signalRight >= i)
            {
                r.stroke(map(i, 0, BARS, 90, 0), 255, 255);
                r.rect(width / 2 + barWidth + 10, height - (i + 1) * (barWidth + 10), barWidth, barWidth);
            }
        }
    }


}
