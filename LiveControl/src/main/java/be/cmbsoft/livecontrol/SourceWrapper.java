package be.cmbsoft.livecontrol;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.livecontrol.sources.Source;

public abstract class SourceWrapper
{
    Source source;
//    Supplier<Source> nextSupplier;
//    Supplier<Source> previousSupplier;

    protected SourceWrapper()
    {
//        next();
//        this.nextSupplier = nextSupplier;
//        this.previousSupplier = previousSupplier;
//        this.source = nextSupplier.get();
    }

    public IldaFrame getFrame()
    {
        return source.getFrame();
    }

    public void next()
    {
        source = provideNextSource();
//        source = nextSupplier.get();
    }

    protected abstract Source provideNextSource();

    public void previous()
    {
        source = providePreviousSource();
//        source = previousSupplier.get();
    }

    protected abstract Source providePreviousSource();

}
