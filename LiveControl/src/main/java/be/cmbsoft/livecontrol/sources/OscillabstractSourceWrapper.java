package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.livecontrol.SourceWrapper;

public class OscillabstractSourceWrapper extends SourceWrapper
{
    private final OscillabstractSource osc = new OscillabstractSource();

    @Override
    protected Source provideNextSource()
    {
        osc.loadWorkspace();
        return osc;
    }

    @Override
    protected Source providePreviousSource()
    {
        osc.loadWorkspace();
        return osc;
    }

}
