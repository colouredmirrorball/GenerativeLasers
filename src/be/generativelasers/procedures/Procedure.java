package be.generativelasers.procedures;


import javax.sound.midi.MidiMessage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import be.cmbsoft.laseroutput.LaserOutput;
import be.generativelasers.MidiNote;
import ilda.IldaFrame;
import ilda.IldaPoint;
import ilda.IldaRenderer;
import ilda.OptimisationSettings;
import processing.core.PApplet;
import processing.core.PGraphics;


/**
 * @author Florian
 * Created on 27/01/2020
 */
public abstract class Procedure
{

    protected final IldaRenderer    renderer;
    protected final PApplet         parent;
    private final   List<MidiNote>  activeNotes = new CopyOnWriteArrayList<>();
    private final   PGraphics       renderedFrame;
    private final   List<IldaPoint> pointBuffer = new CopyOnWriteArrayList<>();
    private         LaserOutput     output;


    protected Procedure(PApplet applet)
    {
        this.parent = applet;
        renderer = new IldaRenderer(applet, applet.height, applet.height);
        renderer.setOverwrite(true);
        renderer.setOptimise(true);
        OptimisationSettings settings =
                new OptimisationSettings().setAngleDwell(false).setInterpolateBlanked(false).setInterpolateLit(false);
        renderer.setOptimisationSettings(settings);
        renderedFrame = applet.createGraphics(applet.height, applet.height);
    }

    public void update()
    {
        renderer.beginDraw();
        updateRender();
        renderer.endDraw();
        IldaFrame currentFrame = renderer.getCurrentFrame();
        if (currentFrame != null)
        {
            synchronized (pointBuffer)
            {
                pointBuffer.clear();
                pointBuffer.addAll(currentFrame.getCopyOnWritePoints());
            }
            Optional.ofNullable(output).ifPresent(l -> output.setCurrentFrame(currentFrame));
        }
    }

    public abstract void updateRender();

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
        synchronized (pointBuffer)
        {
            return pointBuffer;
        }

    }

    public void draw()
    {
        renderedFrame.beginDraw();
        Optional.ofNullable(renderer.getCurrentFrame()).ifPresent(ildaFrame -> ildaFrame.renderFrame(renderedFrame,
            true));
        renderedFrame.endDraw();
    }

    public void setOutput(LaserOutput currentOutput)
    {
        this.output = currentOutput;
    }

}
