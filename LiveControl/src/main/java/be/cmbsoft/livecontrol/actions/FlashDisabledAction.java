package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;

public class FlashDisabledAction extends UndoableAction implements IAction
{
    private final LiveControl parent;

    public FlashDisabledAction(LiveControl parent)
    {
        this.parent = parent;
    }

    @Override
    public void execute()
    {
        parent.setFlash(false);
    }

    @Override
    public void undo()
    {
        parent.setFlash(true);
    }

}
