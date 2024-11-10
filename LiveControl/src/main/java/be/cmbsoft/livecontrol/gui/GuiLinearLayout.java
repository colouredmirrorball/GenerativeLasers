package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import be.cmbsoft.livecontrol.actions.SliderValueUpdatedAction;
import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PApplet.map;

/**
 * Sort cmb.soft.text2laser.gui.GUI elements in a nice list
 * Created by Florian on 12/11/2017.
 */
public class GuiLinearLayout extends GuiElement<GuiLinearLayout>
{
    public static final int HORIZONTAL = 10;
    public static final int VERTICAL   = 11;
    protected final List<GuiElement> elements       = new ArrayList<>();
    int mode;
    int spacing = 10;
    int         contentSize = 0;
    float       scrollOffset;
    boolean     scrolling   = false;
    GuiScroller scrollBar;
    private         boolean          displayOutline = false;

    public GuiLinearLayout(GUIContainer parent)
    {
        super(parent);
    }

    public void display(PGraphics graphics)
    {
        if (mode == VERTICAL)
        {
            if (contentSize > height)
            {
                //scrolling enabled!
                scrolling = true;
                if (parent.isMouseDragged() && mouseOver)
                {
                    scrollOffset += parent.getMouseY() - parent.getPMouseY();
                    if (scrollBar != null) scrollBar.setPosition(map(scrollOffset, 0, -contentSize + height, 1, 0));
                    //println(scrollOffset, scrollBar.updatePosition(), scrollBar.updatePosition()*contentSize,
                    // contentSize);
                }
            }
            else
            {
                scrollOffset = 0;
                scrolling = false;
            }
        }
        else
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

            }
            else
            {
                scrollOffset = 0;
                scrolling = false;
            }
        }
        for (GuiElement element : elements)
        {
            if (mode == VERTICAL)
            {
                PVector position = element.posCalc.updatePosition(parent);

                element.visible = (position.y >= y && position.y <= y + height);
            }
            else
            {
                PVector position = element.posCalc.updatePosition(parent);

                element.visible = (position.x >= x && position.x <= x + width);
            }
            element.display(parent.getGraphics());
        }
        if (displayOutline)
        {
            graphics.strokeWeight(strokeWeight);
            if (mouseOver)
            {
                graphics.stroke(graphics.red(mouseovercolour), graphics.green(mouseovercolour),
                    graphics.blue(mouseovercolour));
            }
            else
            {
                graphics.stroke(graphics.red(strokecolour), graphics.green(strokecolour), graphics.blue(strokecolour));
            }

            graphics.noFill();

            graphics.rect(x, y, width, height, 3);
        }
        if (scrolling)
        {
            if (scrollBar == null)
            {
                scrollBar = new GuiScroller(parent);
                scrollBar
                    .setValueChangedAction(new SliderValueUpdatedAction()
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

                    })
                    .setPosition(1f)
                    .setPosition(new PositionCalculator()
                    {
                        @Override
                        public PVector updatePosition(GUIContainer parent)
                        {
                            return new PVector(x + width - 10, y);
                        }
                    })

                    .setSize(10, height);
                parent.addGuiElement(scrollBar);


            }
        }
    }

    public GuiLinearLayout addElement(GuiElement element)
    {

        elements.add(element);
        updateElementPositions();
        return this;
    }

    public GuiLinearLayout setPosition(int x, int y)
    {
        super.setPosition(x, y);
        updatePosition();
        return this;
    }

    public GuiLinearLayout setPosition(PositionCalculator positionCalculator)
    {
        super.setPosition(positionCalculator);
        updatePosition();
        return this;
    }

    public void updateElementPositions()
    {
        if (elements == null) return;
        int posX = x, posY = y;
        if (mode == HORIZONTAL)
        {
            posX += scrollOffset;
            contentSize = 0;
            for (GuiElement el : elements)
            {
                el.setPosition(posX, posY);
                posX += el.width + spacing;
                contentSize += el.width + spacing;

            }
        }
        else
        {
            posY += scrollOffset;
            contentSize = 0;
            for (GuiElement el : elements)
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

    public GuiLinearLayout setMode(int mode)
    {
        if (mode == HORIZONTAL || mode == VERTICAL)
        {
            this.mode = mode;
        }
        else
        {
            throw new RuntimeException("Error: invalid mode when setting LinearLayout orientation. Mode can only be" +
                " HORIZONTAL or VERTICAL.");
        }
        return this;
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


    public void removeElement(GuiElement guiDraggableButton)
    {
        for (AtomicInteger i = new AtomicInteger(elements.size() - 1); i.get() >= 0; i.getAndDecrement())
        {
            GuiElement element = elements.get(i.get());
            if (element == guiDraggableButton)
            {
                elements.remove(i.get());
            }
        }
    }

    public void clear()
    {
        elements.clear();
    }

}
