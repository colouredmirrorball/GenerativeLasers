package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;

import be.cmbsoft.livecontrol.actions.IAction;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Created by Florian on 12/11/2017.
 */
public class GuiDraggableButton extends GuiImageButton
{
    protected boolean dragging;
    protected ArrayList<GuiElement> containedGuiElements = new ArrayList<>();
    boolean            copy          = true;
    PositionCalculator restPosition;
    IAction            endDragAction;
    boolean            handleEndDrag = false;
    boolean clickedOnElement = false;

    public GuiDraggableButton(GUIContainer graphics, PImage image, String name)
    {
        super(graphics, image, name);
        if (image != null)
        {
            width = image.width + 200;
            height = image.height;
        }
        else
        {
            PGraphics pg = graphics.createGraphics(10, 10);
            pg.beginDraw();
            pg.background(0);
            pg.endDraw();
            this.image = pg;
        }

    }

    @Override
    public void display(PGraphics graphics)
    {
        graphics.image(image, x, y);
        graphics.fill(graphics.red(parent.getGuiStrokeColor()), graphics.green(parent.getGuiStrokeColor()),
            graphics.blue(parent.getGuiStrokeColor()));
        graphics.textFont(parent.getFont(16));
        graphics.textAlign(PConstants.LEFT, PConstants.CENTER);
        graphics.text(title, x + image.width + 10, y, width - image.width - 10, height);
        if (dragging)
        {
            if (!parent.isMousePressed())
            {
                dragging = false;
                endDragging();
            }
            else
            {
                graphics.image(image, parent.getMouseX() - image.width / 2, parent.getMouseY() - image.height / 2);
                if (title != null)
                {
                    graphics.text(title, parent.getMouseX() + image.width / 2 + 10, parent.getMouseY());
                }
            }
        }
        updateContainedGui();


    }

    protected void updateContainedGui()
    {
        for (GuiElement element : containedGuiElements)
        {
            element.updatePosition();
            if (element.checkMouseOver(parent.getMouseX(), parent.getMouseY()))
            {
                if (parent.isMouseClicked())
                {
                    if (element.pressAction != null) element.executePressAction();
                }
                clickedOnElement = true;
            }
            element.display(parent.getGraphics());
        }
    }

    public void mouseClicked()
    {
        super.mouseClicked();
        clickedOnElement = true;
    }

    public GuiDraggableButton setEndDragAction(IAction endDragAction)
    {
        this.endDragAction = endDragAction;
        return this;
    }

    protected void endDragging()
    {
        handleEndDrag = true;
    }

    @Override
    public GuiDraggableButton setSize(int sx, int sy)
    {
        this.width = sx;
        this.height = sy;
        if (image.width > sx)
        {
            image.resize(sx, 0);
        }
        if (image.height > sy)
        {
            image.resize(0, sy);
        }
        return this;
    }

    public void updatePosition()
    {
        super.updatePosition();
    }

    public GuiDraggableButton setPosition(int x, int y)
    {
        super.setPosition(x, y);
        restPosition = posCalc;
        return this;
    }

    public GuiDraggableButton setPosition(PositionCalculator positionCalculator)
    {
        super.setPosition(positionCalculator);
        restPosition = posCalc;
        return this;
    }

    public void startDragging()
    {
        dragging = true;
    }

    public boolean canDrag()
    {
        return clickedOnElement;
    }

}
