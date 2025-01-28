package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.cmbsoft.livecontrol.actions.IAction;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * A parent class for all GUI elements
 * Created by Florian on 10/11/2017.
 */
public abstract class GuiElement<T extends GuiElement<T>>
{
    public int x = 0, y = 0;
    public int width = 20, height = 20;
    public String title = "";
    private final List<Visibility> visibilities = new ArrayList<>();
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
    private final T   me;
    private       int fontSize = 16;

    protected GuiElement(GUIContainer parent)
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
        me = (T) this;
    }

    public void display(PGraphics graphics)
    {
        graphics.strokeWeight(strokeWeight);
        graphics.textFont(parent.getFont(fontSize));
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
        graphics.textAlign(PConstants.CENTER, PConstants.CENTER);
        graphics.text(Optional.ofNullable(title).orElse("Untitled"), x + 3, y + 3, width, height);
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

    public T setReleaseAction(IAction a)
    {
        this.releaseAction = a;
        return me;
    }

    public T setPressAction(IAction a)
    {
        this.pressAction = a;
        return me;
    }

    public T setHoldAction(IAction action)
    {
        this.holdAction = action;
        return me;
    }

    public T setPosition(int x, int y)
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
        return me;
    }

    public T setPosition(PositionCalculator position)
    {
        posCalc = position;
        return me;
    }

    public T setSize(PositionCalculator size)
    {
        sizeCalc = size;
        return me;
    }

    public T setSize(int sx, int sy)
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
        return me;
    }

    public void executePressAction()
    {
        pressAction.execute();
    }


    public T addVisibility(Visibility visibility)
    {
        visibilities.add(visibility);
        return me;
    }

    public T addToLinearLayout(GuiLinearLayout linearLayout)
    {
        linearLayout.addElement(this);
        return me;
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

    public T setInfoText(String infoText)
    {
        this.infoText = infoText;
        return me;
    }

    public T setStrokeColour(int colour)
    {
        this.strokecolour = colour;
        return me;
    }

    public String getTitle()
    {
        return title;
    }

    public T setTitle(String title)
    {
        this.title = title;
        return me;
    }

    public T setFillcolour(int fillcolour)
    {
        this.fillcolour = fillcolour;
        return me;
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

    public T setGroupIndex(int groupIndex)
    {
        this.groupIndex = groupIndex;
        visible = parent.getActiveGroupIndex() == groupIndex;
        return me;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public T setFontSize(int fontSize)
    {
        this.fontSize = fontSize;
        return me;
    }

    public List<Visibility> getVisibilities()
    {
        return visibilities;
    }

    public T setImages(PImage[] sources)
    {
        return me;
    }

}
