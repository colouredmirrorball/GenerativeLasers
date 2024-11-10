package be.cmbsoft.livecontrol;

import be.cmbsoft.ilda.IldaFrame;
import static be.cmbsoft.livecontrol.sources.EmptySource.EMPTY_FRAME;
import be.cmbsoft.livecontrol.sources.Source;

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

    public void mouseClicked()
    {

    }

    protected abstract Source provideNextSource();

    public void previous()
    {
        source = providePreviousSource();
    }

    protected abstract Source providePreviousSource();

}
