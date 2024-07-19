package be.cmbsoft.livecontrol.sources.audio;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.livecontrol.LiveControl;
import static processing.core.PApplet.map;
import static processing.core.PConstants.HSB;

public class SpectrumBarsSource extends AudioSource
{

    private static final int MAX_BANDS = 16;
    private final        int barWidth;
    private final        int height;


    public SpectrumBarsSource(LiveControl parent)
    {
        super(parent);
        barWidth = parent.width / MAX_BANDS;
        height = parent.height;
    }

    @Override
    public void update()
    {
        IldaRenderer renderer = getRenderer();
        renderer.beginDraw();
        renderer.colorMode(HSB);
        float[] spectrum = getProcessor().getAnalysedFftSpectrum();
        for (int i = 0; i < MAX_BANDS; i++)
        {
            float signal = spectrum[i];
            renderer.stroke(map(signal, 0, 0.5f, 90, 0), 255, 255);
            // Draw the rectangles, adjust their height using the scale factor
            renderer.rect(i * barWidth, height, barWidth, -signal * height * 2);
        }
        renderer.endDraw();
    }


}
