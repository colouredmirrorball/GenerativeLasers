package be.generativelasers.output.etherdream;

public enum EtherdreamPlaybackState
{
    IDLE(0), PREPARED(1), PLAYING(2), INVALID(3);

    final byte state;

    EtherdreamPlaybackState(int state)
    {
        this.state = (byte) state;
    }

    public static EtherdreamPlaybackState get(int state)
    {
        if (state > 3) return INVALID;
        return EtherdreamPlaybackState.values()[state];
    }
}
