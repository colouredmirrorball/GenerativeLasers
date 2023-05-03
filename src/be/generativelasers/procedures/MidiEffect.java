package be.generativelasers.procedures;

import java.util.List;

import be.cmbsoft.ilda.IldaRenderer;
import be.generativelasers.MidiNote;

public abstract class MidiEffect {
    public abstract void render(List<MidiNote> activeNotes, IldaRenderer renderer);
}
