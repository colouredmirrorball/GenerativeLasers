package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.OutputOption;
import processing.core.PApplet;

public class Laser
{
    final LaserOutput output;
    private final IldaRenderer renderer;

    public Laser(PApplet parent, String mac)
    {
        this.output = new EtherdreamOutput().setAlias(mac);
        renderer = new IldaRenderer(parent, parent.width / 2, parent.height);
        renderer.setEllipseDetail(7);
    }

    public IldaRenderer getRenderer()
    {
        return renderer;
    }

    public void output()
    {
        output.project(renderer);
    }

    public Laser option(OutputOption option)
    {
        output.option(option);
        return this;
    }

}
