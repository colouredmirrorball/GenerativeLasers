package be.cmbsoft.lichtfestival;

import be.cmbsoft.ilda.IldaRenderer;
import be.cmbsoft.ilda.OptimisationSettings;
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
    private final Effect    currentEffect  = new MovingCircleEffect();
    private       boolean   boundSetupMode = false;


    public Lichtfestival()
    {
/*
        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : midiDeviceInfo)
        {
            println(info + ": " + info.getDescription() + " (" + info.getVendor() + " " + info.getVersion() + ")");
        }
*/
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
//        leftLaser  = new Laser(this, "12A5FD136AFE").option(INVERT_Y);
        leftLaser = new Laser(this, "DE6656C57146");
        rightLaser = new Laser(this, "");
        leftGraphics = createGraphics(width / 2, height, P3D);
        rightGraphics = createGraphics(width / 2, height, P3D);
        currentEffect.initialize(this);

        leftLaser.output.getBounds().setLowerLeft(new PVector(-1, 1));
        leftLaser.output.getBounds().setLowerRight(new PVector(1, 1));
        leftLaser.output.getBounds().setUpperRight(new PVector(1, -1));
        leftLaser.output.getBounds().setUpperLeft(new PVector(-1, -1));

        leftLaser.output.setIntensity(255);
        OptimisationSettings leftSettings = leftLaser.getRenderer().getOptimisationSettings();
        leftSettings.setBlankDwellAmount(16);


    }

    @Override
    public void draw()
    {
        background(0);
        fill(255);

        leftLaser.output.setIntensity(map(mouseY, 0, height, 0, 255));


        IldaRenderer renderer = leftLaser.getRenderer();
        renderer.setOptimise(true);
        renderer.beginDraw();
        currentEffect.generate(renderer, this);
        if (boundSetupMode)
        {
            renderer.stroke(100, 0, 0);
            renderer.rect(0, 0, renderer.width, renderer.height);
            if (mousePressed)
            {
                if (mouseX < width / 2 && mouseY < height / 2)
                {
                    leftLaser.output.getBounds().setUpperLeft(remappedMousePos());
                }
                if (mouseX > width / 2 && mouseY < height / 2)
                {
                    leftLaser.output.getBounds().setUpperRight(remappedMousePos());
                }
                if (mouseX < width / 2 && mouseY > height / 2)
                {
                    leftLaser.output.getBounds().setLowerLeft(remappedMousePos());
                }
                if (mouseX > width / 2 && mouseY > height / 2)
                {
                    leftLaser.output.getBounds().setLowerRight(remappedMousePos());
                }
            }
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
        //rightLaser.output();
    }

    private PVector remappedMousePos()
    {
        return new PVector(map(mouseX, 0, width, -1, 1),
            map(mouseY, 0, height, -1, 1));
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
