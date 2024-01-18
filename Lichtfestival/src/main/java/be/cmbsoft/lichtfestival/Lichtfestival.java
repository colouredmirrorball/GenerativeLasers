package be.cmbsoft.lichtfestival;

import java.io.File;
import java.io.IOException;
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

    private       Laser     leftLaser;
    private       Laser     rightLaser;
    private       PGraphics leftGraphics;
    private       PGraphics rightGraphics;
    private final Settings  settings;
    private final File      settingsFile   = new File("settings.json");
    private final ObjectMapper objectMapper;
    private final Effect       currentEffect  = new MovingCircleEffect();
    //private final Effect    currentEffect  = new HorizontalLineEffect();//new MovingCircleEffect();
    private       boolean      boundSetupMode = false;

    // Custom MIDI Receiver to handle MIDI events
    static class MyMidiReceiver implements Receiver
    {
        private final Receiver delegate;

        public MyMidiReceiver(Receiver delegate)
        {
            this.delegate = delegate;
        }

        @Override
        public void send(MidiMessage message, long timeStamp)
        {
            if (message instanceof ShortMessage shortMessage) {

                int command = shortMessage.getCommand();
                int channel = shortMessage.getChannel();
                int data1   = shortMessage.getData1();
                int data2   = shortMessage.getData2();

                // Note On event
                if (command == ShortMessage.NOTE_ON) {
                    System.out.println("Note On - Channel: " + channel + ", Note: " + data1 + ", Velocity: " + data2);
                    // Add your action here
                }
                // Note Off event
                else if (command == ShortMessage.NOTE_OFF) {
                    System.out.println("Note Off - Channel: " + channel + ", Note: " + data1 + ", Velocity: " + data2);
                    // Add your action here
                }
                // Control Change event
                else if (command == ShortMessage.CONTROL_CHANGE) {
                    System.out.println(
                        "Control Change - Channel: " + channel + ", Controller: " + data1 + ", Value: " + data2);
                    // Add your action here
                }
            }

            // Delegate the MIDI message to the original receiver
            if (delegate != null) {
                delegate.send(message, timeStamp);
            }
        }

        @Override
        public void close()
        {
            // Cleanup resources if needed
            if (delegate != null) {
                delegate.close();
            }
        }
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
            if (selectedDevice != null) {
                MidiDevice midiDevice = MidiSystem.getMidiDevice(selectedDevice);
                //Receiver   receiver   = midiDevice.getReceiver();
                // Create a Transmitter to listen for MIDI events
                Transmitter transmitter = MidiSystem.getTransmitter();
//                transmitter.setReceiver(new MyMidiReceiver(receiver));

                // Open the transmitter
                //transmitter.open();
            } else {
                System.out.println(theMIDIDevice + " is not available...");
            }

        } catch (Exception exception) {
            // Continue without MIDI
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

    @Override
    public void setup()
    {

        leftLaser  = new Laser(this, "12A5FD136AFE");//.option(INVERT_Y);
        rightLaser = new Laser(this, "DE6656C57146");
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

        leftRenderer.getCurrentFrame().renderFrame(leftGraphics, true, width / 2, height);
        image(leftGraphics, 0, 0, width / 2, height);

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

        leftLaser.output();
        rightLaser.output();
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
    private IldaRenderer render(Laser rightLaser)
    {
        IldaRenderer renderer = rightLaser.getRenderer();
        renderer.setOptimise(true);
        renderer.beginDraw();
        currentEffect.generate(renderer, this);
        if (boundSetupMode) {
            renderer.stroke(100, 0, 0);
            renderer.rect(0, 0, renderer.width, renderer.height);
            if (mousePressed) {
                if (mouseX < width / 2 && mouseY < height / 2) {
                    rightLaser.output.getBounds().setUpperLeft(remappedMousePos());
                }
                if (mouseX > width / 2 && mouseY < height / 2) {
                    rightLaser.output.getBounds().setUpperRight(remappedMousePos());
                }
                if (mouseX < width / 2 && mouseY > height / 2) {
                    rightLaser.output.getBounds().setLowerLeft(remappedMousePos());
                }
                if (mouseX > width / 2 && mouseY > height / 2) {
                    rightLaser.output.getBounds().setLowerRight(remappedMousePos());
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
