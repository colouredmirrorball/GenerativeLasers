package be.cmbsoft.lichtfestival;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.ilda.OptimisationSettings;
import be.cmbsoft.laseroutput.Bounds;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;

public class Lichtfestival extends PApplet implements Receiver
{

    private static final int                            MAX_MIDI_EVENTS = 25;
    private final        Settings                       settings;
    private final        File                           settingsFile    = new File("settings.json");
    private final ObjectMapper                   objectMapper;
    private final List<String>                   midiEvents      =
        Collections.synchronizedList(new ArrayList<String>(50));
    private final        Map<Integer, Supplier<Effect>> effects         = new HashMap<>();
    private final        Map<Noot, Consumer<Integer>>   controls        = new HashMap<>();
    private              Laser                          rightLaser;
    private              PGraphics                      leftGraphics;
    private              PGraphics                      rightGraphics;
    private              Laser                          leftLaser;
    private              MidiDevice                     midiDevice;
    private              boolean                        booted          = false;
    private              PGraphics                      leftSafetyZone;
    private              PGraphics                      rightSafetyZone;
    private              boolean                        boundSetupMode                = false;
    private              boolean                        safetyZoneMode                = false;
    private              boolean                        effectSetupMode               = false;
    private              boolean                        effectEditMode                = false;
    private              int                            manuallySelectedEffectNote    = 60;
    private              int                            manuallySelectedEffectChannel = 0;

    {
        effects.put(10, HorizontalLineEffect::new);
        effects.put(20, () -> new TextEffect("Hello, test!"));
    }

    {
        controls.put(new Noot(0, 2), intensity ->
            Optional.ofNullable(leftLaser)
                    .map(laser -> laser.output)
                    .ifPresent(laserOutput -> laserOutput.setIntensity(2f * intensity)));
        controls.put(new Noot(1, 2), intensity ->
            Optional.ofNullable(rightLaser)
                    .map(laser -> laser.output)
                    .ifPresent(laserOutput -> laserOutput.setIntensity(2f * intensity)));
    }

    public Lichtfestival()
    {
        Settings settings1;
        log("Hello there! This is Generative Lasers.");
        objectMapper = new ObjectMapper();
        try {
            if (settingsFile.exists()) {
                settings1 = objectMapper.readValue(settingsFile, Settings.class);
            } else {
                settings1 = new Settings();
            }
        } catch (IOException e) {
            settings1 = new Settings();
            e.printStackTrace();
            log("Could not initialise settings...");
        }
        settings = settings1;
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        String            theMIDIDevice  = "Reaper to Leaser";
        MidiDevice.Info   selectedDevice = null;
        log("Looking for MIDI devices...");
        for (MidiDevice.Info info : midiDeviceInfo)
        {
            println("[" + info.getName() + "] " + info + ": " + info.getDescription() + " (" + info.getVendor() + " "
                + info.getVersion() + ")");
            if (theMIDIDevice.equals(info.getName())) {
                selectedDevice = info;
            }
        }
        try {
            if (selectedDevice != null)
            {
                midiDevice = MidiSystem.getMidiDevice(selectedDevice);
                if (midiDevice.getMaxTransmitters() == 0)
                {
                    log(theMIDIDevice + " is not an input...");
                }

                Transmitter    transmitter = midiDevice.getTransmitter();
                transmitter.setReceiver(this);

                midiDevice.open();
            } else {
                log(theMIDIDevice + " is not available...");
            }

        }
        catch (Exception exception)
        {
            // Continue without MIDI
            exception.printStackTrace();
        }

    }

    @Override
    public void setup()
    {
        leftLaser = new Laser(this, "6E851F3F2177");
//        leftLaser = new Laser(this, "12A5FD136AFE").option(OutputOption.INVERT_Y);
        rightLaser = new Laser(this, "DE6656C57146");

        leftGraphics = createGraphics(width / 2, height, P3D);
        rightGraphics = createGraphics(width / 2, height, P3D);

        Bounds leftBounds = settings.getLeftBounds();
        setBounds(leftBounds, leftLaser);
        Bounds rightBounds = settings.getRightBounds();
        setBounds(rightBounds, rightLaser);
        if (settings.getEffectLocations() == null)
        {
            settings.setEffectLocations(new HashMap<>());
        }
        leftLaser.output.setIntensity(255);
        rightLaser.output.setIntensity(255);
        OptimisationSettings leftSettings = leftLaser.getRenderer().getOptimisationSettings();
        leftSettings.setBlankDwellAmount(16);

        leftSafetyZone = createGraphics(width / 2, height);
        rightSafetyZone = createGraphics(width / 2, height);

        setupSafetyZone(leftSafetyZone, settings.getLeftSafetyZoneImageLocation());
        setupSafetyZone(rightSafetyZone, settings.getRightSafetyZoneImageLocation());

        leftLaser.output.setSafetyZone(leftSafetyZone);
        rightLaser.output.setSafetyZone(rightSafetyZone);

        booted = true;
    }

    private void setupSafetyZone(PGraphics safetyZone, String location)
    {
        safetyZone.beginDraw();
        PImage zonePImage = location == null ? null : loadImage(location);
        if (zonePImage == null)
        {
            safetyZone.background(255);
        }
        else
        {
            safetyZone.image(zonePImage, 0, 0);
        }
        safetyZone.endDraw();
    }

    @Override
    public void draw()
    {
        background(0);

        try
        {
            IldaRenderer leftRenderer  = render(leftLaser);
            IldaRenderer rightRenderer = render(rightLaser);

            int leftPointCount  = leftRenderer.getCurrentPointCount();
            int rightPointCount = rightRenderer.getCurrentPointCount();
            renderGraphics(leftGraphics, leftRenderer, 0);
            renderGraphics(rightGraphics, rightRenderer, width / 2);
            textSize(12);

            displayStatus(leftLaser, "Left laser ", leftPointCount, 50);
            displayStatus(rightLaser, "Right laser ", rightPointCount, height / 2 + 40);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        fill(255);
        synchronized (midiEvents)
        {
            int midiY = 20;
            for (String midiEvent : midiEvents)
            {
                text(midiEvent, 370, midiY += 20);
            }
        }

        leftLaser.output();
        rightLaser.output();

        if (boundSetupMode)
        {
            Bounds leftBounds  = leftLaser.output.getBounds();
            Bounds rightBounds = rightLaser.output.getBounds();
            if (mousePressed && !safetyZoneMode)
            {
                if (mouseX < width / 2)
                {
                    if (mouseX < width / 4 && mouseY < height / 2)
                    {
                        leftBounds.setUpperLeft(remappedMousePos(true));
                    }
                    if (mouseX > width / 4 && mouseY < height / 2)
                    {
                        leftBounds.setUpperRight(remappedMousePos(true));
                    }
                    if (mouseX < width / 4 && mouseY > height / 2)
                    {
                        leftBounds.setLowerLeft(remappedMousePos(true));
                    }
                    if (mouseX > width / 4 && mouseY > height / 2)
                    {
                        leftBounds.setLowerRight(remappedMousePos(true));
                    }
                }
                else
                {
                    if (mouseX < 3 * width / 4 && mouseY < height / 2)
                    {
                        rightBounds.setUpperLeft(remappedMousePos(false));
                    }
                    if (mouseX > 3 * width / 4 && mouseY < height / 2)
                    {
                        rightBounds.setUpperRight(remappedMousePos(false));
                    }
                    if (mouseX < 3 * width / 4 && mouseY > height / 2)
                    {
                        rightBounds.setLowerLeft(remappedMousePos(false));
                    }
                    if (mouseX > 3 * width / 4 && mouseY > height / 2)
                    {
                        rightBounds.setLowerRight(remappedMousePos(false));
                    }
                }
            }

            if (mousePressed && safetyZoneMode)
            {
                leftSafetyZone.beginDraw();
                rightSafetyZone.beginDraw();
                if (mouseX < width / 2)
                {
                    leftSafetyZone.fill(color(mouseButton == LEFT ? 0 : 255));
                    leftSafetyZone.noStroke();
                    leftSafetyZone.ellipse(mouseX, mouseY, 15, 15);
                }
                else
                {
                    rightSafetyZone.fill(color(mouseButton == LEFT ? 0 : 255));
                    rightSafetyZone.noStroke();
                    rightSafetyZone.ellipse(mouseX - width / 2, mouseY, 15, 15);
                }
                leftSafetyZone.endDraw();
                rightSafetyZone.endDraw();
            }

            stroke(255, 0, 0);
            fill(0, 0);
            displayBounds(leftBounds, true);
            displayBounds(rightBounds, false);
        }
        if (safetyZoneMode)
        {
            image(leftSafetyZone, 0, 0);
            image(rightSafetyZone, width / 2, 0);
        }
        if (effectEditMode) {
            fill(255);
            noStroke();
            text("Manually " + (effectSetupMode ? "editing" : "selecting") + " effect: " + manuallySelectedEffectChannel
                + " " + manuallySelectedEffectNote, 10, 30);
        }
    }

    private void displayBounds(Bounds bounds, boolean left)
    {
        beginShape(QUAD);
        remappedVertex(bounds.getUpperLeft(), left);
        remappedVertex(bounds.getUpperRight(), left);
        remappedVertex(bounds.getLowerRight(), left);
        remappedVertex(bounds.getLowerLeft(), left);
        endShape();
    }

    private void remappedVertex(PVector bounds, boolean left)
    {
        vertex(map(bounds.x, -1, 1, left ? 0 : width / 2, left ? width / 2 : width), map(bounds.y, -1, 1, 0, height));
    }

    @Override
    public void keyPressed(KeyEvent event)
    {

        if (event.getKey() == 'b')
        {
            boundSetupMode = !boundSetupMode;
            if (!boundSetupMode)
            {
                safetyZoneMode = false;
            }
            effectEditMode = false;
        }
        if (event.getKey() == 'l' && boundSetupMode)
        {
            safetyZoneMode = !safetyZoneMode;
        }
        if (event.getKey() == 's')
        {
            saveSettings();
        }
        if (event.getKey() == 'e') {
            effectEditMode = !effectEditMode;
            if (!effectEditMode) {
                effectSetupMode = false;
            }
        }
        if (event.getKey() == 'm' && effectEditMode) {
            effectSetupMode = !effectSetupMode;
        }
        if (effectEditMode && !effectSetupMode && event.getKey() == CODED) {
            activateEffect(switch (event.getKeyCode()) {
                case UP -> new Noot(manuallySelectedEffectChannel, ++manuallySelectedEffectNote);
                case DOWN -> new Noot(manuallySelectedEffectChannel, --manuallySelectedEffectNote);
                case LEFT -> new Noot(--manuallySelectedEffectChannel, manuallySelectedEffectNote);
                case RIGHT -> new Noot(++manuallySelectedEffectChannel, manuallySelectedEffectNote);
                default -> null;
            });
        }
        if (effectSetupMode && event.getKey() == CODED) {
            modifyActiveEffect(event.getKeyCode());
        }

    }

    private void processControl(Noot noot, int value)
    {
        try
        {
            Optional.ofNullable(controls.get(noot)).ifPresent(action -> action.accept(value));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public static void main(String[] passedArgs)
    {
        String[]      appletArgs    = new String[]{Lichtfestival.class.getPackageName()};
        Lichtfestival lichtfestival = new Lichtfestival();
        PApplet.runSketch(appletArgs, lichtfestival);
    }

    @Override
    public void settings()
    {
        size(1200, 600, P3D);
    }

    public boolean isEffectSetupMode()
    {
        return effectSetupMode;
    }

    private void deactivateEffect(Noot noot)
    {
        Supplier<Effect> effectSupplier = effects.get(noot.pitch());
        if (effectSupplier != null)
        {
            switch (noot.channel())
            {
                case 0 -> leftLaser.deactivate(effectSupplier.get());
                case 1 -> rightLaser.deactivate(effectSupplier.get());
                default -> log("Wrong channel!");
            }
        }
    }

    private void logMidi(String event)
    {
        event = LocalTime.now() + " " + event;
        if (midiEvents.size() < MAX_MIDI_EVENTS)
        {
            midiEvents.add(event);
        }
        else
        {
            for (int i = 0; i < midiEvents.size() - 1; i++)
            {
                midiEvents.set(i, midiEvents.get(i + 1));
            }
            midiEvents.set(MAX_MIDI_EVENTS - 1, event);
        }
    }

    private void renderGraphics(PGraphics leftGraphics, IldaRenderer leftRenderer, int xPos)
    {
        leftGraphics.beginDraw();
        leftGraphics.background(0);
        leftRenderer.getCurrentFrame().renderFrame(leftGraphics, true, width / 2, height);
        leftGraphics.endDraw();
        image(leftGraphics, xPos, 0, width / 2, height);
    }

    @Override
    public void exit()
    {
        leftLaser.output.halt();
        rightLaser.output.halt();
        saveSettings();
        if (midiDevice != null)
        {
            close();
        }
        super.exit();
    }

    private void log(String what)
    {
        System.out.println(what);
    }

    private void displayStatus(Laser laser, String name, int pointCount, int y)
    {
        boolean connected = laser.output.isConnected();
        fill(255);
        text(name + (connected ? "" : "dis") + "connected, " + pointCount + " points rendered, intensity: "
            + laser.output.getIntensity(), 50, y);
        if (connected)
        {
            fill(0, 255, 0);
        }
        else
        {
            fill(255, 0, 0);
        }
        rect(20, y - 15, 20, 20);
        fill(255);
        for (String activeEffectsName: laser.getActiveEffectsNames()) {
            text(activeEffectsName, 50, y += 20);
        }
    }


    private PVector remappedMousePos(boolean left)
    {
        return new PVector(map(mouseX, left ? 0 : width / 2, left ? width / 2 : width, -1, 1),
            map(mouseY, 0, height, -1, 1));
    }

    @NotNull
    private IldaRenderer render(Laser laser)
    {
        IldaRenderer renderer = laser.getRenderer();
        renderer.setOptimise(true);
        renderer.beginDraw();

        if (boundSetupMode)
        {
            renderer.stroke(100, 0, 0);
            renderer.rect(0, 0, renderer.width, renderer.height);
        }

        if (safetyZoneMode)
        {
            renderer.stroke(127, 127, 127);
            renderer.line(0, mouseY, renderer.width, mouseY);
            renderer.line(mouseX, 0, mouseX, renderer.height);
        }

        laser.processEffects(this);

        renderer.endDraw();
        return renderer;
    }

    private void activateEffect(Noot noot)
    {
        if (noot == null) return;
        if (noot.pitch() == 0)
        {
            leftLaser.removeActiveEffects();
            rightLaser.removeActiveEffects();
        }
        else if (noot.pitch() > 64)
        {
            var info = settings.getEffectLocations().computeIfAbsent(noot.pitch(), p ->
            {
                HighlightEffect.HighlightEffectInfo info1 = new HighlightEffect.HighlightEffectInfo();
                info1.setAlias("Noot " + p);
                return info1;
            });
            switch (noot.channel())
            {
                case 0 -> leftLaser.trigger(new HighlightEffect(info, noot), this);
                case 1 -> rightLaser.trigger(new HighlightEffect(info, noot), this);
                default -> log("Wrong channel!");
            }
        }
        else
        {
            Supplier<Effect> effectSupplier = effects.get(noot.pitch());
            if (effectSupplier != null)
            {
                switch (noot.channel())
                {
                    case 0 -> leftLaser.trigger(effectSupplier.get(), this);
                    case 1 -> rightLaser.trigger(effectSupplier.get(), this);
                    default -> log("Wrong channel!");
                }
            }
        }
    }

    private void setBounds(Bounds bounds, Laser laser)
    {
        Bounds existingBounds = laser.output.getBounds();
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
    }

    private void saveSettings()
    {
        try
        {
            if (!settingsFile.exists() && !settingsFile.createNewFile())
            {
                log("Could not create settings file!");
            }
            settings.setLeftBounds(leftLaser.output.getBounds());
            settings.setRightBounds(rightLaser.output.getBounds());
            String leftSafetyZoneImageLocation =
                Optional.ofNullable(settings.getLeftSafetyZoneImageLocation()).orElse("leftSafetyZone.png");
            String rightSafetyZoneImageLocation =
                Optional.ofNullable(settings.getRightSafetyZoneImageLocation()).orElse("rightSafetyZone.png");
            leftSafetyZone.save(leftSafetyZoneImageLocation);
            settings.setLeftSafetyZoneImageLocation(leftSafetyZoneImageLocation);
            rightSafetyZone.save(rightSafetyZoneImageLocation);
            settings.setRightSafetyZoneImageLocation(rightSafetyZoneImageLocation);
            objectMapper.writeValue(settingsFile, settings);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log("Could not write settings file...");
        }
    }

    private void modifyActiveEffect(int keyCode)
    {
        if (manuallySelectedEffectChannel == 0) {
            leftLaser.modifyEffect(new Noot(manuallySelectedEffectChannel, manuallySelectedEffectNote), keyCode);
        } else if (manuallySelectedEffectChannel == 1) {
            rightLaser.modifyEffect(new Noot(manuallySelectedEffectChannel, manuallySelectedEffectNote), keyCode);
        }
    }

    public int newRandomColour()
    {
        return color(random(255), random(255), random(255));
    }

    @Override
    public void send(MidiMessage message, long timeStamp)
    {
        if (!booted) return;
        if (message instanceof ShortMessage shortMessage)
        {
            int command = shortMessage.getCommand();
            int channel = shortMessage.getChannel();
            int data1   = shortMessage.getData1();
            int data2   = shortMessage.getData2();

            Noot noot = new Noot(channel, data1);

            // Note On event
            if (command == ShortMessage.NOTE_ON)
            {
                logMidi("Note On - Channel: " + channel + ", Note: " + data1 + ", Velocity: " + data2);
                activateEffect(noot);
            }
            // Note Off event
            else if (command == ShortMessage.NOTE_OFF)
            {
                logMidi("Note Off - Channel: " + channel + ", Note: " + data1 + ", Velocity: " + data2);
                deactivateEffect(noot);
            }
            // Control Change event
            else if (command == ShortMessage.CONTROL_CHANGE)
            {
                logMidi("Control Change - Channel: " + channel + ", Controller: " + data1 + ", Value: " + data2);
                processControl(noot, data2);
            }
        }
    }

    PVector newRandomPosition()
    {
        return new PVector(random(width / 2), random(height));
    }

    @Override
    public void close()
    {
        midiDevice.close();
    }

}
