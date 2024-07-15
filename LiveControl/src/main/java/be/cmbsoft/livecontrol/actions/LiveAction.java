package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;
import cmbsoft.cgui.control.CAction;

public abstract class LiveAction implements CAction
{

    private final LiveControl liveControl;

    public LiveAction(LiveControl liveControl)
    {
        this.liveControl = liveControl;
    }

    public LiveControl parent()
    {
        return liveControl;
    }
}
