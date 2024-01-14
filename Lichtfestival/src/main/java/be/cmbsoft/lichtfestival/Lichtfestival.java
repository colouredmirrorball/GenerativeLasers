package be.cmbsoft.lichtfestival;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

import be.cmbsoft.ilda.IldaRenderer;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.KeyEvent;

import static be.cmbsoft.laseroutput.OutputOption.INVERT_Y;

public class Lichtfestival extends PApplet
{

    private Laser     leftLaser;
    private Laser     rightLaser;
    private PGraphics leftGraphics;
    private PGraphics rightGraphics;
    private int       dwellAmount = 6;

    PVector[] positions;
    PVector[] ends;
    int[]     colours;


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
        leftLaser = new Laser(this, "DE6656C57146").option(INVERT_Y);
        rightLaser = new Laser(this, "12A5FD136AFE");
        leftGraphics = createGraphics(width / 2, height, P3D);
        rightGraphics = createGraphics(width / 2, height, P3D);

        int amt = 10;
        positions = new PVector[amt];
        ends = new PVector[amt];
        colours = new int[amt];

        for (int i = 0; i < amt; i++)
        {
            positions[i] = new PVector(random(width), random(height));
            ends[i] = new PVector(random(width), random(height));
            colours[i] = color(random(255), random(255), random(255));
        }
    }

    @Override
    public void draw()
    {
        background(0);
        fill(255);


        IldaRenderer renderer = leftLaser.getRenderer();
        renderer.setOptimise(true);
        renderer.getOptimisationSettings().setBlankDwellAmount(dwellAmount);
        renderer.beginDraw();
        renderer.colorMode(PConstants.HSB);
        renderer.stroke(frameCount % 255, 255, 255);
        renderer.translate(width / 2, height / 2);
        renderer.rotate(map(mouseX, 0, width, 0, TWO_PI));
        renderer.translate(-width / 2, -height / 2);
        for (int i = 0; i < positions.length; i++)
        {
            renderer.stroke(colours[i]);
            renderer.line(positions[i].x, positions[i].y, ends[i].x, ends[i].y);
        }

        renderer.endDraw();
        int leftPointCount = renderer.getCurrentPointCount();

        renderer.getCurrentFrame().renderFrame(leftGraphics, true, width / 2, height);
        image(leftGraphics, 0, 0, width / 2, height);

        boolean leftConnected = leftLaser.output.isConnected();
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

        leftLaser.output();
        rightLaser.output();
    }

    @Override
    public void exit()
    {
        leftLaser.output.halt();
        rightLaser.output.halt();
        super.exit();
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        if (event.getKeyCode() == UP)
        {
            dwellAmount++;
        }
        if (event.getKeyCode() == DOWN)
        {
            dwellAmount = max(dwellAmount - 1, 0);
        }
        System.out.println(dwellAmount);
    }

}
