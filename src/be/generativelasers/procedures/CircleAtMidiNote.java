package be.generativelasers.procedures;

import be.generativelasers.MidiNote;
import processing.core.PApplet;
import processing.core.PConstants;

import javax.sound.midi.MidiMessage;
import java.util.List;

import static processing.core.PApplet.map;

public class CircleAtMidiNote extends Procedure
{

    private int hue = 0;
    private int hue2 = 127;
    private int counter = 0;

    public CircleAtMidiNote(PApplet applet)
    {
        super(applet);
    }

    @Override
    public void update()
    {
        List<MidiNote> activeNotes = getActiveNotes();
        renderer.beginDraw();
        renderer.background();
        renderer.colorMode(PConstants.HSB);

        boolean even = true;
        for (MidiNote note : activeNotes)
        {
            renderer.stroke(even ? hue : hue2, 255, 255);
            renderer.ellipse(map(note.getPitch(), 0, 127, 0, renderer.width), renderer.height * 0.5f, note.getVelocity(),
                    note.getVelocity());
            even = !even;
        }
        counter++;
        if (counter % 10 == 0)
        {
            hue = (hue + 1) % 255;
            hue2 = (hue2 + 1) % 255;
        }
        renderer.endDraw();
        frame = renderer.getCurrentFrame();
    }

    @Override
    public void trigger(float value)
    {

    }

    @Override
    public void acceptMidi(MidiMessage message)
    {

    }


}
