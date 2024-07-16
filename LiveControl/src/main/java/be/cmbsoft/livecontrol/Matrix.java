package be.cmbsoft.livecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.livecontrol.fx.Effect;
import be.cmbsoft.livecontrol.sources.EmptySourceWrapper;
import be.cmbsoft.livecontrol.sources.Source;
import processing.core.PGraphics;

import static processing.core.PConstants.P3D;

public class Matrix
{
    public static final int ROWS      = 8;
    public static final int MODIFIERS = 4;
    public static final int OUTPUTS   = 4;

    private final SourceWrapper[]                sources              = new SourceWrapper[ROWS];
    private final Function<Integer, LaserOutput> outputProvider;
    private final boolean[][]                    matrix               = new boolean[ROWS][MODIFIERS + OUTPUTS];
    private final List<Effect>                   modifiers            = new ArrayList<>();
    private final PGraphics[]                    effectVisualisations = new PGraphics[8];

    public Matrix(Function<Integer, SourceWrapper> sourceProvider, Function<Integer, LaserOutput> outputProvider)
    {
        for (int i = 0; i < ROWS; i++)
        {
            sources[i] = Optional.ofNullable(sourceProvider.apply(i)).orElse(new EmptySourceWrapper());
        }
        this.outputProvider = outputProvider;
    }

    public void update()
    {
        for (SourceWrapper source : sources)
        {
            Optional.ofNullable(source).map(s -> s.source).ifPresent(Source::update);
        }
        // For all outputs
        for (int outputIndex = MODIFIERS; outputIndex < MODIFIERS + OUTPUTS; outputIndex++)
        {
            // Go over every source
            for (int sourceIndex = 0; sourceIndex < ROWS; sourceIndex++)
            {
                if (matrix[sourceIndex][outputIndex])
                {
                    IldaFrame ildaFrame = Optional.ofNullable(sources[sourceIndex])
//                                                  .map(source -> source.source)
                                                  .map(SourceWrapper::getFrame)
                                                  .orElse(new IldaFrame());
                    for (int modifierIndex = 0; modifierIndex < MODIFIERS; modifierIndex++)
                    {
                        if (matrix[sourceIndex][modifierIndex])
                        {
                            ildaFrame = getModifier(modifierIndex).apply(ildaFrame);
                        }
                    }
                    outputProvider.apply(outputIndex).project(ildaFrame);
                }
            }
        }
    }

    private Effect getModifier(int modifierIndex)
    {
        if (modifiers.size() <= modifierIndex)
        {
            return new TrivialEffect();
        }
        return modifiers.get(modifierIndex);
    }

    public void display(LiveControl parent)
    {
        drawSources(parent);
    }

    private void drawSources(LiveControl parent)
    {
        int x = 10;
        int y = 160;
        int w = 100;
        int h = 100;
        int i = 0;
        for (SourceWrapper wrapper : sources)
        {
            Source source = wrapper.source;
            if (effectVisualisations[i] == null)
            {
                effectVisualisations[i] = parent.createGraphics(w, h, P3D);
            }
            PGraphics visualisation = effectVisualisations[i];
            visualisation.beginDraw();
            visualisation.background(0);
            Optional.ofNullable(source).map(Source::getFrame)
                    .ifPresent(frame -> frame.renderFrame(visualisation, true));
            visualisation.endDraw();
            parent.fill(parent.getUiConfig().getForegroundColor());
            parent.noStroke();
            parent.rect(x - 3, y - 3, w + 6, h + 6, 2);
            parent.image(visualisation, x, y);
            y += h + 15;
            i++;
        }
    }

    public void nextSource(int row)
    {
        sources[row].next();
    }

    public void previousSource(int row)
    {
        sources[row].previous();
    }

    public void enable(int row, int column)
    {
        matrix[row][column] = true;
    }

    public void disable(int row, int column)
    {
        matrix[row][column] = false;
    }

    public int getColor(int row, int column)
    {
        return matrix[row][column] ? 255 : 0;
    }

}
