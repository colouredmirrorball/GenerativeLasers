package be.generativelasers.procedures;

import be.generativelasers.MidiNote;
import be.generativelasers.procedures.midi.SineWaveEffect;
import cmb.soft.cgui.CWindow;

import java.util.ArrayList;
import java.util.List;

public class MidiEffects extends Procedure
{
    private final List<MidiEffect> effects = new ArrayList<>();

    public MidiEffects(CWindow window)
    {
        super(window);
        effects.add(new SineWaveEffect());
    }

    @Override
    public void updateRender()
    {
        List<MidiNote> activeNotes = getActiveNotes();
        renderer.background();
        effects.forEach(effect -> effect.render(activeNotes, renderer));
    }

    @Override
    public void trigger(float value)
    {

    }
}
