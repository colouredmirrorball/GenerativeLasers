package be.cmbsoft.livecontrol.fx;

import be.cmbsoft.ilda.IldaFrame;

public abstract class Source
{
    public abstract IldaFrame getFrame();

    public abstract void update();

}
