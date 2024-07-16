package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.livecontrol.SourceWrapper;

public class EmptySourceWrapper extends SourceWrapper
{

    private static final Source emptySource = EmptySource.INSTANCE;

    @Override
    protected Source nextSupplier()
    {
        return emptySource;
    }

    @Override
    protected Source previousSupplier()
    {
        return emptySource;
    }

}
