package be.cmbsoft.livecontrol.fx;


import java.util.List;

import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.livecontrol.LiveControl;

public class StrobeEffect extends Effect
{
    private final Parameter frequency;
    boolean on = true;
    private long lastTime;

    public StrobeEffect(EffectConfigurator configurator)
    {
        frequency = configurator.newParameter("frequency");
    }

    @Override
    public List<IldaPoint> apply(List<IldaPoint> points)
    {
        return on ? points : List.of();
    }

    @Override
    public void update(ProgramState state)
    {
        float frequencyValue = this.frequency.getValue();
        if (frequencyValue == 0f)
        {
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
