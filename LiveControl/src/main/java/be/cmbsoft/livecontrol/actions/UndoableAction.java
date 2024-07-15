package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;

public abstract class UndoableAction
{

    private final LiveControl liveControl;

    protected UndoableAction(LiveControl liveControl)
    {
        this.liveControl = liveControl;
    }

    public LiveControl parent()
    {
        return liveControl;
    }

    public abstract void execute();

    public abstract void undo();
}
