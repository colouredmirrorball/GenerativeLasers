package be.cmbsoft.livecontrol.ui;

import be.cmbsoft.livecontrol.LiveControl;

public class UIConfig
{
    private final int backgroundColor;
    private final int foregroundColor;
    private final int activeColor;
    private final int fontColor;
    private final int mouseOverColor;

    public UIConfig(LiveControl liveControl)
    {
        backgroundColor = liveControl.color(20, 20, 20);
        foregroundColor = liveControl.color(50, 70, 128);
        activeColor = liveControl.color(80, 128, 208);
        fontColor = liveControl.color(200, 200, 255);
        mouseOverColor = liveControl.color(190, 190, 190);
    }

    public int getActiveColor()
    {
        return activeColor;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    public int getFontColor()
    {
        return fontColor;
    }

    public int getForegroundColor()
    {
        return foregroundColor;
    }

    public int getMouseOverColor()
    {
        return mouseOverColor;
    }

}
