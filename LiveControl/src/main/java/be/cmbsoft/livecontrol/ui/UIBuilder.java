package be.cmbsoft.livecontrol.ui;

import be.cmbsoft.livecontrol.LiveControl;
import be.cmbsoft.livecontrol.actions.AddOutput;
import cmbsoft.cgui.CGui;

public class UIBuilder
{
    private UIBuilder()
    {

    }

    public static void buildUI(CGui instance, LiveControl liveControl)
    {
        instance.addButton("Add output").addAction(new AddOutput(liveControl));
    }
}
