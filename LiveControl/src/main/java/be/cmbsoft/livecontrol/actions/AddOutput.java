package be.cmbsoft.livecontrol.actions;

import java.util.UUID;

import be.cmbsoft.livecontrol.LiveControl;

public class AddOutput extends UndoableAction
{
    private final LiveControl parent;
    private final UUID        uuid = UUID.randomUUID();

    public AddOutput(LiveControl parent)
    {
        super();
        this.parent = parent;
    }

    @Override
    public void execute()
    {
        parent.addOutput(uuid);
    }

    @Override
    public void undo()
    {
        parent.removeOutput(uuid);
    }
}
