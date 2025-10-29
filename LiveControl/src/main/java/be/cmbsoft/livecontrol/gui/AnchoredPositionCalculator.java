package be.cmbsoft.livecontrol.gui;

import be.cmbsoft.livecontrol.ui.PositionType;
import processing.core.PVector;

import static be.cmbsoft.ildaviewer.Utilities.requireValue;
import static be.cmbsoft.livecontrol.ui.PositionType.UPPER_LEFT_ANCHOR;

public class AnchoredPositionCalculator extends PositionCalculator
{


    private PositionType type = UPPER_LEFT_ANCHOR;
    private int          offsetX;
    private int          offsetY;

    public int getOffsetX()
    {
        return offsetX;
    }

    public void setOffsetX(int offsetX)
    {
        this.offsetX = offsetX;
    }

    public int getOffsetY()
    {
        return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    public PositionType getType()
    {
        return type;
    }

    public void setType(PositionType type)
    {
        this.type = type;
    }

    @Override
    public PVector updatePosition(GUIContainer parent, int width, int height)
    {

        float x = 0;
        float y = 0;
        switch (requireValue(type, "Position type"))
        {
            case UPPER_RIGHT_ANCHOR ->
            {
                x = parent.getWidth() - offsetX - width;
                y = offsetY;
            }
            case UPPER_LEFT_ANCHOR ->
            {
                x = offsetX;
                y = offsetY;
            }
            case LOWER_RIGHT_ANCHOR ->
            {
                //TODO finish
            }
            case LOWER_LEFT_ANCHOR ->
            {
                //TODO finish
            }

        }
        return new PVector(x, y);
    }


}
