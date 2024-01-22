package be.cmbsoft.lichtfestival;

import java.util.Map;

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

    Map<Integer, HighlightEffect.HighlightEffectInfo> effectLocations;

    public Map<Integer, HighlightEffect.HighlightEffectInfo> getEffectLocations()
    {
        return effectLocations;
    }

    public void setEffectLocations(Map<Integer, HighlightEffect.HighlightEffectInfo> effectLocations)
    {
        this.effectLocations = effectLocations;
    }

    String leftSafetyZoneImageLocation;
    String rightSafetyZoneImageLocation;

    public String getLeftSafetyZoneImageLocation()
    {
        return leftSafetyZoneImageLocation;
    }

    public void setLeftSafetyZoneImageLocation(String leftSafetyZoneImageLocation)
    {
        this.leftSafetyZoneImageLocation = leftSafetyZoneImageLocation;
    }

    public String getRightSafetyZoneImageLocation()
    {
        return rightSafetyZoneImageLocation;
    }

    public void setRightSafetyZoneImageLocation(String rightSafetyZoneImageLocation)
    {
        this.rightSafetyZoneImageLocation = rightSafetyZoneImageLocation;
    }

}
