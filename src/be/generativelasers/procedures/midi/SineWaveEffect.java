package be.generativelasers.procedures.midi;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.cmbsoft.ilda.IldaRenderer;
import be.generativelasers.MidiNote;
import be.generativelasers.procedures.MidiEffect;
import be.generativelasers.util.Smoother;
import static processing.core.PApplet.map;
import processing.core.PConstants;
import static processing.core.PConstants.EPSILON;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

public class SineWaveEffect extends MidiEffect
{

    private final Map<MidiNote, NoteState> noteStateMap = new HashMap<>();
    private float[] xValues;
    private float phase;

    @Override
    public void render(List<MidiNote> activeNotes, IldaRenderer renderer)
    {
        if (xValues == null)
        {
            buildXValues(renderer.width);
        }
        int noteCount = 0;
        float halfHeight = renderer.height * 0.5f;
        float quarterHeight = renderer.height * 0.25f;

        //Disable all released notes
        for (Map.Entry<MidiNote, NoteState> entry : noteStateMap.entrySet())
        {
            if (!activeNotes.contains(entry.getKey()))
            {
                NoteState noteState = entry.getValue();
                noteState.active = false;
                noteState.frequencySmoother.setNewValue(0);
                noteState.intensitySmoother.setNewValue(0);
            }
        }

        // Ensure note is being processed
        for (MidiNote note : activeNotes)
        {
            NoteState noteState = noteStateMap.get(note);
            if (noteState == null)
            {
                noteState = new NoteState(note);
                noteState.frequencySmoother.setDuration(1000).setInitialValue(0).setNewValue(TWO_PI * note.getPitch() / 48f);
                noteState.intensitySmoother.setDuration(1000).setInitialValue(0).setNewValue(255);
                Map.Entry<MidiNote, NoteState> inactiveEntry = noteStateMap.entrySet().stream().filter(entry -> !entry.getValue().active).findFirst().orElse(null);
                if (inactiveEntry == null || activeNotesSize() < 3)
                {
                    if (inactiveEntry != null)
                    {
                        noteStateMap.remove(inactiveEntry.getKey());
                    }
                    noteStateMap.put(note, noteState);
                }

            }
            noteState.active = true;
        }
        for (NoteState noteState : noteStateMap.values())
        {
            if (noteState.note.getPitch() >= 24 && noteState.note.getPitch() <= 48 && noteCount < 3)
            {
                float amplitude = noteState.note.getVelocity();
                float frequency = (float) noteState.frequencySmoother.getValue();
                float offset = switch (noteCount)
                        {
                            case 0 -> halfHeight;
                            case 1 -> halfHeight + quarterHeight;
                            case 2 -> halfHeight - quarterHeight;
                            default -> throw new IllegalStateException("Unexpected value: " + noteCount);
                        };
                renderer.colorMode(PConstants.HSB);
                float hue = map(noteState.note.getPitch(), 24, 48, 0, 255);
                float intensity = (float) noteState.intensitySmoother.getValue();
                if (intensity < EPSILON) continue;

                renderer.beginShape(PConstants.LINES);
                for (float xValue : xValues)
                {
                    renderer.stroke(hue, 255 - amplitude * 2,
                            intensity * (float) Math.sin(PI * frequency * xValue / xValues.length + phase) * 0.5f);
                    renderer.vertex(xValue,
                            offset + (float) Math.sin(frequency * xValue / xValues.length) * amplitude);
                }
                renderer.endShape();
                phase += 0.01f;
            }
            noteCount++;
        }
    }

    private int activeNotesSize()
    {
        Collection<NoteState> states = noteStateMap.values();
        int count = 0;
        for (NoteState state : states)
        {
            if (state.active)
            {
                count++;
            }
        }
        return count;
    }

    private void buildXValues(int width)
    {
        xValues = new float[200];
        float distance = width / 200f;
        for (int i = 0; i < xValues.length; i++)
        {
            xValues[i] = i * distance;
        }
    }

    private static class NoteState
    {
        private final MidiNote note;
        Smoother frequencySmoother = new Smoother();
        Smoother intensitySmoother = new Smoother();
        boolean active = false;

        private NoteState(MidiNote note)
        {
            this.note = note;
        }
    }
}
