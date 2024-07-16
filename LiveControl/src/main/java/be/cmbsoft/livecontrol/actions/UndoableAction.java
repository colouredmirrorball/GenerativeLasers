package be.cmbsoft.livecontrol.actions;

public abstract class UndoableAction implements IAction
{

    protected UndoableAction()
    {

    }

    public abstract void execute();

    public abstract void undo();
}
