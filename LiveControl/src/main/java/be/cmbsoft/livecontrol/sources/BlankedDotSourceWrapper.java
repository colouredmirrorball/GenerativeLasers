package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.livecontrol.SourceWrapper;

public class BlankedDotSourceWrapper extends SourceWrapper
{

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
