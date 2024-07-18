package be.cmbsoft.livecontrol.fx;

import java.util.List;

import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.livecontrol.LiveControl;

public abstract class Effect
{
    public abstract List<IldaPoint> apply(List<IldaPoint> points);

    public abstract void update(ProgramState state);

    public abstract void display(LiveControl parent, int x, int y, int w, int h);

}
