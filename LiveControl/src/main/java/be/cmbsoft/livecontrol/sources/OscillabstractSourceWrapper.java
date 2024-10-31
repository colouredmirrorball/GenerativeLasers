package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.SourceWrapper;

public class OscillabstractSourceWrapper extends SourceWrapper
{
    private final LiveControl parent;
    private final OscillabstractSource source = new OscillabstractSource();

    public OscillabstractSourceWrapper(LiveControl liveControl)
    {
        this.parent = liveControl;
    }

    @Override
    protected Source provideNextSource()
    {
        source.loadWorkspace();
        return source;
    }

    @Override
    protected Source providePreviousSource()
    {
        source.loadWorkspace();
        return source;
    }

}
