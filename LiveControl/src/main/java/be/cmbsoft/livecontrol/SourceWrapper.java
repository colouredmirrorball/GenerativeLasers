package be.cmbsoft.livecontrol;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.livecontrol.sources.Source;

import static be.cmbsoft.livecontrol.sources.EmptySource.EMPTY_FRAME;

public abstract class SourceWrapper
{
    Source source;

    protected SourceWrapper()
    {
    }

    public IldaFrame getFrame()
    {
        return source == null ? EMPTY_FRAME : source.getFrame();
    }

    public void next()
    {
        source = provideNextSource();
    }

    protected abstract Source provideNextSource();

    public void previous()
    {
        source = providePreviousSource();
    }

    protected abstract Source providePreviousSource();

}
