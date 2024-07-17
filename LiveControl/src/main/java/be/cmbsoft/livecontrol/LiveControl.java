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

import be.cmbsoft.ilda.IldaPoint;
import be.cmbsoft.laseroutput.EtherdreamOutput;
import be.cmbsoft.laseroutput.LaserOutput;
import be.cmbsoft.laseroutput.LsxOscOutput;
import be.cmbsoft.laseroutput.etherdream.Etherdream;
import be.cmbsoft.laseroutput.etherdream.EtherdreamSource;
import be.cmbsoft.laseroutput.etherdream.EtherdreamStatus;
import be.cmbsoft.livecontrol.actions.IAction;
import be.cmbsoft.livecontrol.actions.ISimpleAction;
import be.cmbsoft.livecontrol.actions.UndoableAction;
import be.cmbsoft.livecontrol.fx.EffectConfigurator;
import be.cmbsoft.livecontrol.fx.EffectConfiguratorContainer;
import be.cmbsoft.livecontrol.fx.Parameter;
import be.cmbsoft.livecontrol.gui.GUI;
import be.cmbsoft.livecontrol.gui.GUIContainer;
import be.cmbsoft.livecontrol.gui.GuiElement;
import be.cmbsoft.livecontrol.sources.EmptySourceWrapper;
import be.cmbsoft.livecontrol.sources.IldaFolderPlayerSourceWrapper;
import be.cmbsoft.livecontrol.ui.UIBuilder;
import static be.cmbsoft.livecontrol.ui.UIBuilder.buildUI;
import be.cmbsoft.livecontrol.ui.UIConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.ControllerInterface;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class LiveControl extends PApplet implements GUIContainer, EffectConfiguratorContainer
{

    private static final LaserOutput                       DUMMY_OUTPUT = new LaserOutput()
    {
        @Override
        public void project(List<IldaPoint> points)
        {

        }

        @Override
        public boolean isConnected()
        {
            return false;
        }
    };
    private final        Settings                          settings;
    private final        File                              settingsFile = new File("settings.json");
    private final        ObjectMapper                      objectMapper;
    private final        Map<Integer, PFont>               fonts        = new HashMap<>();
    private final        CircularFifoQueue<UndoableAction> actions      = new CircularFifoQueue<>(128);
    private final        List<UndoableAction>              redoList     = new ArrayList<>();

    private final EtherdreamOutput       discoverDevice = new EtherdreamOutput();
    private final Map<UUID, LaserOutput> outputs        = new HashMap<>();
    private final Matrix                 matrix;
    private final EffectConfigurator        effectConfigurator;
    private final Map<String, Parameter<?>> parameterMap = new HashMap<>();
//    private final List<Source>           activeSources        = new ArrayList<>();


    private ControlP5                                              controlP5;
    private GUI                                                    gui;
    private PGraphics                                              defaultIcon;
    private Map<ControllerInterface, UIBuilder.PositionCalculator> uiPositions;
    private int                                                    prevWidth, prevHeight;
    private boolean       mouseClicked  = false;
    private boolean       mouseReleased = false;
    private boolean       mouseDragged;
    private UIConfig      uiConfig;
    private UIBuilder.Tab activeTab     = UIBuilder.Tab.DEFAULT;

    private PImage network;

    public LiveControl()
    {
        Settings settings1;
        log("Hello there! This is Generative Lasers.");
        objectMapper = new ObjectMapper();
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
        matrix = new Matrix(getSourceProvider(), getOutputProvider());
        effectConfigurator = new EffectConfigurator(this);
    }

    @Override
    public void newParameter(String name, Parameter<?> parameter)
    {
        parameterMap.put(name, parameter);
    }

    private Function<Integer, LaserOutput> getOutputProvider()
    {
        return i -> i >= outputs.size() ? DUMMY_OUTPUT :
            outputs.values().stream().distinct().skip(i).iterator().next();
    }

    private Function<Integer, SourceWrapper> getSourceProvider()
    {
        return this::getSourceWrapperFromSettings;
    }

    private SourceWrapper getSourceWrapperFromSettings(Integer i)
    {
        List<SourceSettings> sources = settings.getSources();
        if (sources == null || sources.size() <= i)
        {
            return new EmptySourceWrapper();
        }
        SourceSettings sourceSettings = sources.get(i);
        return switch (sourceSettings.getType())
        {
            case ILDA_FOLDER -> new IldaFolderPlayerSourceWrapper(new File(sourceSettings.getIldaFolder()));
            default -> new EmptySourceWrapper();
        };
    }

    public static void main(String[] passedArgs)
    {
        String[]    appletArgs  = new String[]{LiveControl.class.getPackageName()};
        LiveControl liveControl = new LiveControl();
        PApplet.runSketch(appletArgs, liveControl);
    }

    public static void log(String what)
    {
//        LogManager.getLogger(LiveControl.class).info(what);
        println(what);
    }

    @Override
    public void settings()
    {
        size(1920, 1080, P3D);
    }

    public static void error(Exception exception)
    {
        exception.printStackTrace();
//        LogManager.getLogger(LiveControl.class).error(exception, exception);
    }

    public static void error(String s, Exception e)
    {
        println(s);
        error(e);
    }

    @Override
    public void setup()
    {
        surface.setResizable(true);

        for (Settings.OutputSettings output : settings.etherdreamOutputs)
        {
            outputs.put(UUID.randomUUID(), createOutput(output));
        }
        for (Settings.OutputSettings output : settings.lsxOutputs)
        {
            outputs.put(UUID.randomUUID(), createOutput(output));
        }

        outputs.put(UUID.randomUUID(), new EtherdreamOutput());
        Settings.EtherdreamOutputSettings etherdreamOutputSettings = new Settings.EtherdreamOutputSettings();
        etherdreamOutputSettings.alias = "6E851F3F2177";
        settings.etherdreamOutputs.add(etherdreamOutputSettings);

        SourceSettings sourceSettings = new SourceSettings();
        sourceSettings.setIldaFolder("D:\\Laser\\ILDA");
        sourceSettings.setType(SourceType.ILDA_FOLDER);
        settings.setSources(List.of(sourceSettings));

        gui = new GUI(this);
        controlP5 = new ControlP5(this, getFont(36));
        uiConfig = new UIConfig(this);
        network = shapeToPGraphic(44, 44, uiConfig.getBackgroundColor(), uiConfig.getForegroundColor(),
            loadIconShape("network").orElse(new PShape()));
        buildUI(controlP5, gui, this);
        prevWidth = width;
        prevHeight = height;
        activateTab(UIBuilder.Tab.DEFAULT);
    }

    @Override
    public void draw()
    {
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
            case SETTINGS -> drawSettings();
            case ABOUT -> drawAbout();
        }
        gui.update();
        mouseClicked = false;
        mouseReleased = false;
        mouseDragged = false;
    }

    private void processLasers()
    {
        matrix.update();
    }

    private void drawAbout()
    {

    }

    private void drawSettings()
    {

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
    }

    private void drawDefault()
    {
        matrix.display(this);
    }

    @Override
    public void mouseClicked()
    {
        this.mouseClicked = true;
    }

    @Override
    public void mouseReleased()
    {
        this.mouseReleased = true;
    }

    @Override
    public void mouseDragged()
    {
        this.mouseDragged = true;
    }

    private LaserOutput createOutput(Settings.OutputSettings output)
    {
        if (output instanceof Settings.LsxOutputSettings lsxOutput)
        {
            return new LsxOscOutput(lsxOutput.timeline, lsxOutput.frameNumber, lsxOutput.host, lsxOutput.port);
        }
        if (output instanceof Settings.EtherdreamOutputSettings etherdreamSettings)
        {
            return new EtherdreamOutput().setAlias(etherdreamSettings.getAlias());
        }
        throw new IllegalStateException("Unknown output type");
    }

    @Override
    public void exit()
    {
        outputs.values().forEach(LaserOutput::halt);
        saveSettings();
//        if (midiDevice != null) {
//            close();
//        }
        super.exit();
    }

    public void addOutput(UUID uuid)
    {
        outputs.put(uuid, null);
    }

    public void removeOutput(UUID uuid)
    {
        Optional.ofNullable(outputs.get(uuid)).ifPresent(LaserOutput::halt);
        outputs.remove(uuid);
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

    private @NotNull Optional<PShape> loadIconShape(String key)
    {
        String fileName = "data/icons/" + key + ".svg";
        File   file     = sketchFile(fileName);
        if (file.exists()) {
            return Optional.ofNullable(loadShape(fileName));
        } else {
            log("Could not find icon file " + fileName);
            return Optional.empty();
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

    private void saveSettings()
    {
        try
        {
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

    public void setUIPositions(Map<ControllerInterface, UIBuilder.PositionCalculator> positions)
    {
        this.uiPositions = positions;
    }

    public void updateUIPositions()
    {
        for (Map.Entry<ControllerInterface, UIBuilder.PositionCalculator> entry
            : uiPositions.entrySet())
        {
            ControllerInterface          controller = entry.getKey();
            UIBuilder.PositionCalculator position   = entry.getValue();
            controller.setPosition(calculatePosition(position, controller));
        }
    }

    private float[] calculatePosition(UIBuilder.PositionCalculator position, ControllerInterface controller)
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
            case LOWER_RIGHT_ANCHOR -> {}
            case LOWER_LEFT_ANCHOR -> {}
        }
        return new float[]{x, y};
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

    @Override
    public void setMouseOverInfoText(String infoText)
    {
        //TODO
    }

    @Override
    public void addGuiElement(GuiElement element)
    {
        gui.addGuiElement(element);
    }

    @Override
    public float getSliderDampFactor()
    {
        return 0.25f;
    }

    public UIConfig getUiConfig()
    {
        return uiConfig;
    }

    public void activateTab(UIBuilder.Tab tab)
    {
        this.activeTab = tab;
    }

    // Reflexive usage by ControlP5
    public void controlEvent(ControlEvent theControlEvent)
    {
        if (theControlEvent.isTab())
        {
            UIBuilder.activateTab(gui, UIBuilder.Tab.values()[theControlEvent.getTab().getId()], this);
        }
    }

}
