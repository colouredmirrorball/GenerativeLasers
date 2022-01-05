package be.generativelasers.output.etherdream;

public class EtherdreamStopCommand implements EtherdreamCommand
{
    @Override
    public byte[] getBytes()
    {
        return new byte[]{(byte) 's'};
    }

    @Override
    public char getCommandChar()
    {
        return 's';
    }
}
