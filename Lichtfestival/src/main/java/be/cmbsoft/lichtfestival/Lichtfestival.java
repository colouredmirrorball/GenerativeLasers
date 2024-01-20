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
import processing.core.PVector;
import processing.event.KeyEvent;

public class Lichtfestival extends PApplet
{

    // Custom MIDI Receiver to handle MIDI events
    class MyMidiReceiver implements Receiver
    {

        @Override
        public void send(MidiMessage message, long timeStamp)
        {
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


        @Override
        public void close()
        {
            midiDevice.close();
        }

    }
    private static final int                         MAX_MIDI_EVENTS = 25;
    private       Laser     rightLaser;
    private       PGraphics leftGraphics;
    private       PGraphics rightGraphics;
    private final Settings  settings;
    private final File      settingsFile   = new File("settings.json");
    private final ObjectMapper objectMapper;
    //private final Effect       currentEffect  = new CircleAtMouseEffect();//new MovingCircleEffect();
    private final        Effect                      currentEffect   = new HorizontalLineEffect();
    //private final Effect    currentEffect  = new HorizontalLineEffect();//new MovingCircleEffect();
    private       boolean boundSetupMode = false;
    private final        List<String>                midiEvents      =
        Collections.synchronizedList(new ArrayList<String>(50));
    private final        Map<Noot, Supplier<Effect>> effects         = new HashMap<>();
    private final Map<Noot, Consumer<Integer>> controls = new HashMap<>();
    private              Laser                       leftLaser;

    {
        effects.put(new Noot(0, 10), HorizontalLineEffect::new);
    }

    {
        controls.put(new Noot(0, 2), intensity -> Optional.ofNullable(leftLaser)
            .map(laser -> laser.output)
            .ifPresent(laserOutput -> laserOutput.setIntensity(2 * intensity)));
        controls.put(new Noot(0, 3), intensity -> Optional.ofNullable(rightLaser)
            .map(laser -> laser.output)
            .ifPresent(laserOutput -> laserOutput.setIntensity(2 * intensity)));
    }

    public Lichtfestival()
    {
        Settings settings1;
        System.out.println("Hello there! This is Generative Lasers.");
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
            System.out.println("Could not initialise settings...");
        }
        settings = settings1;
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        String            theMIDIDevice  = "Reaper to Leaser";
        MidiDevice.Info   selectedDevice = null;
        System.out.println("Looking for MIDI devices...");
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
                    System.out.println(theMIDIDevice + " is not an input...");
                }

                Transmitter    transmitter = midiDevice.getTransmitter();
                MyMidiReceiver receiver    = new MyMidiReceiver();
                transmitter.setReceiver(receiver);

                midiDevice.open();
            } else {
                System.out.println(theMIDIDevice + " is not available...");
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

        //leftLaser  = new Laser(this, "12A5FD136AFE");//.option(INVERT_Y);
        leftLaser = new Laser(this, "DE6656C57146");//.option(INVERT_Y);
        //rightLaser = new Laser(this, "DE6656C57146");
        rightLaser = new Laser(this, "");
        leftGraphics = createGraphics(width / 2, height, P3D);
        rightGraphics = createGraphics(width / 2, height, P3D);
        currentEffect.initialize(this);

        Bounds leftBounds = settings.getLeftBounds();
        setBounds(leftBounds, leftLaser);
        Bounds rightBounds = settings.getRightBounds();
        setBounds(rightBounds, rightLaser);
        leftLaser.output.setIntensity(255);
        rightLaser.output.setIntensity(255);
        OptimisationSettings leftSettings = leftLaser.getRenderer().getOptimisationSettings();
        leftSettings.setBlankDwellAmount(16);
    }

    @Override
    public void draw()
    {
        background(0);

        IldaRenderer leftRenderer    = renderLeft();
        IldaRenderer rightRenderer   = renderRight();
        int          leftPointCount  = leftRenderer.getCurrentPointCount();
        int          rightPointCount = rightRenderer.getCurrentPointCount();
        renderGraphics(leftGraphics, leftRenderer, 0);
        renderGraphics(rightGraphics, rightRenderer, width / 2);

        boolean leftConnected = leftLaser.output.isConnected();
        fill(255);
        text("Left laser " + (leftConnected ? "" : "dis") + "connected, " + leftPointCount + " points rendered", 50,
            40);
        if (leftConnected)
        {
            fill(0, 255, 0);
        }
        else
        {
            fill(255, 0, 0);
        }
        rect(20, 20, 20, 20);

        boolean rightConnected = rightLaser.output.isConnected();
        fill(255);
        text("Right laser " + (rightConnected ? "" : "dis") + "connected, " + rightPointCount + " points rendered", 50,
            80);
        if (rightConnected) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }
        rect(20, 60, 20, 20);
        fill(255);
        int midiY = 20;
        synchronized (midiEvents) {
            for (String midiEvent: midiEvents) {
                text(midiEvent, 300, midiY += 20);
            }
        }

        leftLaser.output();
        rightLaser.output();
    }

    private void processControl(Noot noot, int value)
    {
        try {
            Optional.ofNullable(controls.get(noot)).ifPresent(action -> action.accept(value));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private       MidiDevice     midiDevice;

    private void deactivateEffect(Noot noot)
    {
        //activeEffects.put(noot, false);
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

    private void activateEffect(Noot noot)
    {
        //activeEffects.put(noot, true);
    }

    private void logMidi(String event)
    {
        event = LocalTime.now() + " " + event;
        if (midiEvents.size() < MAX_MIDI_EVENTS) {
            midiEvents.add(event);
        } else {
            for (int i = 0; i < midiEvents.size() - 1; i++) {
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
        try {
            if (!settingsFile.exists()) {
                if (settingsFile.createNewFile()) {

                }
            }
            settings.setLeftBounds(leftLaser.output.getBounds());
            settings.setRightBounds(rightLaser.output.getBounds());
            objectMapper.writeValue(settingsFile, settings);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write settings file...");
        }
        if (midiDevice != null) {
            midiDevice.close();
        }
        super.exit();
    }

    private void setBounds(Bounds rightBounds, Laser rightLaser)
    {
        Bounds rightLaserBounds = rightLaser.output.getBounds();
        if (rightBounds != null) {
            rightLaserBounds.setLowerLeft(rightBounds.getLowerLeft());
            rightLaserBounds.setLowerRight(rightBounds.getLowerRight());
            rightLaserBounds.setUpperRight(rightBounds.getUpperRight());
            rightLaserBounds.setUpperLeft(rightBounds.getUpperLeft());
        } else {
            rightLaserBounds.setLowerLeft(new PVector(-1, 1));
            rightLaserBounds.setLowerRight(new PVector(1, 1));
            rightLaserBounds.setUpperRight(new PVector(1, -1));
            rightLaserBounds.setUpperLeft(new PVector(-1, -1));
        }
    }

    @NotNull
    private IldaRenderer renderLeft()
    {
        return render(leftLaser);
    }

    @NotNull
    private IldaRenderer renderRight()
    {
        return render(rightLaser);
    }


    private PVector remappedMousePos()
    {
        return new PVector(map(mouseX, 0, width, -1, 1),
            map(mouseY, 0, height, -1, 1));
    }

    @NotNull
    private IldaRenderer render(Laser laser)
    {
        IldaRenderer renderer = laser.getRenderer();
        renderer.setOptimise(true);
        renderer.beginDraw();
        currentEffect.generate(renderer, this);
        if (boundSetupMode) {
            renderer.stroke(100, 0, 0);
            renderer.rect(0, 0, renderer.width, renderer.height);
            if (mousePressed) {
                if (mouseX < width / 2 && mouseY < height / 2) {
                    laser.output.getBounds().setUpperLeft(remappedMousePos());
                }
                if (mouseX > width / 2 && mouseY < height / 2) {
                    laser.output.getBounds().setUpperRight(remappedMousePos());
                }
                if (mouseX < width / 2 && mouseY > height / 2) {
                    laser.output.getBounds().setLowerLeft(remappedMousePos());
                }
                if (mouseX > width / 2 && mouseY > height / 2) {
                    laser.output.getBounds().setLowerRight(remappedMousePos());
                }
            }
        }
        renderer.endDraw();
        return renderer;
    }

    @Override
    public void keyPressed(KeyEvent event)
    {

        if (event.getKey() == 'b')
        {
            boundSetupMode = !boundSetupMode;
        }

    }

    PVector newRandomPosition()
    {
        return new PVector(random(width), random(height));
    }

    public int newRandomColour()
    {
        return color(random(255), random(255), random(255));
    }

}
