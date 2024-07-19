package be.cmbsoft.livecontrol;

import be.cmbsoft.livecontrol.midi.MidiReceiver;

public class MatrixReceiver extends MidiReceiver
{
    @Override
    protected void noteOn(int channel, int pitch, int velocity)
    {
        super.noteOn(channel, pitch, velocity);
    }

}
