package be.generativelasers;

import be.generativelasers.output.LaserOutput;
import be.generativelasers.output.LsxOscOutput;
import be.generativelasers.procedures.CircleAtMidiNote;
import be.generativelasers.procedures.Procedure;
import be.generativelasers.ui.UIBuilder;
import cmb.soft.cgui.CGui;
import cmb.soft.cgui.CWindow;
import netP5.NetAddress;
import themidibus.MidiBus;
import themidibus.SimpleMidiListener;
import themidibus.StandardMidiListener;

import javax.sound.midi.MidiMessage;

import static processing.core.PApplet.println;

/**
 * @author Florian Created on 26/01/2020
 */
public class GenerativeLasers
{

    private final Procedure currentProcedure;
    private final LaserOutput currentOutput;
    private MidiBus midiBus;

    public static final String VERSION = "0.0.1 Alpha";

    private GenerativeLasers()
    {
        CGui gui = CGui.getInstance();
        gui.setTitle("Generative Lasers");
        gui.launch();
        UIBuilder.buildUI(gui);
        CWindow window = gui.getDefaultWindow();
        currentProcedure = new CircleAtMidiNote(window);
        currentOutput = new LsxOscOutput(window, 0, 10, new NetAddress("127.0.0.1", 10000));
        currentOutput.setProcedure(currentProcedure);
        midiBus = new MidiBus(window);
        println(MidiBus.availableInputs());
        midiBus.addInput("USB-MIDI");
        midiBus.addMidiListener((StandardMidiListener) (message, timeStamp) -> {
            processMidiMessage(message);
        });
        midiBus.addMidiListener(new SimpleMidiListener()
        {
            @Override
            public void noteOn(int channel, int pitch, int velocity)
            {
                processNoteOn(channel, pitch, velocity);
            }

            @Override
            public void noteOff(int channel, int pitch, int velocity)
            {
                processNoteOff(channel, pitch, velocity);
            }

            @Override
            public void controllerChange(int channel, int number, int value)
            {
                processControllerChange(channel, number, value);
            }
        });
        run();
    }

    public static void main(String[] args)
    {
        new GenerativeLasers();
    }

    private void processControllerChange(int channel, int number, int value)
    {
        currentProcedure.controllerChange(channel, number, value);
    }

    private void processNoteOff(int channel, int pitch, int velocity)
    {
        currentProcedure.noteOff(channel, pitch, velocity);
    }

    private void processNoteOn(int channel, int pitch, int velocity)
    {
        currentProcedure.noteOn(channel, pitch, velocity);
    }

    private void processMidiMessage(MidiMessage message)
    {
        currentProcedure.acceptMidi(message);
    }

    private void run()
    {
        ProcedureThread procedureThread = new ProcedureThread();
        procedureThread.addProcedure(currentProcedure);
        procedureThread.start();
        OutputThread outputThread = new OutputThread();
        outputThread.addOutput(currentOutput);
        outputThread.start();
        CGui.log("Generative Lasers version " + VERSION + " booted");
    }

}
