package be.cmbsoft.livecontrol.sources;

import java.util.List;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.SourceWrapper;
import be.cmbsoft.livecontrol.sources.audio.AudioSource;
import be.cmbsoft.livecontrol.sources.audio.SpectrumBarsSource;
import be.cmbsoft.livecontrol.sources.audio.VUBarsSource;
import be.cmbsoft.livecontrol.sources.audio.WaveformSource;

public class AudioEffectsSourceWrapper extends SourceWrapper
{
    private final List<AudioSource> sources;
    private       int               position = 0;

    public AudioEffectsSourceWrapper(LiveControl liveControl)
    {
        sources = List.of(new SpectrumBarsSource(liveControl), new VUBarsSource(liveControl),
            new WaveformSource(liveControl));
    }

    @Override
    protected Source provideNextSource()
    {
        position++;
        if (position >= sources.size())
        {
            position = 0;
        }
        return sources.get(position);
    }

    @Override
    protected Source providePreviousSource()
    {
        position--;
        if (position < 0)
        {
            position = sources.size() - 1;
        }
        return sources.get(position);
    }

}
