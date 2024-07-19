package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;

public class ChaseDisabledAction extends UndoableAction implements IAction
{
    private final int         chaseIndex;
    private final LiveControl parent;

    public ChaseDisabledAction(LiveControl parent, int index)
    {
        this.parent = parent;
        this.chaseIndex = index;
    }

    @Override
    public void execute()
    {
        parent.disableChase(chaseIndex);
    }

    @Override
    public void undo()
    {
        parent.toggleChase(chaseIndex);
    }

}
