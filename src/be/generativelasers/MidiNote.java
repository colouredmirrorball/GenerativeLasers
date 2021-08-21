package be.generativelasers;

public class MidiNote
{
    private int channel;
    private int pitch;
    private int velocity;

    public MidiNote(int channel, int pitch, int velocity)
    {
        this.channel = channel;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    public int getChannel()
    {
        return channel;
    }

    public MidiNote setChannel(int channel)
    {
        this.channel = channel;
        return this;
    }

    public int getPitch()
    {
        return pitch;
    }

    public MidiNote setPitch(int pitch)
    {
        this.pitch = pitch;
        return this;
    }

    public int getVelocity()
    {
        return velocity;
    }

    public MidiNote setVelocity(int velocity)
    {
        this.velocity = velocity;
        return this;
    }
}
