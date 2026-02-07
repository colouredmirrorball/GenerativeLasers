package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import be.cmbsoft.livecontrol.actions.SliderValueUpdatedAction;
import processing.core.PGraphics;
import processing.core.PVector;

import static be.cmbsoft.livecontrol.gui.GuiLinearLayout.Orientation.HORIZONTAL;
import static be.cmbsoft.livecontrol.gui.GuiLinearLayout.Orientation.VERTICAL;
import static processing.core.PApplet.map;

/**
 * Sort GUI elements in a nice list
 * Created by Florian on 12/11/2017.
 */
public class GuiLinearLayout extends GuiElement<GuiLinearLayout>
{
    public enum Orientation
    {
        HORIZONTAL, VERTICAL
    }

    protected final List<GuiElement<?>> elements = new ArrayList<>();
    int spacing = 10;
    int         contentSize = 0;
    float       scrollOffset;
    boolean     scrolling   = false;
    GuiScroller scrollBar;
    private Orientation orientation;
    private boolean     displayOutline = false;

    public GuiLinearLayout(GUIContainer parent)
    {
        super(parent);
    }

    @Override
    public void display(PGraphics graphics)
    {
        if (orientation == VERTICAL)
        {
            displayVertical();
        } else
        {
            displayHorizontal();
        }
        for (GuiElement<?> element : elements)
        {
            displayElement(element);
        }
        if (displayOutline)
        {
            displayOutline(graphics);
        }
        if (scrolling && scrollBar == null)
        {
            displayScrollbar();
        }
    }

    public GuiLinearLayout addElement(GuiElement<?> element)
    {
        elements.add(element);
        element.addVisibility(p -> visible);
        updateElementPositions();
        return this;
    }

    @Override
    public GuiLinearLayout setPosition(int x, int y)
    {
        super.setPosition(x, y);
        updatePosition();
        return this;
    }

    @Override
    public GuiLinearLayout setPosition(PositionCalculator positionCalculator)
    {
        super.setPosition(positionCalculator);
        updatePosition();
        return this;
    }

    public void updateElementPositions()
    {
        if (elements.isEmpty()) return;
        int posX = x, posY = y;
        if (orientation == HORIZONTAL)
        {
            posX += scrollOffset;
            contentSize = 0;
            for (GuiElement<?> el : elements)
            {
                el.setPosition(posX, posY);
                posX += el.width + spacing;
                contentSize += el.width + spacing;

            }
        } else
        {
            posY += scrollOffset;
            contentSize = 0;
            for (GuiElement<?> el : elements)
            {
                el.setPosition(posX, posY);
                posY += el.height + spacing;
                contentSize += el.height + spacing;
                if (scrolling) el.width -= 10;
            }
        }

        if (scrolling && scrollBar != null)
        {
            scrollBar.setSize(10, height);
        }

    }

    public GuiLinearLayout setOrientation(Orientation orientation)
    {
        if (orientation == HORIZONTAL || orientation == VERTICAL)
        {
            this.orientation = orientation;
        } else
        {
            throw new RuntimeException("Error: invalid mode when setting LinearLayout orientation. Mode can only be"
                + " HORIZONTAL or VERTICAL.");
        }
        return this;
    }

    public void removeElement(GuiElement<?> guiDraggableButton)
    {
        for (AtomicInteger i = new AtomicInteger(elements.size() - 1); i.get() >= 0; i.getAndDecrement())
        {
            GuiElement<?> element = elements.get(i.get());
            if (element == guiDraggableButton)
            {
                elements.remove(i.get());
            }
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        for (GuiElement<?> element : elements)
        {
            element.setVisible(visible);
        }
    }

    public int getSpacing()
    {
        return spacing;
    }

    public GuiLinearLayout setSpacing(int spacing)
    {
        this.spacing = spacing;
        return this;
    }

    public GuiLinearLayout setDisplayOutline(boolean b)
    {
        this.displayOutline = b;
        return this;
    }

    public void clear()
    {
        elements.clear();
    }

    private void displayScrollbar()
    {
        scrollBar = new GuiScroller(parent);
        scrollBar.setValueChangedAction(new SliderValueUpdatedAction()
            {
                @Override
                public void execute(GuiSlider slider)
                {
                    scrollOffset = map(slider.getPosition(), 1, 0, 0, -contentSize + height);
                }

                @Override
                public void undo(GuiSlider slider)
                {

                }

            }).setPosition(1f).setPosition(new PositionCalculator()
            {
                @Override
                public PVector updatePosition(GUIContainer parent, int width, int height)
                {
                    return new PVector(x + GuiLinearLayout.this.width - 10, y);
                }
            })

            .setSize(10, height);
        parent.addGuiElement(scrollBar);
    }

    private void displayOutline(PGraphics graphics)
    {
        graphics.strokeWeight(strokeWeight);
        if (mouseOver)
        {
            graphics.stroke(graphics.red(mouseovercolour), graphics.green(mouseovercolour),
                graphics.blue(mouseovercolour));
        } else
        {
            graphics.stroke(graphics.red(strokecolour), graphics.green(strokecolour), graphics.blue(strokecolour));
        }

        graphics.noFill();

        graphics.rect(x, y, width, height, 3);
    }

    private void displayElement(GuiElement<?> element)
    {
        PVector position = element.posCalc.updatePosition(parent, element.width, element.height);
        if (orientation == VERTICAL)
        {
            element.visible = (position.y >= y && position.y <= y + height);
        } else
        {
            element.visible = (position.x >= x && position.x <= x + width);
        }
        element.display(parent.getGraphics());
    }

    private void displayHorizontal()
    {
        if (contentSize > width)
        {
            //scrolling enabled!
            scrolling = true;
            if (parent.isMouseDragged() && mouseOver)
            {
                scrollOffset += parent.getMouseX() - parent.getPMouseX();
                if (scrollBar != null) scrollBar.setPosition(map(scrollOffset, 0, contentSize, 1, 0));
            }

        } else
        {
            scrollOffset = 0;
            scrolling    = false;
        }
    }

    private void displayVertical()
    {
        if (contentSize > height)
        {
            //scrolling enabled!
            scrolling = true;
            if (parent.isMouseDragged() && mouseOver)
            {
                scrollOffset += parent.getMouseY() - parent.getPMouseY();
                if (scrollBar != null) scrollBar.setPosition(map(scrollOffset, 0, -contentSize + height, 1, 0));
            }
        } else
        {
            scrollOffset = 0;
            scrolling    = false;
        }
    }
}
