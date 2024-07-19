package be.cmbsoft.livecontrol.midi;

import java.util.Optional;
import java.util.function.Supplier;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import static be.cmbsoft.livecontrol.LiveControl.error;
import static be.cmbsoft.livecontrol.LiveControl.log;
import be.cmbsoft.livecontrol.MatrixReceiver;
import be.cmbsoft.livecontrol.settings.Settings;

public class MidiDeviceContainer
{

    private MidiDevice matrixInputDevice;
    private MidiDevice matrixOutputDevice;
    private MidiDevice controlDevice;
    private Receiver matrixReceiver;

    public void setupMidi(Settings settings, Supplier<MidiReceiver.NoteListener> controlListenerProvider)
    {
        MidiDevice.Info[] midiDeviceInfo               = MidiSystem.getMidiDeviceInfo();
        String            matrixInputDeviceIdentifier  = settings.getMidiMatrixInputDevice();
        String            matrixOutputDeviceIdentifier = settings.getMidiMatrixOutputDevice();
        String            controlDeviceIdentifier      = settings.getMidiControlDevice();
        MidiDevice.Info selectedMatrixInputDevice  = null;
        MidiDevice.Info selectedMatrixOutputDevice = null;
        MidiDevice.Info selectedControlDevice      = null;
        log("Looking for MIDI devices...");
        for (MidiDevice.Info info : midiDeviceInfo)
        {
            log("[" + info.getName() + "] " + info + ": " + info.getDescription() + " (" + info.getVendor() + " "
                + info.getVersion() + ")");
            if (matrixInputDeviceIdentifier != null && matrixInputDeviceIdentifier.equals(info.getName()))
            {
                selectedMatrixInputDevice = info;
            }
            if (matrixOutputDeviceIdentifier != null && matrixOutputDeviceIdentifier.equals(info.getName()))
            {
                selectedMatrixOutputDevice = info;
            }
            if (controlDeviceIdentifier != null && controlDeviceIdentifier.equals(info.getName()))
            {
                selectedControlDevice = info;
            }
        }
        matrixInputDevice = addInput(selectedMatrixInputDevice, matrixInputDeviceIdentifier, MatrixReceiver::new);
        controlDevice = addInput(selectedControlDevice, controlDeviceIdentifier,
            () -> new MidiReceiver().addNoteListener(controlListenerProvider.get()));
        matrixOutputDevice = addOutput(selectedMatrixOutputDevice, matrixOutputDeviceIdentifier);


    }

    private MidiDevice addOutput(MidiDevice.Info info, String identifier)
    {
        MidiDevice device = null;
        if (info != null)
        {
            try
            {
                device = MidiSystem.getMidiDevice(info);
                if (device.getMaxReceivers() == 0)
                {
                    log(identifier + " is not an output...");
                }
                matrixReceiver = device.getReceiver();
//                Receiver receiver = device.getReceiver();
                device.open();
                log("Opened MIDI output " + identifier);
            }
            catch (MidiUnavailableException e)
            {
                error(e);
            }
        }
        else
        {
            log(identifier + " is not available...");
        }
        return device;
    }

    private MidiDevice addInput(MidiDevice.Info info, String identifier, Supplier<MidiReceiver> receiverProvider)
    {
        MidiDevice device = null;
        try
        {
            if (info != null)
            {
                device = MidiSystem.getMidiDevice(info);
                if (device.getMaxTransmitters() == 0)
                {
                    log(identifier + " is not an input...");
                }

                Transmitter transmitter = device.getTransmitter();
                MidiReceiver receiver = receiverProvider.get();
//                receiver.addNoteListener();
                transmitter.setReceiver(receiver);

                device.open();
                log("Opened MIDI input " + identifier);
            }
            else
            {
                log(identifier + " is not available...");
            }

        }
        catch (Exception exception)
        {
            // Continue without MIDI
            error("Could not open device " + identifier, exception);
        }
        return device;
    }

    public void close()
    {
        Optional.ofNullable(matrixInputDevice).ifPresent(MidiDevice::close);
        Optional.ofNullable(matrixOutputDevice).ifPresent(MidiDevice::close);
        Optional.ofNullable(controlDevice).ifPresent(MidiDevice::close);
    }

    public void output(int x, int y, boolean on)
    {
        MidiMessage message = new ColorMessage(x, y, on);
        Optional.ofNullable(matrixReceiver).ifPresent(receiver -> receiver.send(message, -1));
    }

    private static class ColorMessage extends MidiMessage
    {
        public ColorMessage(int x, int y, boolean on)
        {
            super(transform(x, y, on));
        }

        private static byte[] transform(int x, int y, boolean on)
        {
            return new byte[]{(byte) (240 & 0xff), 0, 32 & 0xff, 41 & 0xff, 2 & 0xff, 16 & 0xff, 11 & 0xff,
                (byte) (getLed(x, y) & 0xff),
                127 & 0xff, 60 & 0xff, 0,
                (byte) (247 & 0xff)};
        }

        private static byte getLed(int x, int y)
        {
            return (byte) (x + 10 * y);
        }

        private static byte getColor(boolean on)
        {
            return (byte) (on ? 0x3 : 0);
        }

        @Override
        public Object clone()
        {
            // ??
            return this;
        }

    }

}
