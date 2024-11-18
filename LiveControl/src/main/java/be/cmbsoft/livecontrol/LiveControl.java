package be.cmbsoft.livecontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;

import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.ilda.OptimisationSettings;
import static be.cmbsoft.ildaviewer.IldaViewer.getDefaultSettings;
import static be.cmbsoft.ildaviewer.IldaViewer.initialiseState;
import static be.cmbsoft.ildaviewer.IldaViewer.setStateFromPApplet;
import be.cmbsoft.ildaviewer.ProgramState;
import be.cmbsoft.ildaviewer.oscillabstract.Oscillabstract;
import be.cmbsoft.ildaviewer.oscillabstract.Workspace;
import be.cmbsoft.laseroutput.Bounds;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.LsxOscOutput;
import be.cmbsoft.laseroutput.OutputOption;
import be.cmbsoft.laseroutput.etherdream.Etherdream;
import be.cmbsoft.laseroutput.etherdream.EtherdreamSource;
import be.cmbsoft.laseroutput.etherdream.EtherdreamStatus;
import be.cmbsoft.livecontrol.actions.IAction;
import be.cmbsoft.livecontrol.actions.ISimpleAction;
import be.cmbsoft.livecontrol.actions.UndoableAction;
import be.cmbsoft.livecontrol.chase.Chaser;
import be.cmbsoft.livecontrol.fx.EffectConfigurator;
import be.cmbsoft.livecontrol.fx.EffectConfiguratorContainer;
import be.cmbsoft.livecontrol.fx.Parameter;
import be.cmbsoft.livecontrol.gui.GUI;
import be.cmbsoft.livecontrol.gui.GUIContainer;
import be.cmbsoft.livecontrol.gui.GuiElement;
import be.cmbsoft.livecontrol.gui.GuiLinearLayout;
import be.cmbsoft.livecontrol.midi.MidiDeviceContainer;
import be.cmbsoft.livecontrol.settings.ChannelAndNote;
import be.cmbsoft.livecontrol.settings.Settings;
import be.cmbsoft.livecontrol.settings.SourceSettings;
import be.cmbsoft.livecontrol.sources.AudioEffectsSourceWrapper;
import be.cmbsoft.livecontrol.sources.BeamSourceWrapper;
import be.cmbsoft.livecontrol.sources.EmptySourceWrapper;
import be.cmbsoft.livecontrol.sources.IldaFolderPlayerSourceWrapper;
import be.cmbsoft.livecontrol.sources.OscillabstractSourceWrapper;
import be.cmbsoft.livecontrol.sources.audio.AudioProcessor;
import be.cmbsoft.livecontrol.ui.UIBuilder;
import static be.cmbsoft.livecontrol.ui.UIBuilder.buildUI;
import be.cmbsoft.livecontrol.ui.UIConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ControllerInterface;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class LiveControl extends PApplet implements GUIContainer, EffectConfiguratorContainer
{


    private static final LaserOutputWrapper DUMMY_OUTPUT = new LaserOutputWrapper(new LaserOutput()
    {
        @Override
        public void projectImpl(List<IldaPoint> points)
        {

        }

        @Override
        public boolean isConnected()
        {
            return false;
        }
    });

    // Program control flow
    private final Settings                                                  settings;
    private final File                                                      settingsFile   = new File("settings.json");
    private final ObjectMapper                                              objectMapper;
    private final Map<Integer, PFont>                                       fonts          = new HashMap<>();
    private final CircularFifoQueue<UndoableAction>                         actions        = new CircularFifoQueue<>(
        128);
    private final List<UndoableAction>                                      redoList       = new ArrayList<>();
    // Laser processing
    private final EtherdreamOutput                                          discoverDevice = new EtherdreamOutput();
    private final Map<String, LaserOutputWrapper> outputs = new HashMap<>();
    private final Matrix                                                    matrix;
    private final Chaser                                                    chaser;
    private final EffectConfigurator                                        effectConfigurator;
    private final Map<String, Parameter>                                    parameterMap   = new HashMap<>();
    private final MidiDeviceContainer                                       midiContainer;
    private final Oscillabstract                                            oscillabstract;
    private final ProgramState oscState;
    //UI
    private ControlP5 controlP5;
    public        PGraphics                                                 previousIcon;
    public        PGraphics                                                 nextIcon;
    private       GUI                                                       gui;
    private       PGraphics                                                 defaultIcon;
    private       Map<ControllerInterface<?>, UIBuilder.PositionCalculator> uiPositions;
    private       int                                                       prevWidth, prevHeight;
    private boolean         mouseClicked  = false;
    private boolean         mouseReleased = false;
    private boolean         mouseDragged;
    private UIConfig        uiConfig;
    private UIBuilder.Tab   activeTab     = UIBuilder.Tab.DEFAULT;
    // I/O
    private AudioProcessor  audioProcessor;
    private PImage          network;
    private GuiLinearLayout workspaceButtons;

    public LiveControl()
    {
        Settings settings1;
        log("Hello there! This is Generative Lasers.");
        objectMapper = new ObjectMapper();
        SimpleModule customDeserialiserModule = new SimpleModule();
        customDeserialiserModule.addKeyDeserializer(ChannelAndNote.class,
            new ChannelAndNote.ChannelAndNoteDeserialiser());
        objectMapper.registerModule(customDeserialiserModule);
        try
        {
            if (settingsFile.exists())
            {
                settings1 = objectMapper.readValue(settingsFile, Settings.class);
            }
            else
            {
                settings1 = new Settings();
            }
        }
        catch (IOException e)
        {
            settings1 = new Settings();
            error(e);
            log("Could not initialise settings...");
        }
        settings = settings1;
        OptimisationSettings optimisationSettings = new OptimisationSettings();
        optimisationSettings.setBlankDwell(true);
        optimisationSettings.setBlankDwellAmount(2);
        optimisationSettings.setMaxDistLit(0.1f);
        optimisationSettings.setMaxDistBlank(2);
        oscState = new ProgramState();
        oscState.setSettings(getDefaultSettings());
        initialiseState(oscState, this, "Arial");
        oscillabstract = new Oscillabstract(oscState);
        matrix = new Matrix(getSourceProvider(), getOutputProvider(), optimisationSettings);
        effectConfigurator = new EffectConfigurator(this);
        midiContainer = new MidiDeviceContainer();
        matrix.addListener((i, j, matrix) ->
        {
            // Here, i = 0 is the first note row, but Launchpad notes start at the bottom
            int y = j % 8;
            int x = 81 - y * i;
            midiContainer.output(x, y, matrix);
        });
        midiContainer.setupMidi(settings, () -> new ControlHandler(this), () -> matrix);
        chaser = new Chaser(this, matrix);
    }

    public static void main(String[] passedArgs)
    {
        String[]    appletArgs  = new String[]{LiveControl.class.getPackageName()};
        LiveControl liveControl = new LiveControl();
        PApplet.runSketch(appletArgs, liveControl);
    }

    public static void log(String what)
    {
        println(what);
    }

    public static void error(Exception exception)
    {
        exception.printStackTrace();
    }

    public static void error(String s, Exception e)
    {
        println(s);
        error(e);
    }

    @Override
    public void newParameter(String name, Parameter parameter)
    {
        parameterMap.put(name, parameter);
    }

    @Override
    public Parameter getController(int channel, int pitch)
    {
        return parameterMap.get(settings.getMidiMap().get(new ChannelAndNote(channel, pitch)));
    }

    public void toggleChase(int chaseIndex)
    {
        chaser.toggle(chaseIndex);
    }

    @Override
    public void settings()
    {
        size(1920, 1080, P3D);
    }

    @Override
    public void setup()
    {
        surface.setResizable(true);

        for (Settings.EtherdreamOutputSettings output : settings.etherdreamOutputs)
        {
            LaserOutputWrapper laser = createOutput(output);
            setBounds(output.getBounds(), laser.getWrappedOutput());
            outputs.put(output.getAlias(), laser);
        }
        for (Settings.OutputSettings output : settings.lsxOutputs)
        {
            outputs.put(UUID.randomUUID().toString(), createOutput(output));
        }


        buildDefaultSettings();

        audioProcessor = new AudioProcessor(this);

        gui = new GUI(this);
        // UI

        controlP5 = new ControlP5(this, getFont(36));
        uiConfig  = new UIConfig(this);
        network   = shapeToPGraphic(44, 44, uiConfig.getBackgroundColor(), uiConfig.getForegroundColor(),
            loadIconShape("network").orElse(new PShape()));
        nextIcon  =
            shapeToPGraphic(20, 20, 0, uiConfig.getForegroundColor(), loadIconShape("next").orElse(new PShape()));
        previousIcon =
            shapeToPGraphic(20, 20, 0, uiConfig.getForegroundColor(), loadIconShape("previous").orElse(new PShape()));
        buildUI(controlP5, gui, this);
        prevWidth = width;
        prevHeight = height;
        activateTab(UIBuilder.Tab.DEFAULT);
        matrix.enable(0, 4);

    }

    @Override
    public void draw()
    {
        updateProgramState();
        background(0);
        if (prevWidth != width || prevHeight != height)
        {
            prevWidth = width;
            prevHeight = height;
            updateUIPositions();
        }

        processLasers();

        switch (activeTab)
        {
            case DEFAULT -> drawDefault();
            case OUTPUTS -> drawOutputs();
            case OSCILLABSTRACT -> drawOscillabstract();
            case SETTINGS -> drawSettings();
            case ABOUT -> drawAbout();
        }
        gui.update();
        mouseClicked = false;
        mouseReleased = false;
        mouseDragged = false;
    }

    public void activateUITab(UIBuilder.Tab tab)
    {
        controlP5.getTab(StringUtils.capitalize(tab.name().toLowerCase())).bringToFront();
        activateTab(tab);

    }

    private void drawOscillabstract()
    {
        oscillabstract.update(oscState, this.g);
    }

    private void updateProgramState()
    {
        setStateFromPApplet(oscState, this);
    }

    @Override
    public void mouseReleased()
    {
        this.mouseReleased = true;
        updateProgramState();
    }

    @Override
    public void mousePressed()
    {
        updateProgramState();

        if (activeTab == UIBuilder.Tab.OSCILLABSTRACT) oscillabstract.mousePressed(oscState);
    }

    @Override
    public void mouseClicked()
    {
        this.mouseClicked = true;
        updateProgramState();
    }

    @Override
    public void mouseDragged()
    {
        this.mouseDragged = true;
        updateProgramState();
    }

    @Override
    public void keyPressed()
    {
        if (key == ESC)
        {
            outputs.values().forEach(LaserOutputWrapper::halt);
            key = 0;

        }
    }

    @Override
    public void exit()
    {
        outputs.values().forEach(LaserOutputWrapper::halt);
        saveSettings();
        midiContainer.close();
        super.exit();
    }

    public void addOutput(String id)
    {
//        outputs.put(uuid, null);
    }

    private LaserOutputWrapper createOutput(Settings.OutputSettings output)
    {

        if (output instanceof Settings.LsxOutputSettings lsxOutput)
        {
            return new LaserOutputWrapper(
                new LsxOscOutput(lsxOutput.getTimeline(), lsxOutput.getFrameNumber(), lsxOutput.getHost(),
                    lsxOutput.getPort()));
        }
        if (output instanceof Settings.EtherdreamOutputSettings etherdreamSettings)
        {
            EtherdreamOutput etherdreamOutput = new EtherdreamOutput().setAlias(etherdreamSettings.getAlias());
            if (etherdreamSettings.isInvertX())
            {
                etherdreamOutput.option(OutputOption.INVERT_X);
            }
            if (etherdreamSettings.isInvertY())
            {
                etherdreamOutput.option(OutputOption.INVERT_Y);
            }
            etherdreamOutput.setIntensity(etherdreamSettings.getIntensity());
            return new LaserOutputWrapper(etherdreamOutput);
        }
        throw new IllegalStateException("Unknown output type");
    }

    public void updateUIPositions()
    {
        for (Map.Entry<ControllerInterface<?>, UIBuilder.PositionCalculator> entry
            : uiPositions.entrySet())
        {
            ControllerInterface<?> controller = entry.getKey();
            UIBuilder.PositionCalculator position   = entry.getValue();
            controller.setPosition(calculatePosition(position, controller));
        }
    }

    public void doAction(UndoableAction action)
    {
        action.execute();
        actions.add(action);
        redoList.clear();
    }

    public void doAction(ISimpleAction action)
    {
        action.execute();
    }

    public void removeOutput(String id)
    {
        Optional.ofNullable(outputs.get(id)).ifPresent(LaserOutputWrapper::halt);
        outputs.remove(id);
    }

    private float[] calculatePosition(UIBuilder.PositionCalculator position, ControllerInterface<?> controller)
    {
        float x = 0;
        float y = 0;
        switch (position.getType())
        {
            case UPPER_RIGHT_ANCHOR ->
            {
                x = width - position.getOffsetX() - controller.getWidth();
                y = position.getOffsetY();
            }
            case UPPER_LEFT_ANCHOR ->
            {
                x = position.getOffsetX();
                y = position.getOffsetY();
            }
            case LOWER_RIGHT_ANCHOR ->
            {
            }
            case LOWER_LEFT_ANCHOR ->
            {
            }
        }
        return new float[]{x, y};
    }

    private void drawOutputs()
    {
        int x = 10;
        int y = 200;
        int w = 400;
        int h = 64;
        textAlign(RIGHT);
        for (Etherdream detectedDevice : discoverDevice.getDetectedDevices())
        {
            stroke(uiConfig.getForegroundColor());
            strokeWeight(2);
            fill(uiConfig.getBackgroundColor());
            rect(x, y, w, h, 10);
            fill(uiConfig.getFontColor());
            text(detectedDevice.getMac(), x + 60, y + 20, w - 70, h);

            EtherdreamStatus status = detectedDevice.getBroadcast().getStatus();

            if (detectedDevice.stale())
            {
                fill(255, 0, 0);
            }
            else
            {
                fill(0, 255, 0);
            }
            rect(x + 10, y + 10, h - 20, h - 20);
            if (status.getSource() == EtherdreamSource.NETWORK_STREAMING)
            {
                image(network, x + h, y + 10);
            }
        }

        x = 500;
        w = 400;
        h = 400;
        for (LaserOutputWrapper wrapper : outputs.values())
        {
            LaserOutput output = wrapper.getWrappedOutput();
            Bounds bounds = output.getBounds();
            if (mousePressed && isMouseOver(x, y, x + w, y + h))
            {
                if (isMouseOver(x, y + h / 2, x + w / 2, y + h))
                {
                    bounds.setLowerLeft(getRemappedMouse(x, w, y, h));
                }
                if (isMouseOver(x, y, x + w / 2, y + h / 2))
                {
                    bounds.setUpperLeft(getRemappedMouse(x, w, y, h));
                }
                if (isMouseOver(x + w / 2, y, x + w, y + h / 2))
                {
                    bounds.setUpperRight(getRemappedMouse(x, w, y, h));
                }
                if (isMouseOver(x + w / 2, y + h / 2, x + w, y + h))
                {
                    bounds.setLowerRight(getRemappedMouse(x, w, y, h));
                }
                persistBounds(output, bounds);
            }
            stroke(255, 0, 0);
            strokeWeight(3);
            fill(150, 50, 50);
            beginShape(QUADS);
            vertex(map(bounds.getLowerLeft().x, -1, 1, x, x + w), map(bounds.getLowerLeft().y, -1, 1, y, y + h));
            vertex(map(bounds.getUpperLeft().x, -1, 1, x, x + w), map(bounds.getUpperLeft().y, -1, 1, y, y + h));
            vertex(map(bounds.getUpperRight().x, -1, 1, x, x + w), map(bounds.getUpperRight().y, -1, 1, y, y + h));
            vertex(map(bounds.getLowerRight().x, -1, 1, x, x + w), map(bounds.getLowerRight().y, -1, 1, y, y + h));
            endShape();
            noFill();
            stroke(uiConfig.getForegroundColor());
            strokeWeight(1);
            rect(x, y, w, h);

            fill(uiConfig.getFontColor());
            textAlign(LEFT);
            text("Points: " + output.getCurrentFramePointCount(), x, y + h + 80);

            x += w + 20;
        }

    }

    public void setUIPositions(Map<ControllerInterface<?>, UIBuilder.PositionCalculator> positions)
    {
        this.uiPositions = positions;
    }

    public void setBounds(Bounds bounds, LaserOutput laser)
    {
        Bounds existingBounds = laser.getBounds();
        if (bounds != null)
        {
            existingBounds.setLowerLeft(bounds.getLowerLeft());
            existingBounds.setLowerRight(bounds.getLowerRight());
            existingBounds.setUpperRight(bounds.getUpperRight());
            existingBounds.setUpperLeft(bounds.getUpperLeft());
        }
        else
        {
            existingBounds.setLowerLeft(new PVector(-1, 1));
            existingBounds.setLowerRight(new PVector(1, 1));
            existingBounds.setUpperRight(new PVector(1, -1));
            existingBounds.setUpperLeft(new PVector(-1, -1));
        }
        persistBounds(laser, existingBounds);
    }

    @Override
    public int getMouseX()
    {
        return mouseX;
    }

    @Override
    public int getMouseY()
    {
        return mouseY;
    }

    @Override
    public int getPMouseX()
    {
        return pmouseX;
    }

    @Override
    public int getPMouseY()
    {
        return pmouseY;
    }

    @Override
    public boolean isMouseClicked()
    {
        return mouseClicked;
    }

    @Override
    public boolean isMouseReleased()
    {
        return mouseReleased;
    }

    @Override
    public boolean isMousePressed()
    {
        return mousePressed;
    }

    @Override
    public boolean isMouseDragged()
    {
        return mouseDragged;
    }

    @Override
    public void releaseMouse()
    {
        this.mouseReleased = true;
        this.mouseClicked = false;
        oscState.setMousePressed(false);
    }

    @Override
    public int getGuiStrokeColor()
    {
        return uiConfig.getForegroundColor();
    }

    @Override
    public int getGuiFillColor()
    {
        return uiConfig.getBackgroundColor();
    }

    @Override
    public int getGuiMouseOverColor()
    {
        return uiConfig.getMouseOverColor();
    }

    @Override
    public int getGuiActiveColor()
    {
        return uiConfig.getActiveColor();
    }

    @Override
    public PFont getFont(int size)
    {
        return fonts.computeIfAbsent(size, s -> createFont("Roboto", s));
    }

    private @NotNull Optional<PShape> loadIconShape(String key)
    {
        String fileName = "data/icons/" + key + ".svg";
        File   file     = sketchFile(fileName);
        if (file.exists())
        {
            return Optional.ofNullable(loadShape(fileName));
        }
        else
        {
            log("Could not find icon file " + fileName);
            return Optional.empty();
        }
    }

    @Override
    public void setMouseOverInfoText(String infoText)
    {
        //TODO
    }

    private void persistBounds(LaserOutput laser, Bounds existingBounds)
    {
        if (laser instanceof EtherdreamOutput etherdream)
        {
            settings.etherdreamOutputs.stream()
                                      .filter(output -> output.getAlias().equals(etherdream.getAlias()))
                                      .forEach(output -> output.setBounds(existingBounds));
        }
    }

    @Override
    public float getSliderDampFactor()
    {
        return 0.25f;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    public UIConfig getUiConfig()
    {
        return uiConfig;
    }

    public void activateTab(UIBuilder.Tab tab)
    {
        this.activeTab = tab;
        if (tab == UIBuilder.Tab.OSCILLABSTRACT)
        {
            initOscillabstract();
        }
    }

    public void undo()
    {
        log("undo");
        if (!actions.isEmpty())
        {
            Optional.ofNullable(actions.get(actions.size() - 1)).ifPresent(action ->
            {
                action.undo();
                actions.remove(action);
                redoList.add(action);
            });
        }
    }

    public void redo()
    {
        log("redo");
        if (!redoList.isEmpty())
        {
            redoList.get(redoList.size() - 1).execute();
            redoList.remove(redoList.size() - 1);
        }
    }

    public AudioProcessor getAudioProcessor()
    {
        return audioProcessor;
    }

    public boolean isMouseOver(int x, int y, int x2, int y2)
    {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    private Function<Integer, LaserOutputWrapper> getOutputProvider()
    {
        return i -> i >= outputs.size() ? DUMMY_OUTPUT
            : outputs.values().stream().distinct().skip(i).iterator().next();
    }

    private IntFunction<SourceWrapper> getSourceProvider()
    {
        return this::getSourceWrapperFromSettings;
    }

    public PImage[] getIcons(String key, int width, int height, int backgroundColor, int foregroundColor,
        int activeColor)
    {
        return loadIconShape(key).map(shape ->
        {
            PGraphics def = shapeToPGraphic(width, height, backgroundColor, foregroundColor, shape);
            PGraphics active = createGraphics(width, height);
            active.beginDraw();
            active.background(activeColor);
            active.fill(foregroundColor);
            active.shape(shape, 0, 0, width, height);
            active.endDraw();
            return new PImage[]{def, active, active, active};
        }).orElse(new PImage[]{getDefaultIcon()});
    }

    private void buildDefaultSettings()
    {
//        Settings.EtherdreamOutputSettings etherdreamOutputSettings = new Settings.EtherdreamOutputSettings();
//        etherdreamOutputSettings.alias = "6E851F3F2177";
//        settings.etherdreamOutputs.add(etherdreamOutputSettings);

//        SourceSettings ildaSource = new IldaFolderPlayerSourceWrapper.IldaFolderPlayerSettings("D:\\Laser\\ILDA\\Live");
        SourceSettings ildaSource =
            new IldaFolderPlayerSourceWrapper.IldaFolderPlayerSettings("C:\\Users\\Florian\\ILDA\\Live");

        SourceSettings audioSource = new AudioEffectsSourceWrapper.AudioEffectsSettings(0);
        SourceSettings beamSource  = new BeamSourceWrapper.BeamSourceSettings(0);
        SourceSettings oscillabstractSource =
            new OscillabstractSourceWrapper.OscillabstractSourceSettings(List.of(createDefaultWorkspace()));
        settings.setSources(new ArrayList<>(List.of(ildaSource, audioSource, beamSource, oscillabstractSource)));

        settings.setMidiMatrixInputDevice("MIDIIN2 (Launchpad Pro)");
        settings.setMidiMatrixOutputDevice("MIDIOUT3 (Launchpad Pro)");
        settings.setMidiControlDevice("nanoKONTROL2");

        settings.getMidiMap().put(new ChannelAndNote(0, 7), "Chase speed");
        settings.getMidiMap().put(new ChannelAndNote(0, 23), "First chase row");
        settings.getMidiMap().put(new ChannelAndNote(1, 12), "waveformHue");
        settings.getMidiMap().put(new ChannelAndNote(0, 0), "Playback speed");
        settings.getMidiMap().put(new ChannelAndNote(0, 32), "Beam one");
        settings.getMidiMap().put(new ChannelAndNote(0, 48), "Beam two");
        settings.getMidiMap().put(new ChannelAndNote(0, 64), "Beam three");
    }

    private Workspace createDefaultWorkspace()
    {
        return Oscillabstract.createDefaultWorkspace(oscState);
    }

    private void processLasers()
    {
        chaser.update();
        matrix.update();
    }

    private void drawAbout()
    {

    }

    private void drawSettings()
    {

    }

    private PGraphics getDefaultIcon()
    {
        if (defaultIcon == null)
        {
            defaultIcon = createGraphics(20, 20);
            defaultIcon.beginDraw();
            defaultIcon.background(255);
            defaultIcon.stroke(255, 0, 0);
            defaultIcon.strokeWeight(5);
            defaultIcon.line(1, 1, 20, 20);
            defaultIcon.line(20, 1, 1, 20);
            defaultIcon.endDraw();
        }
        return defaultIcon;
    }

    private @NotNull PVector getRemappedMouse(int x, int w, int y, int h)
    {
        return new PVector(map(mouseX, x, x + w, -1, 1), map(mouseY, y, y + h, -1, 1));
    }

    private void drawDefault()
    {
        matrix.display(this);
    }

    @Override
    public void doAction(IAction action)
    {
        if (action instanceof ISimpleAction simpleAction)
        {
            doAction(simpleAction);
        }
        else if (action instanceof UndoableAction undoableAction)
        {
            doAction(undoableAction);
        }
    }

    private void initOscillabstract()
    {
        workspaceButtons.clear();
        oscillabstract.getWorkspaces()
            .forEach(workspace -> workspaceButtons.addElement(gui.addButton(workspace.getName())
                .setTitle(workspace.getName())
                .setPressAction(() -> oscillabstract.activateWorkspace(workspace))
                .setSize(200, 50)));
    }

    // Reflexive usage by ControlP5
    public void controlEvent(ControlEvent theControlEvent)
    {
        if (theControlEvent.isTab())
        {
            UIBuilder.activateTab(gui, UIBuilder.Tab.values()[theControlEvent.getTab().getId()], this);
        }
    }

    private @NotNull PGraphics shapeToPGraphic(int width, int height, int backgroundColor, int foregroundColor,
        PShape shape)
    {
        PGraphics def = createGraphics(width, height);
        def.beginDraw();
        def.background(backgroundColor);
        def.fill(foregroundColor);
        def.stroke(foregroundColor);
        shape.setStroke(foregroundColor);
        shape.setFill(foregroundColor);
        def.shape(shape, 0, 0, width, height);
        def.endDraw();
        return def;
    }

    private SourceWrapper getSourceWrapperFromSettings(Integer i)
    {
        List<SourceSettings> sources = settings.getSources();
        if (sources == null || sources.size() <= i)
        {
            return new EmptySourceWrapper();
        }
        SourceSettings sourceSettings = sources.get(i);
        return switch (sourceSettings)
        {
            case
                IldaFolderPlayerSourceWrapper.IldaFolderPlayerSettings ildaFolderPlayerSettings -> new IldaFolderPlayerSourceWrapper(
                new File(ildaFolderPlayerSettings.ildaFolder()), this);
            case AudioEffectsSourceWrapper.AudioEffectsSettings audioEffectsSettings -> new AudioEffectsSourceWrapper(
                this).setSettings(audioEffectsSettings);
            case BeamSourceWrapper.BeamSourceSettings beamSourceSettings -> new BeamSourceWrapper(this).setSettings(
                beamSourceSettings);
            case
                OscillabstractSourceWrapper.OscillabstractSourceSettings oscillabstractSourceSettings -> new OscillabstractSourceWrapper(
                this).setSettings(oscillabstractSourceSettings);

            default -> throw new IllegalStateException("Unexpected value: " + sourceSettings);
        };
    }

    private void saveSettings()
    {
        try
        {
            settings.getSources().clear();
            settings.getSources().addAll(matrix.getSourceSettings());
            if (!settingsFile.exists() && !settingsFile.createNewFile())
            {
                log("Could not create settings file!");
            }
            objectMapper.writeValue(settingsFile, settings);
            log("Persisted the settings");
        }
        catch (Exception e)
        {
            error(e);
            log("Could not write settings file...");
        }
    }

    @Override
    public void addGuiElement(GuiElement<?> element)
    {
        gui.addGuiElement(element);
    }

    public void enableChase(int chaseIndex)
    {
        chaser.enable(chaseIndex);
    }

    public void disableChase(int chaseIndex)
    {
        chaser.disable(chaseIndex);
    }

    public void setFlash(boolean b)
    {
        matrix.setFlashMode(b);
    }

    public ProgramState getOscState()
    {
        return oscState;
    }

    public Oscillabstract getOscillabstract()
    {
        return oscillabstract;
    }

    public void setOscillabstractWorkspaceButtons(GuiLinearLayout guiLinearLayout)
    {
        this.workspaceButtons = guiLinearLayout;
    }

}
