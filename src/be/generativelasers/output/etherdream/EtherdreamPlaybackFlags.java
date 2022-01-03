package be.generativelasers.output.etherdream;

import static be.generativelasers.output.etherdream.Etherdream.isFlag;

public class EtherdreamPlaybackFlags
{
    private final boolean shutterState;
    private final boolean underFlow;
    private final boolean eStop;

    public EtherdreamPlaybackFlags(short flags)
    {
        shutterState = isFlag(flags, 0);
        underFlow = isFlag(flags, 1);
        eStop = isFlag(flags, 2);
    }

    public boolean isShutterState()
    {
        return shutterState;
    }

    public boolean isUnderFlow()
    {
        return underFlow;
    }

    public boolean iseStop()
    {
        return eStop;
    }
}
