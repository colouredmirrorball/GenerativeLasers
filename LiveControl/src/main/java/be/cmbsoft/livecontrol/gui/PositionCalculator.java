package be.cmbsoft.livecontrol.gui;

import processing.core.PVector;

/**
 * A way to calculate relative positions that can constantly be updated, to allow easy resizing and dynamic position
 * calculation
 * Created by Florian on 10/11/2017.
 */
public abstract class PositionCalculator
{
    public PVector position = new PVector();

    public abstract PVector updatePosition(GUIContainer parent, int width, int height);

}
