package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.ilda.IldaFrame;

public class EmptySource extends Source
{
    public static final  EmptySource INSTANCE    = new EmptySource();
    public static final IldaFrame EMPTY_FRAME = new IldaFrame();

    @Override
    public IldaFrame getFrame()
    {
        return EMPTY_FRAME;
    }

    @Override
    public void update()
    {

    }

}
