package be.cmbsoft.lichtfestival;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

import processing.core.PApplet;

public class Lichtfestival extends PApplet
{

    private Laser leftLaser;
    private Laser rightLaser;

    public Lichtfestival()
    {

        MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info: midiDeviceInfo) {
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
        size(1200, 600);
    }

    @Override
    public void setup()
    {
        leftLaser = new Laser(this, "12A5FD136AFE");
        rightLaser = new Laser(this, "456");
    }

    @Override
    public void draw()
    {
        background(0);
        fill(255);
        text("Left laser connected", 50, 40);
        if (leftLaser.output.isConnected()) {
            fill(0, 255, 0);
        } else {
            fill(255, 0, 0);
        }
        rect(20, 20, 20, 20);
        leftLaser.output();
        rightLaser.output();
    }
}
