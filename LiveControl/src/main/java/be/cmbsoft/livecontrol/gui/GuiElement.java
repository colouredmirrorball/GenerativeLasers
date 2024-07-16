package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.livecontrol.actions.IAction;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * A parent class for all cmb.soft.text2laser.gui.GUI elements
 * Created by Florian on 10/11/2017.
 */
public class GuiElement
{
    public int x = 0, y = 0;
    public int width = 20, height = 20;
    public String title = "";
    public List<Visibility> visibilities = new ArrayList<>();
    protected int strokecolour;
    protected int mouseovercolour;
    protected GUIContainer parent;
    protected boolean mouseOver = false;
    protected boolean visible = true;
    int fillcolour;
    int activecolour;
    int strokeWeight = 1;
    IAction pressAction;
    boolean addActionToStack = false;
    PositionCalculator posCalc;
    PositionCalculator sizeCalc;
    boolean clicked = false;
    String infoText = "";
    private IAction releaseAction;
    private IAction holdAction;
    private   int     groupIndex;

    public GuiElement(GUIContainer parent)
    {
        this.parent = parent;
        fillcolour = parent.getGuiFillColor();
        strokecolour = parent.getGuiStrokeColor();
        mouseovercolour = parent.getGuiMouseOverColor();
        activecolour = parent.getGuiActiveColor();
        visibilities.add(new Visibility()
        {
            @Override
            public boolean isVisible(GUIContainer parent)
            {
                return visible;
            }
        });
        setPosition(x, y);
    }

    public void display(PGraphics graphics)
    {


        graphics.strokeWeight(strokeWeight);
        graphics.textFont(parent.getFont(16));
        if (mouseOver)
        {
            graphics.stroke(graphics.red(mouseovercolour), graphics.green(mouseovercolour),
                graphics.blue(mouseovercolour));
        }
        else {graphics.stroke(graphics.red(strokecolour), graphics.green(strokecolour), graphics.blue(strokecolour));}
        if (clicked)
        {
            graphics.fill(graphics.red(activecolour), graphics.green(activecolour),
                graphics.blue(activecolour));
        }
        else {graphics.fill(graphics.red(fillcolour), graphics.green(fillcolour), graphics.blue(fillcolour));}
        graphics.rect(x, y, width, height);
        if (clicked) {graphics.fill(0);}
        else {graphics.fill(graphics.red(strokecolour), graphics.green(strokecolour), graphics.blue(strokecolour));}
        graphics.textAlign(PConstants.CENTER, PConstants.TOP);
        graphics.text(title, x + 3, y + 3, width, height);
        if (clicked && !parent.isMousePressed()) clicked = false;

    }

    public void updatePosition()
    {
        PVector position = posCalc.updatePosition(parent);
        x = (int) position.x;
        y = (int) position.y;
        if (sizeCalc != null)
        {
            PVector size = sizeCalc.updatePosition(parent);
            width = (int) size.x;
            height = (int) size.y;
        }

    }

    public GuiElement setReleaseAction(IAction a)
    {
        this.releaseAction = a;
        return this;
    }

    public GuiElement setPressAction(IAction a)
    {
        this.pressAction = a;
        return this;
    }

    public GuiElement setHoldAction(IAction action)
    {
        this.holdAction = action;
        return this;
    }

    public GuiElement setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
        posCalc = new PositionCalculator()
        {
            //Absolute position
            @Override
            public PVector updatePosition(GUIContainer parent)
            {
                position.set(x, y);
                return position;
            }
        };
        return this;
    }

    public GuiElement setPosition(PositionCalculator position)
    {
        posCalc = position;
        return this;
    }

    public GuiElement setSize(PositionCalculator size)
    {
        sizeCalc = size;
        return this;
    }

    public GuiElement setSize(int sx, int sy)
    {
        sizeCalc = new PositionCalculator()
        {
            @Override
            public PVector updatePosition(GUIContainer parent)
            {
                position.set(sx, sy);
                return position;
            }
        };
        return this;
    }

    public void executePressAction()
    {
        pressAction.execute();
    }


    public GuiElement addVisibility(Visibility visibility)
    {
        visibilities.add(visibility);
        return this;
    }

    public GuiElement addToLinearLayout(GuiLinearLayout linearLayout)
    {
        linearLayout.addElement(this);
        return this;
    }

    public boolean checkMouseOver(int mouseX, int mouseY)
    {
        mouseOver = x <= mouseX && x + width >= mouseX && y <= mouseY && y + height >= mouseY;
        if (mouseOver && !infoText.isEmpty())
        {
            parent.setMouseOverInfoText(infoText);
        }
        return mouseOver;
    }

    public void mouseClicked()
    {
        clicked = true;
        if (pressAction != null)
        {
            if (addActionToStack)
            {
                parent.doAction(pressAction);
            }
            else {pressAction.execute();}
        }

    }

    public void mouseReleased()
    {
        clicked = false;
        if (releaseAction != null)
        {
            if (addActionToStack)
            {
                parent.doAction(releaseAction);
            }
            else {releaseAction.execute();}
        }
    }

    public void mousePressed()
    {
        clicked = true;
        if (holdAction != null)
        {
            if (addActionToStack)
            {
                parent.doAction(holdAction);
            }
            else {holdAction.execute();}
        }
    }

    public String getInfoText()
    {
        return infoText;
    }

    public GuiElement setInfoText(String infoText)
    {
        this.infoText = infoText;
        return this;
    }

    public GuiElement setStrokeColour(int colour)
    {
        this.strokecolour = colour;
        return this;
    }

    public String getTitle()
    {
        return title;
    }

    public GuiElement setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public GuiElement setFillcolour(int fillcolour)
    {
        this.fillcolour = fillcolour;
        return this;
    }

    public void setMouseovercolour(int mouseovercolour)
    {
        this.mouseovercolour = mouseovercolour;
    }

    public void setActivecolour(int activecolour)
    {
        this.activecolour = activecolour;
    }

    public int getGroupIndex()
    {
        return groupIndex;
    }

    public GuiElement setGroupIndex(int groupIndex)
    {
        this.groupIndex = groupIndex;
        return this;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

}
