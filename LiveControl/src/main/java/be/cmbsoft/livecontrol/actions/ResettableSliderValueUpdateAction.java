package be.cmbsoft.livecontrol.actions;


import be.cmbsoft.livecontrol.gui.GuiSlider;

public abstract class ResettableSliderValueUpdateAction implements SliderValueUpdatedAction
{
    float oldValue = 0;

    public void setOldValue(float value)
    {
        oldValue = value;
    }

    @Override
    public void execute(GuiSlider slider)
    {

    }

    @Override
    public void undo(GuiSlider slider)
    {
        slider.setPosition(oldValue);
    }

}
