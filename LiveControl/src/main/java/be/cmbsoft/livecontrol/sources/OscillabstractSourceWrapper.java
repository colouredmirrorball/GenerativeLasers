package be.cmbsoft.livecontrol.sources;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.ildaviewer.oscillabstract.Workspace;
import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.SourceWrapper;
import be.cmbsoft.livecontrol.settings.SourceSettings;
import be.cmbsoft.livecontrol.ui.UIBuilder;

public class OscillabstractSourceWrapper extends SourceWrapper
{
    public record OscillabstractSourceSettings(List<Workspace> workspaces) implements SourceSettings
    {
    }

    private final OscillabstractSource osc;
    private final List<Workspace>      workspaces = new ArrayList<>();
    private final LiveControl parent;

    public OscillabstractSourceWrapper(LiveControl parent)
    {
        osc = new OscillabstractSource(parent.getOscState(), parent.getOscillabstract());
        this.parent = parent;
    }

    public SourceWrapper setSettings(OscillabstractSourceSettings oscillabstractSourceSettings)
    {
        workspaces.clear();
        workspaces.addAll(oscillabstractSourceSettings.workspaces());
        return this;
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

    @Override
    public void mouseClicked()
    {
        parent.activateUITab(UIBuilder.Tab.OSCILLABSTRACT);
        parent.getOscillabstract().activateWorkspace(osc.getWorkspace());
    }

    @Override
    public SourceSettings getSettings()
    {
        return new OscillabstractSourceSettings(workspaces);
    }

}
