package be.cmbsoft.livecontrol;

import java.util.Optional;

import be.cmbsoft.livecontrol.midi.MidiReceiver;

public class ControlHandler implements MidiReceiver.NoteListener
{
    private final LiveControl parent;

    public ControlHandler(LiveControl parent)
    {
        this.parent = parent;
    }

    @Override
    public void noteOn(int channel, int pitch, int velocity)
    {

    }

    @Override
    public void noteOff(int channel, int pitch, int velocity)
    {

    }

    @Override
    public void controlChange(int channel, int pitch, int velocity)
    {
        Optional.ofNullable(parent.getController(channel, pitch))
            .ifPresent(controller -> controller.setValue(velocity));
    }
}
