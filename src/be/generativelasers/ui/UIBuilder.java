package be.generativelasers.ui;

import cmb.soft.cgui.CGui;
import cmb.soft.cgui.CLayout;
import cmb.soft.cgui.CPane;

/**
 * @author Florian
 * Created on 25/07/2020
 */
public class UIBuilder
{

    private UIBuilder()
    {
    }

    public static void buildUI(CGui gui)
    {
        CLayout mainButtonsLayout = new CLayout("mainButtons");
        mainButtonsLayout.addButton(gui, "procedures");
        mainButtonsLayout.addButton(gui, "button2");
        CPane pane = new CPane(mainButtonsLayout);
        gui.addPane(pane);
    }
}
