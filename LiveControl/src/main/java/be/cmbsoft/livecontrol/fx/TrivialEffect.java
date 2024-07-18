package be.cmbsoft.livecontrol.fx;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.livecontrol.LiveControl;

public class TrivialEffect extends Effect
{
    @Override
    public IldaFrame apply(IldaFrame ildaFrame)
    {
        return ildaFrame;
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
