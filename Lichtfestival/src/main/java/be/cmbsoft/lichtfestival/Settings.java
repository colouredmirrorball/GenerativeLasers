package be.cmbsoft.lichtfestival;

import java.util.Map;

import be.cmbsoft.laseroutput.Bounds;
import processing.core.PVector;

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

    Map<Integer, PVector> effectLocations;

    public Map<Integer, PVector> getEffectLocations()
    {
        return effectLocations;
    }

    public void setEffectLocations(Map<Integer, PVector> effectLocations)
    {
        this.effectLocations = effectLocations;
    }

}
