package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.livecontrol.SourceWrapper;
import be.cmbsoft.livecontrol.settings.SourceSettings;

public class BlankedDotSourceWrapper extends SourceWrapper
{

    private record BlankedDotSettings() implements SourceSettings
    {
    }

    @Override
    public SourceSettings getSettings()
    {
        return new BlankedDotSettings();
    }

    @Override
    protected Source provideNextSource()
    {
        return null;
    }

    @Override
    protected Source providePreviousSource()
    {
        return null;
    }
}
