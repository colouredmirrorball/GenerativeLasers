package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;

public class FlashEnabledAction extends UndoableAction implements IAction
{
    private final LiveControl parent;

    public FlashEnabledAction(LiveControl parent)
    {
        this.parent = parent;
    }

    @Override
    public void execute()
    {
        parent.setFlash(true);
    }

    @Override
    public void undo()
    {
        parent.setFlash(false);
    }

}
