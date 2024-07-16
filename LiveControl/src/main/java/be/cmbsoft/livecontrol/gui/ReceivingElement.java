package be.cmbsoft.livecontrol.gui;

/**
 * Created by Florian on 17/11/2017.
 */
public interface ReceivingElement
{

    public void dropped(GuiDraggableButton button);

    public boolean isOverElement(int x, int y);

}
