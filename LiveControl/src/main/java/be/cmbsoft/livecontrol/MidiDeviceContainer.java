package be.cmbsoft.livecontrol;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Optional;
import java.util.function.Supplier;

import static be.cmbsoft.livecontrol.LiveControl.error;
import static be.cmbsoft.livecontrol.LiveControl.log;

public class MidiDeviceContainer
{

    private MidiDevice matrixInputDevice;
    private MidiDevice matrixOutputDevice;
    private MidiDevice controlDevice;

    public void setupMidi(Settings settings)
    {
        MidiDevice.Info[] midiDeviceInfo               = MidiSystem.getMidiDeviceInfo();
        String            matrixInputDeviceIdentifier  = settings.getMidiMatrixInputDevice();
        String            matrixOutputDeviceIdentifier = settings.getMidiMatrixOutputDevice();
        String            controlDeviceIdentifier      = settings.getMidiControlDevice();
//        String            theMIDIDevice                = "MIDIIN2 (Launchpad Pro)";
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
        controlDevice = addInput(selectedControlDevice, controlDeviceIdentifier, MidiReceiver::new);
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

                Receiver receiver = device.getReceiver();
                device.open();
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

    private MidiDevice addInput(MidiDevice.Info info, String identifier, Supplier<Receiver> receiverProvider)
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
                transmitter.setReceiver(receiverProvider.get());

                device.open();
            }
            else
            {
                log(identifier + " is not available...");
            }

        }
        catch (Exception exception)
        {
            // Continue without MIDI
            exception.printStackTrace();
        }
        return device;
    }

    public void close()
    {
        Optional.ofNullable(matrixInputDevice).ifPresent(MidiDevice::close);
        Optional.ofNullable(matrixOutputDevice).ifPresent(MidiDevice::close);
        Optional.ofNullable(controlDevice).ifPresent(MidiDevice::close);
    }

}
