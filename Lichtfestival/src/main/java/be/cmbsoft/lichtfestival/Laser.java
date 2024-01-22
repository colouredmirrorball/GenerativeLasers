package be.cmbsoft.lichtfestival;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.OutputOption;
import processing.core.PApplet;
import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.UP;
import processing.core.PVector;

public class Laser
{
    final         LaserOutput  output;
    private final IldaRenderer renderer;
    private final List<Effect> activeEffects = new ArrayList<>();


    public Laser(PApplet parent, String mac)
    {
        this.output = new EtherdreamOutput().setAlias(mac);
        renderer = new IldaRenderer(parent, parent.width / 2, parent.height);
        renderer.setEllipseDetail(2);
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
        synchronized (activeEffects) {
            activeEffects.removeIf(Effect::isExpired);
            for (Effect effect: activeEffects) {
                effect.generate(renderer, parent);
            }
        }
    }

    public void deactivate(Effect effect)
    {
        synchronized (activeEffects) {
            if (effect != null) {
                if (effect.getType() == Effect.Type.FLASH) {
                    Class<? extends Effect> effectClass = effect.getClass();
                    activeEffects.removeIf(ef -> ef.getClass().equals(effectClass));
                }
            }
        }
    }

    public void removeActiveEffects()
    {
        synchronized (activeEffects) {
            activeEffects.clear();
        }
    }

    public List<String> getActiveEffectsNames()
    {
        return activeEffects.stream().map(Effect::getAlias).toList();
    }

    public void modifyEffect(Noot noot, int keyCode)
    {
        activeEffects.stream()
            .filter(effect -> effect instanceof HighlightEffect)
            .map(effect -> (HighlightEffect) effect)
            .filter(effect -> noot.equals(effect.getNoot()))
            .findFirst()
            .ifPresent(effect -> {
                HighlightEffect.HighlightEffectInfo info     = effect.getInfo();
                PVector                             position = info.getPosition();
                switch (keyCode) {
                    case UP -> position.y--;
                    case DOWN -> position.y++;
                    case LEFT -> position.x--;
                    case RIGHT -> position.x++;
                    case 16 -> info.setRadius(info.getRadius() + 1); // page up
                    case 11 -> info.setRadius(info.getRadius() - 1); // page down
                }
            });

    }
}
