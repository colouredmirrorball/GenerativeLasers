package be.cmbsoft.lichtfestival;

import be.cmbsoft.laseroutput.Bounds;

public class Settings
{
    Bounds leftBounds;
    Bounds rightBounds;

    public Bounds getLeftBounds()
    {
        return leftBounds;
    }

    public void setLeftBounds(Bounds leftBounds)
    {
        this.leftBounds = leftBounds;
    }

    public Bounds getRightBounds()
    {
        return rightBounds;
    }

    public void setRightBounds(Bounds rightBounds)
    {
        this.rightBounds = rightBounds;
    }
}
