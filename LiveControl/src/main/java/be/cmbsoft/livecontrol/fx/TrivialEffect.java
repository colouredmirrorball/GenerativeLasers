package be.cmbsoft.livecontrol.fx;

import be.cmbsoft.ilda.IldaFrame;

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

}
