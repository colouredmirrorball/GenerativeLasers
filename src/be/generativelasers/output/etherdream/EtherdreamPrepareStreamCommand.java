package be.generativelasers.output.etherdream;

public class EtherdreamPrepareStreamCommand implements EtherdreamCommand
{
    @Override
    public byte[] getBytes()
    {
        return new byte[]{(byte) 'p'};
    }

    @Override
    public char getCommandChar()
    {
        return 'p';
    }
}
