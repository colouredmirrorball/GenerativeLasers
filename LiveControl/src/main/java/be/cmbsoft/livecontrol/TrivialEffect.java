package be.cmbsoft.livecontrol;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.livecontrol.fx.Effect;

public class TrivialEffect extends Effect
{
    @Override
    public IldaFrame apply(IldaFrame ildaFrame)
    {
        return ildaFrame;
    }

}
