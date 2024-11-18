package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.livecontrol.SourceWrapper;
import be.cmbsoft.livecontrol.settings.SourceSettings;

public class EmptySourceWrapper extends SourceWrapper
{

    private record EmptySourceSettings() implements SourceSettings
    {
    }

    private static final Source emptySource = EmptySource.INSTANCE;

    @Override
    public SourceSettings getSettings()
    {
        return new EmptySourceSettings();
    }

    @Override
    protected Source provideNextSource()
    {
        return emptySource;
    }

    @Override
    protected Source providePreviousSource()
    {
        return emptySource;
    }

}
