package be.generativelasers.util;

import static be.generativelasers.util.Utilities.map;

public class Smoother
{
    private double initialValue;
    private double newValue;
    private double value;
    private double duration;
    private long previousTime;

    public double getValue()
    {
        update();
        return value;
    }

    public Smoother setNewValue(double newValue)
    {
        initialValue = value;
        this.newValue = newValue;
        return this;
    }

    public Smoother setInitialValue(double initialValue)
    {
        this.initialValue = initialValue;
        return this;
    }

    public Smoother setDuration(double duration)
    {
        this.duration = duration;
        return this;
    }

    private void update()
    {
        if (duration == 0)
        {
            throw new UnsupportedOperationException("duration not set");
        }
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - previousTime;
        previousTime = currentTime;
        if (difference >= duration) value = newValue;
        else value = map(difference, 0, duration, initialValue, newValue);
    }


}
