package be.cmbsoft.livecontrol.actions;

import java.util.UUID;

import be.cmbsoft.livecontrol.LiveControl;

public class AddOutput extends UndoableAction
{
    private final LiveControl parent;
    private final String id = UUID.randomUUID().toString();

    public AddOutput(LiveControl parent)
    {
        super();
        this.parent = parent;
    }

    @Override
    public void execute()
    {
        System.out.println("new output");
        parent.addOutput(id);
    }

    @Override
    public void undo()
    {
        parent.removeOutput(id);
    }
}
