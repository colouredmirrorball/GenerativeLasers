package be.cmbsoft.livecontrol.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;
import java.util.Optional;
import java.util.function.Supplier;

import be.cmbsoft.livecontrol.settings.Settings;

import static be.cmbsoft.livecontrol.LiveControl.error;
import static be.cmbsoft.livecontrol.LiveControl.log;

public class MidiDeviceContainer
{

    private MidiDevice matrixInputDevice;
    private MidiDevice matrixOutputDevice;
    private MidiDevice controlDevice;
    private Receiver matrixReceiver;

    private static byte[] transform(int x, int y, boolean on)
    {
//        return new byte[]{(byte) (240 & 0xff), 0, 32 & 0xff, 41 & 0xff, 2 & 0xff, 16 & 0xff, 11 & 0xff,
//            (byte) (getLed(x, y) & 0xff),
//            127 & 0xff, 60 & 0xff, 0,
//            (byte) (247 & 0xff)};
        return new byte[]{(byte) (0xf0 & 0xff), 0, 0x20 & 0xff, 0x29 & 0xff, 0x02 & 0xff, 0x10 & 0xff, 0x8 & 0xff,
            (byte) (getLed(x, y) & 0xff),
            127 & 0xff, 60 & 0xff, 0,
            (byte) (247 & 0xff)};
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

    private static byte getLed(int x, int y)
    {
        return (byte) (x + 10 * y);
    }

    public void setupMidi(Settings settings, Supplier<MidiReceiver.NoteListener> controlListenerProvider,
        Supplier<MidiReceiver.NoteListener> matrixListenerProvider)
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
        matrixInputDevice = addInput(selectedMatrixInputDevice, matrixInputDeviceIdentifier,
            () -> new MidiReceiver().addNoteListener(matrixListenerProvider.get()));
        controlDevice = addInput(selectedControlDevice, controlDeviceIdentifier,
            () -> new MidiReceiver().addNoteListener(controlListenerProvider.get()));
        matrixOutputDevice = addOutput(selectedMatrixOutputDevice, matrixOutputDeviceIdentifier);


    }

    public void output(int x, int y, boolean on)
    {
//        MidiMessage message = new ColorMessage(x, y, on);
        SysexMessage message   = new SysexMessage();
        byte[]       transform = transform(x, y, on);
        try
        {
            message.setMessage(transform, transform.length);
        }
        catch (InvalidMidiDataException e)
        {
            throw new RuntimeException(e);
        }
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
