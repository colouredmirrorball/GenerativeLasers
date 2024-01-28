package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;

public class ResonanceEffect extends Effect
{

    public ResonanceEffect()
    {
        setType(Type.FLASH);
    }

    @Override

    public void initialize(Lichtfestival parent)
    {

    }

    @Override
    public void generate(IldaRenderer renderer, Lichtfestival parent, float offset, Laser laser)
    {
        setAlias("Resonance effect at " + laser.getCenterX() + " " + laser.getCenterY());
        if (offset < 10) {
            return;
        }
        renderer.stroke(laser.getEditRed(), laser.getEditGreen(), laser.getEditBlue());
        renderer.ellipse(laser.getCenterX(), laser.getCenterY(), offset, offset);
    }
}
