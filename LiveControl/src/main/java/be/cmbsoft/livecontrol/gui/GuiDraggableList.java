package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;

import be.cmbsoft.livecontrol.actions.DropAction;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Created by Florian on 12/11/2017.
 */
public class GuiDraggableList extends GuiLinearLayout implements ReceivingElement
{
    PositionCalculator elementsSize;

    ArrayList<ReceivingElement> receivers            = new ArrayList<ReceivingElement>();
    GuiDraggableButton          currentDraggedButton = null;

    DropAction dropAction;


    public GuiDraggableList(GUIContainer parent)
    {
        super(parent);
        elementsSize = new PositionCalculator()
        {
            @Override
            public PVector updatePosition(GUIContainer parent)
            {
                position.set(width, 30);
                return position;
            }
        };

    }

    public GuiDraggableButton getCurrentDraggedButton()
    {
        return currentDraggedButton;
    }

    @Override
    public void display(PGraphics graphics)
    {
        super.display(graphics);
        for (GuiElement element : elements)
        {
            GuiDraggableButton button = (GuiDraggableButton) element;
            boolean            over   = element.checkMouseOver(parent.getMouseX(), parent.getMouseY());
            if (parent.isMouseClicked() && over)
            {

                element.mouseClicked();
                if (button.canDrag())
                {
                    button.startDragging();
                    currentDraggedButton = button;
                    parent.releaseMouse();
                }
            }
            if (button.handleEndDrag)
            {
                handleDrop(button);
                currentDraggedButton = null;
                button.handleEndDrag = false;
            }
            if (currentDraggedButton != null && over)
            {
                //display insert cursor at bottom of element
                graphics.stroke(255);
                graphics.line(button.x, button.y + button.height + 2, button.x + button.width,
                    button.y + button.height + 2);

            }


        }
    }

    public GuiDraggableList setElementSize(int elsx, int elsy)
    {
        setElementSize(new PositionCalculator()
        {
            @Override
            public PVector updatePosition(GUIContainer parent)
            {
                position.set(elsx, elsy);
                return position;
            }
        });
        return this;
    }

    public GuiDraggableList setElementSize(PositionCalculator calculator)
    {
        this.elementsSize = calculator;
        return this;
    }

    public void updatePosition()
    {
        super.updatePosition();
        updateElementPositions();
    }

    public void updateElementPositions()
    {
        if (elements == null)
        {
            return;
        }


        for (GuiElement element : elements)
        {
            element.setSize(elementsSize);

            element.updatePosition();

        }

        super.updateElementPositions();
    }

    public boolean contains(String what)
    {
        for (GuiElement element : elements)
        {
            if (element.title.equals(what))
            {
                return true;
            }
        }
        return false;
    }


    public GuiDraggableButton addButton(PImage image, String text)
    {
        GuiDraggableButton button = new GuiDraggableButton(parent, image, text);
        elements.add(button);
        button.setSize((int) elementsSize.updatePosition(parent).x, (int) elementsSize.updatePosition(parent).y);
        updateElementPositions();
        return button;
    }

    public void clearElements()
    {
        elements.clear();
    }

    public GuiDraggableList setSize(int sx, int sy)
    {

        super.setSize(sx, sy);
        setElementSize(sx - (scrolling ? 10 : 0), (int) elementsSize.updatePosition(parent).y);
        return this;
    }

    public GuiDraggableList addReceiver(ReceivingElement receiver)
    {
        receivers.add(receiver);
        return this;
    }

    public GuiDraggableList addReceivers(ArrayList<ReceivingElement> receivingElements)
    {
        receivers.addAll(receivingElements);
        return this;
    }

    public void handleDrop(GuiDraggableButton button)
    {
        if (button.endDragAction != null)
        {
            parent.doAction(button.endDragAction);
        }
        if (dropAction != null)
        {
            GuiDraggableList receiver = null;
            for (ReceivingElement element : receivers)
            {
                if (element.isOverElement(parent.getMouseX(), parent.getMouseY()))
                {
                    receiver = (GuiDraggableList) element;
                }
            }
            dropAction.execute(button, this, receiver);
        }
        for (ReceivingElement element : receivers)
        {
            if (element.isOverElement(parent.getMouseX(), parent.getMouseY()))
            {
                element.dropped(button);
            }
        }
    }

    public GuiDraggableList setDropAction(DropAction dropAction)
    {
        this.dropAction = dropAction;
        return this;
    }

    @Override
    public void dropped(GuiDraggableButton button)
    {

    }

    @Override
    public boolean isOverElement(int x, int y)
    {
        updatePosition();
        boolean over = super.checkMouseOver(x, y);
        //indicate
        return over;
    }

}
