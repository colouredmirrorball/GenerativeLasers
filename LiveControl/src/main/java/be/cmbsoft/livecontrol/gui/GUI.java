package be.cmbsoft.livecontrol.gui;


import java.util.ArrayList;
import java.util.List;

import processing.core.PImage;

import static be.cmbsoft.livecontrol.LiveControl.error;

/**
 * Wrapper class that groups all cmb.soft.text2laser.gui.GUI elements
 * Created by Florian on 10/11/2017.
 */
public class GUI
{
    private final GUIContainer     parent;
    public        List<GuiElement> elements = new ArrayList<>();

    public GUI(GUIContainer parent)
    {
        this.parent = parent;
    }

    public void update()
    {
        try
        {
            for (GuiElement element : elements)
            {
                boolean invisible = false;
                for (Visibility v : element.visibilities)
                {
                    invisible = invisible || !v.isVisible(parent);
                }
                if (!invisible)
                {
                    element.updatePosition();
                    boolean over = element.checkMouseOver(parent.getMouseX(), parent.getMouseY());
                    if (over && parent.isMouseClicked())
                    {
                        element.mouseClicked();

                    }
                    if (element.clicked)
                    {
                        if (parent.isMouseReleased())
                        {
                            element.mouseReleased();
                        }
                        else if (parent.isMousePressed())
                        {
                            element.mousePressed();
                        }
                    }
                    element.display(parent.getGraphics());
                }
            }
        }
        catch (Exception e)
        {
            error("Error when updating GUI!", e);
        }
    }


    public GuiToggle addToggle(String name)
    {
        GuiToggle t = new GuiToggle(parent, name);
        elements.add(t);
        return t;
    }

    public GuiButton addButton(String name)
    {
        GuiButton button = new GuiButton(parent, name);
        elements.add(button);
        return button;
    }

    public GuiSlider addSlider()
    {
        GuiSlider slider = new GuiSlider(parent);
        elements.add(slider);
        return slider;
    }

    public GuiImageButton addImageButton(PImage image, String name)
    {
        GuiImageButton imageButton = new GuiImageButton(parent, image, name);
        elements.add(imageButton);
        return imageButton;
    }

    public GuiLinearLayout addLinearLayout()
    {
        GuiLinearLayout linearLayout = new GuiLinearLayout(parent);
        elements.add(linearLayout);
        return linearLayout;
    }

    public GuiToggleList addToggleList()
    {
        GuiToggleList toggleList = new GuiToggleList(parent);
        elements.add(toggleList);
        return toggleList;
    }

    public GuiDraggableList addDraggableList()
    {
        GuiDraggableList draggableList = new GuiDraggableList(parent);
        elements.add(draggableList);
        return draggableList;
    }

    public GuiElement addGuiElement(GuiElement element)
    {
        elements.add(element);
        return element;
    }

    public void updateElementsPositions()
    {
        for (GuiElement element : elements)
        {
            element.updatePosition();
        }
    }

}
