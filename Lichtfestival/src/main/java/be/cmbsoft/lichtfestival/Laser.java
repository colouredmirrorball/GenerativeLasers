package be.cmbsoft.lichtfestival;

import java.util.ArrayList;
import java.util.List;

import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.OutputOption;
import processing.core.PApplet;
import static processing.core.PConstants.CODED;
import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.UP;
import processing.core.PVector;
import processing.event.KeyEvent;

public class Laser
{
    final         LaserOutput  output;
    private final IldaRenderer renderer;
    private final List<Effect> activeEffects = new ArrayList<>();
    private       boolean      writeOutActive;
    private       float        writeOutSpeed;
    private       float        writeOutPosition;
    private       float        offset;
    private       float        editBlue;
    private       float        editGreen;
    private       float        editRed;


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
                effect.generate(renderer, parent, offset);
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

    public void modifyEffect(Noot noot, KeyEvent event, Lichtfestival parent)
    {
        activeEffects.stream().filter(HighlightEffect.class::isInstance).map(HighlightEffect.class::cast)
            .filter(effect -> noot.equals(effect.getNoot()))
            .findFirst()
            .ifPresent(effect -> {
                HighlightEffect.HighlightEffectInfo info     = effect.getInfo();
                PVector                             position = info.getPosition();
                int                                 keyCode  = event.getKeyCode();
                if (event.getKey() == CODED) {
                    if (event.isShiftDown()) {
                        switch (keyCode) {
                            case UP -> info.setHeight(info.getHeight() + 1);
                            case DOWN -> info.setHeight(info.getHeight() - 1);
                            case LEFT -> info.setWidth(info.getWidth() - 1);
                            case RIGHT -> info.setWidth(info.getWidth() + 1);
                        }
                    } else {
                        switch (keyCode) {
                            case UP -> position.y--;
                            case DOWN -> position.y++;
                            case LEFT -> position.x--;
                            case RIGHT -> position.x++;
                            case 16 -> { // page up
                                info.setWidth(info.getWidth() + 1);
                                info.setHeight(info.getHeight() + 1);
                            }
                            case 11 -> { // page down
                                info.setWidth(info.getWidth() - 1);
                                info.setHeight(info.getHeight() - 1);
                            }
                        }
                    }
                    if (event.getKey() == 'c') {
                        int newColor = parent.color(editRed, editGreen, editBlue);
                        info.setColor(newColor);
                    }
                }
            });

    }

    public void setWriteoutEffect(int value)
    {
        this.writeOutActive = value > 64;
        this.writeOutSpeed  = (value - 64) / 64f;
    }

    public void postProcess(List<IldaPoint> points)
    {
        if (writeOutActive) {
            int size = points.size();
            for (int i = 0; i < points.size(); i++) {
                IldaPoint point       = points.get(i);
                float     progression = (float) i / size;
                if (progression > writeOutPosition) {
                    point.setBlanked(true);
                }
            }
            writeOutPosition += writeOutSpeed;
            if (writeOutPosition > 1) {
                writeOutPosition = 0;
            }
        }
    }

    public void setOffset(float value)
    {
        this.offset = value;
    }

    public void setEditRed(float red)
    {
        this.editRed = red;
    }

    public void setEditGreen(float green)
    {
        this.editGreen = green;
    }

    public void setEditBlue(float blue)
    {
        this.editBlue = blue;
    }
}
