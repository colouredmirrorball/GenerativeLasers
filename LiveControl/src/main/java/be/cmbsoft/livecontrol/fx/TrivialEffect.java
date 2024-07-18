package be.cmbsoft.livecontrol.fx;

import java.util.List;

import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.livecontrol.LiveControl;

public class TrivialEffect extends Effect
{
    @Override
    public List<IldaPoint> apply(List<IldaPoint> points)
    {
        return points;
    }

    @Override
    public void update(ProgramState state)
    {

    }

    @Override
    public void display(LiveControl parent, int x, int y, int w, int h)
    {

    }

}
