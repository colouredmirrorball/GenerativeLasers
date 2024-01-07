package be.cmbsoft.lichtfestival;

import processing.core.PApplet;
import themidibus.MidiBus;

public class Lichtfestival extends PApplet
{

    private Laser leftLaser;
    private Laser rightLaser;


    public static void main(String[] passedArgs)
    {
        String[] appletArgs = new String[]{Lichtfestival.class.getPackageName()};
        Lichtfestival lichtfestival = new Lichtfestival();
        PApplet.runSketch(appletArgs, lichtfestival);
    }

    @Override
    public void settings()
    {
        size(1200, 600);
    }

    @Override
    public void setup()
    {
        System.out.println("Inputs");
        for (String s: MidiBus.availableInputs()) {
            System.out.println(s);
        }
        System.out.println("Outputs");
        for (String s: MidiBus.availableOutputs()) {
            System.out.println(s);
        }
        midi = new MidiBus(this, "Reaper to Leaser", "");
        leftLaser = new Laser(this, "123");
        rightLaser = new Laser(this, "456");
    }

    @Override
    public void draw()
    {
        background(0);
        leftLaser.output();
        rightLaser.output();
    }
}
