package be.cmbsoft.livecontrol.sources;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.ildaviewer.oscillabstract.Workspace;
import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.SourceWrapper;

public class OscillabstractSourceWrapper extends SourceWrapper
{
    private final OscillabstractSource osc;
    private final List<Workspace>      workspaces = new ArrayList<>();

    public OscillabstractSourceWrapper(LiveControl parent)
    {
        osc = new OscillabstractSource(parent.getOscState(), parent.getOscillabstract());
    }

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
