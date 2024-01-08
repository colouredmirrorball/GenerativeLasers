package be.cmbsoft.lichtfestival;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public class Lichtfestival extends PApplet
{

    private Laser     leftLaser;
    private Laser     rightLaser;
    private PGraphics leftGraphics;
    private PGraphics rightGraphics;


    public Lichtfestival()
    {

        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : midiDeviceInfo)
        {
            println(info + ": " + info.getDescription() + " (" + info.getVendor() + " " + info.getVersion() + ")");
        }

    }


    public static void main(String[] passedArgs)
    {
        String[] appletArgs = new String[]{Lichtfestival.class.getPackageName()};
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
        leftLaser = new Laser(this, "DE6656C57146");
        rightLaser = new Laser(this, "12A5FD136AFE");
        leftGraphics = createGraphics(width / 2, height, P3D);
        rightGraphics = createGraphics(width / 2, height, P3D);
    }

    @Override
    public void draw()
    {
        background(0);
        fill(255);
        text("Left laser connected", 50, 40);
        if (leftLaser.output.isConnected())
        {
            fill(0, 255, 0);
        }
        else
        {
            fill(255, 0, 0);
        }
        rect(20, 20, 20, 20);

        IldaRenderer renderer = leftLaser.getRenderer();
        renderer.beginDraw();
        renderer.colorMode(PConstants.HSB);
        renderer.stroke(frameCount % 255, 255, 255);
        renderer.ellipse(constrain(mouseX, 0, width / 2), mouseY, 50, 50);
        renderer.endDraw();

        renderer.getCurrentFrame().renderFrame(leftGraphics, true);
        image(leftGraphics, 0, 0, width / 2, height);

        leftLaser.output();
        rightLaser.output();
    }
}
