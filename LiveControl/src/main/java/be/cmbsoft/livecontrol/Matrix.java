package be.cmbsoft.livecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.IntFunction;

import be.cmbsoft.ilda.IldaFrame;
import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.ilda.OptimisationSettings;
import be.cmbsoft.ilda.Optimiser;
import be.cmbsoft.livecontrol.chase.ChaseReceiver;
import be.cmbsoft.livecontrol.fx.Effect;
import be.cmbsoft.livecontrol.fx.TrivialEffect;
import be.cmbsoft.livecontrol.midi.MidiReceiver;
import be.cmbsoft.livecontrol.settings.SourceSettings;
import be.cmbsoft.livecontrol.sources.EmptySourceWrapper;
import be.cmbsoft.livecontrol.sources.Source;
import processing.core.PGraphics;

import static be.cmbsoft.livecontrol.LiveControl.log;
import static processing.core.PConstants.P3D;

public class Matrix implements ChaseReceiver, MidiReceiver.NoteListener
{
    public static final int                                   ROWS                = 8;
    public static final int                                   MODIFIERS           = 4;
    public static final int                                   OUTPUTS             = 4;

    public interface MatrixListener
    {
        void onUpdate(int i, int j, boolean matrix);

    }
    private final       SourceWrapper[]                       sources             = new SourceWrapper[ROWS];
    private final       Function<Integer, LaserOutputWrapper> outputProvider;
    private final       boolean[][]                           matrix              =
        new boolean[ROWS][MODIFIERS + OUTPUTS];
    private final       List<Effect>                          modifiers           = new ArrayList<>();
    private final       PGraphics[]                           sourceVisualisation = new PGraphics[8];
    private final       List<List<IldaPoint>>                 processedFrames     = new ArrayList<>(ROWS);
    private final       Optimiser                             optimiser;
    private final       List<MatrixListener>                  listeners           = new ArrayList<>();
    private             boolean                               flashMode           = true;
    private int offsetX       = 250;
    private int offsetY       = 200;
    private int elementWidth  = 80;
    private int elementHeight = 80;
    private int padding       = 15;

    private final IldaFrame emptyDebugFrame;

    public Matrix(IntFunction<SourceWrapper> sourceProvider, Function<Integer, LaserOutputWrapper> outputProvider,
        OptimisationSettings optimisationSettings)
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
        IldaPoint ildaPoint = new IldaPoint(0, 0, 0, 1, 1, 1, true);
        emptyDebugFrame = new IldaFrame();
        emptyDebugFrame.getPoints().add(ildaPoint);
    }

    @Override
    public void noteOn(int channel, int pitch, int velocity)
    {
        int x = 8 - pitch / 10;
        int y = pitch % 10 - 1;
        if (flashMode)
        {
            enable(x, y);
        }
        else
        {
            toggleAndPublish(x, y);
        }
    }

    @Override
    public void noteOff(int channel, int pitch, int velocity)
    {
        int x = 8 - pitch / 10;
        int y = pitch % 10 - 1;
        if (flashMode)
        {
            disable(x, y);
        }
    }

    @Override
    public void controlChange(int channel, int pitch, int velocity)
    {

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
//            List<IldaPoint> frame = null;//emptyDebugFrame.getPoints();
            List<IldaPoint> frame = emptyDebugFrame.getCopyOnWritePoints();
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

    @Override
    public void deactivate(int x, int y)
    {
        disable(x, y);
    }

    @Override
    public void activate(int x, int y)
    {
        enable(x, y);
    }

    public void display(LiveControl parent)
    {
        drawSources(parent);
        int x = offsetX + 150;
        int y = offsetY - 120;
        for (int i = 0; i < MODIFIERS; i++)
        {
            if (i < modifiers.size())
            {
                Effect modifier = modifiers.get(i);
                drawModifier(parent, modifier, x, y, elementWidth, elementHeight);
            }
            x += elementWidth + padding;
        }
        for (int i = 0; i < OUTPUTS; i++)
        {
            LaserOutputWrapper output = outputProvider.apply(i);
            output.display(parent, x, y, elementWidth, elementHeight);
            x += elementWidth + padding;
        }
        x = offsetX + 150;
        y = offsetY;
        parent.stroke(parent.getUiConfig().getForegroundColor());
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[i].length; j++)
            {
                if (parent.isMouseClicked() && parent.isMouseOver(x, y, x + elementWidth, y + elementHeight))
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
                parent.rect(x, y, elementWidth, elementHeight);
                x += elementWidth + 15;
            }
            x = offsetX + 150;
            y += elementHeight + 15;
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
        publish(row, column, true);
    }

    public void disable(int row, int column)
    {
        matrix[row][column] = false;
        publish(row, column, false);
    }

    public void addListener(MatrixListener listener)
    {
        listeners.add(listener);
    }

    public List<SourceSettings> getSourceSettings()
    {
        List<SourceSettings> settings = new ArrayList<>();
        for (SourceWrapper source : sources)
        {
            settings.add(source.getSettings());
        }
        return settings;
    }

    private Effect getModifier(int modifierIndex)
    {
        if (modifiers.size() <= modifierIndex)
        {
            return new TrivialEffect();
        }
        return modifiers.get(modifierIndex);
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
        int x = offsetX;
        int y = offsetY;
        int w = elementWidth;
        int h = elementHeight;
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
            if (parent.isMouseClicked())
            {

                if (parent.isMouseOver(x - 27, y, x, y + h))
                {
                    wrapper.previous();
                    parent.releaseMouse();
                }
                if (parent.isMouseOver(x + w, y, x + w + 27, y + h))
                {
                    wrapper.next();
                    parent.releaseMouse();
                }
                if (parent.isMouseOver(x, y, x + w, y + h))
                {
                    wrapper.mouseClicked();
                    parent.releaseMouse();
                }
            }

            PGraphics visualisation = sourceVisualisation[i];
            visualisation.beginDraw();
            visualisation.background(0);
            Optional.ofNullable(source)
                    .map(Source::getFrame)
                    .ifPresent(frame -> frame.renderFrame(visualisation, true));
            visualisation.endDraw();
            parent.fill(parent.getUiConfig().getForegroundColor());
            parent.noStroke();
            parent.rect(x - 3, y - 3, w + 6, h + 6, 2);
            parent.image(visualisation, x, y);
            y += h + padding;
            i++;
        }
    }

    public boolean isFlashMode()
    {
        return flashMode;
    }

    public void setFlashMode(boolean flashMode)
    {
        this.flashMode = flashMode;
    }


}
