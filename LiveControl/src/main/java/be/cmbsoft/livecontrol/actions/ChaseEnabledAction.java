package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;

public class ChaseEnabledAction extends UndoableAction implements IAction
{
    private final int         chaseIndex;
    private final LiveControl parent;

    public ChaseEnabledAction(LiveControl parent, int chaseIndex)
    {
        this.parent     = parent;
        this.chaseIndex = chaseIndex;
    }

    @Override
    public void execute()
    {
        parent.enableChase(chaseIndex);
    }

    @Override
    public void undo()
    {
        parent.toggleChase(chaseIndex);
    }
}
