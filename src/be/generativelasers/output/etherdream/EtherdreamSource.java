package be.generativelasers.output.etherdream;

public enum EtherdreamSource
{
    NETWORK_STREAMING(0), SD_CARD_PLAYBACK(1), ABSTRACT_GENERATOR(2), INVALID(3);

    final byte state;

    EtherdreamSource(int state)
    {
        this.state = (byte) state;
    }

    public static EtherdreamSource get(int state)
    {
        if (state > 3) return INVALID;
        return EtherdreamSource.values()[state];
    }
}
