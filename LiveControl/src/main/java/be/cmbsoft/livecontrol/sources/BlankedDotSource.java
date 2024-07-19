package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaPoint;

public class BlankedDotSource extends Source

{
    private final IldaFrame frame;

    public BlankedDotSource()
    {
        IldaPoint ildaPoint = new IldaPoint(0, 0, 0, 1, 1, 1, true);
        frame = new IldaFrame();
        frame.getPoints().add(ildaPoint);
    }

    @Override
    public IldaFrame getFrame()
    {
        return frame;
    }

    @Override
    public void update()
    {

    }
}
