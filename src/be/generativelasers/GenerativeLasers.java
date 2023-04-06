package be.generativelasers;

import javax.sound.midi.MidiMessage;

import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.generativelasers.procedures.Procedure;
import be.generativelasers.procedures.test.SimpleCircle;
import be.generativelasers.ui.UIBuilder;
import cmb.soft.cgui.CGui;
import cmb.soft.cgui.CWindow;
import themidibus.MidiBus;
import themidibus.SimpleMidiListener;
import themidibus.StandardMidiListener;

import static processing.core.PApplet.println;

/**
 * @author Florian Created on 26/01/2020
 */
public class GenerativeLasers
{

    public static final String VERSION = "0.0.1 Alpha";
    private final Procedure currentProcedure;
    private final LaserOutput currentOutput;
    private OutputAggregator outputAggregator;

    private GenerativeLasers()
    {
        CGui gui = CGui.getInstance();
        gui.setTitle("Generative Lasers");
        gui.addExitListener(this::stop);
        gui.launch();
        UIBuilder.buildUI(gui);
        CWindow window = gui.getDefaultWindow();
        currentProcedure = new SimpleCircle(window);
//        currentOutput = new LsxOscOutput(window, 0, 9, new NetAddress("127.0.0.1", 10000));
        currentOutput = new EtherdreamOutput();
        currentProcedure.setOutput(currentOutput);
        MidiBus midiBus = new MidiBus(window, "bus1");
        println((Object) MidiBus.availableInputs());
        midiBus.addInput("USB-MIDI");
        midiBus.addMidiListener((StandardMidiListener) (message, timeStamp) -> processMidiMessage(message));
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
        outputAggregator = new OutputAggregator();
        outputAggregator.addOutput(currentOutput);
        outputAggregator.start();
        CGui.log("Generative Lasers version " + VERSION + " booted");
    }

    public void stop()
    {
        outputAggregator.stop();
    }

}
