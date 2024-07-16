package be.cmbsoft.livecontrol.gui;


/**
 * A list with toggles
 * Created by Florian on 12/11/2017.
 */
public class GuiToggleList extends GuiLinearLayout
{
    boolean allowMultiselect = false;


    public GuiToggleList(GUIContainer parent)
    {
        super(parent);
    }

    public GuiToggleList activate(int which)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            GuiToggle toggle = (GuiToggle) elements.get(i);


            toggle.setState(i == which);

        }
        return this;
    }

    public GuiToggle addToggle(GuiToggle toggle)
    {
        elements.add(toggle);
        return toggle;
    }

}
