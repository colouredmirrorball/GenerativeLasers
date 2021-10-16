package be.generativelasers.procedures;

import be.generativelasers.MidiNote;
import ilda.IldaRenderer;

import java.util.List;

public abstract class MidiEffect
{
    public abstract void render(List<MidiNote> activeNotes, IldaRenderer renderer);
}
