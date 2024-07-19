package be.cmbsoft.livecontrol.midi;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static be.cmbsoft.livecontrol.LiveControl.log;

public class MidiReceiver implements Receiver
{
    private final List<NoteListener> listeners = new ArrayList<>();

    public MidiReceiver()
    {
    }

    @Override
    public void send(MidiMessage message, long timeStamp)
    {
//        if (!booted) return;
        if (message instanceof ShortMessage shortMessage)
        {
            int command = shortMessage.getCommand();
            int channel = shortMessage.getChannel();
            int data1   = shortMessage.getData1();
            int data2   = shortMessage.getData2();

//            Noot noot = new Noot(channel, data1);

            // Note On event
            if (command == ShortMessage.NOTE_ON)
            {
                logMidi("Note On - Channel: " + channel + ", Note: " + data1 + ", Velocity: " + data2);
                noteOn(channel, data1, data2);
//                activateEffect(noot);
            }
            // Note Off event
            else if (command == ShortMessage.NOTE_OFF)
            {
                logMidi("Note Off - Channel: " + channel + ", Note: " + data1 + ", Velocity: " + data2);
                noteOff(channel, data1, data2);
//                deactivateEffect(noot);
            }
            // Control Change event
            else if (command == ShortMessage.CONTROL_CHANGE)
            {
                logMidi("Control Change - Channel: " + channel + ", Controller: " + data1 + ", Value: " + data2);
                controlChange(channel, data1, data2);
//                processControl(noot, data2);
            }
        }
    }

    protected void controlChange(int channel, int controller, int value)
    {
        listeners.forEach(rec -> rec.controlChange(channel, controller, value));
    }

    protected void noteOff(int channel, int pitch, int velocity)
    {
        listeners.forEach(rec -> rec.noteOff(channel, pitch, velocity));
    }

    protected void noteOn(int channel, int pitch, int velocity)
    {
        if (velocity == 0)
        {
            listeners.forEach(rec -> rec.noteOff(channel, pitch, velocity));
        }
        else
        {
            listeners.forEach(rec -> rec.noteOn(channel, pitch, velocity));
        }
    }

    private void logMidi(String message)
    {
        log(message);
    }

    @Override
    public void close()
    {
//        device.close();
    }

    public void addNoteListener(NoteListener listener)
    {
        listeners.add(listener);
    }

    public interface NoteListener
    {
        void noteOn(int channel, int pitch, int velocity);

        void noteOff(int channel, int pitch, int velocity);

        void controlChange(int channel, int pitch, int velocity);

    }

}
