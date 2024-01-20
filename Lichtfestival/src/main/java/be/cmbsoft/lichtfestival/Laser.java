package be.cmbsoft.lichtfestival;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.OutputOption;
import processing.core.PApplet;

public class Laser
{
    final         LaserOutput  output;
    private final IldaRenderer renderer;
    private final List<Effect> activeEffects = new ArrayList<>();


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

    public void trigger(Effect effect, Lichtfestival parent)
    {
        activeEffects.add(effect);
        effect.initialize(parent);
    }

    public void processEffects(Lichtfestival parent)
    {
        activeEffects.removeIf(effect -> effect.isExpired());
        for (Effect effect : activeEffects)
        {

            effect.generate(renderer, parent);
        }
    }

    public void deactivate(Effect effect)
    {
        if (effect != null)
        {
            if (effect.getType() == Effect.Type.FLASH)
            {
                Class<? extends Effect> effectClass = effect.getClass();
                activeEffects.removeIf(ef -> ef.getClass().equals(effectClass));
            }
        }
    }

    public void removeActiveEffects()
    {
        activeEffects.clear();
    }

}
