package be.cmbsoft.livecontrol.gui;

import be.cmbsoft.livecontrol.actions.SliderValueUpdatedAction;
import be.cmbsoft.livecontrol.actions.UndoableAction;
import processing.core.PGraphics;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.constrain;
import static processing.core.PApplet.map;

/**
 * Slip 'n' slide
 * Created by Florian on 11/11/2017.
 */
public class GuiSlider extends GuiElement<GuiSlider>
{
    float pos = 0;
    float min = 0;
    float max = 1;
    private boolean                  dragging   = false;
    private float                    sliderSize = 10;
    private float                    oldC       = 0;
    private boolean                  easing     = false;
    private float                    dampFactor = 0.25f;
    private float                    newValue   = 0;
    private SliderValueUpdatedAction valueUpdatedAction;

    GuiSlider(GUIContainer parent)
    {
        super(parent);
        dampFactor = parent.getSliderDampFactor();
    }

    public void display(PGraphics graphics)
    {

        move();

//Display
        graphics.strokeWeight(1);
        graphics.stroke(graphics.red(strokecolour), graphics.green(strokecolour), graphics.blue(strokecolour));
        graphics.fill(graphics.red(fillcolour), graphics.green(fillcolour), graphics.blue(fillcolour));
        if (width < height)
        {
            //vertical slider
            graphics.line(x + width * 0.5f, y, x + width * 0.5f, y + height);
            graphics.line(x + width * 0.25f, y, x + width * 0.75f, y);
            graphics.line(x + width * 0.25f, y + height, x + width * 0.75f, y + height);
            graphics.fill(graphics.red(fillcolour), graphics.green(fillcolour), graphics.blue(fillcolour), 150);
            float sliderPos = map(pos, min, max, y + height, y);
            graphics.rect(x, sliderPos - sliderSize, width, sliderSize);
            graphics.rect(x, sliderPos, width, sliderSize);
        }
        else
        {
            //horizontal slider
            graphics.line(x, y + height * 0.5f, x + width, y + height * 0.5f);
            graphics.line(x, y + height * 0.25f, x, y + height * 0.75f);
            graphics.line(x + width, y + height * 0.25f, x + width, y + height * 0.75f);
            graphics.fill(graphics.red(fillcolour), graphics.green(fillcolour), graphics.blue(fillcolour), 150);
            float sliderPos = map(pos, min, max, x, x + width);
            graphics.rect(sliderPos - sliderSize, y, sliderSize, height);
            graphics.rect(sliderPos, y, sliderSize, height);
            if (title != null)
            {
                graphics.fill(strokecolour);
                graphics.textSize(10);
                graphics.text(title, x, y + height + 6);
            }
        }

    }

    void move()
    {
        //Easing/damping:
        if (easing)
        {
            //parent.println(pos, newValue, (newValue - pos)*dampFactor);
            pos = pos + (newValue - pos) * dampFactor;
            valueUpdated();

            float dampEpsilon = 0.0025f;
            if (abs((pos - newValue) / (max - min)) < dampEpsilon)
            {
                easing = false;
            }
        }

        if (dragging && !parent.isMousePressed())
        {
            dragging = false;
        }
    }

    public GuiSlider setSliderPosition(float position)
    {
        this.pos = constrain(position, min, max);
        valueUpdatedAction.execute(this);
        return this;
    }

    public GuiSlider setBounds(float min, float max)
    {
        this.min = min;
        this.max = max;
        return this;
    }

    public void mousePressed()
    {
        /*
        if (mouseOverFader())
        {
            dragging = true;
            oldC = pos;
        }
        else
        {
        */
        //ease into the new value
        float newValue;
        if (width < height)
        {
            //vertical slider
            newValue = constrain(map(parent.getMouseY(), y, y + height, max, min), min, max);
        }
        else
        {
            //horizontal slider
            newValue = constrain(map(parent.getMouseX(), x, x + width, min, max), min, max);
        }
        reachValue(newValue);
        valueUpdated();
        //parent.println(newValue);
        //}
        super.mousePressed();
    }

    private void valueUpdated()
    {
        if (valueUpdatedAction != null)
        {
            if (addActionToStack)
            {
                GuiSlider currentSlider = this;
                parent.doAction(new UndoableAction()
                {
                    @Override
                    public void execute()
                    {
                        valueUpdatedAction.execute(currentSlider);
                    }

                    @Override
                    public void undo()
                    {
                        valueUpdatedAction.undo(currentSlider);
                    }
                });
            }
            else {valueUpdatedAction.execute(this);}
        }
    }

    public void reachValue(float newValue)
    {
        easing = true;
        this.newValue = newValue;
    }

    public boolean mouseOverFader()
    {
        int posX = parent.getMouseX();
        int posY = parent.getMouseY();
        if (width < height)
        {
            //vertical
            float faderY = map(pos, max, min, y, y + height);
            return posY >= faderY - sliderSize && posY <= faderY + sliderSize;
        }
        //horizontal
        float faderX = map(pos, min, max, x, x + width);
        return posX >= faderX - sliderSize && posX <= faderX + sliderSize;
    }

    public float getPosition()
    {
        return pos;
    }

    public GuiSlider setPosition(float value)
    {
        this.pos = value;
        return this;
    }

    public float getSliderSize()
    {
        return sliderSize;
    }

    public void setSliderSize(float sliderSize)
    {
        this.sliderSize = sliderSize;
    }

    public GuiSlider setValueChangedAction(SliderValueUpdatedAction action)
    {
        this.valueUpdatedAction = action;
        return this;
    }

}
