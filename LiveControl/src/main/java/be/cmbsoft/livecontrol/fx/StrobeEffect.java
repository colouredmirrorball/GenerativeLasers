package be.cmbsoft.livecontrol.fx;


import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.livecontrol.LiveControl;

import static be.cmbsoft.livecontrol.sources.EmptySource.EMPTY_FRAME;

public class StrobeEffect extends Effect
{
    private final Parameter<Float> frequency;
    boolean on = true;
    private long lastTime;

    public StrobeEffect(EffectConfigurator configurator)
    {
        frequency = configurator.newParameter("frequency", Float.class);
    }

    @Override
    public IldaFrame apply(IldaFrame ildaFrame)
    {
        return on ? ildaFrame : EMPTY_FRAME;
    }

    @Override
    public void update(ProgramState state)
    {
        Float frequencyValue = this.frequency.getValue();
        if (frequencyValue == null || frequencyValue.equals(0f)) {
            on = true;
        } else {
            long millis   = state.millis();
            long interval = (long) (1000 / frequencyValue);
            if (lastTime - millis > interval) {
                on       = !on;
                lastTime = millis;
            }
        }

    }

    @Override
    public void display(LiveControl parent, int x, int y, int w, int h)
    {
        if (on)
        {
            parent.fill(255);
        }
        else
        {
            parent.fill(0);
        }
        parent.rect(x, y, w, h);
    }

}
