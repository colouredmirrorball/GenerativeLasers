package be.cmbsoft.livecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.ilda.OptimisationSettings;
import be.cmbsoft.ilda.Optimiser;
import be.cmbsoft.livecontrol.fx.Effect;
import be.cmbsoft.livecontrol.fx.TrivialEffect;
import be.cmbsoft.livecontrol.sources.EmptySourceWrapper;
import be.cmbsoft.livecontrol.sources.Source;
import processing.core.PGraphics;

import static be.cmbsoft.livecontrol.LiveControl.log;
import static processing.core.PConstants.P3D;

public class Matrix
{
    public static final int ROWS      = 8;
    public static final int MODIFIERS = 4;
    public static final int OUTPUTS   = 4;

    private final SourceWrapper[] sources             = new SourceWrapper[ROWS];
    private final Function<Integer, LaserOutputWrapper> outputProvider;
    private final boolean[][]     matrix              = new boolean[ROWS][MODIFIERS + OUTPUTS];
    private final List<Effect>    modifiers           = new ArrayList<>();
    private final PGraphics[]     sourceVisualisation = new PGraphics[8];
    private final List<List<IldaPoint>> processedFrames = new ArrayList<>(ROWS);
    private final Optimiser optimiser;
    private final List<MatrixListener> listeners = new ArrayList<>();

    public Matrix(Function<Integer, SourceWrapper> sourceProvider,
        Function<Integer, LaserOutputWrapper> outputProvider, OptimisationSettings optimisationSettings)
    {
        for (int i = 0; i < ROWS; i++)
        {
            sources[i] = Optional.ofNullable(sourceProvider.apply(i)).orElse(new EmptySourceWrapper());
        }
        this.outputProvider = outputProvider;
        optimiser = new Optimiser(optimisationSettings);
        for (int i = 0; i < ROWS; i++)
        {
            processedFrames.add(List.of());
        }
    }

    public void update()
    {
        for (SourceWrapper source : sources)
        {
            Optional.ofNullable(source).map(s -> s.source).ifPresent(Source::update);
        }
        // Go over every source
        for (int sourceIndex = 0; sourceIndex < ROWS; sourceIndex++)
        {

            List<IldaPoint> points = Optional.ofNullable(sources[sourceIndex])
                                          .map(SourceWrapper::getFrame)
                                             .map(IldaFrame::getCopyOnWritePoints)
                                             .orElse(new ArrayList<>());
            for (int modifierIndex = 0; modifierIndex < MODIFIERS; modifierIndex++)
            {
                if (matrix[sourceIndex][modifierIndex])
                {
                    points = getModifier(modifierIndex).apply(points);
                }
            }
            processedFrames.set(sourceIndex, points);
        }
        // For all outputs
        for (int outputIndex = MODIFIERS; outputIndex < MODIFIERS + OUTPUTS; outputIndex++)
        {
            List<IldaPoint> frame = null;
            for (int sourceIndex = 0; sourceIndex < ROWS; sourceIndex++)
            {

                if (matrix[sourceIndex][outputIndex])
                {
                    List<IldaPoint> processedPoints = processedFrames.get(sourceIndex);
                    if (frame == null)
                    {
                        frame = processedPoints;
                    }
                    else
                    {
                        if (!processedPoints.isEmpty())
                        {
                            IldaPoint firstPoint = processedPoints.get(0);
                            IldaPoint duplicateFirst = new IldaPoint(firstPoint);
                            duplicateFirst.setBlanked(true);
                            frame.add(duplicateFirst);
                            frame.addAll(processedPoints);
                        }
                    }
                }
            }
            if (frame == null)
            {
                frame = List.of();
            }
            frame = optimiser.optimiseSegment(new CopyOnWriteArrayList<>(frame));
            outputProvider.apply(outputIndex - MODIFIERS).project(frame);
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
        int x = 200;
        int y = 80;
        int w = 80;
        int h = 80;
        for (int i = 0; i < MODIFIERS; i++)
        {
            if (i < modifiers.size())
            {
                Effect modifier = modifiers.get(i);
                drawModifier(parent, modifier, x, y, w, h);
            }
            x += w + 15;
        }
        for (int i = 0; i < OUTPUTS; i++)
        {
            LaserOutputWrapper output = outputProvider.apply(i);
            output.display(parent, x, y, w, h);
            x += w + 15;
        }
        x = 200;
        y = 200;
        parent.stroke(parent.getUiConfig().getForegroundColor());
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[i].length; j++)
            {
                if (parent.isMouseClicked() && parent.isMouseOver(x, y, x + w, y + h))
                {
                    toggleAndPublish(i, j);
                    parent.releaseMouse();
                }
                if (matrix[i][j])
                {
                    parent.fill(255);
                }
                else
                {
                    parent.fill(0);
                }
                parent.rect(x, y, w, h);
                x += w + 15;
            }
            x = 200;
            y += h + 15;
        }
    }

    private void toggleAndPublish(int i, int j)
    {
        matrix[i][j] = !matrix[i][j];
        publish(i, j, matrix[i][j]);
    }

    private void publish(int i, int j, boolean matrix)
    {
        log("Toggling " + i + " " + j + " " + matrix);
        listeners.forEach(listener -> listener.onUpdate(i, j, matrix));
    }

    private void drawModifier(LiveControl parent, Effect modifier, int x, int y, int w, int h)
    {
        modifier.display(parent, x, y, w, h);
    }

    private void drawSources(LiveControl parent)
    {
        int x = 50;
        int y = 200;
        int w = 80;
        int h = 80;
        int i = 0;
        for (SourceWrapper wrapper : sources)
        {
            Source source = wrapper.source;
            if (sourceVisualisation[i] == null)
            {
                sourceVisualisation[i] = parent.createGraphics(w, h, P3D);
            }
            parent.image(parent.previousIcon, x - 27, y + 30);
            parent.image(parent.nextIcon, x + w + 7, y + 30);
            if (parent.isMouseClicked() && parent.isMouseOver(x - 27, y, x, y + h))
            {
                wrapper.previous();
            }
            if (parent.isMouseClicked() && parent.isMouseOver(x + w, y, x + w + 27, y + h))
            {
                wrapper.next();
            }
            PGraphics visualisation = sourceVisualisation[i];
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

    public void addListener(MatrixListener listener)
    {
        listeners.add(listener);
    }

    public interface MatrixListener
    {
        void onUpdate(int i, int j, boolean matrix);

    }

}
