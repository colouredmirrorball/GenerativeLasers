package be.cmbsoft.livecontrol;

import processing.core.PApplet;

public class LiveControl extends PApplet
{

    public static void main(String[] passedArgs)
    {
        String[]    appletArgs  = new String[]{LiveControl.class.getPackageName()};
        LiveControl liveControl = new LiveControl();
        PApplet.runSketch(appletArgs, liveControl);
    }

    @Override
    public void settings()
    {
        size(1920, 1080);
    }

    @Override
    public void setup()
    {
        surface.setResizable(true);
    }

    @Override
    public void draw()
    {
        background(0);
    }
}
