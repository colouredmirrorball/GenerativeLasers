package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;

import static processing.core.PApplet.map;
import static processing.core.PConstants.HSB;

public class VUBarsSource extends AudioSource
{

    private static final int BARS = 10;
    //    private final        int barWidth;
    private final        int width;
    private final        int height;


    public VUBarsSource(LiveControl parent)
    {
        super(parent);
        width = parent.width;
        height = parent.height;

    }

    @Override
    public void update()
    {
        IldaRenderer r = getRenderer();
        r.beginDraw();
        r.colorMode(HSB);
        float signalLeft  = map(getProcessor().getAmplitudeLeft().analyze(), 0, 0.5f, 0, BARS);
        float signalRight = map(getProcessor().getAmplitudeRight().analyze(), 0, 0.5f, 0, BARS);
        int barWidth = barWidth = r.height / BARS;
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
        r.endDraw();
    }


}
