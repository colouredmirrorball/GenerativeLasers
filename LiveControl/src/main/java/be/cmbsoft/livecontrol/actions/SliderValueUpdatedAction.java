package be.cmbsoft.livecontrol.actions;

import be.cmbsoft.livecontrol.gui.GuiSlider;

public interface SliderValueUpdatedAction
{
    void execute(GuiSlider slider);

    void undo(GuiSlider slider);

}
