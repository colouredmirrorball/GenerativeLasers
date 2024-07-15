package be.cmbsoft.livecontrol.actions;

import java.util.UUID;

import be.cmbsoft.livecontrol.LiveControl;

public class AddOutput extends LiveAction
{
    UUID uuid = UUID.randomUUID();

    public AddOutput(LiveControl liveControl)
    {
        super(liveControl);
    }

    @Override
    public void execute()
    {
        parent().addOutput(uuid);
    }

    @Override
    public void undo()
    {
        parent().removeOutput(uuid);
    }
}
