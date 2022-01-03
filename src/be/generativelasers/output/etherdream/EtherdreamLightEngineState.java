package be.generativelasers.output.etherdream;

public enum EtherdreamLightEngineState
{
    READY(0), WARMUP(1), COOLDOWN(2), EMERGENCY_STOP(3), INVALID(4);

    final byte state;

    EtherdreamLightEngineState(int state)
    {
        this.state = (byte) state;
    }

    public static EtherdreamLightEngineState get(int state)
    {
        if (state > 4) return INVALID;
        return EtherdreamLightEngineState.values()[state];
    }

}
