package be.generativelasers.procedures;


import be.generativelasers.MidiNote;
import ilda.IldaFrame;
import ilda.IldaPoint;
import ilda.IldaRenderer;
import processing.core.PApplet;

import javax.sound.midi.MidiMessage;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Florian
 * Created on 27/01/2020
 */
public abstract class Procedure
{
    protected IldaFrame frame = new IldaFrame();
    protected final IldaRenderer renderer;
    protected final PApplet parent;
    private final CopyOnWriteArrayList<MidiNote> activeNotes = new CopyOnWriteArrayList<>();

    protected Procedure(PApplet applet)
    {
        this.parent = applet;
        renderer = new IldaRenderer(applet);
        renderer.setOverwrite(true);
    }

    public abstract void update();

    public abstract void trigger(float value);

    public void acceptMidi(MidiMessage message)
    {
        // don't do a thing! ignore! except if you're a subclass, maybe?
    }

    public void controllerChange(int channel, int number, int value)
    {

    }

    public void noteOff(int channel, int pitch, int velocity)
    {
        activeNotes.removeIf(note -> note.getChannel() == channel && note.getPitch() == pitch);
    }

    public void noteOn(int channel, int pitch, int velocity)
    {
        activeNotes.add(new MidiNote(channel, pitch, velocity));
    }

    public void midiPanic()
    {
        activeNotes.clear();
    }

    protected List<MidiNote> getActiveNotes()
    {
        return activeNotes;
    }

    public List<IldaPoint> getPoints()
    {
        if (frame == null) return Collections.emptyList();
        return frame.getCopyOnWritePoints();
    }

}
