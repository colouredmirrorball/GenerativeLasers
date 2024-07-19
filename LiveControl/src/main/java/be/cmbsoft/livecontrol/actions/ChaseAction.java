package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.LiveControl;

public class ChaseAction extends UndoableAction implements IAction
{
    private final int         chaseIndex;
    private final LiveControl parent;

    public ChaseAction(LiveControl parent, int chaseIndex)
    {
        this.parent     = parent;
        this.chaseIndex = chaseIndex;
    }

    @Override
    public void execute()
    {

        parent.toggleChase(chaseIndex);
    }

    @Override
    public void undo()
    {
        parent.toggleChase(chaseIndex);
    }
}
