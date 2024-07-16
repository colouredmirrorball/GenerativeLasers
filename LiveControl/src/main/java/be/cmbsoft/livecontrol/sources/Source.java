package be.cmbsoft.livecontrol.sources;

import be.cmbsoft.ilda.IldaFrame;

public abstract class Source
{
    public abstract IldaFrame getFrame();

    public abstract void update();

}
