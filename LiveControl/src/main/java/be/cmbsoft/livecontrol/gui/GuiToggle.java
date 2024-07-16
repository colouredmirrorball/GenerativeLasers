package be.cmbsoft.livecontrol.gui;


import be.cmbsoft.livecontrol.actions.IAction;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * A button with a state that can be toggled on or off
 * Created by Florian on 11/11/2017.
 */
public class GuiToggle extends GuiImageButton
{
    boolean state = false;
    IAction enabledAction;
    IAction disabledAction;
    public GuiToggle(GUIContainer parent, String name)
    {
        super(parent, null, name);
        setNamePosition(PConstants.CENTER);
    }

    public GuiToggle(GUIContainer parent, PImage image, String name)
    {
        super(parent, image, name);
    }

    public void display(PGraphics graphics)
    {
        if (state)
        {
            strokeWeight = 3;
            fillcolour = parent.getGuiStrokeColor();
            strokecolour = parent.getGuiActiveColor();
        }
        else
        {
            strokeWeight = 1;
            fillcolour = parent.getGuiFillColor();
            strokecolour = parent.getGuiStrokeColor();
        }
        super.display(graphics);
    }

    public GuiToggle toggle()
    {
        state = !state;
        return this;
    }

    public GuiToggle setState(boolean state)
    {
        this.state = state;
        return this;
    }

    @Override
    public void mouseClicked()
    {
        toggle();
        if (state)
        {
            if (enabledAction != null)
            {
                if (addActionToStack)
                {
                    parent.doAction(enabledAction);
                }
                else {enabledAction.execute();}
            }
        }
        else
        {
            if (disabledAction != null)
            {
                if (addActionToStack)
                {
                    parent.doAction(disabledAction);
                }
                else {disabledAction.execute();}
            }
        }
        super.mouseClicked();
    }

    public GuiToggle setEnabledAction(IAction action)
    {
        this.enabledAction = action;
        return this;
    }

    public GuiToggle setDisabledAction(IAction action)
    {
        this.disabledAction = action;
        return this;
    }

}
