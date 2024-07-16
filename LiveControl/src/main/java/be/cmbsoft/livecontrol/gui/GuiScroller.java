package be.cmbsoft.livecontrol.gui;

import processing.core.PGraphics;

import static processing.core.PApplet.constrain;

public class GuiScroller extends GuiSlider
{
    GuiScroller(GUIContainer parent)
    {
        super(parent);
        setBounds(0, 1);
    }

    public void display(PGraphics graphics)
    {
        super.display(graphics);
    }

    public GuiSlider setPosition(float value)
    {
        super.setPosition(value);
        pos = constrain(pos, min, max);
        return this;
    }

}
