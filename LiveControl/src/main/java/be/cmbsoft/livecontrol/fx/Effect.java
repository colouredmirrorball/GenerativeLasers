package be.cmbsoft.livecontrol.fx;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.livecontrol.LiveControl;

public abstract class Effect
{
    public abstract IldaFrame apply(IldaFrame ildaFrame);

    public abstract void update(ProgramState state);

    public abstract void display(LiveControl parent, int x, int y, int w, int h);

}
