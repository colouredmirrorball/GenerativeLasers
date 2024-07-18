package be.cmbsoft.livecontrol;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static be.cmbsoft.livecontrol.LiveControl.log;

public class MidiReceiver implements Receiver
{


//    private final MidiDevice device;

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

    }

    protected void noteOff(int channel, int pitch, int velocity)
    {

    }

    protected void noteOn(int channel, int pitch, int velocity)
    {

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

}
