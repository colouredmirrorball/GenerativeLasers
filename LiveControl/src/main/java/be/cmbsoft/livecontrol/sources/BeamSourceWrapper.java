package be.cmbsoft.livecontrol.sources;

import java.util.List;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.SourceWrapper;
import be.cmbsoft.livecontrol.sources.beams.BeamEffectSource;
import be.cmbsoft.livecontrol.sources.beams.BeamSource;
import be.cmbsoft.livecontrol.sources.beams.ConeSource;
import be.cmbsoft.livecontrol.sources.beams.LineEffect;
import be.cmbsoft.livecontrol.sources.beams.SineSource;

public class BeamSourceWrapper extends SourceWrapper
{
    private final List<BeamSource> sources;
    private       int              position = 0;

    public BeamSourceWrapper(LiveControl parent)
    {
        sources = List.of(new ConeSource(parent), new BeamEffectSource(parent), new LineEffect(parent),
            new SineSource(parent));
    }

    @Override

    protected Source provideNextSource()
    {
        position++;
        if (position >= sources.size()) {
            position = 0;
        }
        return sources.get(position);
    }

    @Override
    protected Source providePreviousSource()
    {
        position--;
        if (position < 0) {
            position = sources.size() - 1;
        }
        return sources.get(position);
    }
}
