package be.cmbsoft.livecontrol.actions;


import be.cmbsoft.livecontrol.gui.GuiDraggableButton;
import be.cmbsoft.livecontrol.gui.GuiDraggableList;

public abstract class DropAction implements IAction
{
    public abstract void execute(GuiDraggableButton guiDraggableButton, GuiDraggableList origin,
        GuiDraggableList target);

}
